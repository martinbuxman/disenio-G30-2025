/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.dto;

import java.time.LocalDate;
import tp.g30.clases.Direccion;
import tp.g30.clases.Huesped;
import tp.g30.enums.TipoDocumento;

/**
 *
 * @author juanc
 */
public class HuespedDTO extends PersonaDTO{
    
    private String telefono;
    private String email;
    private String ocupacion;
    
    //Constructores
    public HuespedDTO(String nombre, String apellido, TipoDocumento tipoDocumento, String numeroDocumento) {
        super(apellido, nombre, tipoDocumento, numeroDocumento);
    }
    public HuespedDTO(String telefono, String email, String ocupacion, String apellido, String nombre, TipoDocumento tipo_documento, int num_documento, long cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
    }
    public HuespedDTO(Huesped huesped){
        super(huesped.getApellido(), huesped.getNombre(), huesped.getTipoDocumento(), huesped.getNumDocumento(),
              huesped.getCUIT(), huesped.getFechaNacimiento(), huesped.getDIRECCION(), huesped.getNacionalidad());
        this.telefono = huesped.getTelefono();
        this.email = huesped.getEmail();
        this.ocupacion = huesped.getOcupacion();
    }
    
    //GETTERS
    public String getTelefono(){
        return  this.telefono;
    }
    public String getEmail(){
        return  this.email;
    }
    public String getOcupacion(){
        return  this.ocupacion;
    }
    
    //SETTERS
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setOcupacion(String ocupacion){
        this.ocupacion = ocupacion;
    }
}
