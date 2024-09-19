package main;

import Bean.RelatorioClientesInadimplentesBean;
import Bean.RelatorioEmprestimosAtrasadosBean;
import Bean.RelatorioLivrosSessaoBean;
import Controller.ClientesController;
import Model.ListarClientesInadimplentesModel;
import Model.RelatorioEmprestimosAtrasadosModel;
import Model.RelatorioLivrosSessaoModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;

/**
 *
 * @author Andrei
 */
public class Relatorios {
    
    public void menu(Connection con) throws SQLException {
        int opt = EntradaSaida.getMenuRelatorio();
        switch (opt) {
            case 1 -> {
                EntradaSaida.showMessage(relatorioLivrosSessao(con));
                menu(con);
            }
            case 2 -> {
                EntradaSaida.showMessage(listarEmprestimosAtrasados(con));
                menu(con);
            }
            case 3 -> {
                EntradaSaida.showMessage(listarClientesInadimplentes(con));
                menu(con);
            }
        }
    }
    
    public static String relatorioLivrosSessao(Connection con) throws SQLException {
        LinkedHashSet<RelatorioLivrosSessaoBean> sessoesComLivros = RelatorioLivrosSessaoModel.listarLivrosPorSessao(con);

            StringBuilder sb = new StringBuilder();

            if (sessoesComLivros.isEmpty()) {
                sb.append("Não há sessões ou livros cadastrados.\n");
            } else {
                sb.append("Relatório de Livros por Sessão:\n\n");
                for (RelatorioLivrosSessaoBean sessao : sessoesComLivros) {
                    sb.append(sessao.toString()).append("\n");
                }
            }

            return sb.toString();
    }
    
   public String listarEmprestimosAtrasados(Connection con) throws SQLException {
       StringBuilder sb = new StringBuilder();
       if(ClientesController.listarClientes(con).equals("")){
            sb.append("Não há clientes cadastrados\n");
            return sb.toString();
        }
        EntradaSaida.showMessage(ClientesController.listarClientes(con));
        long idCliente = EntradaSaida.getNumber("Digite o id do cliente: ");
        LinkedHashSet<RelatorioEmprestimosAtrasadosBean> emprestimosAtrasados = RelatorioEmprestimosAtrasadosModel.listarEmprestimosAtrasadosPorCliente(idCliente, con);

        if (emprestimosAtrasados.isEmpty()) {
            sb.append("Não há empréstimos atrasados para o cliente informado.\n");
        } else {
            sb.append("Relatório de Empréstimos Atrasados:\n\n");
            for (RelatorioEmprestimosAtrasadosBean cliente : emprestimosAtrasados) {
                sb.append(cliente.toString()).append("\n");
            }
        }

        return sb.toString();
    }
   
   public static String listarClientesInadimplentes(Connection con) throws SQLException {
        LinkedHashSet<RelatorioClientesInadimplentesBean> clientesInadimplentes = ListarClientesInadimplentesModel.listarClientesInadimplentes(con);

        StringBuilder sb = new StringBuilder();

        if (clientesInadimplentes.isEmpty()) {
            sb.append("Não há clientes inadimplentes.\n");
        } else {
            sb.append("Relatório de Clientes Inadimplentes:\n\n");
            for (RelatorioClientesInadimplentesBean cliente : clientesInadimplentes) {
                sb.append(cliente.toString()).append("\n");
            }
        }

        return sb.toString();
    }
    
}
