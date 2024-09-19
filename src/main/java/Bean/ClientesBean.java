package Bean;

public class ClientesBean {
    private long idCliente;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;

    public ClientesBean(long idCliente, String nome, String email, String telefone, String cpf) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public ClientesBean(String nome, String email, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "ID: " + idCliente + " | " 
                + "Nome: " + nome + " | " 
                + "E-mail: " + email  + " | " 
                + "Telefone: " + telefone + " | " 
                + "CPF: " + cpf ;
    }
}
