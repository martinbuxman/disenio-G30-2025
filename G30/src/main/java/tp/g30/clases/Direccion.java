/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import jakarta.persistence.Embeddable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 *
 * @author Cesar
 */
@Entity
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    
    String calle;
    int numero;
    String departamento;
    int piso;
    Integer codigoPostal;
    String localidad;
    String provincia;
    String pais;

    public Direccion(String calle, int numero, String departamento, int piso, Integer codigoPostal, String localidad, String provincia, String pais) {
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
        this.piso = piso;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
    }
    public Direccion()
    {}
    //Getters1
    public String getCalle() {
        return calle;
    }
    public int getNumero() {
        return numero;
    }
    public Integer getCodigoPostal() {
        return codigoPostal;
    }
    public String getLocalidad() {
        return localidad;
    }
    public String getProvincia() {
        return provincia;
    }
    public String getPais() {
        return pais;
    }
    public String getDepartamento() {
        return departamento;
    }
    public int getPiso() {
        return piso;
    }
    //Setters
    public void setCalle(String calle) {
        this.calle = calle;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public void setCodigoPostal(Integer cp) {
        this.codigoPostal = cp;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public void setDepartamento(String depa) {
        this.departamento = depa;
    }
    public void setPiso(int piso) {
        this.piso = piso;
    }
}
