/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import tp.g30.enums.TipoDocumento;
/**
 *
 * @author Cesar
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apellido;
    private String nombre;
    @JsonProperty("tipo_documento")
    private TipoDocumento tipo_documento;
    @JsonProperty("num_documento")
    private long num_documento;
    @JsonProperty("cuit")
    private long cuit = 0;
    @JsonProperty("fecha_nacimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
    public Persona() {
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
    
    //SETTERS
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTipoDocumento(TipoDocumento tipo_documento) {
        this.tipo_documento = tipo_documento;
    }
    public void setNumDocumento(long num_documento) {
        this.num_documento = num_documento;
    }
    public void setCUIT(long cuit) {
        this.cuit = cuit;
    }
    public void setFechaNacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}