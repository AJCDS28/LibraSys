package Model;

import Bean.RelatorioEmprestimosAtrasadosBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

/**
 *
 * @author Andrei
 */

public class RelatorioEmprestimosAtrasadosModel {

    public static LinkedHashSet<RelatorioEmprestimosAtrasadosBean> listarEmprestimosAtrasadosPorCliente(long idCliente, Connection con) throws SQLException {
        String sql = "SELECT e.id_emprestimo, c.nome AS nome_cliente, l.titulo, e.valor_emprestimo, e.data_devolucao " +
                     "FROM emprestimos e " +
                     "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                     "JOIN concessao co ON e.id_emprestimo = co.id_emprestimo " +
                     "JOIN livros l ON co.id_livro = l.id_livro " +
                     "WHERE e.status = 3 AND e.id_cliente = ? " +
                     "ORDER BY e.id_emprestimo";

        LinkedHashSet<RelatorioEmprestimosAtrasadosBean> emprestimosAtrasados = new LinkedHashSet<>();
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setLong(1, idCliente);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    long idEmprestimo = rs.getLong("id_emprestimo");
                    String nomeCliente = rs.getString("nome_cliente");
                    String tituloLivro = rs.getString("titulo");
                    double valorEmprestimo = rs.getDouble("valor_emprestimo");
                    Date dataDevolucao = rs.getDate("data_devolucao");

                    RelatorioEmprestimosAtrasadosBean clienteBean = emprestimosAtrasados.stream()
                        .filter(cliente -> cliente.getNomeCliente().equals(nomeCliente))
                        .findFirst()
                        .orElse(null);

                    if (clienteBean == null) {
                        clienteBean = new RelatorioEmprestimosAtrasadosBean(nomeCliente);
                        emprestimosAtrasados.add(clienteBean);
                    }

                    clienteBean.addEmprestimo(idEmprestimo, valorEmprestimo, tituloLivro, dataDevolucao);
                }
            }
        }
        
        return emprestimosAtrasados;
    }
}
