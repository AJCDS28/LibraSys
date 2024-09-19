package Controller;

import Bean.SessoesBean;
import Model.SessoesModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import main.EntradaSaida;

/**
 *
 * @author Andrei
 */
public class SessoesController {
    
    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuSessoes();
        switch (opt) {
            case 1 -> createSessao(con);
            case 2 -> {
                    if(listarSessoes(con).equals("")){
                    EntradaSaida.showMessage("Não há sessões cadastradas.\n");
                    menu(con);
                }else{
                    EntradaSaida.showMessage(listarSessoes(con)) ;
                    menu(con);
                }
            }
            case 3 -> updateSessao(con);
            case 4 -> deleteSessao(con);
        }
    }
    
    private void createSessao(Connection con) throws SQLException {
        int codigo = EntradaSaida.getNumber("Digite o codigo da sessão: ");
        String nome = EntradaSaida.getText("Digite o nome da sessao: ");
        SessoesBean editora = new SessoesBean(codigo, nome);
        boolean succes = SessoesModel.createSessao(editora, con);
         
         if(succes) {
              EntradaSaida.showMessage("Sessão cadastrada com sucesso!");
              menu(con);
              return;
         }
         EntradaSaida.showMessage("Falha ao cadastrar a sessao");
         menu(con);
    }
    
    private void updateSessao (Connection con) throws SQLException {
        String coluna, resposta;
        boolean succes = false;
        if(listarSessoes(con).equals("")){
                EntradaSaida.showMessage("Não há sessões cadastradas.\n");
                menu(con);
                return;
            }
        EntradaSaida.showMessage(listarSessoes(con));
        
        Integer idSessao = EntradaSaida.getNumber("Digite o ID do sessão: ");
        
        do{
            int opc = EntradaSaida.getNumber("Qual dos dados gostaria de alterar? \n 1 - Codigo\n 2 - Nome");
            
            switch(opc) {
                case 1 -> {
                    coluna = "codigo";
                    String novoCodigo = EntradaSaida.getText("Digite o novo codigo: ");
                    succes = SessoesModel.alterarSessao(idSessao, coluna, novoCodigo, con);
                }
                case 2 -> {
                    coluna = "nome";
                    String novoNome = EntradaSaida.getText("Digite o novo nome: ");
                    succes = SessoesModel.alterarSessao(idSessao, coluna, novoNome, con);
                }

            }
            if(succes) {
              EntradaSaida.showMessage("Sessão alterada com sucesso!");
              menu(con);
              return;
            } else {
                EntradaSaida.showMessage("Falha ao alterar a sessao");
            }

            resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
        }while( resposta.equalsIgnoreCase("s") );
        
        menu(con);
    }
    
    private void deleteSessao(Connection con) throws SQLException {
        if(listarSessoes(con).equals("")){
            EntradaSaida.showMessage("Não há sessões cadastradas.\n");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarSessoes(con));
        
        Integer id_sessao = EntradaSaida.getNumber("Digite o ID da sessão: ");
        int associacao = SessoesModel.verificaAssociacao(id_sessao, con);
        
        if(associacao <= 0){
            boolean succes = SessoesModel.deleteSessao(id_sessao, con);
        
            if(succes) {
                  EntradaSaida.showMessage("Sessão excluida com sucesso!");
                  menu(con);
                  return;
             } else {
                 EntradaSaida.showMessage("Falha ao excluir a sessão");
                 menu(con);
                 return;
             }
        }
        EntradaSaida.showMessage("A sessao tem livros associados, não foi possivel realizar a exclusão!");
        
        menu(con);
    }
    
    public String listarSessoes(Connection con) throws SQLException {
        LinkedHashSet<SessoesBean> all = SessoesModel.listarSessoes(con);

        StringBuilder sb = new StringBuilder();
        if (all.isEmpty()) {
            sb.append("");
        } else{
            sb.append("Lista de Sessões:\n\n");

            for (SessoesBean autor : all) {
                sb.append(autor.toString()).append("\n");
            }
        }
        return sb.toString();
    }
    
}
