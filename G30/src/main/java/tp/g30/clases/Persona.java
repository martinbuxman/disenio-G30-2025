/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;
import tp.g30.enums.TipoDocumento;
/**
 *
 * @author Cesar
 */
public class Persona {
    private String apellido;
    private String nombre;
    private TipoDocumento tipo_documento;
    private int num_documento;
    private int cuit;
    private LocalDate fecha_nacimiento;
    private Direccion direccion;
    private String nacionalidad;

    public Persona(String apellido, String nombre, TipoDocumento tipo_documento, int num_documento, int cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.cuit = cuit;
        this.fecha_nacimiento = fecha_nacimiento;
        this.direccion = direccion;
        this.nacionalidad = nacionalidad;
    }
    
    //GETTERS
    public String getApellido(){
        return this.apellido;
    }
    public String getNombre(){
        return this.nombre;
    }
    public TipoDocumento getTipoDocumento(){
        return this.tipo_documento;
    }
    public int getNumDocumento(){
        return this.num_documento;
    }
    public int getCUIT(){
        return this.cuit;
    }
    public LocalDate getFechaNacimiento(){
        return this.fecha_nacimiento;
    }
    public Direccion getDIRECCION(){
        return this.direccion;
    }
    public String getNacionalidad(){
        return this.nacionalidad;
    }
}
