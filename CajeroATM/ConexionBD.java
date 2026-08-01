package CajeroATM;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static final Properties propiedades = new Properties();

    static {
        try {
            try (FileInputStream fis = new FileInputStream("resources/config.properties")) {
                propiedades.load(fis);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Fallo en la inicialización de la conexión", e);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = propiedades.getProperty("db.url");
        String user = propiedades.getProperty("db.user");
        String password = propiedades.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}