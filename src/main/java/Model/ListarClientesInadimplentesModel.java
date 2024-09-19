package Model;

import Bean.RelatorioClientesInadimplentesBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

/**
 *
 * @author Andrei
 */
public class ListarClientesInadimplentesModel {
    public static LinkedHashSet<RelatorioClientesInadimplentesBean> listarClientesInadimplentes(Connection con) throws SQLException {
        String sql = "SELECT c.nome AS nome_cliente, e.id_emprestimo, e.valor_emprestimo, " +
                    "COALESCE(SUM(p.valor_pago), 0) AS valor_pago, " +
                    "(e.valor_emprestimo - COALESCE(SUM(p.valor_pago), 0)) AS valor_pendente " +
                    "FROM emprestimos e " +
                    "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                    "LEFT JOIN pagamentos p ON e.id_emprestimo = p.id_emprestimo " +
                    "GROUP BY c.nome, e.id_emprestimo, e.valor_emprestimo " +
                    "HAVING (e.valor_emprestimo - COALESCE(SUM(p.valor_pago), 0)) > 0 " +
                    "ORDER BY c.nome";


        LinkedHashSet<RelatorioClientesInadimplentesBean> listaInadimplentes = new LinkedHashSet<>();

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                RelatorioClientesInadimplentesBean inadimplente = new RelatorioClientesInadimplentesBean(
                    rs.getString("nome_cliente"),
                    rs.getLong("id_emprestimo"),
                    rs.getDouble("valor_emprestimo"),
                    rs.getDouble("valor_pago"),
                    rs.getDouble("valor_pendente")
                );
                listaInadimplentes.add(inadimplente);
            }
        }

        return listaInadimplentes;
    }
    
}
