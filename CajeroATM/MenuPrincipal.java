package CajeroATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {

    private String tokenUsuario;
    private Label nombreUsuarioLabel;
    private Label tipoCuentaLabel;
    private Label saldoLabel;
    private Stage primaryStage;

    public MenuPrincipal(String token) {
        this.tokenUsuario = token;
    }

    public MenuPrincipal() {
        this.tokenUsuario = "";
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - Menú Principal");
        stage.setResizable(false);

        VBox mainPanel = createMainPanel();
        Scene scene = new Scene(mainPanel, 750, 500);
        stage.setScene(scene);
        stage.show();

        cargarInformacionUsuario();
    }

    private VBox createMainPanel() {
        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.TOP_CENTER);
        mainPanel.setSpacing(20);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #c41e3a, #8b1538);");

        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setSpacing(40);
        topSection.setPadding(new Insets(0, 0, 20, 0));

        HBox headerSection = createHeaderSection();
        VBox infoSection = createInfoSection();

        topSection.getChildren().addAll(headerSection, infoSection);

        Label tituloTransacciones = new Label("Transaccione Bancaria que desea realizar");
        tituloTransacciones.setTextFill(Color.WHITE);
        tituloTransacciones.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tituloTransacciones.setAlignment(Pos.CENTER);

        GridPane botonesGrid = createBotonesGrid();
        HBox salirSection = createSalirSection();

        mainPanel.getChildren().addAll(topSection, tituloTransacciones, botonesGrid, salirSection);
        return mainPanel;
    }

    private HBox createHeaderSection() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(20);
        headerBox.setPadding(new Insets(10));

        // Cargar imagen con ImageLoader
        Image logoImage = ImageLoader.cargarImagen("Rino.png");
        ImageView logoImageView = new ImageView();
        if (logoImage != null) {
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(80);
            logoImageView.setFitHeight(80);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.5));
            shadow.setOffsetX(2);
            shadow.setOffsetY(2);
            shadow.setRadius(5);
            logoImageView.setEffect(shadow);
        } else {
            logoImageView = new ImageView();
            logoImageView.setFitWidth(80);
            logoImageView.setFitHeight(80);
        }

        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(2);

        Label chronosLabel = new Label("Chronos");
        chronosLabel.setTextFill(Color.WHITE);
        chronosLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label axiosLabel = new Label("Axios");
        axiosLabel.setTextFill(Color.WHITE);
        axiosLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label subtitleLabel = new Label("Para el futuro de la gente");
        subtitleLabel.setTextFill(Color.WHITE);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        subtitleLabel.setOpacity(0.9);

        titleBox.getChildren().addAll(chronosLabel, axiosLabel, subtitleLabel);
        headerBox.getChildren().addAll(logoImageView, titleBox);

        return headerBox;
    }

    private VBox createInfoSection() {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setSpacing(5);
        infoBox.setPadding(new Insets(15, 30, 15, 30));
        infoBox.setMinWidth(250);
        infoBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9); " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        nombreUsuarioLabel = new Label("Bienvenido ");
        nombreUsuarioLabel.setTextFill(Color.BLACK);
        nombreUsuarioLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        tipoCuentaLabel = new Label("cuenta: ");
        tipoCuentaLabel.setTextFill(Color.BLACK);
        tipoCuentaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        saldoLabel = new Label("$0.00 mxn");
        saldoLabel.setTextFill(Color.BLACK);
        saldoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        saldoLabel.setAlignment(Pos.CENTER_LEFT);

        infoBox.getChildren().addAll(nombreUsuarioLabel, tipoCuentaLabel, saldoLabel);
        return infoBox;
    }

    private GridPane createBotonesGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(40);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        Button btnRetirar = createTransactionButton("Retirar saldo");
        Button btnDepositos = createTransactionButton("Depósitos en efectivo");
        Button btnTransferencia = createTransactionButton("Transferencia");
        Button btnMovimientos = createTransactionButton("movimientos");
        Button btnRecarga = createTransactionButton("Recarga");
        Button btnPagoServicios = createTransactionButton("Pago de servicios");

        btnRetirar.setOnAction(e -> abrirRetirarSaldo());
        btnDepositos.setOnAction(e -> abrirDepositosEfectivo());
        btnTransferencia.setOnAction(e -> abrirTransferencia());
        btnMovimientos.setOnAction(e -> abrirMovimientos());
        btnRecarga.setOnAction(e -> abrirRecarga());
        btnPagoServicios.setOnAction(e -> abrirPagoServicios());

        grid.add(btnRetirar, 0, 0);
        grid.add(btnDepositos, 1, 0);
        grid.add(btnTransferencia, 0, 1);
        grid.add(btnMovimientos, 1, 1);
        grid.add(btnRecarga, 0, 2);
        grid.add(btnPagoServicios, 1, 2);

        return grid;
    }

    private Button createTransactionButton(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 3);"));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);"));

        return button;
    }

    private HBox createSalirSection() {
        HBox salirBox = new HBox();
        salirBox.setAlignment(Pos.BOTTOM_RIGHT);
        salirBox.setPadding(new Insets(10));

        Button salirButton = new Button("Salir");
        salirButton.setPrefWidth(80);
        salirButton.setPrefHeight(35);
        salirButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        salirButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8); " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        salirButton.setOnAction(e -> salirDelSistema());

        salirBox.getChildren().add(salirButton);
        return salirBox;
    }

    private void cargarInformacionUsuario() {
        if (tokenUsuario == null || tokenUsuario.isEmpty()) {
            return;
        }

        Thread cargarThread = new Thread(() -> {
            try {
                String[] infoUsuario = obtenerInformacionUsuario(tokenUsuario);
                String nombre = infoUsuario[0];
                String tipoCuenta = infoUsuario[1];
                double saldo = Double.parseDouble(infoUsuario[2]);

                javafx.application.Platform.runLater(() -> {
                    nombreUsuarioLabel.setText("Bienvenido " + nombre);
                    tipoCuentaLabel.setText("cuenta: " + tipoCuenta);
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    saldoLabel.setText("$" + df.format(saldo) + " mxn");
                });

            } catch (SQLException e) {
                mostrarError("Error de base de datos", "No se pudo cargar la información: " + e.getMessage());
            } catch (Exception e) {
                mostrarError("Error inesperado", "Ocurrió un error al cargar los datos: " + e.getMessage());
            }
        });

        cargarThread.setDaemon(true);
        cargarThread.start();
    }

    private String[] obtenerInformacionUsuario(String token) throws SQLException {
        String sql = "SELECT c.nombre, cu.tipo_cuenta, cu.saldo " +
                "FROM clientes c " +
                "JOIN cuentas cu ON c.id = cu.cliente_id " +
                "WHERE cu.token = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new String[] {
                        rs.getString("nombre"),
                        rs.getString("tipo_cuenta"),
                        String.valueOf(rs.getDouble("saldo"))
                };
            }
            // Si no encuentra el token, devuelve valores por defecto
            return new String[] { "Usuario", "Axios debito", "0.00" };
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    // Métodos para abrir las diferentes funcionalidades
    private void abrirRetirarSaldo() {
        primaryStage.close();
        RetirarSaldo.abrirRetirarSaldo(tokenUsuario);
    }

    private void abrirDepositosEfectivo() {
        primaryStage.close();
        Deposito.abrirDeposito(tokenUsuario);
    }

    private void abrirTransferencia() {
        primaryStage.close();
        Transferencia.abrirTransferencia(tokenUsuario);
    }

    private void abrirMovimientos() {
        primaryStage.close();
        MovimientosMes.abrirMovimientos(tokenUsuario);
    }

    private void abrirRecarga() {
        primaryStage.close();
        Recarga.abrirRecarga(tokenUsuario);
    }

    private void abrirPagoServicios() {
        primaryStage.close();
        PagoServicios.abrirPagoServicios(tokenUsuario);
    }

    private void salirDelSistema() {
        primaryStage.close();
        abrirLogin();
    }

    public static void abrirLogin() {
        Token login = new Token();
        Stage stage = new Stage();
        try {
            login.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTokenUsuario(String token) {
        this.tokenUsuario = token;
    }

    public static void abrirMenuPrincipal(String token) {
        MenuPrincipal menu = new MenuPrincipal(token);
        Stage stage = new Stage();
        try {
            menu.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}