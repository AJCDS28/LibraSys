package Bean;
import java.util.List;
/**
 *
 * @author Andrei
 */

public class RelatorioLivrosSessaoBean {
    private long idSessao;
    private int codigo;
    private String nomeSessao;
    private List<String> titulosLivros;

    public RelatorioLivrosSessaoBean(long idSessao, int codigo, String nomeSessao, List<String> titulosLivros) {
        this.idSessao = idSessao;
        this.codigo = codigo;
        this.nomeSessao = nomeSessao;
        this.titulosLivros = titulosLivros;
    }

    public long getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(long idSessao) {
        this.idSessao = idSessao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNomeSessao() {
        return nomeSessao;
    }

    public void setNomeSessao(String nomeSessao) {
        this.nomeSessao = nomeSessao;
    }

    public List<String> getTitulosLivros() {
        return titulosLivros;
    }

    public void setTitulosLivros(List<String> titulosLivros) {
        this.titulosLivros = titulosLivros;
    }

    @Override
    public String toString() {
        return "Sessão: " + codigo + " - " + nomeSessao + "\nLivros: " + String.join(", ", titulosLivros) + "\n";
    }
}
