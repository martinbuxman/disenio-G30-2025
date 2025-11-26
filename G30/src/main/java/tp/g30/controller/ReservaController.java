package tp.g30.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tp.g30.dto.HabitacionDTO;
import tp.g30.dto.ReservaDTO;
import tp.g30.gestores.Gestor_Reserva;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final Gestor_Reserva gestorReserva;

     public ReservaController(Gestor_Reserva gestorReserva) {
         this.gestorReserva = gestorReserva;
    }

     @GetMapping("/disponibilidad")
     public List<HabitacionDTO> getDisponibilidad(
             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return gestorReserva.mostrarEstadoHabitaciones(fechaDesde, fechaHasta);
     }
    
    @PostMapping("/confirmar")
     public ResponseEntity<?> confirmarReserva(@RequestBody ReservaDTO reserva) {
         try {
            Long idReservaPrincipal = gestorReserva.confirmarReserva(reserva);
             return ResponseEntity.ok(Map.of(
                "idReserva", idReservaPrincipal, 
                "message", "Reserva guardada con éxito."
            ));
        } catch (Exception e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
             return ResponseEntity.internalServerError().body(
             Map.of("error", "Error interno del servidor al procesar la reserva: " + e.getMessage())
            );
         }
     }
}