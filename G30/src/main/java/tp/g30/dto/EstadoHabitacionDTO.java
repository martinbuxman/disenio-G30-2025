package tp.g30.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EstadoHabitacionDTO {
    private String estado; 
    private LocalDate fechaInicio; 
    private LocalDate fechaFin;
    public EstadoHabitacionDTO(String estado, LocalDate fechaInicio, LocalDate fechaFin) {
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}