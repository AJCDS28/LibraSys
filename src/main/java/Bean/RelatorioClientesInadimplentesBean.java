package Bean;

/**
 *
 * @author Andrei
 */
public class RelatorioClientesInadimplentesBean {
    private String nomeCliente;
    private long idEmprestimo;
    private double valorEmprestimo;
    private double valorPago;
    private double valorPendente;

    public RelatorioClientesInadimplentesBean(String nomeCliente, long idEmprestimo, double valorEmprestimo, double valorPago, double valorPendente) {
        this.nomeCliente = nomeCliente;
        this.idEmprestimo = idEmprestimo;
        this.valorEmprestimo = valorEmprestimo;
        this.valorPago = valorPago;
        this.valorPendente = valorPendente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public long getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(long idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public double getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(double valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public double getValorPendente() {
        return valorPendente;
    }

    public void setValorPendente(double valorPendente) {
        this.valorPendente = valorPendente;
    }

    @Override
    public String toString() {
        return "Cliente: " + nomeCliente + " | " +
               "Empréstimo ID: " + idEmprestimo + " | " +
               "Valor Total do Empréstimo: R$ " + valorEmprestimo + " | " +
               "Valor Pago: R$ " + valorPago + " | " +
               "Valor Pendente: R$ " + valorPendente;
    }
}
