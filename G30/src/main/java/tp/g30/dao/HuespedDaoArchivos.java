package tp.g30.dao;
import tp.g30.interfaces.HuespedDAO;
import tp.g30.enums.TipoDocumento;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import tp.g30.clases.Huesped;
import tp.g30.dto.HuespedDTO;
import org.springframework.stereotype.Repository;
@Repository
public class HuespedDaoArchivos implements HuespedDAO{
    @Override
    public void guardarHuesped(Huesped huesped) {
    }
    public void modificar_huesped(HuespedDTO huespedOriginal, HuespedDTO huespedModificado) {
    File archivoOriginal = new File("src/main/java/tp/desarrollo/db/huespedes.csv");
    File archivoTmp = new File("src/main/java/tp/desarrollo/db/huespedes_tmp.csv");

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoOriginal));
         BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTmp))) {

        String linea;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while ((linea = reader.readLine()) != null) {
            String[] campos = linea.split(",");

            String apellido = campos[3];
            String nombre = campos[4];
            String tipoDoc = campos[5];
            String nroDoc = campos[6];

            if (apellido.equalsIgnoreCase(huespedOriginal.getApellido().trim())
                    && nombre.equalsIgnoreCase(huespedOriginal.getNombre().trim())
                    && tipoDoc.equalsIgnoreCase(huespedOriginal.getTipo_documento().toString().trim())
                    && nroDoc.equals(String.valueOf(huespedOriginal.getNum_documento()))) {

                String nuevaLinea = String.join(",",
                    huespedModificado.getTelefono(),
                    huespedModificado.getEmail(),
                    huespedModificado.getOcupacion(),
                    huespedModificado.getApellido(),
                    huespedModificado.getNombre(),
                    huespedModificado.getTipo_documento().toString(),
                    String.valueOf(huespedModificado.getNum_documento()),
                    String.valueOf(huespedModificado.getCuit()),
                    huespedModificado.getFecha_nacimiento().format(formatter),
                    huespedModificado.getDireccion().getCalle(),
                    String.valueOf(huespedModificado.getDireccion().getNumero()),
                    String.valueOf(huespedModificado.getDireccion().getPiso()),
                    String.valueOf(huespedModificado.getDireccion().getDepartamento()),
                    String.valueOf(huespedModificado.getDireccion().getCodigoPostal()),
                    huespedModificado.getDireccion().getLocalidad(),
                    huespedModificado.getDireccion().getProvincia(),
                    huespedModificado.getDireccion().getPais(),
                    huespedModificado.getNacionalidad()
                );
                writer.write(nuevaLinea);
            } else {
                writer.write(linea);
            }
            writer.newLine();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    // Reemplazo seguro con NIO
    try {
        Files.deleteIfExists(archivoOriginal.toPath());
        Files.move(archivoTmp.toPath(), archivoOriginal.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    public void registrar_huesped(HuespedDTO huesped){
        String archivo = "src/main/java/tp/desarrollo/db/huespedes.csv";
        //Lógica para registrar un nuevo huésped en el archivo CSV
        try (java.io.FileWriter fw = new java.io.FileWriter(archivo, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fechaNacimiento = huesped.getFecha_nacimiento().format(formatter);
            tp.g30.clases.Direccion direccion = huesped.getDireccion();
            String nuevaLinea = String.join(",",
                huesped.getTelefono(),
                huesped.getEmail(),
                huesped.getOcupacion(),
                huesped.getApellido(),
                huesped.getNombre(),
                huesped.getTipo_documento().toString(),
                String.valueOf(huesped.getNum_documento()),
                String.valueOf(huesped.getCuit()),
                fechaNacimiento,
                direccion.getCalle(),
                String.valueOf(direccion.getNumero()),
                String.valueOf(direccion.getPiso()),
                String.valueOf(direccion.getDepartamento()),
                String.valueOf(direccion.getCodigoPostal()),
                direccion.getLocalidad(),
                direccion.getProvincia(),
                direccion.getPais(),
                huesped.getNacionalidad()
            ) + "\n";
            fw.write(nuevaLinea);
            System.out.println("Huésped registrado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean existe_documento(TipoDocumento tipoDocumento, long numeroDocumento){
        String archivo = "src/main/java/tp/desarrollo/db/huespedes.csv";
        String linea;
        boolean existe = false;
        //Lógica para buscar huéspedes en el archivo CSV
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            linea = br.readLine(); // salta el encabezado
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String tipoDoc = datos[5];
                int numDoc = Integer.parseInt(datos[6]);
                if(tipoDocumento.toString().equalsIgnoreCase(tipoDoc) && numeroDocumento == numDoc){
                    System.out.println("El documento ya existe en el sistema.");
                    existe = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existe;
    }
    
    public List<Huesped> buscar_huespedes(HuespedDTO huesped){  
        return null;
    }
    
    public void eliminar(Huesped huespedAEliminar) {
        String archivo = "src/main/java/tp/desarrollo/db/huespedes.csv";
        File archivoOriginal = new File(archivo);
        // Creamos un archivo temporal en el mismo directorio que el original.
        File archivoTemporal = new File(archivoOriginal.getParent(), "temp_huespedes.csv");
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemporal))) {

            String linea;
            
            if ((linea = br.readLine()) != null) {
                bw.write(linea + System.lineSeparator());
            }

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // Asumimos que las columnas 5 y 6 contienen el tipo y nro de documento.
                String tipoDocArchivo = datos[5].trim();
                int numDocArchivo = Integer.parseInt(datos[6].trim());

                // Si la línea actual NO corresponde al huesped a eliminar la escribimos en el temporal.
                if (!(huespedAEliminar.getTipoDocumento().toString().equalsIgnoreCase(tipoDocArchivo) &&
                      huespedAEliminar.getNumDocumento() == numDocArchivo)) {
                    bw.write(linea + System.lineSeparator());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error durante la operación de eliminación de huésped: " + e.getMessage());
            return;
        }

        // Una vez que el archivo temporal está completo reemplazamos el original.
        if (archivoOriginal.delete()) {
            if (!archivoTemporal.renameTo(archivoOriginal)) {
                System.err.println("Error crítico: No se pudo renombrar el archivo temporal. Los datos originales pueden estar en 'temp_huespedes.csv'");
            }
        } else {
            System.err.println("Error crítico: No se pudo borrar el archivo original.");
        }
    }
}
