package Controller;

import Bean.ClientesBean;
import Model.ClientesModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import main.EntradaSaida;

/**
 *
 * @author Andrei
 */
public class ClientesController {

    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuClientes();
        switch (opt) {
            case 1 -> createCliente(con);
            case 2 -> {
                if(listarClientes(con).equals("")){
                    EntradaSaida.showMessage("Não há clientes cadastrados.\n");
                    menu(con);
                }else {
                    EntradaSaida.showMessage(listarClientes(con));
                    menu(con);
                }
            }
            case 3 -> updateCliente(con);
            case 4 -> deleteCliente(con);
        }
    }

    private void createCliente(Connection con) throws SQLException {
        String nome = EntradaSaida.getText("Digite o nome do cliente: ");
        String email = EntradaSaida.getText("Digite o email do cliente: ");
        String telefone = EntradaSaida.getText("Digite o telefone do cliente: ");
        String cpf = EntradaSaida.getText("Digite o cpf do cliente: ");
        ClientesBean cliente = new ClientesBean(nome, email, telefone, cpf);
        boolean success = ClientesModel.createCliente(cliente, con);
         
        if(success) {
            EntradaSaida.showMessage("Cliente cadastrado com sucesso!");
        } else {
            EntradaSaida.showMessage("Falha ao cadastrar o cliente");
        }
        menu(con);
    }

    private void updateCliente(Connection con) throws SQLException {
        String coluna, resposta;
        boolean success = false;
        if(listarClientes(con).equals("")){
            EntradaSaida.showMessage("Não há clientes cadastrados.\n");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarClientes(con));
        
        Integer idCliente = EntradaSaida.getNumber("Digite o ID do cliente: ");
        
        do {
            int opc = EntradaSaida.getNumber("Qual dado você gostaria de alterar? \n1 - Nome\n2 - Email\n3 - Telefone");
            
            switch(opc) {
                case 1 -> {
                    coluna = "nome";
                    String novoNome = EntradaSaida.getText("Digite o novo nome: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoNome, con);
                }
                case 2 -> {
                    coluna = "email";
                    String novoEmail = EntradaSaida.getText("Digite o novo email: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoEmail, con);
                }
                case 3 -> {
                    coluna = "telefone";
                    String novoTelefone = EntradaSaida.getText("Digite o novo telefone: ");
                    success = ClientesModel.alterarCliente(idCliente, coluna, novoTelefone, con);
                }
            }
            if(success) {
                EntradaSaida.showMessage("Cliente alterado com sucesso!");
            } else {
                EntradaSaida.showMessage("Falha ao alterar o cliente");
            }

            resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
        } while(resposta.equalsIgnoreCase("s"));
        
        menu(con);
    }

    private void deleteCliente(Connection con) throws SQLException {
        if(listarClientes(con).equals("")){
            EntradaSaida.showMessage("Não há clientes cadastrados.\n");
            menu(con);
            return;
        }       
        EntradaSaida.showMessage(listarClientes(con));
        
        Integer idCliente = EntradaSaida.getNumber("Digite o ID do cliente: ");
        boolean emprestimosAtivos = ClientesModel.verificarEmprestimosAtivos(idCliente, con);
        
        if(!emprestimosAtivos){
            boolean success = ClientesModel.excluirCliente(idCliente, con);
        
            if(success) {
                EntradaSaida.showMessage("Cliente excluído com sucesso!");
            } else {
                EntradaSaida.showMessage("Falha ao excluir o cliente");
            }
        } else {
            EntradaSaida.showMessage("O cliente possui empréstimos e não pode ser excluído.");
        }
        menu(con);
    }

    public static String listarClientes(Connection con) throws SQLException {
        LinkedHashSet<ClientesBean> all = ClientesModel.listarClientes(con);

        StringBuilder sb = new StringBuilder();
        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Clientes:\n\n");

            for (ClientesBean cliente : all) {
                sb.append(cliente.toString()).append("\n");
            }
        }
        return sb.toString();
    }
    
    public int getQuantClientesCadas(Connection con) {
        try {
            return ClientesModel.getQuantClientes(con);
        } catch (SQLException e) {
                EntradaSaida.showMessage("Erro ao verificar quantidades de clientes.");
        }
        return -1;
    }
}
