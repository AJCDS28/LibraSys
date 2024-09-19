package Model;
import Bean.RelatorioLivrosSessaoBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class RelatorioLivrosSessaoModel {

    public static LinkedHashSet<RelatorioLivrosSessaoBean> listarLivrosPorSessao(Connection con) throws SQLException {
        String sql = "SELECT s.id_sessao, s.codigo, s.nome, l.titulo " +
                     "FROM sessoes s " +
                     "JOIN livros l ON s.id_sessao = l.id_sessao " +
                     "ORDER BY s.id_sessao";

        LinkedHashSet<RelatorioLivrosSessaoBean> sessoesComLivros = new LinkedHashSet<>();
        List<String> titulosLivros = new ArrayList<>();
        long idSessaoAtual = -1;
        int codigoSessaoAtual = -1;
        String nomeSessaoAtual = null;

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                long idSessao = rs.getLong("id_sessao");
                int codigoSessao = rs.getInt("codigo");
                String nomeSessao = rs.getString("nome");
                String tituloLivro = rs.getString("titulo");

                if (idSessao != idSessaoAtual) {
                    if (idSessaoAtual != -1) {
                        sessoesComLivros.add(new RelatorioLivrosSessaoBean(idSessaoAtual, codigoSessaoAtual, nomeSessaoAtual, new ArrayList<>(titulosLivros)));
                    }
                    titulosLivros.clear();
                    idSessaoAtual = idSessao;
                    nomeSessaoAtual = nomeSessao;
                    codigoSessaoAtual = codigoSessao;
                }

                titulosLivros.add(tituloLivro);
            }

            if (idSessaoAtual != -1) {
                sessoesComLivros.add(new RelatorioLivrosSessaoBean(idSessaoAtual, codigoSessaoAtual, nomeSessaoAtual, new ArrayList<>(titulosLivros)));
            }
        }

        return sessoesComLivros;
    }

}
