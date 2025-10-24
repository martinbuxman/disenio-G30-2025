/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.dto;

import java.time.LocalDate;
import tp.g30.clases.Direccion;
import tp.g30.enums.TipoDocumento;

/**
 *
 * @author juanc
 */
public class PersonaDTO {
    private String apellido;
    private String nombre;
    private TipoDocumento tipo_documento;
    private int num_documento;
    private long cuit;
    private LocalDate fecha_nacimiento;
    private Direccion direccion;
    private String nacionalidad;

    public PersonaDTO(String apellido, String nombre, TipoDocumento tipo_documento, String num_documento) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_documento = tipo_documento;
        this.num_documento = Integer.parseInt(num_documento);
    }
    public PersonaDTO(String apellido, String nombre, TipoDocumento tipo_documento, int num_documento, long cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.cuit = cuit;
        this.fecha_nacimiento = fecha_nacimiento;
        this.direccion = direccion;
        this.nacionalidad = nacionalidad;
    }
    public String getApellido() {
        return apellido;
    }
    public String getNombre() {
        return nombre;
    }
    public TipoDocumento getTipo_documento() {
        return tipo_documento;
    }
    public int getNum_documento() {
        return num_documento;
    }
    public long getCuit() {
        return this.cuit;
    }
    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }
    public Direccion getDireccion() {
        return direccion;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTipo_documento(TipoDocumento tipo_documento) {
        this.tipo_documento = tipo_documento;
    }
    public void setNum_documento(int num_documento) {
        this.num_documento = num_documento;
    }
    public void setCuit(long cuit) {
        this.cuit = cuit;
    }
    public void setFecha_nacimiento(LocalDate fecha) {
        this.fecha_nacimiento = fecha;
    }
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}
