package model;

public class Agendamento {
    private String clienteNome;
    private String clienteTelefone;
    private String clienteEmail;
    private String profissional;
    private String data; // Novo campo
    private String horario;
    private String procedimento;
    private String observacoes;

    public Agendamento(String clienteNome, String clienteTelefone, String clienteEmail, 
                       String profissional, String data, String horario, String procedimento, String observacoes) {
        this.clienteNome = clienteNome;
        this.clienteTelefone = clienteTelefone;
        this.clienteEmail = clienteEmail;
        this.profissional = profissional;
        this.data = data;
        this.horario = horario;
        this.procedimento = procedimento;
        this.observacoes = observacoes;
    }

    public String getClienteNome() { return clienteNome; }
    public String getClienteTelefone() { return clienteTelefone; }
    public String getClienteEmail() { return clienteEmail; }
    public String getProfissional() { return profissional; }
    public String getData() { return data; }
    public String getHorario() { return horario; }
    public String getProcedimento() { return procedimento; }
    public String getObservacoes() { return observacoes; }
}
