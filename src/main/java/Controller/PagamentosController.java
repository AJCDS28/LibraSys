package Controller;

import Bean.PagamentosBean;
import Model.EmprestimosModel;
import Model.PagamentosModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import main.EntradaSaida;

public class PagamentosController {
    private final PagamentosModel pagamentosModel = new PagamentosModel();
    private final EmprestimosModel emprestimosModel = new EmprestimosModel();
    
    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuPagamentos();
        switch (opt) {
            case 1 -> processarPagamentoNaDevolucao(con);
            case 2 -> {
                    EntradaSaida.showMessage(listarPagamentos(con));
                    menu(con);
            }
            case 3 -> excluirPagamento(con);
        }
    }

    public boolean createPagamento(PagamentosBean pagamento, Connection con) throws SQLException {
        try {
            return PagamentosModel.createPagamento(pagamento, con);
        } catch (SQLException e) {
            System.err.println("Erro ao criar pagamento: " + e.getMessage());
            return false;
        }
    }
    
    public void processarPagamentoNaDevolucao(Connection con) throws SQLException {
        EmprestimosController emp = new EmprestimosController();
        
        if(emp.listarEmprestimosNãoQuitados(con).equals("")){
             EntradaSaida.showMessage("Não há empréstimos sem quitação.");
             menu(con);
             return;
        }
        EntradaSaida.showMessage(emp.listarEmprestimosNãoQuitados(con));

        long idEmprestimo = EntradaSaida.getNumber("Digite o ID do empréstimo:");

        double valorEmprestimo = emprestimosModel.getValorEmprestimo(idEmprestimo, con);
        double valorPagoTotal = pagamentosModel.getValorPagoTotal(idEmprestimo, con);
        double valorDevido = valorEmprestimo - valorPagoTotal;
        valorDevido = Math.round(valorDevido * 100.0) / 100.0;

        if (valorDevido <= 0) {
            EntradaSaida.showMessage("O empréstimo já está totalmente pago.");
            menu(con);
            return;
        }

        EntradaSaida.showMessage("O valor total devido é R$ " + String.format("%.2f", valorDevido));

        while (valorDevido > 0) {
            double valorPago = EntradaSaida.getDecimal("Digite o valor exato a ser pago:");

            if (valorPago < valorDevido) {
                EntradaSaida.showMessage("O valor pago é menor que o valor devido.");
            } else if (valorPago > valorDevido) {
                EntradaSaida.showMessage("O valor pago é maior que o valor devido.");
            } else {
                PagamentosBean pagamentoExistente = PagamentosModel.getPagamentoPorEmprestimo(idEmprestimo, con);

                if (pagamentoExistente != null) {

                    boolean sucessoUpdate = PagamentosModel.atualizarPagamento(idEmprestimo, valorPago, con);

                    if (sucessoUpdate) {
                        EntradaSaida.showMessage("Pagamento atualizado com sucesso. Empréstimo totalmente quitado.");
                        valorDevido = 0;
                    } else {
                        EntradaSaida.showMessage("Erro ao atualizar o pagamento.");
                        break;
                    }
                } else {
                    PagamentosBean novoPagamento = new PagamentosBean(valorPago, 2, idEmprestimo);
                    boolean sucessoInsert = PagamentosModel.createPagamento(novoPagamento, con);

                    if (sucessoInsert) {
                        EntradaSaida.showMessage("Pagamento registrado com sucesso. Empréstimo totalmente quitado.");
                        valorDevido = 0;
                    } else {
                        EntradaSaida.showMessage("Erro ao registrar o pagamento.");
                        break;
                    }
                }
            }
        }
        menu(con);
    }

    public void excluirPagamento(Connection con) throws SQLException {
        if(listarPagamentosExclusao(con).equals("")){
            EntradaSaida.showMessage("Não há emprestimos cadastrados");
            menu(con);
            return;
        }
        EntradaSaida.showMessage(listarPagamentosExclusao(con));

        long idPagamento = EntradaSaida.getNumber("Digite o ID do pagamento que deseja excluir: ");

        if (!PagamentosModel.existePagamento(con, idPagamento)) {
            EntradaSaida.showMessage("Pagamento não encontrado.");
            menu(con);
            return;
        }

        int confirmacao = EntradaSaida.getNumber("Tem certeza que deseja excluir o pagamento? (1 - Sim, 2 - Não)");

        if (confirmacao == 1) {
            if (PagamentosModel.excluirPagamento(con, idPagamento)) {
                EntradaSaida.showMessage("Pagamento excluído com sucesso.");
            } else {
                EntradaSaida.showMessage("Erro ao excluir pagamento.");
            }
        } else {
            EntradaSaida.showMessage("Exclusão cancelada.");
        }
        menu(con);
    }
    public String listarPagamentosExclusao(Connection con) throws SQLException {
        LinkedHashSet<PagamentosBean> pagamentos = PagamentosModel.listarPagamentos(con);
        StringBuilder sb = new StringBuilder();

        if (pagamentos.isEmpty()) {
            sb.append("");
        } else {
            sb.append("Lista de Pagamentos:\n");
            for (PagamentosBean pagamento : pagamentos) {
                sb.append(pagamento.listaPagamentos()).append("\n");
            }
        }

        return sb.toString();
    }
    
    public String listarPagamentos(Connection con)throws SQLException {
        EmprestimosController emp = new EmprestimosController();
        
        if(emp.listarEmprestimos(con).equals("")){
            return "Não há emprestimoss cadastrados";
        }
        EntradaSaida.showMessage(emp.listarEmprestimos(con));
        
        long idEmprestimo = EntradaSaida.getNumber("Digite o id do emprestimo: ");
        
        StringBuilder sb = new StringBuilder();
        try {
            LinkedHashSet<PagamentosBean> pagamentos = PagamentosModel.listarPagamentos(idEmprestimo, con);
            sb.append("Lista de Pagamentos:\n\n");
            for (PagamentosBean pagamento : pagamentos) {
                sb.append(pagamento.pagamentoEmprestimo()).append("\n");
            }
        } catch (SQLException e) {
            sb.append("Erro ao listar pagamentos: ").append(e.getMessage());
        }
        return sb.toString();
    }
    
}
