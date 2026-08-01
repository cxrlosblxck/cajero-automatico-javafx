package CajeroATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MovimientosMes extends Application {

    private String tokenUsuario;
    private Stage primaryStage;
    private Label nombreUsuarioLabel;
    private Label tipoCuentaLabel;
    private Label saldoLabel;
    private TabPane tabPane;

    public MovimientosMes(String token) {
        this.tokenUsuario = token;
        System.out.println("Token recibido: " + token);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - Movimientos del Mes");
        stage.setResizable(false);

        VBox mainPanel = createMainPanel();
        Scene scene = new Scene(mainPanel, 950, 700);
        stage.setScene(scene);
        stage.show();
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

        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-tab-min-width: 140px;");
        tabPane.setTabMinWidth(140);
        tabPane.setTabMaxWidth(200);
        // Estilo para las pestañas en monocromático
        tabPane.setStyle("-fx-background-color: transparent; -fx-tab-min-width: 140px; -fx-tab-max-width: 200px;");
        // Puedes agregar más estilos si quieres

        HBox salirSection = createSalirSection();

        mainPanel.getChildren().addAll(topSection, tabPane, salirSection);

        cargarInformacionUsuario();

        return mainPanel;
    }

    private HBox createHeaderSection() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(20);
        headerBox.setPadding(new Insets(10));

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
        headerBox.getChildren().addAll(logoImageView, titleBox);

        return headerBox;
    }

    private VBox createInfoSection() {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setSpacing(5);
        infoBox.setPadding(new Insets(15, 30, 15, 30));
        infoBox.setMinWidth(280);
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

    private ScrollPane crearScrollTabla(String tipo, String colorFondo) {
        // Se puede usar un fondo monocromático claro, aunque el colorFondo se pasa
        // desde las pestañas (tonos pastel). Para mantener monocromático, podemos
        // ignorar el colorFondo y usar gris.
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        // Fondo gris claro para la tabla (reemplazamos colorFondo por un gris)
        box.setStyle("-fx-background-color: #f0f0f0; " +
                "-fx-border-color: #888; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;");

        Label titulo = new Label("Historial de " + tipo.replace("_", " "));
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.BLACK); // Texto negro
        titulo.setPadding(new Insets(0, 0, 10, 0));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #ccc; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;");

        // Columnas con tamaño flexible
        for (int i = 0; i < 5; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setMinWidth(120);
            grid.getColumnConstraints().add(colConst);
        }

        // Encabezados: fondo gris oscuro, texto blanco
        String[] headers = { "Tiempo", "Contabilidad", "Saldo", "Fecha", "Referencia" };
        for (int i = 0; i < headers.length; i++) {
            Label header = new Label(headers[i]);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            header.setTextFill(Color.WHITE);
            header.setStyle(
                    "-fx-padding: 10px; -fx-background-color: #333333; -fx-border-color: #555; -fx-border-width: 0 0 1 0;");
            header.setMaxWidth(Double.MAX_VALUE);
            grid.add(header, i, 0);
        }

        // Cargar datos en hilo
        new Thread(() -> cargarMovimientos(grid, tipo)).start();

        box.getChildren().addAll(titulo, grid);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scroll;
    }

    private void cargarMovimientos(GridPane grid, String tipoMovimiento) {
        String sql = "SELECT m.tipo_movimiento, m.cantidad, m.saldo_posterior, m.fecha_movimiento, m.referencia " +
                "FROM movimientos m " +
                "JOIN cuentas c ON m.cuenta_id = c.id " +
                "WHERE c.token = ? AND m.tipo_movimiento = ? " +
                "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tokenUsuario);
            stmt.setString(2, tipoMovimiento);
            System.out.println("Ejecutando consulta: " + stmt.toString());

            ResultSet rs = stmt.executeQuery();

            List<Movimiento> movimientos = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#,##0.00");

            while (rs.next()) {
                movimientos.add(new Movimiento(
                        rs.getString("tipo_movimiento"),
                        rs.getDouble("cantidad"),
                        rs.getDouble("saldo_posterior"),
                        rs.getTimestamp("fecha_movimiento").toLocalDateTime(),
                        rs.getString("referencia")));
            }

            Platform.runLater(() -> {
                // Limpiar filas (excepto encabezados)
                grid.getChildren()
                        .removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

                if (movimientos.isEmpty()) {
                    Label mensaje = new Label("No se encontraron movimientos de este tipo");
                    mensaje.setStyle("-fx-text-fill: #555; -fx-font-size: 16px; -fx-padding: 20px;");
                    grid.add(mensaje, 0, 1, 5, 1);
                } else {
                    for (int i = 0; i < movimientos.size(); i++) {
                        Movimiento mov = movimientos.get(i);
                        // Alternar colores de fila (zebra)
                        String colorFila = (i % 2 == 0) ? "#f9f9f9" : "#f2f2f2";
                        // Crear etiquetas con estilo monocromático
                        Label tipoLabel = crearLabelEstilizado(mov.getTipo(), colorFila);
                        Label cantidadLabel = crearLabelEstilizado("$" + df.format(mov.getCantidad()), colorFila);
                        Label saldoLabelCelda = crearLabelEstilizado("$" + df.format(mov.getSaldoPosterior()),
                                colorFila);
                        Label fechaLabel = crearLabelEstilizado(
                                mov.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), colorFila);
                        Label refLabel = crearLabelEstilizado(mov.getReferencia(), colorFila);

                        grid.addRow(i + 1, tipoLabel, cantidadLabel, saldoLabelCelda, fechaLabel, refLabel);
                    }
                }
            });

        } catch (SQLException e) {
            System.err.println("Error al cargar movimientos: " + e.getMessage());
            Platform.runLater(() -> {
                Label errorLabel = new Label("Error al cargar datos. Intente nuevamente.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 10px;");
                grid.add(errorLabel, 0, 1, 5, 1);
                mostrarError("Error de base de datos: " + e.getMessage());
            });
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            Platform.runLater(() -> {
                Label errorLabel = new Label("Error inesperado. Reinicie la aplicación.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 10px;");
                grid.add(errorLabel, 0, 1, 5, 1);
                mostrarError("Error: " + e.getMessage());
            });
        }
    }

    // Nueva versión con color de fondo y texto negro
    private Label crearLabelEstilizado(String texto, String colorFondo) {
        Label label = new Label(texto);
        label.setStyle("-fx-padding: 10px; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-text-fill: #000000; " + // Texto negro
                "-fx-font-size: 14px; " +
                "-fx-background-color: " + colorFondo + ";");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
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

        salirButton.setOnAction(e -> regresarMenuPrincipal());

        salirBox.getChildren().add(salirButton);
        return salirBox;
    }

    private void cargarInformacionUsuario() {
        if (tokenUsuario == null || tokenUsuario.isEmpty()) {
            mostrarError("No se proporcionó un token válido");
            return;
        }

        new Thread(() -> {
            try {
                String[] infoUsuario = obtenerInformacionUsuario(tokenUsuario);
                String nombre = infoUsuario[0];
                String tipoCuenta = infoUsuario[1];
                double saldo = Double.parseDouble(infoUsuario[2]);

                Platform.runLater(() -> {
                    nombreUsuarioLabel.setText("Bienvenido " + nombre);
                    tipoCuentaLabel.setText("cuenta: " + tipoCuenta);

                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    saldoLabel.setText("$" + df.format(saldo) + " mxn");

                    tabPane.getTabs().clear();
                    tabPane.getTabs().addAll(
                            new Tab("💵 Retiros", crearScrollTabla("RETIRO", "#f0f0f0")),
                            new Tab("💰 Depósitos", crearScrollTabla("DEPOSITO", "#f0f0f0")),
                            new Tab("📤 Enviadas", crearScrollTabla("TRANSFERENCIA_ENVIADA", "#f0f0f0")),
                            new Tab("📥 Recibidas", crearScrollTabla("TRANSFERENCIA_RECIBIDA", "#f0f0f0")));
                });

            } catch (SQLException e) {
                Platform.runLater(() -> mostrarError("Error de base de datos: " + e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al cargar información del usuario: " + e.getMessage()));
            }
        }).start();
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
            throw new SQLException("Token no encontrado");
        }
    }

    private void regresarMenuPrincipal() {
        primaryStage.close();
        MenuPrincipal.abrirMenuPrincipal(tokenUsuario);
    }

    public static void abrirMovimientos(String token) {
        MovimientosMes movimientos = new MovimientosMes(token);
        Stage stage = new Stage();
        movimientos.start(stage);
    }

    private static class Movimiento {
        private String tipo;
        private double cantidad;
        private double saldoPosterior;
        private LocalDateTime fecha;
        private String referencia;

        public Movimiento(String tipo, double cantidad, double saldoPosterior, LocalDateTime fecha, String referencia) {
            this.tipo = tipo;
            this.cantidad = cantidad;
            this.saldoPosterior = saldoPosterior;
            this.fecha = fecha;
            this.referencia = referencia;
        }

        public String getTipo() {
            return tipo;
        }

        public double getCantidad() {
            return cantidad;
        }

        public double getSaldoPosterior() {
            return saldoPosterior;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public String getReferencia() {
            return referencia;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}