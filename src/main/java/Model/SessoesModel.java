package Model;

import Bean.SessoesBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

/**
 *
 * @author Pichau
 */
public class SessoesModel {
    
     public static boolean createSessao(SessoesBean sessao, Connection con) throws SQLException {
        String sql = "INSERT INTO sessoes (codigo, nome) VALUES (?,?)";
    
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, sessao.getCodigo());
            pst.setString(2, sessao.getNome());
            
            int quantLinhas = pst.executeUpdate();
            
            if (quantLinhas > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar a sessao: " + e.getMessage());
            return false;
        }
         return false;
    }
     
    public static boolean alterarSessao(Integer id_sessao, String coluna, String new_value, Connection con) throws SQLException {
    String sql = "UPDATE sessoes SET " + coluna + " = ? WHERE id_sessao = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        if (coluna.equalsIgnoreCase("codigo")) {
            int codigo = Integer.parseInt(new_value); 
            pst.setInt(1, codigo);
        } else {
            pst.setString(1, new_value);
        }

        pst.setInt(2, id_sessao);

        int rowsAffected = pst.executeUpdate();

        return rowsAffected > 0;
    } catch (SQLException e) {
        System.err.println("Erro ao atualizar sessao: " + e.getMessage());
        return false;
    }
}
    
    public static int verificaAssociacao (Integer id_sessao, Connection con) throws SQLException {
       String sql = "SELECT COUNT(*) AS qtd FROM Livros WHERE id_sessao = ?";
    
       try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id_sessao);
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
     
    public static boolean deleteSessao(Integer id_sessao, Connection con) throws SQLException {
        String sql = "DELETE FROM sessoes WHERE id_sessao = ?";
    
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id_sessao);

            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir a sessao: " + e.getMessage());
            return false;
        }
    }
     
     
    public static LinkedHashSet<SessoesBean> listarSessoes(Connection con) throws SQLException {
        LinkedHashSet<SessoesBean> sessoes = new LinkedHashSet<>();
        String sql = "SELECT id_sessao, codigo, nome FROM sessoes ORDER BY id_sessao ASC";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                long idSessao = rs.getLong("id_sessao");
                int codigo = rs.getInt("codigo");
                String nome = rs.getString("nome");
                
                SessoesBean sessao = new SessoesBean(idSessao, codigo, nome);
                sessoes.add(sessao);
            }
        }
        
        return sessoes;
    }
}
