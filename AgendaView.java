package view;

import controller.AgendaController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Agendamento;
import util.LoggerUtil;
import java.time.LocalDate;
import java.util.List;

public class AgendaView extends HBox {

    private AgendaController agendaController = new AgendaController();
    private String usuarioAtual;

    private ComboBox<String> cbProfissional = new ComboBox<>();
    private DatePicker pickerData = new DatePicker(LocalDate.now());
    private ComboBox<String> cbHorarios = new ComboBox<>();
    private TextField txtCliente = new TextField();
    private TextField txtTelefone = new TextField();
    private TextField txtEmail = new TextField();
    private ComboBox<String> cbProcedimento = new ComboBox<>();
    private TextArea txtMensagem = new TextArea();
    
    private Button btnConfirmar = new Button("CONFIRMAR AGENDAMENTO");
    private Button btnAddProcedimento = new Button("➕ Novo Serviço");
    private Button btnAddBarbeiro = new Button("➕ Novo Profissional");

    public AgendaView(String usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        this.setPadding(new Insets(10));
        this.setSpacing(30);
        this.setStyle("-fx-background-color: transparent;");

        String estiloInput = "-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: #444444; -fx-border-radius: 4; -fx-padding: 8;";
        String estiloLabel = "-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px;";
        String estiloBtnGerenciamento = "-fx-background-color: #3a3a3a; -fx-text-fill: #ff9900; -fx-font-size: 11px; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10 5 10;";

        // --- COLUNA ESQUERDA (Dados Básicos de Tempo e Profissional) ---
        VBox painelEsquerda = new VBox(12);
        painelEsquerda.setPrefWidth(450);

        Label lblProf = new Label("PROFISSIONAL RESPONSÁVEL"); lblProf.setStyle(estiloLabel);
        carregarProfissionais();
        cbProfissional.setStyle(estiloInput);
        cbProfissional.setMaxWidth(Double.MAX_VALUE);

        Label lblData = new Label("DATA DO ATENDIMENTO"); lblData.setStyle(estiloLabel);
        pickerData.setStyle("-fx-control-inner-background: #333333;");
        pickerData.setMaxWidth(Double.MAX_VALUE);

        Label lblHorario = new Label("HORÁRIO DISPONÍVEL"); lblHorario.setStyle(estiloLabel);
        cbHorarios.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00");
        cbHorarios.setValue("08:00");
        cbHorarios.setStyle(estiloInput);
        cbHorarios.setMaxWidth(Double.MAX_VALUE);

        Label lblDados = new Label("INFORMAÇÕES DO CLIENTE"); lblDados.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        txtCliente.setPromptText("Nome Completo"); txtCliente.setStyle(estiloInput);
        txtTelefone.setPromptText("Telefone / WhatsApp"); txtTelefone.setStyle(estiloInput);
        txtEmail.setPromptText("E-mail de Contato"); txtEmail.setStyle(estiloInput);

        painelEsquerda.getChildren().addAll(lblProf, cbProfissional, lblData, lblHorario, cbHorarios, 
                                            lblDados, txtCliente, txtTelefone, txtEmail);

        // --- COLUNA DIREITA (Serviços, Gestão Rápida e Confirmação) ---
        VBox painelDireita = new VBox(12);
        painelDireita.setPrefWidth(450);
        painelDireita.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 20; -fx-border-color: #2d2d2d; -fx-border-radius: 8;");

        Label lblProc = new Label("PROCEDIMENTO / SERVIÇO"); lblProc.setStyle(estiloLabel);
        cbProcedimento.setStyle(estiloInput);
        cbProcedimento.setMaxWidth(Double.MAX_VALUE);
        carregarProcedimentos();

        HBox painelBotoesConfig = new HBox(10);
        btnAddProcedimento.setStyle(estiloBtnGerenciamento);
        btnAddBarbeiro.setStyle(estiloBtnGerenciamento);
        painelBotoesConfig.getChildren().addAll(btnAddProcedimento, btnAddBarbeiro);

        Label lblObs = new Label("OBSERVAÇÕES ADICIONAIS"); lblObs.setStyle(estiloLabel);
        txtMensagem.setPromptText("Restrições, especificações do corte, etc...");
        txtMensagem.setStyle(estiloInput);
        txtMensagem.setPrefHeight(150);

        btnConfirmar.setStyle("-fx-background-color: #ff9900; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 4;");
        btnConfirmar.setMaxWidth(Double.MAX_VALUE);

        painelDireita.getChildren().addAll(lblProc, cbProcedimento, painelBotoesConfig, new Separator(), lblObs, txtMensagem, btnConfirmar);

        this.getChildren().addAll(painelEsquerda, painelDireita);

        configurarAcoes();
    }

    private void carregarProcedimentos() {
        cbProcedimento.getItems().clear();
        List<String> procedimentos = agendaController.buscarProcedimentos();
        cbProcedimento.getItems().addAll(procedimentos);
        if (!procedimentos.isEmpty()) cbProcedimento.setValue(procedimentos.get(0));
    }

    private void carregarProfissionais() {
        cbProfissional.getItems().clear();
        List<String> profissionais = agendaController.buscarProfissionais();
        cbProfissional.getItems().addAll(profissionais);
        if (!profissionais.isEmpty()) cbProfissional.setValue(profissionais.get(0));
    }

    private void configurarAcoes() {
        btnConfirmar.setOnAction(e -> executarAgendamento());
        
        txtCliente.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) executarAgendamento();
        });

        btnAddProcedimento.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Configuração do Sistema");
            dialog.setHeaderText("Cadastrar Novo Procedimento");
            dialog.setContentText("Nome do Serviço:");
            dialog.showAndWait().ifPresent(nome -> {
                if (!nome.trim().isEmpty()) {
                    agendaController.cadastrarProcedimento(nome.trim());
                    carregarProcedimentos();
                    LoggerUtil.registrarAcao(usuarioAtual, "Cadastrou o procedimento: " + nome);
                }
            });
        });

        btnAddBarbeiro.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Configuração do Sistema");
            dialog.setHeaderText("Cadastrar Novo Profissional da Equipe");
            dialog.setContentText("Nome Completo:");
            dialog.showAndWait().ifPresent(nome -> {
                if (!nome.trim().isEmpty()) {
                    agendaController.cadastrarProfissional(nome.trim(), nome.toLowerCase().replace(" ", "") + "@barbearia.com");
                    carregarProfissionais();
                    LoggerUtil.registrarAcao(usuarioAtual, "Cadastrou o profissional: " + nome);
                }
            });
        });
    }

    private void executarAgendamento() {
        LocalDate dataSelecionada = pickerData.getValue();
        String horarioSelecionado = cbHorarios.getValue();
        String cliente = txtCliente.getText();
        String procedimentoSelecionado = cbProcedimento.getValue();

        if (cliente.isEmpty() || dataSelecionada == null || horarioSelecionado == null || procedimentoSelecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Campos obrigatórios ausentes. Verifique os dados do formulário.");
            alert.showAndWait();
            return;
        }

        Agendamento novo = new Agendamento(
                cliente, txtTelefone.getText(), txtEmail.getText(), 
                cbProfissional.getValue(), dataSelecionada.toString(), horarioSelecionado, procedimentoSelecionado, txtMensagem.getText()
        );

        if (agendaController.salvarAgendamento(novo)) {
            LoggerUtil.registrarAcao(usuarioAtual, "Agendamento confirmado para " + cliente + " | Data: " + dataSelecionada + " às " + horarioSelecionado);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Atendimento agendado com sucesso!");
            alert.showAndWait();
            limparCampos();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Falha de persistência no banco de dados.");
            alert.showAndWait();
        }
    }

    private void limparCampos() {
        txtCliente.clear(); txtTelefone.clear(); txtEmail.clear(); txtMensagem.clear();
    }
}
