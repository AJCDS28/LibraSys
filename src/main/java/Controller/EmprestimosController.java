package Controller;

import Bean.EmprestimosBean;
import Bean.LivrosBean;
import Bean.PagamentosBean;
import Model.EmprestimosModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import main.EntradaSaida;

/**
 *
 * @author Pichau
 */
public class EmprestimosController {
    
    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuEmprestimos();
        switch (opt) {
            case 1 -> createEmprestimo(con);
            case 2 -> {
                if(listarEmprestimos(con).equals("")){
                    EntradaSaida.showMessage("Não há emprestimos cadastrados");
                    menu(con);
                } else{
                    EntradaSaida.showMessage(listarEmprestimos(con));
                    menu(con);
                }
            }
            case 3 -> atualizarEmprestimo(con);
            case 4 -> excluirEmprestimo(con);
            case 5 -> realizarDevolucao(con);
        }
    }
    
   private int createEmprestimo(Connection con) throws SQLException {
        LocalDate data = LocalDate.now(); 
        Date dataEmprestimo = Date.valueOf(data);
        int quantLivros;
        List<LivrosBean> livros = new ArrayList<>();

        LivrosController livroController = new LivrosController();
        int quantLivrosCadas = livroController.getQuantLivrosCadas(con);
        if(quantLivrosCadas == 0 ){
            EntradaSaida.showMessage("Não há livros cadastrados para realizar o emprestimo");
             return 0;
        }
        
        ClientesController clienteController = new ClientesController();
        int quantClientes = clienteController.getQuantClientesCadas(con);
        if (quantClientes == -1) {
            EntradaSaida.showMessage("Não há clientes cadastrados para realizar o emprestimo");
            return 0;
        }
        do {
            String sql = "Total de livros cadastrados: " + quantLivrosCadas + "\n\nQuantos livros serão emprestados?";
            quantLivros = EntradaSaida.getNumber(sql);

            if (quantLivros > quantLivrosCadas) {
                EntradaSaida.showMessage("A quantidade de livros a ser emprestada não pode ser maior que a quantidade total de livros cadastrados. Tente novamente.");
            }

        } while (quantLivros > quantLivrosCadas);

        long[] idLivrosArray = new long[quantLivros];

        for (int i = 0; i < quantLivros; i++) {
            if(livroController.listarLivros(con).equals("")){
            EntradaSaida.showMessage("Não há livros cadastrados.\n");
            menu(con);
            return 0;
        }
            EntradaSaida.showMessage(livroController.listarLivros(con));
            long idLivro = EntradaSaida.getNumber("Digite o ID do livro: ");
            idLivrosArray[i] = idLivro;
        }

        List<LivrosBean> livrosInformacoes = livroController.getLivrosInformacoes(con, idLivrosArray);

        double valorEmprestimo = 0.0;
        for (LivrosBean livro : livrosInformacoes) {
            if (livro.getQuantidadeDisponivel() < 1) {
                EntradaSaida.showMessage("O livro " + livro.getTitulo() + " não está disponível para empréstimo.");
                menu(con);
                return 0;
            }
            valorEmprestimo += livro.getValor();
        }

        valorEmprestimo = Math.round(valorEmprestimo * 100.0) / 100.0;
        
        if(ClientesController.listarClientes(con).equals("")){
            EntradaSaida.showMessage("Não clientes cadastrados");
            menu(con);
            return 0;
        }
        EntradaSaida.showMessage(ClientesController.listarClientes(con));
        long idCliente = EntradaSaida.getNumber("Digite o id do cliente: ");

        Date dataDevolucao = EntradaSaida.getDate("Digite a data de devolução (dd/MM/yyyy): ");
        java.sql.Date hoje = new java.sql.Date(System.currentTimeMillis());
        
        if (dataDevolucao.before(hoje)) {
            EntradaSaida.showMessage("A data de devolução deve ser maior que a data atual.");
            menu(con); 
            return 0;
        }
        
        valorEmprestimo = Math.round(valorEmprestimo * 100.0) / 100.0;
        int tipoPagamento = EntradaSaida.getNumber("Qual a forma de pagamento?\n1- À vista\n2- Parcelado\n3- Na devolução");

        StringBuilder resumoEmprestimo = new StringBuilder();
        resumoEmprestimo.append("Resumo do Empréstimo:\n");
        resumoEmprestimo.append("Cliente ID: ").append(idCliente).append("\n");
        resumoEmprestimo.append("Data de Devolução: ").append(new SimpleDateFormat("dd/MM/yyyy").format(dataDevolucao)).append("\n");
        resumoEmprestimo.append("Valor Total do Empréstimo: R$ ").append(valorEmprestimo).append("\n");
        resumoEmprestimo.append("Forma de Pagamento: ");
        switch (tipoPagamento) {
            case 1 -> resumoEmprestimo.append("À vista");
            case 2 -> resumoEmprestimo.append("Entrada");
            case 3 -> resumoEmprestimo.append("Na devolução");
        }
        resumoEmprestimo.append("\nLivros Emprestados: ");
        String livrosEmprestados = livrosInformacoes.stream()
            .map(LivrosBean::getTitulo)
            .collect(Collectors.joining(", "));

        resumoEmprestimo.append(livrosEmprestados).append("\n");

        EntradaSaida.showMessage(resumoEmprestimo.toString());

        String confirmar = EntradaSaida.getText("Deseja confirmar o empréstimo? (s/n)");
        if (confirmar.equalsIgnoreCase("s")) {
            EmprestimosBean emprestimo = new EmprestimosBean(idCliente, dataEmprestimo, dataDevolucao, 1, valorEmprestimo);
            long id_emprestimo = EmprestimosModel.createEmprestimo(emprestimo, idLivrosArray, con);

            if (id_emprestimo != 0) {
                PagamentosController pagamentosController = new PagamentosController();
                switch (tipoPagamento) {
                    case 1 -> {
                        PagamentosBean pagamentoAVista = new PagamentosBean( valorEmprestimo, 2, id_emprestimo);
                        pagamentosController.createPagamento(pagamentoAVista, con);
                        EntradaSaida.showMessage("Emprestimos realizado. Pagamento à vista registrado com sucesso!");
                    }

                    case 2 -> {
                        double valorEntrada = EntradaSaida.getDecimal("Qual o valor da entrada?");
                        
                        while(valorEntrada > valorEmprestimo){
                            EntradaSaida.showMessage("Valor invalido");
                            valorEntrada = EntradaSaida.getNumber("Qual o valor da entrada?");
                        }
                        
                        PagamentosBean pagamentoParcelado = new PagamentosBean( valorEntrada, 1, id_emprestimo);
                        pagamentosController.createPagamento(pagamentoParcelado, con);
                        
                        EntradaSaida.showMessage("Emprestimos realizado. Pagamento da entrada registrado com sucesso!");
                    }
                    default -> EntradaSaida.showMessage("Emprestimos realizado com sucesso.");
                }
            } else {
                EntradaSaida.showMessage("Falha ao cadastrar o empréstimo.");
            }
        } else {
            EntradaSaida.showMessage("Empréstimo cancelado.");
        }
        menu(con);
        return 1;
    }
   
   public String listarEmprestimos(Connection con) throws SQLException {
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimos(con);

        StringBuilder sb = new StringBuilder();

        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos:\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public String listarEmprestimosNãoQuitados(Connection con) throws SQLException {
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimosSemPagamento(con);

        StringBuilder sb = new StringBuilder();

        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos pendentes de pagamento:\n\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public String listarEmprestimosPendentes(Connection con) throws SQLException {
        LinkedHashSet<EmprestimosBean> all = EmprestimosModel.listarEmprestimosPendentes(con);

        StringBuilder sb = new StringBuilder();

        if (all.isEmpty()) {
            return "";
        } else {
            sb.append("Lista de Empréstimos pendentes:\n\n");
            for (EmprestimosBean emprestimo : all) {
                sb.append(emprestimo.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public void atualizarEmprestimo(Connection con)throws SQLException {
       if(listarEmprestimos(con).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(con);
            return;
        }
       EntradaSaida.showMessage(listarEmprestimos(con));
       
       long idEmprestimo = EntradaSaida.getNumber("Digite o id do emprestimo: ");
       Date dataDevolucao = EntradaSaida.getDate("Digite a data de devolução (dd/MM/yyyy): ");
       java.sql.Date hoje = new java.sql.Date(System.currentTimeMillis());
        if (dataDevolucao.before(hoje)) {
            EntradaSaida.showMessage("A data de devolução deve ser maior que a data atual.");
            menu(con); 
            return;
        }
       boolean success = EmprestimosModel.alterarEmprestimo(idEmprestimo, dataDevolucao, con);
       
       if(success){
           EntradaSaida.showMessage("Emprestimo atualizado com sucesso");
           menu(con);
           return;
       }
       EntradaSaida.showMessage("Não foi possivel atualizar o emprestimo");
       menu(con);
   }
   
   public void excluirEmprestimo(Connection con) throws SQLException {
       if(listarEmprestimos(con).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarEmprestimos(con));

        long idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo que deseja excluir: ");

        if (EmprestimosModel.temPagamentosRelacionados(idEmprestimo, con)) {
            EntradaSaida.showMessage("Não é possível excluir o empréstimo, pois há pagamentos associados.");
            menu(con);
            return;
        }

        boolean sucesso = EmprestimosModel.excluirEmprestimo(idEmprestimo, con);

        if (sucesso) {
            EntradaSaida.showMessage("Empréstimo excluído com sucesso.");
            menu(con);
            return;
        }
        EntradaSaida.showMessage("Não foi possível excluir o empréstimo.");
        menu(con);
    }
   
   public static void atualizarEmprestimos(Connection con) throws SQLException {
       EmprestimosModel.atualizarStatusEmprestimos(con);
   }
   
   public void realizarDevolucao(Connection con) throws SQLException {
        if(listarEmprestimosPendentes(con).equals("")){
            EntradaSaida.showMessage("Não há emprestimos pendentes");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarEmprestimosPendentes(con));
        long idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo a ser devolvido: ");
        if (!EmprestimosModel.verificarEmprestimoPago(idEmprestimo, con)) {
            EntradaSaida.showMessage("O empréstimo não pode ser devolvido, pois ainda não foi totalmente pago.");
            menu(con);
            return;
        }
        boolean succes = EmprestimosModel.darBaixaEmprestimo(idEmprestimo, con);

        if(succes) {
            EntradaSaida.showMessage("Emprestimo devolvido com sucesso");
        } else {
            EntradaSaida.showMessage("Não foi possivel realizar a devolução");
        }
        menu(con);
    }
    
}
