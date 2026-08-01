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

public class Transferencia extends Application {

    private String tokenUsuario;
    private Stage primaryStage;
    private Label tipoCuentaLabel;
    private Label saldoLabel;
    private TextField numeroCuentaField;
    private TextField cantidadField;
    private Button siguienteButton;
    private Button salirButton;

    private double saldoActual = 0.0;
    private int cuentaId = 0;
    private String clienteNombre = "";
    private int numeroCuenta = 0;
    private boolean datosCargados = false;

    public Transferencia(String token) {
        this.tokenUsuario = token;
    }

    public Transferencia() {
        this.tokenUsuario = "";
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - Transferencia");
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
        mainPanel.setSpacing(25);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background: linear-gradient(to bottom, #c41e3a, #8b1538);");

        HBox headerSection = createHeaderSection();

        Label tituloLabel = new Label("Ingresa el ID de la cuenta destino");
        tituloLabel.setTextFill(Color.WHITE);
        tituloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        tituloLabel.setAlignment(Pos.CENTER);

        VBox numeroCuentaSection = createNumeroCuentaSection();

        Label cantidadTituloLabel = new Label("Ingresa cantidad a transferir");
        cantidadTituloLabel.setTextFill(Color.WHITE);
        cantidadTituloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        cantidadTituloLabel.setAlignment(Pos.CENTER);

        VBox cantidadSection = createCantidadSection();
        HBox buttonSection = createButtonSection();
        HBox salirSection = createSalirSection();

        mainPanel.getChildren().addAll(headerSection, tituloLabel, numeroCuentaSection,
                cantidadTituloLabel, cantidadSection, buttonSection, salirSection);
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

    private VBox createNumeroCuentaSection() {
        VBox numeroSection = new VBox();
        numeroSection.setAlignment(Pos.CENTER);
        numeroSection.setSpacing(8);

        numeroCuentaField = new TextField();
        numeroCuentaField.setPromptText("ID de la cuenta destino");
        numeroCuentaField.setPrefWidth(300);
        numeroCuentaField.setPrefHeight(40);
        numeroCuentaField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        numeroCuentaField.setAlignment(Pos.CENTER);
        numeroCuentaField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10; " +
                        "-fx-text-fill: #333;");

        numeroCuentaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numeroCuentaField.setText(oldValue);
            }
            validarFormulario();
        });

        Label instruccionLabel = new Label("Ingresa el ID numérico de la cuenta destino");
        instruccionLabel.setTextFill(Color.WHITE);
        instruccionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        instruccionLabel.setOpacity(0.8);

        numeroSection.getChildren().addAll(numeroCuentaField, instruccionLabel);
        return numeroSection;
    }

    private VBox createCantidadSection() {
        VBox cantidadSection = new VBox();
        cantidadSection.setAlignment(Pos.CENTER);
        cantidadSection.setSpacing(10);

        cantidadField = new TextField();
        cantidadField.setPromptText("0.00");
        cantidadField.setPrefWidth(200);
        cantidadField.setPrefHeight(50);
        cantidadField.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        cantidadField.setAlignment(Pos.CENTER);
        cantidadField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10; " +
                        "-fx-text-fill: #333;");

        cantidadField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                cantidadField.setText(oldValue);
            }
            validarFormulario();
        });

        cantidadSection.getChildren().add(cantidadField);
        return cantidadSection;
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);

        siguienteButton = new Button("Siguiente");
        siguienteButton.setPrefWidth(120);
        siguienteButton.setPrefHeight(40);
        siguienteButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
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

        siguienteButton.setOnAction(e -> procesarTransferencia());
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
                String nombre = infoCuenta[3];
                int numCuenta = Integer.parseInt(infoCuenta[4]);

                Platform.runLater(() -> {
                    this.saldoActual = saldo;
                    this.cuentaId = cuentaIdTemp;
                    this.clienteNombre = nombre;
                    this.numeroCuenta = numCuenta;
                    this.datosCargados = true;

                    tipoCuentaLabel.setText("Cuenta: " + tipoCuenta);
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    saldoLabel.setText("Saldo: $" + df.format(saldo) + " MXN");
                });

            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error de base de datos",
                            "No se pudo conectar con el servidor.\n\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    tipoCuentaLabel.setText("Cuenta: Error de conexión");
                    saldoLabel.setText("Saldo: $0.00 MXN");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error inesperado",
                            "Ocurrió un error al cargar la información.\n\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    tipoCuentaLabel.setText("Cuenta: Error");
                    saldoLabel.setText("Saldo: $0.00 MXN");
                });
            }
        });

        cargarThread.setDaemon(true);
        cargarThread.start();
    }

    private String[] obtenerInformacionCuenta(String token) throws SQLException {
        String sql = "SELECT c.tipo_cuenta, c.saldo, c.id, cl.nombre, c.id as numero_cuenta " +
                "FROM cuentas c " +
                "JOIN clientes cl ON c.cliente_id = cl.id " +
                "WHERE c.token = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new String[] {
                        rs.getString("tipo_cuenta"),
                        String.valueOf(rs.getDouble("saldo")),
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getInt("numero_cuenta"))
                };
            }
            throw new SQLException("Token no encontrado");
        }
    }

    private void validarFormulario() {
        String numeroCuentaDestino = numeroCuentaField.getText().trim();
        String cantidadTexto = cantidadField.getText().trim();

        if (!datosCargados || numeroCuentaDestino.isEmpty() || cantidadTexto.isEmpty()) {
            siguienteButton.setDisable(true);
            return;
        }

        try {
            // Validar que el ID destino sea un número
            Integer.parseInt(numeroCuentaDestino);
            double cantidad = Double.parseDouble(cantidadTexto);

            if (cantidad <= 0 || cantidad > saldoActual) {
                siguienteButton.setDisable(true);
                cantidadField.setStyle(cantidad <= 0 ? "-fx-background-color: #ffebee; -fx-border-color: #f44336;"
                        : "-fx-background-color: #fff3e0; -fx-border-color: #ff9800;");
            } else {
                siguienteButton.setDisable(false);
                cantidadField.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #4caf50;");
            }
        } catch (NumberFormatException e) {
            siguienteButton.setDisable(true);
        }
    }

    private void procesarTransferencia() {
        if (!datosCargados) {
            mostrarAlerta("Espera", "La información de la cuenta aún se está cargando.", Alert.AlertType.WARNING);
            return;
        }

        String numeroCuentaDestino = numeroCuentaField.getText().trim();
        String cantidadTexto = cantidadField.getText().trim();

        if (numeroCuentaDestino.isEmpty() || cantidadTexto.isEmpty()) {
            mostrarAlerta("Error", "Por favor complete todos los campos", Alert.AlertType.WARNING);
            return;
        }

        try {
            double cantidadTransferencia = Double.parseDouble(cantidadTexto);

            if (cantidadTransferencia <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0", Alert.AlertType.WARNING);
                return;
            }

            if (cantidadTransferencia > saldoActual) {
                mostrarAlerta("Saldo Insuficiente",
                        "No tiene suficiente saldo para realizar esta transferencia.\n" +
                                "Saldo disponible: $" + new DecimalFormat("#,##0.00").format(saldoActual) + " MXN",
                        Alert.AlertType.WARNING);
                return;
            }

            siguienteButton.setDisable(true);
            siguienteButton.setText("Procesando...");

            Thread procesoTransferencia = new Thread(() -> {
                boolean exitoso = realizarTransferencia(numeroCuentaDestino, cantidadTransferencia);
                Platform.runLater(() -> {
                    siguienteButton.setDisable(false);
                    siguienteButton.setText("Siguiente");
                    if (exitoso) {
                        mostrarExitoTransferencia(cantidadTransferencia, numeroCuentaDestino);
                    }
                });
            });

            procesoTransferencia.setDaemon(true);
            procesoTransferencia.start();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese una cantidad válida", Alert.AlertType.WARNING);
        }
    }

    private boolean realizarTransferencia(String numeroCuentaDestino, double cantidad) {
        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Verificar cuenta destino
            String sqlBuscarDestino = "SELECT id, cliente_id, saldo FROM cuentas WHERE id = ?";
            PreparedStatement stmtBuscar = conn.prepareStatement(sqlBuscarDestino);
            stmtBuscar.setString(1, numeroCuentaDestino);
            ResultSet rsDestino = stmtBuscar.executeQuery();

            if (!rsDestino.next()) {
                mostrarAlerta("Error", "La cuenta destino no existe", Alert.AlertType.ERROR);
                conn.rollback();
                return false;
            }

            int cuentaDestinoId = rsDestino.getInt("id");
            double saldoDestino = rsDestino.getDouble("saldo");

            if (cuentaDestinoId == cuentaId) {
                mostrarAlerta("Error", "No puede transferir a la misma cuenta", Alert.AlertType.ERROR);
                conn.rollback();
                return false;
            }

            // 2. Obtener nombre del destinatario
            String sqlNombreDestino = "SELECT nombre, apellido_paterno FROM clientes WHERE id = ?";
            PreparedStatement stmtNombre = conn.prepareStatement(sqlNombreDestino);
            stmtNombre.setInt(1, rsDestino.getInt("cliente_id"));
            ResultSet rsNombre = stmtNombre.executeQuery();
            String nombreDestino = rsNombre.next()
                    ? rsNombre.getString("nombre") + " " + rsNombre.getString("apellido_paterno")
                    : "Cliente Desconocido";

            // 3. Actualizar saldos
            // Restar de origen
            String sqlActualizarOrigen = "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?";
            PreparedStatement stmtOrigen = conn.prepareStatement(sqlActualizarOrigen);
            stmtOrigen.setDouble(1, cantidad);
            stmtOrigen.setInt(2, cuentaId);
            int filasOrigen = stmtOrigen.executeUpdate();

            if (filasOrigen == 0) {
                conn.rollback();
                return false;
            }

            // Sumar a destino
            String sqlActualizarDestino = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
            PreparedStatement stmtDestino = conn.prepareStatement(sqlActualizarDestino);
            stmtDestino.setDouble(1, cantidad);
            stmtDestino.setInt(2, cuentaDestinoId);
            stmtDestino.executeUpdate();

            String referencia = generarReferencia();

            // 4. Registrar movimiento en origen (TRANSFERENCIA_ENVIADA)
            String sqlMovOrigen = "INSERT INTO movimientos (" +
                    "cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, cuenta_destino_id, referencia, fecha_movimiento) "
                    +
                    "VALUES (?, 'TRANSFERENCIA_ENVIADA', ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement stmtMovOrigen = conn.prepareStatement(sqlMovOrigen);
            stmtMovOrigen.setInt(1, cuentaId);
            stmtMovOrigen.setDouble(2, cantidad);
            stmtMovOrigen.setDouble(3, saldoActual);
            stmtMovOrigen.setDouble(4, saldoActual - cantidad);
            stmtMovOrigen.setString(5, "Transferencia a cuenta " + numeroCuentaDestino + " (" + nombreDestino + ")");
            stmtMovOrigen.setInt(6, cuentaDestinoId);
            stmtMovOrigen.setString(7, referencia);
            stmtMovOrigen.executeUpdate();

            // 5. Registrar movimiento en destino (TRANSFERENCIA_RECIBIDA)
            String sqlMovDestino = "INSERT INTO movimientos (" +
                    "cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, cuenta_destino_id, referencia, fecha_movimiento) "
                    +
                    "VALUES (?, 'TRANSFERENCIA_RECIBIDA', ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement stmtMovDestino = conn.prepareStatement(sqlMovDestino);
            stmtMovDestino.setInt(1, cuentaDestinoId);
            stmtMovDestino.setDouble(2, cantidad);
            stmtMovDestino.setDouble(3, saldoDestino);
            stmtMovDestino.setDouble(4, saldoDestino + cantidad);
            stmtMovDestino.setString(5,
                    "Transferencia recibida de cuenta " + numeroCuenta + " (" + clienteNombre + ")");
            stmtMovDestino.setInt(6, cuentaId);
            stmtMovDestino.setString(7, referencia);
            stmtMovDestino.executeUpdate();

            conn.commit();
            this.saldoActual = saldoActual - cantidad;
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            Platform.runLater(() -> mostrarAlerta("Error de base de datos",
                    "No se pudo realizar la transferencia.\n\n" + e.getMessage(),
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
                    "Ocurrió un error al procesar la transferencia.\n\n" + e.getMessage(),
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

    private String generarReferencia() {
        return "TRF" + System.currentTimeMillis();
    }

    private void mostrarExitoTransferencia(double cantidad, String numeroDestino) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transferencia Exitosa");
        alert.setHeaderText("Transacción completada");
        alert.setContentText(
                "Transferencia realizada exitosamente\n\n" +
                        "Cantidad transferida: $" + df.format(cantidad) + " MXN\n" +
                        "Cuenta destino: " + numeroDestino + "\n" +
                        "Saldo restante: $" + df.format(saldoActual) + " MXN\n\n" +
                        "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        alert.showAndWait();
        saldoLabel.setText("Saldo: $" + df.format(saldoActual) + " MXN");
        numeroCuentaField.clear();
        cantidadField.clear();
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

    public static void abrirTransferencia(String token) {
        Transferencia transferencia = new Transferencia(token);
        Stage stage = new Stage();
        try {
            transferencia.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}