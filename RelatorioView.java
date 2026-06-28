package view;

import controller.AgendaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Agendamento;
import java.util.List;

public class RelatorioView extends VBox {

    private AgendaController agendaController = new AgendaController();
    private String usuarioAtual;
    
    private ComboBox<String> cbProfissional = new ComboBox<>();
    private Button btnFiltrar = new Button("FILTRAR DADOS");
    private TableView<Agendamento> tabela = new TableView<>();

    public RelatorioView(String usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        this.setPadding(new Insets(5, 10, 5, 10));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: transparent;");

        Label lblTitulo = new Label("HISTÓRICO OPERACIONAL DE ATENDIMENTOS");
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-letter-spacing: 1px;");

        // Barra de Filtro superior estilizada
        HBox painelFiltro = new HBox(15);
        painelFiltro.setAlignment(Pos.CENTER_LEFT);
        painelFiltro.setPadding(new Insets(12));
        painelFiltro.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 6; -fx-border-color: #2d2d2d; -fx-border-radius: 6;");
        
        Label lblSelect = new Label("Profissional:"); 
        lblSelect.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold;");
        
        List<String> profissionais = agendaController.buscarProfissionais();
        cbProfissional.getItems().addAll(profissionais);
        if (!profissionais.isEmpty()) cbProfissional.setValue(profissionais.get(0));
        cbProfissional.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5;");
        
        btnFiltrar.setStyle("-fx-background-color: #ff9900; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 6 15 6 15; -fx-cursor: hand; -fx-background-radius: 4;");
        painelFiltro.getChildren().addAll(lblSelect, cbProfissional, btnFiltrar);

        // --- MAPEAMENTO DAS COLUNAS DA TABELA ---
        TableColumn<Agendamento, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setPrefWidth(110);

        TableColumn<Agendamento, String> colHorario = new TableColumn<>("Horário");
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colHorario.setPrefWidth(90);

        TableColumn<Agendamento, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNome"));
        colCliente.setPrefWidth(180);

        TableColumn<Agendamento, String> colProcedimento = new TableColumn<>("Procedimento / Serviço");
        colProcedimento.setCellValueFactory(new PropertyValueFactory<>("procedimento"));
        colProcedimento.setPrefWidth(180);

        TableColumn<Agendamento, String> colTelefone = new TableColumn<>("Contato");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("clienteTelefone"));
        colTelefone.setPrefWidth(140);

        tabela.getColumns().addAll(colData, colHorario, colCliente, colProcedimento, colTelefone);
        tabela.setPrefHeight(400);
        
        // Estilização completa da tabela para modo escuro
        tabela.setStyle("-fx-control-inner-background: #1e1e1e; -fx-background-color: #1e1e1e; -fx-table-cell-border-color: #2d2d2d; -fx-text-fill: white;");

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
        }
    }
}
