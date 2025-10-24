package tp.g30.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UsuarioDaoArchivos {

    private final String archivo = "src/main/java/tp/desarrollo/db/usuarios.csv";

    public boolean validarCredenciales(String usuarioBuscado, String contraseniaBuscada) {

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.startsWith("#")) continue;

                String[] datos = linea.split(";");
                if (datos.length < 3) continue;

                String usuario = datos[0].trim();
                String contrasenia = datos[1].trim();
                boolean activo = Boolean.parseBoolean(datos[2].trim());

                if (usuario.equalsIgnoreCase(usuarioBuscado)) {
                    if (!activo) {
                        System.out.println("El usuario está inactivo.");
                        return false;
                    }
                    return contrasenia.equals(contraseniaBuscada);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer usuarios.csv: " + e.getMessage());
        }
        return false;
    }
}
