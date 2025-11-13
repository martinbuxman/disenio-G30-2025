/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.dto;

import java.time.LocalDate;
import tp.g30.clases.Direccion;
import tp.g30.clases.Huesped;
import tp.g30.enums.CondicionIVA;
import tp.g30.enums.TipoDocumento;

/**
 *
 * @author juanc
 */
public class HuespedDTO extends PersonaDTO{
    
    private String telefono;
    private String email;
    private String ocupacion;
    private CondicionIVA condicionIVA;
    
    //Constructores
    public HuespedDTO(String nombre, String apellido, TipoDocumento tipoDocumento, String numeroDocumento) {
        super(apellido, nombre, tipoDocumento, numeroDocumento);
    }
    public HuespedDTO(String telefono, String email, String ocupacion, CondicionIVA condicionIVA,
                      String apellido, String nombre, TipoDocumento tipo_documento, long num_documento,
                      long cuit, LocalDate fecha_nacimiento, Direccion direccion,  String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.condicionIVA =  condicionIVA;
    }
    public HuespedDTO(){
        this.condicionIVA = CondicionIVA.CONSUMIDOR_FINAL;
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
    public CondicionIVA getCondicionIVA(){
        return this.condicionIVA;
    }
    public String getOcupacion(){
        return  this.ocupacion;
    }
    public Direccion getDIRECCION(){
        return super.getDireccion();
    }
    public TipoDocumento getTipoDocumento(){
        return super.getTipo_documento();
    }
    public long getNumDocumento(){
        return super.getNum_documento();
    }
    public long getCUIT(){
        return super.getCuit();
    }
    public LocalDate getFechaNacimiento(){
        return super.getFecha_nacimiento();
    }
    public String getApellido(){
        return super.getApellido();
    }
    public String getNombre(){
        return super.getNombre();
    }
    public String getNacionalidad(){
        return super.getNacionalidad();
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
    public void setDireccion(Direccion direccion){
        super.setDireccion(direccion);
    }
    public void setCondicionIVA(CondicionIVA condicionIVA){
        this.condicionIVA = (condicionIVA != null) ? condicionIVA : CondicionIVA.CONSUMIDOR_FINAL;
    }

}
