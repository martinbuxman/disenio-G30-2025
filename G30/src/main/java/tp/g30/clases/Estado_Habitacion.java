package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import tp.g30.enums.Estado;

@Entity
@Data
public class Estado_Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Estado estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
