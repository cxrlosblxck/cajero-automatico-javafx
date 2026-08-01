package CajeroATM;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.image.Image;

public class ImageLoader {

    public static Image cargarImagen(String nombreArchivo) {
        // 1. Buscar en classpath (para JAR o cuando resources está en el classpath)
        try {
            String rutaClasspath = "images/" + nombreArchivo;
            InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(rutaClasspath);
            if (is != null) {
                System.out.println("✅ Imagen cargada desde classpath");
                return new Image(is);
            }
        } catch (Exception e) {
            /* ignorar */ }

        // 2. Buscar en el sistema de archivos: subir niveles desde el directorio de
        // trabajo
        try {
            String userDir = System.getProperty("user.dir");
            Path base = Paths.get(userDir);
            while (base != null) {
                Path testPath = base.resolve("resources/images/" + nombreArchivo);
                File archivo = testPath.toFile();
                if (archivo.exists()) {
                    System.out.println("✅ Imagen cargada desde archivo: " + testPath.toString());
                    return new Image(new FileInputStream(archivo));
                }
                base = base.getParent();
                if (base == null)
                    break;
            }
        } catch (Exception e) {
            /* ignorar */ }

        // 3. Buscar relativo a la ubicación de la clase (para JARs o ejecución desde
        // directorio de clases)
        try {
            URL location = ImageLoader.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                Path path = Paths.get(location.toURI());
                // Si el location es un directorio (clases sueltas) o un JAR
                Path base = path.getParent(); // podría ser el directorio raíz del proyecto
                while (base != null) {
                    Path testPath = base.resolve("resources/images/" + nombreArchivo);
                    File archivo = testPath.toFile();
                    if (archivo.exists()) {
                        System.out.println("✅ Imagen cargada desde ubicación de clase: " + testPath.toString());
                        return new Image(new FileInputStream(archivo));
                    }
                    base = base.getParent();
                }
            }
        } catch (Exception e) {
            /* ignorar */ }

        System.err.println("❌ Imagen no encontrada: " + nombreArchivo);
        return null;
    }
}