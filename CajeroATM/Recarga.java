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

public class Recarga extends Application {

    private String tokenUsuario;
    private Stage primaryStage;
    private Label tipoCuentaLabel;
    private Label saldoLabel;
    private TextField telefonoField;
    private TextField montoField;
    private Button confirmarButton;
    private Button salirButton;

    private double saldoActual = 0.0;
    private int cuentaId = 0;
    private boolean datosCargados = false;
    private Button companiaSeleccionada = null; // Para rastrear el botón activo

    public Recarga(String token) {
        this.tokenUsuario = token;
    }

    public Recarga() {
        this.tokenUsuario = "";
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - Recarga Telefónica");
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

        Label tituloLabel = new Label("Recarga Telefónica");
        tituloLabel.setTextFill(Color.WHITE);
        tituloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        tituloLabel.setAlignment(Pos.CENTER);

        HBox companiasBox = createCompaniasBox();
        VBox camposBox = createCamposBox();
        HBox buttonSection = createButtonSection();
        VBox spacer = new VBox();
        spacer.setPrefHeight(50);
        HBox salirSection = createSalirSection();

        mainPanel.getChildren().addAll(headerSection, tituloLabel, companiasBox,
                camposBox, buttonSection, spacer, salirSection);
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

    private HBox createCompaniasBox() {
        HBox companiasBox = new HBox(15);
        companiasBox.setAlignment(Pos.CENTER);

        Button btnTelcel = createCompaniaButton("Telcel");
        Button btnAtt = createCompaniaButton("AT&T");
        Button btnMovistar = createCompaniaButton("Movistar");
        Button btnUnefon = createCompaniaButton("Unefon");

        companiasBox.getChildren().addAll(btnTelcel, btnAtt, btnMovistar, btnUnefon);
        return companiasBox;
    }

    private Button createCompaniaButton(String nombre) {
        Button btn = new Button(nombre);
        btn.setPrefWidth(120);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #c41e3a; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");

        // Efecto hover
        btn.setOnMouseEntered(e -> {
            if (btn != companiaSeleccionada) {
                btn.setStyle(
                        "-fx-background-color: #f0f0f0; " +
                                "-fx-text-fill: #c41e3a; " +
                                "-fx-border-radius: 20; " +
                                "-fx-background-radius: 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 3);");
            }
        });
        btn.setOnMouseExited(e -> {
            if (btn != companiaSeleccionada) {
                btn.setStyle(
                        "-fx-background-color: white; " +
                                "-fx-text-fill: #c41e3a; " +
                                "-fx-border-radius: 20; " +
                                "-fx-background-radius: 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");
            }
        });

        // Evento de selección
        btn.setOnAction(e -> {
            // Restablecer estilo del botón anterior
            if (companiaSeleccionada != null) {
                companiaSeleccionada.setStyle(
                        "-fx-background-color: white; " +
                                "-fx-text-fill: #c41e3a; " +
                                "-fx-border-radius: 20; " +
                                "-fx-background-radius: 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");
            }
            // Establecer estilo de selección al botón actual
            btn.setStyle(
                    "-fx-background-color: #2196F3; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-radius: 20; " +
                            "-fx-background-radius: 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 3);");
            companiaSeleccionada = btn;
            // Opcional: mostrar mensaje o guardar compañía seleccionada
            System.out.println("Compañía seleccionada: " + nombre);
            validarFormulario(); // re-evaluar campos
        });

        return btn;
    }

    private VBox createCamposBox() {
        VBox camposBox = new VBox(15);
        camposBox.setAlignment(Pos.CENTER);

        telefonoField = new TextField();
        telefonoField.setPromptText("Número telefónico (10 dígitos)");
        telefonoField.setPrefWidth(300);
        telefonoField.setPrefHeight(50);
        telefonoField.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        telefonoField.setAlignment(Pos.CENTER);
        telefonoField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10; " +
                        "-fx-text-fill: #333;");

        telefonoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                telefonoField.setText(oldValue);
            }
            if (newValue.length() > 10) {
                telefonoField.setText(oldValue);
            }
            validarFormulario();
        });

        montoField = new TextField();
        montoField.setPromptText("Monto a recargar (ej. 100)");
        montoField.setPrefWidth(300);
        montoField.setPrefHeight(50);
        montoField.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        montoField.setAlignment(Pos.CENTER);
        montoField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10; " +
                        "-fx-text-fill: #333;");

        montoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                montoField.setText(oldValue);
            }
            validarFormulario();
        });

        camposBox.getChildren().addAll(telefonoField, montoField);
        return camposBox;
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);

        confirmarButton = new Button("Confirmar Recarga");
        confirmarButton.setPrefWidth(180);
        confirmarButton.setPrefHeight(45);
        confirmarButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        confirmarButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");

        confirmarButton.setOnMouseEntered(e -> confirmarButton.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 3);"));

        confirmarButton.setOnMouseExited(e -> confirmarButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);"));

        confirmarButton.setOnAction(e -> procesarRecarga());
        confirmarButton.setDisable(true);

        buttonBox.getChildren().add(confirmarButton);
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
        String sql = "SELECT c.tipo_cuenta, c.saldo, c.id " +
                "FROM cuentas c " +
                "WHERE c.token = ?";

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
            throw new SQLException("Token no encontrado");
        }
    }

    private void validarFormulario() {
        if (!datosCargados) {
            confirmarButton.setDisable(true);
            return;
        }

        String telefono = telefonoField.getText().trim();
        String montoTexto = montoField.getText().trim();

        boolean telefonoValido = telefono.length() == 10;
        boolean montoValido = false;

        try {
            double monto = Double.parseDouble(montoTexto);
            if (monto > 0 && monto <= saldoActual) {
                montoValido = true;
            }
        } catch (NumberFormatException e) {
            montoValido = false;
        }

        // Opcional: podríamos requerir que se haya seleccionado una compañía,
        // pero no es necesario para la funcionalidad. Si quieres forzarlo, descomenta:
        // boolean companiaSeleccionadaOK = (companiaSeleccionada != null);
        boolean companiaSeleccionadaOK = true; // por ahora no es obligatorio

        confirmarButton.setDisable(!(telefonoValido && montoValido && companiaSeleccionadaOK));
    }

    private void procesarRecarga() {
        if (!datosCargados) {
            mostrarAlerta("Espera", "La información de la cuenta aún se está cargando.", Alert.AlertType.WARNING);
            return;
        }

        String telefono = telefonoField.getText().trim();
        String montoStr = montoField.getText().trim();

        if (telefono.length() != 10) {
            mostrarAlerta("Error", "El número telefónico debe tener 10 dígitos", Alert.AlertType.WARNING);
            return;
        }

        if (montoStr.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese un monto", Alert.AlertType.WARNING);
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
                mostrarAlerta("Error", "El monto debe ser mayor a 0", Alert.AlertType.WARNING);
                return;
            }

            if (monto > saldoActual) {
                mostrarAlerta("Saldo Insuficiente",
                        "No tiene suficiente saldo para realizar esta recarga.\n" +
                                "Saldo disponible: $" + new DecimalFormat("#,##0.00").format(saldoActual) + " MXN",
                        Alert.AlertType.WARNING);
                return;
            }

            confirmarButton.setDisable(true);
            confirmarButton.setText("Procesando...");

            Thread procesoRecarga = new Thread(() -> {
                boolean exitoso = realizarRecarga(telefono, monto);
                Platform.runLater(() -> {
                    confirmarButton.setDisable(false);
                    confirmarButton.setText("Confirmar Recarga");
                    if (exitoso) {
                        mostrarExitoRecarga(monto, telefono);
                    }
                });
            });

            procesoRecarga.setDaemon(true);
            procesoRecarga.start();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese un monto válido", Alert.AlertType.WARNING);
        }
    }

    private boolean realizarRecarga(String telefono, double monto) {
        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // Actualizar saldo
            String sqlActualizarSaldo = "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?";
            PreparedStatement stmtActualizar = conn.prepareStatement(sqlActualizarSaldo);
            stmtActualizar.setDouble(1, monto);
            stmtActualizar.setInt(2, cuentaId);
            int filasAfectadas = stmtActualizar.executeUpdate();

            if (filasAfectadas == 0) {
                conn.rollback();
                Platform.runLater(
                        () -> mostrarAlerta("Error", "No se pudo actualizar el saldo", Alert.AlertType.ERROR));
                return false;
            }

            double nuevoSaldo = saldoActual - monto;
            String referencia = generarReferencia();

            // Registrar movimiento
            String sqlMovimiento = "INSERT INTO movimientos (" +
                    "cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia, fecha_movimiento) "
                    +
                    "VALUES (?, 'RECARGA', ?, ?, ?, ?, ?, NOW())";

            PreparedStatement stmtMovimiento = conn.prepareStatement(sqlMovimiento);
            stmtMovimiento.setInt(1, cuentaId);
            stmtMovimiento.setDouble(2, monto);
            stmtMovimiento.setDouble(3, saldoActual);
            stmtMovimiento.setDouble(4, nuevoSaldo);
            stmtMovimiento.setString(5, "Recarga telefónica a " + telefono);
            stmtMovimiento.setString(6, referencia);
            stmtMovimiento.executeUpdate();

            conn.commit();
            this.saldoActual = nuevoSaldo;

            Platform.runLater(() -> {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                saldoLabel.setText("Saldo: $" + df.format(nuevoSaldo) + " MXN");
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
                    "No se pudo realizar la recarga.\n\n" + e.getMessage(),
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
                    "Ocurrió un error al procesar la recarga.\n\n" + e.getMessage(),
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
        return "REC" + System.currentTimeMillis();
    }

    private void mostrarExitoRecarga(double monto, String telefono) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recarga Exitosa");
        alert.setHeaderText("Transacción completada");
        alert.setContentText(
                "Recarga realizada exitosamente\n\n" +
                        "Número recargado: " + telefono + "\n" +
                        "Monto: $" + df.format(monto) + " MXN\n" +
                        "Saldo restante: $" + df.format(saldoActual) + " MXN\n\n" +
                        "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        alert.showAndWait();
        telefonoField.clear();
        montoField.clear();
        // Restablecer estilo de compañía seleccionada
        if (companiaSeleccionada != null) {
            companiaSeleccionada.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: #c41e3a; " +
                            "-fx-border-radius: 20; " +
                            "-fx-background-radius: 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 2);");
            companiaSeleccionada = null;
        }
        confirmarButton.setDisable(true);
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

    public static void abrirRecarga(String token) {
        Recarga recarga = new Recarga(token);
        Stage stage = new Stage();
        try {
            recarga.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}