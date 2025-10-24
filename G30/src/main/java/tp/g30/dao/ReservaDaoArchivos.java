package tp.g30.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import tp.g30.clases.Huesped;

public class ReservaDaoArchivos {
    private static final String RUTA_ARCHIVO_RESERVAS = "C:\\Users\\Maria Sol\\Desarrollo\\reservas_ejemplo.csv";

    /*
      Verifica si un huésped tiene al menos una reserva en el sistema.
      Lee el archivo de reservas y compara el tipo y número de documento del huésped.
     */
    public boolean tieneReservas(Huesped huesped) {
        
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO_RESERVAS))) {
            String linea;
            br.readLine(); // Salta la línea del encabezado

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                // Asegura que la línea tiene suficientes columnas para evitar errores
                if (datos.length > 2) {
                    String tipoDocArchivo = datos[1].trim(); 
                    int numDocArchivo = Integer.parseInt(datos[2].trim());

                    // Compara si la reserva pertenece al huesped buscado
                    if (huesped.getTipoDocumento().toString().equalsIgnoreCase(tipoDocArchivo) &&
                        huesped.getNumDocumento() == numDocArchivo) {
                        return true;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer el archivo de reservas: " + e.getMessage());
        }
        
        return false;
    }
}
