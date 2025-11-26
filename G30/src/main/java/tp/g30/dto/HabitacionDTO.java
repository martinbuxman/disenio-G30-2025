package tp.g30.dto;

import java.util.List;

import lombok.Data;

@Data
public class HabitacionDTO {
        private Long numeroHabitacion;
        private String tipo;
        private List<EstadoHabitacionDTO> historiaEstados;
        
        public HabitacionDTO() {
        }
        public HabitacionDTO(Long numeroHabitacion, List<EstadoHabitacionDTO> historiaEstados) {
            this.numeroHabitacion = numeroHabitacion;
            this.historiaEstados = historiaEstados;
        }
        public HabitacionDTO(Long numeroHabitacion, String tipo, List<EstadoHabitacionDTO> historiaEstados) {
        this.numeroHabitacion = numeroHabitacion;
        this.tipo = tipo;
        this.historiaEstados = historiaEstados;
    }
}
