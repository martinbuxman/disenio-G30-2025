/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author juanc
 */
@Entity
@Data
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany
    @JoinTable(
        name = "reserva_habitacion", 
        joinColumns = @JoinColumn(name = "reserva_id"), 
        inverseJoinColumns = @JoinColumn(name = "habitacion_id") 
    )
    private List<Habitacion> listaHabitaciones;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huespedPrincipal;

}
