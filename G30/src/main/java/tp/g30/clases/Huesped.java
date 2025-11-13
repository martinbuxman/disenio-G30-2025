/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import tp.g30.enums.TipoDocumento;

import jakarta.validation.constraints.*;
import tp.g30.enums.CondicionIVA;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;



/**
 *
 * @author Cesar
 */
@Entity
public class Huesped extends Persona{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[0-9\\-\\s]+$", message = "El teléfono solo puede contener números, espacios y guiones")
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    @Email(message = "Formato de correo electrónico inválido")
    private String email;
    @NotBlank(message = "La ocupación es obligatoria")
    private String ocupacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicionIVA condicionIVA = CondicionIVA.CONSUMIDOR_FINAL; // Valor por defecto

    public Huesped(String telefono, String email, String ocupacion, CondicionIVA condicionIVA,
                   String apellido, String nombre, TipoDocumento tipo_documento, int num_documento,
                   int cuit, LocalDate fecha_nacimiento, Direccion direccion, String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.condicionIVA = (condicionIVA != null) ? condicionIVA : CondicionIVA.CONSUMIDOR_FINAL;
    }
    public Huesped() {
        super();
         this.condicionIVA = CondicionIVA.CONSUMIDOR_FINAL;
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
    public CondicionIVA getCondicionIVA() { return condicionIVA; }
    
    
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
    public void setCondicionIVA(CondicionIVA condicionIVA) {
        this.condicionIVA = (condicionIVA != null) ? condicionIVA : CondicionIVA.CONSUMIDOR_FINAL;
    }
}
