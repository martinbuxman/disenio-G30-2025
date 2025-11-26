package tp.g30.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tp.g30.dto.HabitacionDTO;
import tp.g30.gestores.Gestor_Habitacion;


@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final Gestor_Habitacion gestorHabitacion;
    
    public HabitacionController(Gestor_Habitacion gestorHabitacion) {
        this.gestorHabitacion = gestorHabitacion;
    }

}