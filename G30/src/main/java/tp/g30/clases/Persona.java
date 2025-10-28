/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import tp.g30.enums.TipoDocumento;
/**
 *
 * @author Cesar
 */
@Entity
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apellido;
    private String nombre;
    private TipoDocumento tipo_documento;
    private long num_documento;
    private long cuit;
    private LocalDate fecha_nacimiento;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    private String nacionalidad;

    public Persona(String apellido, String nombre, TipoDocumento tipo_documento, long num_documento, long cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
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
    public long getNumDocumento(){
        return this.num_documento;
    }
    public long getCUIT(){
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
