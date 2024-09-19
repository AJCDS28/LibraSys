package Model;

import Bean.LivrosBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author andrei
 */
public class LivrosModel {
    
    public static boolean createLivro(LivrosBean livro, Connection con) throws SQLException {
        String sql = "INSERT INTO livros (titulo, data_publicacao, quantidade_disponivel, quantidade_total, valor, nome_autor, id_sessao) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, livro.getTitulo());
            pst.setDate(2, livro.getAnoPublicacao());
            pst.setInt(3, livro.getQuantidadeDisponivel());
            pst.setInt(4, livro.getQuantidadeTotal());
            pst.setDouble(5, livro.getValor());
            pst.setString(6, livro.getNomeAutor());
           
            if (livro.getIdSessao() == null) {
                pst.setNull(7, java.sql.Types.BIGINT);
            } else {
                pst.setLong(7, livro.getIdSessao());
            }
            
            int quantLinhas = pst.executeUpdate();
            
            if (quantLinhas > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar livro: " + e.getMessage());
            return false;
        }
        return false;
    }
    
    public static boolean alterarLivro(Integer idLivro, double newValue, Connection con) throws SQLException {
        String sql = "UPDATE livros SET valor = ? WHERE id_livro = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, newValue);

            pst.setInt(2, idLivro);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }
    
    public static int verificaAssociacaoEmprestimos(Integer id_autor, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) AS qtd FROM emprestimos e JOIN concessao co ON e.id_emprestimo = co.id_emprestimo WHERE co.id_livro = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id_autor);

            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int rowsAffected = rs.getInt("qtd");
                return rowsAffected;
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar assosição: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean deleteLivro(Integer id_livro, Connection con) throws SQLException {
        String sql = "DELETE FROM livros WHERE id_livro = ?";
    
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id_livro);

            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir o livro: " + e.getMessage());
            return false;
        }
    }

    public static LinkedHashSet<LivrosBean> listarLivos(Connection con) throws SQLException {
        LinkedHashSet<LivrosBean> livros = new LinkedHashSet<>();
        String sql = "SELECT l.id_livro, l.titulo, l.data_publicacao, l.quantidade_disponivel, l.quantidade_total, l.valor, l.nome_autor, s.id_sessao, s.codigo, s.nome " +
                 "FROM livros l " +
                 "LEFT JOIN sessoes s ON l.id_sessao = s.id_sessao " +
                 "ORDER BY l.id_livro ASC";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                long idLivro = rs.getLong("id_livro");
                String titulo = rs.getString("titulo");
                Date data = rs.getDate("data_publicacao");
                int disponivel = rs.getInt("quantidade_disponivel");
                int total = rs.getInt("quantidade_total");
                double valor = rs.getDouble("valor");
                String autor = rs.getString("nome_autor");
                Long idSessao = rs.getObject("id_sessao", Long.class);
                Integer codigo = rs.getObject("codigo", Integer.class);
                String nome = rs.getString("nome");
                
                if (idSessao == null) {
                    codigo = -1;
                }
                
                LivrosBean livro = new LivrosBean(idLivro, titulo, data, disponivel, total, valor, autor, codigo, nome );
                livros.add(livro);
            }
        }
        return livros;
    }
    
    public static LinkedHashSet<LivrosBean> listarLivrosSemSessao(Connection con) throws SQLException {
        LinkedHashSet<LivrosBean> livros = new LinkedHashSet<>();
        String sql = "SELECT l.id_livro, l.titulo, l.data_publicacao, l.quantidade_disponivel, l.quantidade_total, l.valor, l.nome_autor " +
                     "FROM livros l " +
                     "LEFT JOIN sessoes s ON l.id_sessao = s.id_sessao " +
                     "WHERE l.id_sessao IS NULL " +
                     "ORDER BY l.id_livro ASC";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idLivro = rs.getLong("id_livro");
                String titulo = rs.getString("titulo");
                Date data = rs.getDate("data_publicacao");
                int disponivel = rs.getInt("quantidade_disponivel");
                int total = rs.getInt("quantidade_total");
                double valor = rs.getDouble("valor");
                String autor = rs.getString("nome_autor");

                LivrosBean livro = new LivrosBean(idLivro, titulo, data, disponivel, total, valor, autor, -1, null);
                livros.add(livro);
            }
        }
        return livros;
    }

    
    public static double somarValoresLivros(long[] idsLivros, Connection con) throws SQLException {
        double total = 0;
        String sql = "SELECT valor FROM livros WHERE id_livro = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (long idLivro : idsLivros) {
                pst.setLong(1, idLivro);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        total += rs.getDouble("valor");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao somar valores dos livros: " + e.getMessage());
            throw e;
        }
        
        return total;
    }
    
    public static int getQuantLivros(Connection con) throws SQLException {
        String sql = "SELECT COUNT(id_livro) AS total FROM livros";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int quant = rs.getInt("total");
                return quant;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar quantidades de livros: " + e.getMessage());
            return 0;
        }
    }
    
    public static List<LivrosBean> getLivrosInformacoes(Connection con, long[] idsLivros) throws SQLException {
        List<LivrosBean> livros = new ArrayList<>();
        String sql = "SELECT id_livro, titulo, quantidade_disponivel, valor " +
                     "FROM livros " +
                     "WHERE id_livro = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (long idLivro : idsLivros) {
                pst.setLong(1, idLivro);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String titulo = rs.getString("titulo");
                        int quantidadeDisponivel = rs.getInt("quantidade_disponivel");
                        double valor = rs.getDouble("valor");
                        livros.add(new LivrosBean(idLivro, titulo, quantidadeDisponivel, valor));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter informações dos livros: " + e.getMessage());
        }
        return livros;
    }
    
    public static boolean associarSessao(long idLivro, long idSessao, Connection con) throws SQLException {
        String sql = "UPDATE livros set id_sessao = ? WHERE id_livro = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idSessao);
            pst.setLong(2, idLivro);
            
            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;
        
        } catch (SQLException e) {
            System.err.println("Erro ao associar a sessao: " + e.getMessage());
            return false;
        }

    }
}