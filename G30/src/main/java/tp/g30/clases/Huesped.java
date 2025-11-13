/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import tp.g30.enums.TipoDocumento;

/**
 *
 * @author Cesar
 */
@Entity
public class Huesped extends Persona{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telefono;
    private String email;
    private String ocupacion;

    public Huesped(String telefono, String email, String ocupacion, String apellido, String nombre, TipoDocumento tipo_documento, int num_documento, int cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
    }
    public Huesped() {
        super();
    }
    @Override
    public String toString() {
        return this.getApellido() + ", " + this.getNombre();
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
