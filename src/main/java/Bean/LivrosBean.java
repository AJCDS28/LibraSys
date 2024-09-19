package Bean;

import java.sql.Date;

/**
 * 
 * @author Pichau
 */
public class LivrosBean {

    private Long idLivro;
    private String titulo;
    private Date anoPublicacao;
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private double valor;
    private String nomeAutor;
    private Long idSessao;
    private String nomeSessao;
    private Integer codigoSessao;

    public LivrosBean(Long idLivro, String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor, Long idSessao) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.idSessao = idSessao;
    }
    
    public LivrosBean( String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor, Long idSessao) {
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.idSessao = idSessao;
    }

    public LivrosBean(String titulo, Date anoPublicacao, int quantidadeDisponivel, int quantidadeTotal, Double valor, String nomeAutor) {
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
    }
    
    public LivrosBean(Long idLivro, String titulo, Date dataPublicacao, int quantidadeDisponivel, int quantidadeTotal, double valor, String nomeAutor, Integer codigoSessao,  String nomeSessao) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.anoPublicacao = dataPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
        this.valor = valor;
        this.nomeAutor = nomeAutor;
        this.codigoSessao = codigoSessao;
        this.nomeSessao = nomeSessao ;
    }
    
    public LivrosBean(Long idLivro, Long idSessao) {
        this.idLivro = idLivro;
        this.idSessao = idSessao;
    }
    
    public LivrosBean(Long idLivro, String titulo, int quantidadeDisponivel, double valor) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Id Livro: " + idLivro  
         + " | Título: " + titulo 
         + " | Ano de Publicação: " + anoPublicacao
         + " | Quantidade Total: " + quantidadeTotal 
         + " | Quantidade Disponivel: " + quantidadeDisponivel 
         + " | Valor: R$" + valor 
         + " | Autor: " + nomeAutor
         + " | Sessão: " + getCodigoENomeSessao();
    }
    
    public Long getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Long idLivro) {
        this.idLivro = idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Date anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
    
    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public Long getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(Long idSessao) {
        this.idSessao = idSessao;
    }
    
     public String getCodigoENomeSessao() {
         if(codigoSessao == -1){
             return "Não associada";
         }
        return codigoSessao + " - " + nomeSessao ;
    }

   
}
