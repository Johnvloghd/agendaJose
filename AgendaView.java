package view;

import controller.AgendaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Agendamento;
import util.LoggerUtil;
import java.time.LocalDate;
import java.util.List;

public class AgendaView extends HBox {

    private AgendaController agendaController = new AgendaController();
    private String usuarioAtual;

    // Componentes Visuais Modernizados
    private ComboBox<String> cbProfissional = new ComboBox<>();
    private DatePicker pickerData = new DatePicker(LocalDate.now()); // CALENDÁRIO NATIVO
    private ComboBox<String> cbHorarios = new ComboBox<>();
    private TextField txtCliente = new TextField();
    private TextField txtTelefone = new TextField();
    private TextField txtEmail = new TextField();
    private ComboBox<String> cbProcedimento = new ComboBox<>();
    private TextArea txtMensagem = new TextArea();
    
    // Botões de Ação e Gerenciamento
    private Button btnConfirmar = new Button("Confirmar Agendamento (ENTER)");
    private Button btnAddProcedimento = new Button("➕ Procedimento");
    private Button btnAddBarbeiro = new Button("➕ Barbeiro");

    public AgendaView(String usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        this.setPadding(new Insets(20));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: #242424;"); // Fundo Grafite Escuro

        // Estilização Comum (CSS Embutido para Interface Premium)
        String estiloInput = "-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: #444444; -fx-border-radius: 4; -fx-padding: 6;";
        String estiloLabel = "-fx-text-fill: #b3b3b3; -fx-font-weight: bold;";

        // --- PAINEL DA ESQUERDA (Agendamento e Contatos) ---
        VBox painelEsquerda = new VBox(10);
        painelEsquerda.setPrefWidth(380);

        Label lblProf = new Label("Profissional:"); lblProf.setStyle(estiloLabel);
        carregarProfissionais();
        cbProfissional.setStyle(estiloInput);

        Label lblData = new Label("Selecionar Data (Calendário):"); lblData.setStyle(estiloLabel);
        pickerData.setStyle("-fx-control-inner-background: #333333;");

        Label lblHorario = new Label("Horário:"); lblHorario.setStyle(estiloLabel);
        cbHorarios.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "13:00", "14:00", "15:00");
        cbHorarios.setValue("08:00");
        cbHorarios.setStyle(estiloInput);

        Label lblDados = new Label("Dados do Cliente:"); lblDados.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        txtCliente.setPromptText("Nome do Cliente"); txtCliente.setStyle(estiloInput);
        txtTelefone.setPromptText("Telefone Celular"); txtTelefone.setStyle(estiloInput);
        txtEmail.setPromptText("E-mail corporativo"); txtEmail.setStyle(estiloInput);

        painelEsquerda.getChildren().addAll(lblProf, cbProfissional, lblData, lblHorario, cbHorarios, 
                                            new Separator(), lblDados, txtCliente, txtTelefone, txtEmail);

        // --- PAINEL DA DIREITA (Serviços e Gestão Rápida) ---
        VBox painelDireita = new VBox(12);
        painelDireita.setPrefWidth(380);
        painelDireita.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 15; -fx-border-color: #3a3a3a; -fx-border-radius: 8;");

        Label lblProc = new Label("Procedimento:"); lblProc.setStyle(estiloLabel);
        cbProcedimento.setStyle(estiloInput);
        carregarProcedimentos();

        // Linha com os botões rápidos de cadastrar itens novos
        HBox painelBotoesConfig = new HBox(10);
        btnAddProcedimento.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-cursor: hand;");
        btnAddBarbeiro.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-cursor: hand;");
        painelBotoesConfig.getChildren().addAll(btnAddProcedimento, btnAddBarbeiro);

        Label lblObs = new Label("Observações do Serviço:"); lblObs.setStyle(estiloLabel);
        txtMensagem.setPromptText("Ex: Cliente quer degradê navalhado, restrição a produtos alérgicos...");
        txtMensagem.setStyle(estiloInput);
        txtMensagem.setPrefHeight(100);

        btnConfirmar.setStyle("-fx-background-color: #ff9900; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 5;");
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

        // Janela Pop-up rápida para Adicionar Novo Procedimento dinamicamente
        btnAddProcedimento.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Novo Procedimento");
            dialog.setHeaderText("Cadastrar serviço na Barbearia");
            dialog.setContentText("Nome do Procedimento:");
            dialog.showAndWait().ifPresent(nome -> {
                if (!nome.trim().isEmpty()) {
                    agendaController.cadastrarProcedimento(nome.trim());
                    carregarProcedimentos(); // Recarrega o menu na hora
                    LoggerUtil.registrarAcao(usuarioAtual, "Cadastrou novo procedimento: " + nome);
                }
            });
        });

        // Janela Pop-up rápida para Adicionar Novo Barbeiro dinamicamente
        btnAddBarbeiro.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Novo Barbeiro");
            dialog.setHeaderText("Cadastrar profissional na equipe");
            dialog.setContentText("Nome do Barbeiro:");
            dialog.showAndWait().ifPresent(nome -> {
                if (!nome.trim().isEmpty()) {
                    agendaController.cadastrarProfissional(nome.trim(), nome.toLowerCase().replace(" ", "") + "@barbearia.com");
                    carregarProfissionais(); // Recarrega o menu na hora
                    LoggerUtil.registrarAcao(usuarioAtual, "Cadastrou novo profissional: " + nome);
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
            Alert alert = new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!");
            alert.showAndWait();
            return;
        }

        Agendamento novo = new Agendamento(
                cliente, txtTelefone.getText(), txtEmail.getText(), 
                cbProfissional.getValue(), dataSelecionada.toString(), horarioSelecionado, procedimentoSelecionado, txtMensagem.getText()
        );

        boolean sucesso = agendaController.salvarAgendamento(novo);

        if (sucesso) {
            LoggerUtil.registrarAcao(usuarioAtual, "Agendamento marcado para " + cliente + " no dia " + dataSelecionada + " às " + horarioSelecionado);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Agendamento salvo com sucesso no sistema!");
            alert.showAndWait();
            limparCampos();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar no PostgreSQL!");
            alert.showAndWait();
        }
    }

    private void limparCampos() {
        txtCliente.clear(); txtTelefone.clear(); txtEmail.clear(); txtMensagem.clear();
    }
}
