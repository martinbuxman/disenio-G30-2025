package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ReservaHabitacion {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne 
    @JoinColumn(name = "habitacion_fk", nullable = false)
    private Habitacion habitacion;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
        @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

}
