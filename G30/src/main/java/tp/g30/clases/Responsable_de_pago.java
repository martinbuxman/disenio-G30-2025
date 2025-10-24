/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;
import tp.g30.enums.TipoDocumento;
/**
 *
 * @author juanc
 */
public class Responsable_de_pago extends Persona {
    
    public Responsable_de_pago(String apellido, String nombre, TipoDocumento tipo_documento, int num_documento, int cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
    }
    
}
