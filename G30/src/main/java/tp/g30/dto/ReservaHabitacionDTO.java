package tp.g30.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class ReservaHabitacionDTO {
    private HabitacionDTO habitacion;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
}
