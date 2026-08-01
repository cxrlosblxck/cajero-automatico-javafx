package CajeroATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Token extends Application {

    private PasswordField nipField;
    private Button siguienteButton;
    private Label mensajeLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Chronos Axios - ATM");
        stage.setResizable(false);

        VBox mainPanel = createMainPanel();
        Scene scene = new Scene(mainPanel, 400, 500);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createMainPanel() {
        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setSpacing(30);
        mainPanel.setPadding(new Insets(40));
        mainPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #c41e3a, #8b1538);");

        HBox headerBox = createHeaderSection();
        VBox inputBox = createInputSection();
        VBox buttonBox = createButtonSection();

        mensajeLabel = new Label();
        mensajeLabel.setTextFill(Color.WHITE);
        mensajeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        mensajeLabel.setVisible(false);

        mainPanel.getChildren().addAll(headerBox, inputBox, buttonBox, mensajeLabel);
        return mainPanel;
    }

    private HBox createHeaderSection() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(20);

        // Cargar imagen con ImageLoader
        Image logoImage = ImageLoader.cargarImagen("Rino.png");
        ImageView logoImageView = new ImageView();
        if (logoImage != null) {
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(130);
            logoImageView.setFitHeight(130);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.5));
            shadow.setOffsetX(3);
            shadow.setOffsetY(3);
            shadow.setRadius(8);
            logoImageView.setEffect(shadow);
        } else {
            // Si no hay imagen, crear un ImageView vacío o con texto alternativo
            logoImageView = new ImageView();
            logoImageView.setFitWidth(130);
            logoImageView.setFitHeight(130);
        }

        // Títulos
        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(5);

        Label chronosLabel = new Label("Chronos");
        chronosLabel.setTextFill(Color.WHITE);
        chronosLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        Label axiosLabel = new Label("Axios");
        axiosLabel.setTextFill(Color.WHITE);
        axiosLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        Label subtitleLabel = new Label("Para el futuro de la gente");
        subtitleLabel.setTextFill(Color.WHITE);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setOpacity(0.9);

        titleBox.getChildren().addAll(chronosLabel, axiosLabel, subtitleLabel);
        headerBox.getChildren().addAll(logoImageView, titleBox);

        return headerBox;
    }

    private VBox createInputSection() {
        VBox inputBox = new VBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setSpacing(10);

        Label nipLabel = new Label("Ingresa con NIP");
        nipLabel.setTextFill(Color.WHITE);
        nipLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        nipField = new PasswordField();
        nipField.setPromptText("Ingrese su NIP/Token");
        nipField.setPrefWidth(250);
        nipField.setPrefHeight(40);
        nipField.setFont(Font.font("Arial", 14));
        nipField.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10;");

        nipField.setOnAction(e -> verificarToken());

        inputBox.getChildren().addAll(nipLabel, nipField);
        return inputBox;
    }

    private VBox createButtonSection() {
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);

        siguienteButton = new Button("Siguiente");
        siguienteButton.setPrefWidth(120);
        siguienteButton.setPrefHeight(40);
        siguienteButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        siguienteButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand;");

        siguienteButton.setOnMouseEntered(e -> siguienteButton.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand;"));

        siguienteButton.setOnMouseExited(e -> siguienteButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #333; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand;"));

        siguienteButton.setOnAction(e -> verificarToken());

        buttonBox.getChildren().add(siguienteButton);
        return buttonBox;
    }

    private void verificarToken() {
        String token = nipField.getText().trim();

        if (token.isEmpty()) {
            mostrarMensaje("Por favor, ingrese su NIP/Token", Color.YELLOW);
            return;
        }

        siguienteButton.setDisable(true);
        siguienteButton.setText("Verificando...");

        Thread verificacionThread = new Thread(() -> {
            boolean tokenValido = verificarTokenEnBD(token);
            String nombreUsuario = obtenerNombreUsuario(token);

            javafx.application.Platform.runLater(() -> {
                siguienteButton.setDisable(false);
                siguienteButton.setText("Siguiente");

                if (tokenValido) {
                    mostrarMensajeBienvenida(nombreUsuario);
                } else {
                    mostrarMensaje("Token inválido. Acceso denegado.", Color.LIGHTCORAL);
                    nipField.clear();
                }
            });
        });

        verificacionThread.setDaemon(true);
        verificacionThread.start();
    }

    public boolean verificarTokenEnBD(String token) {
        String sql = "SELECT * FROM cuentas WHERE token = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            mostrarAlertaError("Error de base de datos",
                    "No se pudo conectar con el servidor. Verifica tu conexión.\n\n" + e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarAlertaError("Error inesperado",
                    "Ocurrió un error al verificar el token.\n\n" + e.getMessage());
            return false;
        }
    }

    public String obtenerNombreUsuario(String token) {
        String sql = "SELECT c.nombre FROM clientes c JOIN cuentas cu ON c.id = cu.cliente_id WHERE cu.token = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
            return "Cliente";
        } catch (SQLException e) {
            mostrarAlertaError("Error de base de datos",
                    "No se pudo obtener el nombre del usuario.\n\n" + e.getMessage());
            return "Cliente";
        } catch (Exception e) {
            mostrarAlertaError("Error inesperado",
                    "Ocurrió un error al obtener el nombre del usuario.\n\n" + e.getMessage());
            return "Cliente";
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    private void mostrarMensaje(String mensaje, Color color) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setTextFill(color);
        mensajeLabel.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), mensajeLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void mostrarMensajeBienvenida(String nombreUsuario) {
        Stage bienvenidaStage = new Stage();
        bienvenidaStage.setTitle("Bienvenido");
        bienvenidaStage.initOwner(primaryStage);

        VBox bienvenidaBox = new VBox();
        bienvenidaBox.setAlignment(Pos.CENTER);
        bienvenidaBox.setSpacing(20);
        bienvenidaBox.setPadding(new Insets(40));
        bienvenidaBox.setStyle("-fx-background-color: #2d5a27;");

        Label bienvenidaLabel = new Label("¡Bienvenido " + nombreUsuario + "!");
        bienvenidaLabel.setTextFill(Color.WHITE);
        bienvenidaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        bienvenidaLabel.setWrapText(true);
        bienvenidaLabel.setMaxWidth(300);
        bienvenidaLabel.setAlignment(Pos.CENTER);

        Label accesoLabel = new Label("Acceso concedido exitosamente");
        accesoLabel.setTextFill(Color.LIGHTGREEN);
        accesoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Button continuarButton = new Button("Continuar");
        continuarButton.setPrefWidth(100);
        continuarButton.setPrefHeight(35);
        continuarButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;");

        continuarButton.setOnAction(e -> {
            bienvenidaStage.close();
            abrirMenuPrincipal();
        });

        bienvenidaBox.getChildren().addAll(bienvenidaLabel, accesoLabel, continuarButton);
        Scene bienvenidaScene = new Scene(bienvenidaBox, 350, 200);
        bienvenidaStage.setScene(bienvenidaScene);
        bienvenidaStage.setResizable(false);
        bienvenidaStage.showAndWait();
    }

    private void abrirMenuPrincipal() {
        primaryStage.close();
        String token = nipField.getText().trim();
        MenuPrincipal.abrirMenuPrincipal(token);
    }

    public static void main(String[] args) {
        launch(args);
    }
}