package tp.g30.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data   
public class EstadiaDTO {
    private HabitacionDTO habitacion;
    private List<HuespedDTO> huespedes;
    private LocalDate fecha_check_in;
}
