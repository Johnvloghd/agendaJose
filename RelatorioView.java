package view;

import controller.AgendaController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Agendamento;
import util.LoggerUtil;
import java.util.List;

public class RelatorioView extends VBox {

    private AgendaController agendaController = new AgendaController();
    private String usuarioAtual;
    
    private ComboBox<String> cbProfissional = new ComboBox<>();
    private Button btnFiltrar = new Button("Filtrar Relatório");
    private TableView<Agendamento> tabela = new TableView<>();

    public RelatorioView(String usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #242424;");

        Label lblTitulo = new Label("📊 Histórico de Atendimentos");
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        HBox painelFiltro = new HBox(10);
        painelFiltro.setAlignment(Pos.CENTER_LEFT);
        List<String> profissionais = agendaController.buscarProfissionais();
        cbProfissional.getItems().addAll(profissionais);
        if (!profissionais.isEmpty()) cbProfissional.setValue(profissionais.get(0));
        
        Label lblSelect = new Label("Filtrar Profissional:"); lblSelect.setStyle("-fx-text-fill: #ccc;");
        painelFiltro.getChildren().addAll(lblSelect, cbProfissional, btnFiltrar);

        // --- COLUNAS DA TABELA ---
        TableColumn<Agendamento, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data")); // Nova Coluna
        colData.setPrefWidth(100);

        TableColumn<Agendamento, String> colHorario = new TableColumn<>("Horário");
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colHorario.setPrefWidth(80);

        TableColumn<Agendamento, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNome"));
        colCliente.setPrefWidth(140);

        TableColumn<Agendamento, String> colProcedimento = new TableColumn<>("Procedimento");
        colProcedimento.setCellValueFactory(new PropertyValueFactory<>("procedimento"));
        colProcedimento.setPrefWidth(140);

        TableColumn<Agendamento, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("clienteTelefone"));
        colTelefone.setPrefWidth(110);

        tabela.getColumns().addAll(colData, colHorario, colCliente, colProcedimento, colTelefone);
        tabela.setPrefHeight(380);
        tabela.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: white;");

        this.getChildren().addAll(lblTitulo, painelFiltro, tabela);

        btnFiltrar.setOnAction(e -> atualizarTabela());
        atualizarTabela();
    }

    private void atualizarTabela() {
        String profSelecionado = cbProfissional.getValue();
        if (profSelecionado != null) {
            List<Agendamento> dados = agendaController.buscarRelatorioPorProfissional(profSelecionado);
            tabela.getItems().clear();
            tabela.getItems().addAll(dados);
            LoggerUtil.registrarAcao(usuarioAtual, "Consultou relatório do profissional: " + profSelecionado);
        }
    }
}
