package Model;

import Bean.PagamentosBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class PagamentosModel {

    public static boolean createPagamento(PagamentosBean pagamento, Connection con) throws SQLException {
        String sql = "INSERT INTO pagamentos (valor_pago, status, id_emprestimo) VALUES (?, ?, ?) RETURNING id_pagamento";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, pagamento.getValorPago());
            pst.setInt(2, pagamento.getStatus());
            pst.setLong(3, pagamento.getIdEmprestimo());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pagamento.setIdPagamento(rs.getLong("id_pagamento"));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar pagamento: " + e.getMessage());
        }
        return false;
    }

    public static LinkedHashSet<PagamentosBean> listarPagamentos(long idEmprestimo, Connection con) throws SQLException {
        LinkedHashSet<PagamentosBean> pagamentos = new LinkedHashSet<>();
        String sql = "SELECT e.id_emprestimo, e.valor_emprestimo, " +
                     "COALESCE(SUM(p.valor_pago), 0) AS total_pago, " +
                     "(SELECT p2.status FROM pagamentos p2 WHERE p2.id_emprestimo = e.id_emprestimo ORDER BY p2.id_pagamento DESC LIMIT 1) AS status_final " +
                     "FROM emprestimos e " +
                     "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                     "WHERE e.id_emprestimo = ? " +
                     "GROUP BY e.id_emprestimo";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double valorEmprestimo = rs.getDouble("valor_emprestimo");
                    double totalPago = rs.getDouble("total_pago");
                    int statusFinal = rs.getInt("status_final");

                    String statusDescricao;
                    switch (statusFinal) {
                        case 1 -> statusDescricao = "Pendente";
                        case 2 -> statusDescricao = "Pago";
                        case 3 -> statusDescricao = "Atrasado";
                        default -> statusDescricao = "Pendente";
                    }

                    PagamentosBean pagamento = new PagamentosBean(idEmprestimo, valorEmprestimo, totalPago, statusDescricao);
                    pagamentos.add(pagamento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar pagamentos por empréstimo: " + e.getMessage());
        }

        return pagamentos;
    }

    public static boolean atualizarPagamento(long idEmprestimo,double valorPago, Connection con) throws SQLException {
        String sql = "UPDATE pagamentos SET valor_pago = valor_pago + ?, status = 2 WHERE id_emprestimo = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, valorPago);
            pst.setLong(2, idEmprestimo);
            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o status do pagamento: " + e.getMessage());
            return false;
        }
    }
    
    public double getValorPagoTotal(long idEmprestimo, Connection con) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor_pago), 0) AS valor_pago_total FROM pagamentos WHERE id_emprestimo = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor_pago_total");
            }
        }
        return 0;
    }
    
    public static PagamentosBean getPagamentoPorEmprestimo(long idEmprestimo, Connection con) throws SQLException {
        String sql = "SELECT id_pagamento, valor_pago, status FROM pagamentos WHERE id_emprestimo = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idEmprestimo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    long idPagamento = rs.getLong("id_pagamento");
                    double valorPago = rs.getDouble("valor_pago");
                    int status = rs.getInt("status");

                    return new PagamentosBean(idPagamento, valorPago, status, idEmprestimo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pagamento por empréstimo: " + e.getMessage());
            throw e;
        }
        return null;
    }
    
    public static boolean existePagamento(Connection con, long idPagamento) throws SQLException {
        String sql = "SELECT id_pagamento FROM pagamentos WHERE id_pagamento = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idPagamento);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean excluirPagamento(Connection con, long idPagamento) throws SQLException {
        String sql = "DELETE FROM pagamentos WHERE id_pagamento = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idPagamento);
            int linhasAfetadas = pst.executeUpdate();
            return linhasAfetadas > 0;
        }
    }
    
    public static LinkedHashSet<PagamentosBean> listarPagamentos(Connection con) throws SQLException {
        String sql = "SELECT id_pagamento, valor_pago FROM pagamentos";

        LinkedHashSet<PagamentosBean> pagamentos = new LinkedHashSet<>();

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idPagamento = rs.getLong("id_pagamento");
                double valorPago = rs.getDouble("valor_pago");
                PagamentosBean pagamento = new PagamentosBean(idPagamento, valorPago);
                pagamentos.add(pagamento);
            }
        }
        return pagamentos;
    }
}
