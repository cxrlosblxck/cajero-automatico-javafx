package CajeroATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Deposito extends Application {

    private String tokenUsuario;
    private Stage primaryStage;
    private Label tipoCuentaLabel;
    private Label saldoLabel;
    private TextField cantidadField;
    private Button siguienteButton;
    private Button salirButton;

    // Datos de la cuenta (se cargan desde BD)
    private double saldoActual = 0.0;
    private int cuentaId = 0;
    private boolean datosCargados = false;

    public Deposito(String token) {
        this.tokenUsuario = token;
    }

    public Deposito() {
        this.tokenUsuario = "";
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - Depósito");
        stage.setResizable(false);

        VBox mainPanel = createMainPanel();
        Scene scene = new Scene(mainPanel, 700, 550);
        stage.setScene(scene);
        stage.show();

        cargarInformacionCuenta();
    }

    private VBox createMainPanel() {
        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.TOP_CENTER);
        mainPanel.setSpacing(30);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background: linear-gradient(to bottom, #c41e3a, #8b1538);");

        HBox headerSection = createHeaderSection();

        Label tituloLabel = new Label("Ingresa cantidad a depositar");
        tituloLabel.setTextFill(Color.WHITE);
        tituloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        tituloLabel.setAlignment(Pos.CENTER);

        VBox cantidadSection = createCantidadSection();
        HBox buttonSection = createButtonSection();
        VBox spacer = new VBox();
        spacer.setPrefHeight(50);
        HBox salirSection = createSalirSection();

        mainPanel.getChildren().addAll(headerSection, tituloLabel, cantidadSection,
                buttonSection, spacer, salirSection);
        return mainPanel;
    }

    private HBox createHeaderSection() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(40);
        headerBox.setPadding(new Insets(10));

        HBox logoSection = createLogoSection();
        VBox infoSection = createInfoSection();

        headerBox.getChildren().addAll(logoSection, infoSection);
        return headerBox;
    }

    private HBox createLogoSection() {
        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setSpacing(15);

        // Usar ImageLoader en lugar de ruta absoluta
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
        logoBox.getChildren().addAll(logoImageView, titleBox);

        return logoBox;
    }

    private VBox createInfoSection() {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(5);
        infoBox.setPadding(new Insets(15, 30, 15, 30));
        infoBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95); " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        tipoCuentaLabel = new Label("Cargando cuenta...");
        tipoCuentaLabel.setTextFill(Color.BLACK);
        tipoCuentaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        saldoLabel = new Label("Cargando saldo...");
        saldoLabel.setTextFill(Color.BLACK);
        saldoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        infoBox.getChildren().addAll(tipoCuentaLabel, saldoLabel);
        return infoBox;
    }

    private VBox createCantidadSection() {
        VBox cantidadSection = new VBox();
        cantidadSection.setAlignment(Pos.CENTER);
        cantidadSection.setSpacing(15);

        cantidadField = new TextField();
        cantidadField.setPromptText("0.00");
        cantidadField.setPrefWidth(300);
        cantidadField.setPrefHeight(60);
        cantidadField.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        cantidadField.setAlignment(Pos.CENTER);
        cantidadField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 15; " +
                        "-fx-text-fill: #333;");

        // Validar solo números decimales
        cantidadField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                cantidadField.setText(oldValue);
            }
            validarCantidadTiempoReal();
        });

        cantidadSection.getChildren().add(cantidadField);
        return cantidadSection;
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);

        siguienteButton = new Button("Siguiente");
        siguienteButton.setPrefWidth(140);
        siguienteButton.setPrefHeight(45);
        siguienteButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        siguienteButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");

        siguienteButton.setOnMouseEntered(e -> siguienteButton.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 3);"));

        siguienteButton.setOnMouseExited(e -> siguienteButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);"));

        siguienteButton.setOnAction(e -> procesarDeposito());
        siguienteButton.setDisable(true);

        buttonBox.getChildren().add(siguienteButton);
        return buttonBox;
    }

    private HBox createSalirSection() {
        HBox salirBox = new HBox();
        salirBox.setAlignment(Pos.BOTTOM_LEFT);
        salirBox.setPadding(new Insets(10));

        salirButton = new Button("Salir");
        salirButton.setPrefWidth(80);
        salirButton.setPrefHeight(35);
        salirButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        salirButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8); " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        salirButton.setOnAction(e -> regresarMenuPrincipal());

        salirBox.getChildren().add(salirButton);
        return salirBox;
    }

    private void cargarInformacionCuenta() {
        if (tokenUsuario == null || tokenUsuario.isEmpty()) {
            mostrarAlerta("Error", "No se ha proporcionado un token válido", Alert.AlertType.ERROR);
            return;
        }

        Thread cargarThread = new Thread(() -> {
            try {
                String[] infoCuenta = obtenerInformacionCuenta(tokenUsuario);
                String tipoCuenta = infoCuenta[0];
                double saldo = Double.parseDouble(infoCuenta[1]);
                int cuentaIdTemp = Integer.parseInt(infoCuenta[2]);

                Platform.runLater(() -> {
                    this.saldoActual = saldo;
                    this.cuentaId = cuentaIdTemp;
                    this.datosCargados = true;

                    tipoCuentaLabel.setText("cuenta: " + tipoCuenta);
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    saldoLabel.setText("total: $" + df.format(saldo) + " mxn");
                });

            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error de base de datos",
                            "No se pudo conectar con el servidor.\n\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    tipoCuentaLabel.setText("cuenta: Error de conexión");
                    saldoLabel.setText("total: $0.00 mxn");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error inesperado",
                            "Ocurrió un error al cargar la información.\n\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    tipoCuentaLabel.setText("cuenta: Error");
                    saldoLabel.setText("total: $0.00 mxn");
                });
            }
        });

        cargarThread.setDaemon(true);
        cargarThread.start();
    }

    private String[] obtenerInformacionCuenta(String token) throws SQLException {
        String sql = "SELECT cu.tipo_cuenta, cu.saldo, cu.id " +
                "FROM cuentas cu " +
                "WHERE cu.token = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new String[] {
                        rs.getString("tipo_cuenta"),
                        String.valueOf(rs.getDouble("saldo")),
                        String.valueOf(rs.getInt("id"))
                };
            }
            throw new SQLException("Token no encontrado en la base de datos");
        }
    }

    private void validarCantidadTiempoReal() {
        String texto = cantidadField.getText().trim();

        if (texto.isEmpty()) {
            siguienteButton.setDisable(true);
            cantidadField.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #ddd; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-padding: 15; " +
                            "-fx-text-fill: #333;");
            return;
        }

        try {
            double cantidad = Double.parseDouble(texto);

            if (cantidad <= 0) {
                cantidadField.setStyle(
                        "-fx-background-color: #ffebee; " +
                                "-fx-border-color: #f44336; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 15; " +
                                "-fx-text-fill: #333;");
                siguienteButton.setDisable(true);
            } else {
                cantidadField.setStyle(
                        "-fx-background-color: #e8f5e8; " +
                                "-fx-border-color: #4caf50; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 15; " +
                                "-fx-text-fill: #333;");
                siguienteButton.setDisable(false);
            }

        } catch (NumberFormatException e) {
            cantidadField.setStyle(
                    "-fx-background-color: #ffebee; " +
                            "-fx-border-color: #f44336; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-padding: 15; " +
                            "-fx-text-fill: #333;");
            siguienteButton.setDisable(true);
        }
    }

    private boolean realizarDeposito(double cantidad) {
        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // Actualizar saldo
            String sqlActualizarSaldo = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
            PreparedStatement stmtActualizar = conn.prepareStatement(sqlActualizarSaldo);
            stmtActualizar.setDouble(1, cantidad);
            stmtActualizar.setInt(2, cuentaId);
            int filasAfectadas = stmtActualizar.executeUpdate();

            if (filasAfectadas == 0) {
                conn.rollback();
                Platform.runLater(
                        () -> mostrarAlerta("Error", "No se pudo actualizar el saldo", Alert.AlertType.ERROR));
                return false;
            }

            String referencia = generarReferencia();
            double nuevoSaldo = saldoActual + cantidad;

            // Registrar movimiento
            String sqlMovimiento = "INSERT INTO movimientos (" +
                    "cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia) " +
                    "VALUES (?, 'DEPOSITO', ?, ?, ?, ?, ?)";

            PreparedStatement stmtMovimiento = conn.prepareStatement(sqlMovimiento);
            stmtMovimiento.setInt(1, cuentaId);
            stmtMovimiento.setDouble(2, cantidad);
            stmtMovimiento.setDouble(3, saldoActual);
            stmtMovimiento.setDouble(4, nuevoSaldo);
            stmtMovimiento.setString(5, "Depósito en efectivo");
            stmtMovimiento.setString(6, referencia);
            stmtMovimiento.executeUpdate();

            conn.commit();

            // Actualizar variables locales
            this.saldoActual = nuevoSaldo;
            Platform.runLater(() -> {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                saldoLabel.setText("total: $" + df.format(nuevoSaldo) + " mxn");
                mostrarExitoDeposito(cantidad);
                cantidadField.clear();
            });

            return true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            Platform.runLater(() -> mostrarAlerta("Error de base de datos",
                    "No se pudo realizar el depósito.\n\n" + e.getMessage(),
                    Alert.AlertType.ERROR));
            return false;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            Platform.runLater(() -> mostrarAlerta("Error inesperado",
                    "Ocurrió un error al procesar el depósito.\n\n" + e.getMessage(),
                    Alert.AlertType.ERROR));
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    private void procesarDeposito() {
        if (!datosCargados) {
            mostrarAlerta("Espera", "La información de la cuenta aún se está cargando.", Alert.AlertType.WARNING);
            return;
        }

        String cantidadTexto = cantidadField.getText().trim();

        if (cantidadTexto.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese una cantidad", Alert.AlertType.WARNING);
            return;
        }

        try {
            double cantidadDeposito = Double.parseDouble(cantidadTexto);

            if (cantidadDeposito <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0", Alert.AlertType.WARNING);
                return;
            }

            siguienteButton.setDisable(true);
            siguienteButton.setText("Procesando...");

            Thread procesoDeposito = new Thread(() -> {
                boolean exitoso = realizarDeposito(cantidadDeposito);
                Platform.runLater(() -> {
                    siguienteButton.setDisable(false);
                    siguienteButton.setText("Siguiente");
                    if (!exitoso) {
                        // El mensaje ya se muestra en realizarDeposito
                    }
                });
            });

            procesoDeposito.setDaemon(true);
            procesoDeposito.start();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese una cantidad válida", Alert.AlertType.WARNING);
        }
    }

    private String generarReferencia() {
        return "DEP" + System.currentTimeMillis();
    }

    private void mostrarExitoDeposito(double cantidad) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Depósito Exitoso");
        alert.setHeaderText("Transacción completada");
        alert.setContentText(
                "Depósito realizado exitosamente\n\n" +
                        "Cantidad depositada: $" + df.format(cantidad) + " MXN\n" +
                        "Nuevo saldo: $" + df.format(saldoActual) + " MXN\n\n" +
                        "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void regresarMenuPrincipal() {
        primaryStage.close();
        MenuPrincipal.abrirMenuPrincipal(tokenUsuario);
    }

    public void setTokenUsuario(String token) {
        this.tokenUsuario = token;
    }

    public static void abrirDeposito(String token) {
        Deposito deposito = new Deposito(token);
        Stage stage = new Stage();
        try {
            deposito.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}