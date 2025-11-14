/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.dto;

import java.time.LocalDate;
import tp.g30.clases.Direccion;
import tp.g30.enums.TipoDocumento;
import jakarta.validation.Valid; // Para validar campos anidados
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public class PersonaDTO {
    
    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres.")
    private String apellido;
    
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres.")
    private String nombre;
    
    @NotNull(message = "El Tipo de Documento es obligatorio.")
    private TipoDocumento tipo_documento;
    
    // Usamos @NotNull para tipos primitivos long/int que se esperan como objetos
    // Spring puede tener problemas mapeando 0L a null si se omite, pero @NotNull ayuda.
    @NotNull(message = "El número de documento es obligatorio.")
    private Long num_documento; // Cambiar a Long para permitir @NotNull si es null desde JSON

    // CUIL/CUIT no es obligatorio, por lo que no lleva @NotNull
    private Long cuit; // Cambiar a Long
    
    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate fecha_nacimiento;
    
    // **CLAVE:** Para que Spring valide los campos dentro de 'Direccion'
    @Valid 
    @NotNull(message = "La dirección es obligatoria.")
    private DireccionDTO direccion; // Asume que la clase Direccion ya tiene sus propias anotaciones
    
    @NotBlank(message = "La nacionalidad es obligatoria.")
    private String nacionalidad;

    // ... (restantes constructores, getters y setters sin cambios)
    
    // CLAVE: Cambia los tipos primitivos long a Long en los campos y setters

    public PersonaDTO(String apellido, String nombre, TipoDocumento tipo_documento, String num_documento) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_documento = tipo_documento;
        this.num_documento = Long.parseLong(num_documento);
    }
    public PersonaDTO(String apellido, String nombre, TipoDocumento tipo_documento, long num_documento, long cuit, LocalDate fecha_nacimiento, DireccionDTO direccion, String nacionalidad) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.cuit = cuit;
        this.fecha_nacimiento = fecha_nacimiento;
        this.direccion = direccion;
        this.nacionalidad = nacionalidad;
    }
    public PersonaDTO() {
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
    public long getNum_documento() {
        return num_documento;
    }
    public long getCuit() {
        return this.cuit;
    }
    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }
    public DireccionDTO getDireccion() {
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
    public void setNum_documento(Long num_documento) {
        this.num_documento = num_documento;
    }
    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }
    public void setFecha_nacimiento(LocalDate fecha) {
        this.fecha_nacimiento = fecha;
    }
    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}
