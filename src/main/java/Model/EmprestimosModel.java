package Model;

import Bean.EmprestimosBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import main.EntradaSaida;

public class EmprestimosModel {

    public static long createEmprestimo(EmprestimosBean emprestimo, long[] livros, Connection con) throws SQLException {
        String sql = "INSERT INTO emprestimos (id_cliente, data_emprestimo, data_devolucao, valor_emprestimo, status) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id_emprestimo";
        
        String updateQuantidadeSql = "UPDATE livros SET quantidade_disponivel = quantidade_disponivel - 1 WHERE id_livro = ?";

        try (PreparedStatement pst = con.prepareStatement(sql);
             PreparedStatement updateStmt = con.prepareStatement(updateQuantidadeSql)) {
            pst.setLong(1, emprestimo.getIdCliente());
            pst.setDate(2,emprestimo.getDataEmprestimo());
            pst.setDate(3, emprestimo.getDataDevolucao());
            pst.setDouble(4, emprestimo.getvalorEmprestimo());
            pst.setInt(5, emprestimo.getStatusEmp());
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                long idEmprestimo = rs.getLong("id_emprestimo");
                for (long idLivro : livros) {
                    boolean sucessoAssociacao = createConcessao(idEmprestimo, idLivro, con);
                    if (!sucessoAssociacao) {
                        return 0;
                    }
                    
                    updateStmt.setLong(1, idLivro);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        return 0;
                    }
                }
                return idEmprestimo;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar o empréstimo: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean createConcessao(long idEmprestimo, long idLivro, Connection con) throws SQLException {
        String sql = "INSERT INTO concessao (id_emprestimo, id_livro) VALUES (?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);
            pst.setLong(2, idLivro);
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao associar livro ao empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean alterarEmprestimo(long idEmprestimo, Date novaDataDevolucao, Connection con) throws SQLException {
        String sql = "UPDATE emprestimos SET data_devolucao = ?, status = 1 WHERE id_emprestimo = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDate(1, novaDataDevolucao);
            pst.setLong(2, idEmprestimo);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar o empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean excluirEmprestimo(Long idEmprestimo, Connection con) throws SQLException {
        String updateLivroSql = "UPDATE livros SET quantidade_disponivel = quantidade_disponivel + 1 " +
                                "WHERE id_livro IN (SELECT id_livro FROM concessao WHERE id_emprestimo = ?)";
        String deleteConcessaoSql = "DELETE FROM concessao WHERE id_emprestimo = ?";
        String deleteEmprestimoSql = "DELETE FROM emprestimos WHERE id_emprestimo = ?";

        try {
            try (PreparedStatement updateLivroPst = con.prepareStatement(updateLivroSql)) {
                updateLivroPst.setLong(1, idEmprestimo);
                updateLivroPst.executeUpdate();
            }

            try (PreparedStatement deleteConcessaoPst = con.prepareStatement(deleteConcessaoSql)) {
                deleteConcessaoPst.setLong(1, idEmprestimo);
                deleteConcessaoPst.executeUpdate();
            }

            try (PreparedStatement deleteEmprestimoPst = con.prepareStatement(deleteEmprestimoSql)) {
                deleteEmprestimoPst.setLong(1, idEmprestimo);
                int affectedRows = deleteEmprestimoPst.executeUpdate();

                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir o empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean temPagamentosRelacionados(long idEmprestimo, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pagamentos WHERE id_emprestimo = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar pagamentos relacionados: " + e.getMessage());
        }
        return false;
    }

    public static LinkedHashSet<EmprestimosBean> listarEmprestimos(Connection con) throws SQLException {
        LinkedHashMap<Long, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();
        String sql = "SELECT " +
                     "e.id_emprestimo, " +
                     "c.nome AS nome_cliente, " +
                     "e.data_emprestimo, " +
                     "e.data_devolucao, " +
                     "e.valor_emprestimo, " +
                     "CASE e.status " +
                     "    WHEN 1 THEN 'No prazo' " +
                     "    WHEN 2 THEN 'Devolvido' " +
                     "    WHEN 3 THEN 'Atrasado' " +
                     "END AS status_emprestimo, " +
                     "p.status AS status_pagamento, " +
                     "l.titulo AS titulo_livro " +
                     "FROM emprestimos e " +
                     "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                     "JOIN concessao co ON e.id_emprestimo = co.id_emprestimo " +
                     "JOIN livros l ON co.id_livro = l.id_livro " +
                     "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                     "ORDER BY e.id_emprestimo ASC";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idEmprestimo = rs.getLong("id_emprestimo");
                String nomeCliente = rs.getString("nome_cliente");
                java.sql.Date dataEmprestimo = rs.getDate("data_emprestimo");
                java.sql.Date dataDevolucao = rs.getDate("data_devolucao");
                String statusEmprestimo = rs.getString("status_emprestimo");
                double valorEmprestimo = rs.getDouble("valor_emprestimo");
                int statusPagamento = rs.getInt("status_pagamento");
                String tituloLivro = rs.getString("titulo_livro");
                
                String statusPagamentoDescricao;
                switch (statusPagamento) {
                    case 1 -> statusPagamentoDescricao = "Pendente";
                    case 2 -> statusPagamentoDescricao = "Pago";
                    case 3 -> statusPagamentoDescricao = "Atrasado";
                    default -> statusPagamentoDescricao = "Pendente";
                }

                if (!emprestimosMap.containsKey(idEmprestimo)) {
                    EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao, 
                                                                      statusEmprestimo, 
                                                                      valorEmprestimo, statusPagamentoDescricao, tituloLivro, nomeCliente);
                    emprestimosMap.put(idEmprestimo, emprestimo);
                } 

                EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
                emprestimo.addTituloLivro(tituloLivro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LinkedHashSet<EmprestimosBean> emprestimos = new LinkedHashSet<>(emprestimosMap.values());
        return emprestimos;

    }
    
    public static LinkedHashSet<EmprestimosBean> listarEmprestimosPendentes(Connection con) throws SQLException {
        LinkedHashMap<Long, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();
        String sql = "SELECT " +
                     "e.id_emprestimo, " +
                     "c.nome AS nome_cliente, " +
                     "e.data_emprestimo, " +
                     "e.data_devolucao, " +
                     "e.valor_emprestimo, " +
                     "CASE e.status " +
                     "    WHEN 1 THEN 'No prazo' " +
                     "    WHEN 2 THEN 'Devolvido' " +
                     "    WHEN 3 THEN 'Atrasado' " +
                     "END AS status_emprestimo, " +
                     "p.status AS status_pagamento, " +
                     "l.titulo AS titulo_livro " +
                     "FROM emprestimos e " +
                     "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                     "JOIN concessao co ON e.id_emprestimo = co.id_emprestimo " +
                     "JOIN livros l ON co.id_livro = l.id_livro " +
                     "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                     "WHERE e.status != 2 " +
                     "ORDER BY e.id_emprestimo ASC";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idEmprestimo = rs.getLong("id_emprestimo");
                String nomeCliente = rs.getString("nome_cliente");
                java.sql.Date dataEmprestimo = rs.getDate("data_emprestimo");
                java.sql.Date dataDevolucao = rs.getDate("data_devolucao");
                String statusEmprestimo = rs.getString("status_emprestimo");
                double valorEmprestimo = rs.getDouble("valor_emprestimo");
                int statusPagamento = rs.getInt("status_pagamento");
                String tituloLivro = rs.getString("titulo_livro");
                
                String statusPagamentoDescricao;
                switch (statusPagamento) {
                    case 1 -> statusPagamentoDescricao = "Pendente";
                    case 2 -> statusPagamentoDescricao = "Pago";
                    case 3 -> statusPagamentoDescricao = "Atrasado";
                    default -> statusPagamentoDescricao = "Pendente";
                }

                if (!emprestimosMap.containsKey(idEmprestimo)) {
                    EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao, 
                                                                      statusEmprestimo, 
                                                                      valorEmprestimo, statusPagamentoDescricao, tituloLivro, nomeCliente);
                    emprestimosMap.put(idEmprestimo, emprestimo);
                } 

                EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
                emprestimo.addTituloLivro(tituloLivro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LinkedHashSet<EmprestimosBean> emprestimos = new LinkedHashSet<>(emprestimosMap.values());
        return emprestimos;

    }
    
    public static LinkedHashSet<EmprestimosBean> listarEmprestimosSemPagamento(Connection con) throws SQLException {
        LinkedHashMap<Long, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();
        String sql = "SELECT " +
                    "e.id_emprestimo, " +
                    "c.nome AS nome_cliente, " +
                    "e.data_emprestimo, " +
                    "e.data_devolucao, " +
                    "e.valor_emprestimo, " +
                    "CASE e.status " +
                    "    WHEN 1 THEN 'No prazo' " +
                    "    WHEN 2 THEN 'Devolvido' " +
                    "    WHEN 3 THEN 'Atrasado' " +
                    "END AS status_emprestimo, " +
                    "l.titulo AS titulo_livro " +
                    "FROM emprestimos e " +
                    "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                    "JOIN concessao co ON e.id_emprestimo = co.id_emprestimo " +
                    "JOIN livros l ON co.id_livro = l.id_livro " +
                    "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                    "WHERE p.valor_pago < e.valor_emprestimo OR p.id_pagamento IS NULL " + 
                    "ORDER BY e.id_emprestimo ASC";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idEmprestimo = rs.getLong("id_emprestimo");
                String nomeCliente = rs.getString("nome_cliente");
                java.sql.Date dataEmprestimo = rs.getDate("data_emprestimo");
                java.sql.Date dataDevolucao = rs.getDate("data_devolucao");
                String statusEmprestimo = rs.getString("status_emprestimo");
                double valorEmprestimo = rs.getDouble("valor_emprestimo");
                String tituloLivro = rs.getString("titulo_livro");

                if (!emprestimosMap.containsKey(idEmprestimo)) {
                    EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao, 
                                                                      statusEmprestimo, 
                                                                      valorEmprestimo, "Pendente", tituloLivro, nomeCliente);
                    emprestimosMap.put(idEmprestimo, emprestimo);
                }

                EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
                emprestimo.addTituloLivro(tituloLivro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new LinkedHashSet<>(emprestimosMap.values());
    }

    
    public double getValorEmprestimo(long idEmprestimo, Connection con) throws SQLException {
        String sql = "SELECT valor_emprestimo FROM emprestimos WHERE id_emprestimo = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor_emprestimo");
            }
        }
        return 0;
    }
    
    public static void atualizarStatusEmprestimos(Connection con) throws SQLException {
        String sql = "UPDATE emprestimos SET status = 3 " +
                     "WHERE data_devolucao < CURRENT_DATE AND status = 1";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            int rowsUpdated = pst.executeUpdate();
            System.out.println(rowsUpdated + " empréstimos atualizados para 'Atrasado'.");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status dos empréstimos: " + e.getMessage());
        }
    }
    
    public static boolean verificarEmprestimoPago(long idEmprestimo, Connection con) throws SQLException {
        String sql = "SELECT e.valor_emprestimo, COALESCE(SUM(p.valor_pago), 0) AS valor_pago " +
                     "FROM emprestimos e " +
                     "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                     "WHERE e.id_emprestimo = ? " +
                     "GROUP BY e.valor_emprestimo";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double valorEmprestimo = rs.getDouble("valor_emprestimo");
                    double valorPago = rs.getDouble("valor_pago");

                    return valorPago == valorEmprestimo;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar pagamento: " + e.getMessage());
            throw e;
        }

        return false;
    }
    
    public static boolean darBaixaEmprestimo(long idEmprestimo, Connection con) throws SQLException {
        String updateStatusSQL = "UPDATE emprestimos SET status = 2 WHERE id_emprestimo = ?";
        String updateLivroQuantidadeSQL = "UPDATE livros SET quantidade_disponivel = quantidade_disponivel + 1 " +
                                          "WHERE id_livro IN (SELECT id_livro FROM concessao WHERE id_emprestimo = ?)";

        try (PreparedStatement pstUpdateStatus = con.prepareStatement(updateStatusSQL);
             PreparedStatement pstUpdateLivroQuantidade = con.prepareStatement(updateLivroQuantidadeSQL)) {

            pstUpdateStatus.setLong(1, idEmprestimo);
            int rowsAffectedStatus = pstUpdateStatus.executeUpdate();

            pstUpdateLivroQuantidade.setLong(1, idEmprestimo);
            int rowsAffectedLivros = pstUpdateLivroQuantidade.executeUpdate();

            return rowsAffectedStatus > 0 && rowsAffectedLivros > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
