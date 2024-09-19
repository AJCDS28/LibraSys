package main;

import Controller.LivrosController;
import Controller.SessoesController;
import Controller.ClientesController;
import Controller.EmprestimosController;
import Controller.PagamentosController;
import java.sql.Connection;
import java.sql.SQLException;
import conexao.Conexao;
/**
 *
 * @author Andrei
 */
public class Principal {

    public static void main(String[] args) throws SQLException {
        Conexao c = new Conexao();
        try (Connection con = c.getConnection()) {
            EmprestimosController.atualizarEmprestimos(con);
            int op;
            do {
                op = EntradaSaida.getMenu();
                switch (op) {
                    case 1 -> new SessoesController().menu(con);
                    case 2 -> new LivrosController().menu(con);
                    case 3 -> new ClientesController().menu(con);
                    case 4 -> new EmprestimosController().menu(con);
                    case 5 -> new PagamentosController().menu(con);
                    case 6 -> new Relatorios().menu(con);
                }
            } while (op >= 1 && op <= 7);
        } catch (SQLException ex) {
                System.out.println("Erro ao executar a operação: " + ex.getMessage());
        }
    }
}
