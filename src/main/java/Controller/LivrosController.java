package Controller;

import main.EntradaSaida;
import Bean.LivrosBean;
import Model.LivrosModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author Pichau
 */
public class LivrosController {
    
    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuLivros();
        switch (opt) {
            case 1 -> createLivro(con);
            case 2 -> {
                if(listarLivros(con).equals("")){
                    EntradaSaida.showMessage("Não há livros cadastrados.\n");
                    menu(con);
                }else{
                    EntradaSaida.showMessage(listarLivros(con));
                    menu(con);
                }
            }
            case 3 -> updateLivro(con);
            case 4 -> deleteLivro(con);
            case 5 -> associarSessao(con);
        }
    }

    private void createLivro(Connection con) throws SQLException {
        boolean success = false;
        String titulo = EntradaSaida.getText("Digite o nome do livro: ");
        Date data = EntradaSaida.getDate("Digite a data de publicação do livro: ");
        int quant_total = EntradaSaida.getNumber("Digite a quantidade total de livros");
        int quant, quant_disponivel;
        Long idSessao = null;
        
        do{
            quant_disponivel = EntradaSaida.getNumber("Digite a quantidade de livros disponiveis");
            if(quant_disponivel > quant_total) {
                EntradaSaida.showMessage("Quantidade maior que a quantidade total!");
                quant = 0;
            }else{
                quant = 1;
            }
            
        }while(quant != 1);
        
        Double valor = EntradaSaida.getDecimal("Digite o valor do livro");
        String autor = EntradaSaida.getText("Digite o nome do autor do livro: ");
        String issessao = EntradaSaida.getText("Deseja adiciona-lo a uma sessão? (s/n)");
        
        if(issessao.equalsIgnoreCase("s")){
            SessoesController sessao = new SessoesController();
            if(sessao.listarSessoes(con).equals("")){
                EntradaSaida.showMessage("Não há sessões cadastradas.\n");
            } else {
                EntradaSaida.showMessage(sessao.listarSessoes(con));
                long id_sessao = EntradaSaida.getNumber("Digite o id da sessão:");
                idSessao = (Long)id_sessao;
            }
            
            LivrosBean livro = new LivrosBean(titulo, data, quant_disponivel, quant_total, valor, autor, idSessao);
            success = LivrosModel.createLivro(livro, con);
        } else{
            LivrosBean livro = new LivrosBean(titulo, data, quant_disponivel, quant_total, valor, autor);
            success = LivrosModel.createLivro(livro, con);
        }
        
         if(success) {
              EntradaSaida.showMessage("Livro cadastrado com sucesso!");
         } else {
             EntradaSaida.showMessage("Falha ao cadastrar o livro");
         }
         menu(con);
    }
    
    public String listarLivros(Connection con) throws SQLException {
        LinkedHashSet<LivrosBean> all = LivrosModel.listarLivos(con);

        StringBuilder sb = new StringBuilder();
        
        if (all.isEmpty()) {
            sb.append("");
        } else{
            sb.append("Lista de Livros:\n");

            for (LivrosBean autor : all) {
                sb.append(autor.toString()).append("\n");
            }
        }
        return sb.toString();
    }
    
    public String listarLivrosSemSessao(Connection con) throws SQLException {
        LinkedHashSet<LivrosBean> all = LivrosModel.listarLivrosSemSessao(con);

        StringBuilder sb = new StringBuilder();
        
        if (all.isEmpty()) {
            return "";
        } else{
            sb.append("Lista de Livros sem sessão:\n");

            for (LivrosBean autor : all) {
                sb.append(autor.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    private void updateLivro(Connection con) throws SQLException {
        String resposta;
        boolean success = false;
        if(listarLivros(con).equals("")){
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarLivros(con));

        Integer idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
        do {
            double novoValor = EntradaSaida.getDecimal("Digite o novo valor: ");
            success = LivrosModel.alterarLivro(idLivro, novoValor, con);

            if (success) {
                EntradaSaida.showMessage("Livro alterado com sucesso!");
                resposta = EntradaSaida.getText("Deseja alterar mais algum dado? s/n");
            } else {
                EntradaSaida.showMessage("Falha ao alterar o livro");
                resposta = "n";
                menu(con);
                return;
            }
        } while (resposta.equalsIgnoreCase("s"));
        menu(con);
    }

    private void deleteLivro(Connection con) throws SQLException {
        if(listarLivros(con).equals("")){
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarLivros(con));
        
        Integer id_livro = EntradaSaida.getNumber("Digite o ID do livro: ");
        int associacao = LivrosModel.verificaAssociacaoEmprestimos(id_livro, con);
        
        if(associacao <=0) {
            boolean succes = LivrosModel.deleteLivro(id_livro, con);

            if(succes) {
                  EntradaSaida.showMessage("Livro excluido com sucesso!");
                  menu(con);
                  return;
             } else {
                 EntradaSaida.showMessage("Falha ao excluir o livro");
                 menu(con);
                 return;
             }
        }
        EntradaSaida.showMessage("Livro esta associado a um empréstimo, não é possivel removê-lo");
        menu(con);
            
    }
    
    private void associarSessao(Connection con) throws SQLException {
        if(listarLivrosSemSessao(con).equals("")){
            EntradaSaida.showMessage("Não há livros sem sessão.");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarLivrosSemSessao(con));
        long idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
        
        SessoesController sessao = new SessoesController();
        if(sessao.listarSessoes(con).equals("")){
            EntradaSaida.showMessage("Não há sessões cadastradas.\n");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(sessao.listarSessoes(con));
        long idSessao = EntradaSaida.getNumber("Digite o id da sessão:");
        boolean livro = LivrosModel.associarSessao(idLivro, idSessao, con);
        
        if(livro) {
            EntradaSaida.showMessage("Sessão associada ao livro com sucesso!");
            menu(con);
            return;
        }
        EntradaSaida.showMessage("Falha ao associar sessão ao livro");
        menu(con);
    }
    
    public double mostrarSomaValoresLivros(Connection con, long[] idsLivros) {
        try {
            return LivrosModel.somarValoresLivros(idsLivros, con);
        } catch (SQLException e) {
            EntradaSaida.showMessage("Erro ao calcular o valor total dos livros.");
        }
        return 0;
    }
    
    public int getQuantLivrosCadas(Connection con) {
        try {
            return LivrosModel.getQuantLivros(con);
        } catch (SQLException e) {
                EntradaSaida.showMessage("Erro ao verificar quantidades de livros.");
        }
        return 0;
    }
    
    public List<LivrosBean> getLivrosInformacoes(Connection con, long[] idsLivros) {
        try {
            return LivrosModel.getLivrosInformacoes(con, idsLivros);
        } catch (SQLException e) {
            EntradaSaida.showMessage("Erro ao obter informações dos livros.");
        }
        return Collections.emptyList();
    }
}
