package Model;

import Bean.ClientesBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class ClientesModel {
    
    public static boolean createCliente(ClientesBean cliente, Connection con) throws SQLException {
        String sql = "INSERT INTO clientes (nome, email, telefone, cpf) VALUES (?, ?, ?, ?) RETURNING id_cliente";
    
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, cliente.getNome());
            pst.setString(2, cliente.getEmail());
            pst.setString(3, cliente.getTelefone());
            pst.setString(4, cliente.getCpf());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return true;
            } 
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            return false;
        }
        return false;
    }
    
    public static boolean alterarCliente(Integer idCliente, String coluna, Object newValue, Connection con) throws SQLException {
        String sql = "UPDATE clientes SET " + coluna + " = ? WHERE id_cliente = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            if (newValue instanceof String) {
                pst.setString(1, (String) newValue);
            } else {
                throw new IllegalArgumentException("Tipo de dado não suportado.");
            }

            pst.setInt(2, idCliente);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean verificarEmprestimosAtivos(Integer idCliente, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprestimos WHERE id_cliente = ? ";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idCliente);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar empréstimos ativos: " + e.getMessage());
        }
        return false;
    }

    public static boolean excluirCliente(Integer idCliente, Connection con) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idCliente);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
            return false;
        }
    }

    public static LinkedHashSet<ClientesBean> listarClientes(Connection con) throws SQLException {
        LinkedHashSet<ClientesBean> clientes = new LinkedHashSet<>();
        String sql = "SELECT id_cliente, nome, email, telefone, cpf FROM clientes ORDER BY id_cliente ASC";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idCliente = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");

                ClientesBean cliente = new ClientesBean(idCliente, nome, email, telefone, cpf);
                clientes.add(cliente);
            }
        }

        return clientes;
    }
    
    public static int getQuantClientes(Connection con) throws SQLException {
        String sql = "SELECT COUNT(id_cliente) AS total FROM clientes";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int quant = rs.getInt("total");
                return quant;
            } else {
                return 0; 
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar quantidade de clientes: " + e.getMessage());
            return -1;
        }
    }

}
