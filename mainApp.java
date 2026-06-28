package view;

import controller.AuthController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Usuario;
import util.LoggerUtil;

public class MainApp extends Application {

    private Stage primaryStage;
    private AuthController authController = new AuthController();
    private Usuario usuarioLogado;
    private StackPane conteudoCentral;

    // Estilos globais reutilizáveis
    private final String ESTILO_INPUT = "-fx-background-color: #333333; -fx-text-fill: white; -fx-border-color: #444444; -fx-border-radius: 5; -fx-padding: 8;";
    private final String ESTILO_BOTAO_MENU = "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 15 10 15;";
    private final String ESTILO_BOTAO_ATIVO = "-fx-background-color: #333333; -fx-text-fill: #ff9900; -fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 15 10 15; -fx-border-color: transparent transparent transparent #ff9900; -fx-border-width: 0 0 0 4;";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Barber System Pro");
        mostrarTelaLogin();
    }

    private void mostrarTelaLogin() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1e1e1e;");

        Label lblTitulo = new Label("BARBER SYSTEM");
        lblTitulo.setStyle("-fx-text-fill: #ff9900; -fx-font-size: 24px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        Label lblSubtitulo = new Label("Controle de Acesso");
        lblSubtitulo.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("E-mail");
        txtEmail.setStyle(ESTILO_INPUT);
        txtEmail.setPrefWidth(280);
        
        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");
        txtSenha.setStyle(ESTILO_INPUT);

        Button btnLogin = new Button("ENTRAR");
        btnLogin.setStyle("-fx-background-color: #ff9900; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 5;");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Label lblErro = new Label();
        lblErro.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");

        root.getChildren().addAll(lblTitulo, lblSubtitulo, txtEmail, txtSenha, btnLogin, lblErro);
        
        btnLogin.setOnAction(e -> {
            Usuario u = authController.autenticar(txtEmail.getText(), txtSenha.getText());
            if (u != null) {
                this.usuarioLogado = u;
                LoggerUtil.registrarAcao(u.getEmail(), "Login efetuado com sucesso.");
                mostrarPainelPrincipal();
            } else {
                lblErro.setText("Usuário ou senha inválidos.");
                LoggerUtil.registrarAcao(txtEmail.getText().isEmpty() ? "Desconhecido" : txtEmail.getText(), "Tentativa falha de login.");
            }
        });

        Scene scene = new Scene(root, 360, 320);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void mostrarPainelPrincipal() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #242424;");
        
        VBox menuLateral = new VBox(5);
        menuLateral.setPadding(new Insets(20, 0, 20, 0));
        menuLateral.setStyle("-fx-background-color: #1a1a1a;");
        menuLateral.setPrefWidth(220);

        Label lblUser = new Label(usuarioLogado.getNome().toUpperCase());
        lblUser.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 5 15;");
        
        Label lblPerfil = new Label("Perfil: " + usuarioLogado.getPerfil());
        lblPerfil.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px; -fx-padding: 0 0 20 15;");

        Button btnAgenda = new Button("📅   Agenda");
        btnAgenda.setStyle(ESTILO_BOTAO_MENU);
        btnAgenda.setMaxWidth(Double.MAX_VALUE);

        Button btnRelatorios = new Button("📊   Histórico / Relatórios");
        btnRelatorios.setStyle(ESTILO_BOTAO_MENU);
        btnRelatorios.setMaxWidth(Double.MAX_VALUE);

        Button btnLogout = new Button("🚪   Sair");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 15 10 15;");
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        if (!usuarioLogado.getPerfil().equalsIgnoreCase("ADMIN")) {
            btnRelatorios.setDisable(true);
            btnRelatorios.setStyle(ESTILO_BOTAO_MENU + " -fx-opacity: 0.3; -fx-cursor: default;");
        }

        menuLateral.getChildren().addAll(lblUser, lblPerfil, btnAgenda, btnRelatorios, new Separator(), btnLogout);
        root.setLeft(menuLateral);

        conteudoCentral = new StackPane();
        conteudoCentral.setPadding(new Insets(25));
        root.setCenter(conteudoCentral);

        // Eventos do Menu de Navegação com alteração visual de botão ativo
        btnAgenda.setOnAction(e -> {
            btnAgenda.setStyle(ESTILO_BOTAO_ATIVO);
            btnRelatorios.setStyle(usuarioLogado.getPerfil().equalsIgnoreCase("ADMIN") ? ESTILO_BOTAO_MENU : ESTILO_BOTAO_MENU + " -fx-opacity: 0.3;");
            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(new AgendaView(usuarioLogado.getEmail()));
        });

        btnRelatorios.setOnAction(e -> {
            if (usuarioLogado.getPerfil().equalsIgnoreCase("ADMIN")) {
                btnRelatorios.setStyle(ESTILO_BOTAO_ATIVO);
                btnAgenda.setStyle(ESTILO_BOTAO_MENU);
                conteudoCentral.getChildren().clear();
                conteudoCentral.getChildren().add(new RelatorioView(usuarioLogado.getEmail()));
            }
        });

        btnLogout.setOnAction(e -> {
            LoggerUtil.registrarAcao(usuarioLogado.getEmail(), "Fez logout do sistema.");
            usuarioLogado = null;
            mostrarTelaLogin();
        });

        // Tela inicial padrão pós-login
        btnAgenda.fire();

        Scene scene = new Scene(root, 1050, 650);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
