/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanc
 */
@Entity
public class Reserva {
    private int id;
    private List<Habitacion> listaHabitaciones = new ArrayList<>();
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    Huesped huesped;

    @Data


}
