package tp.g30.clases;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroHabitacion;
    private String tipo;
    private int cantidadHuespedes;
    private int cantidadCamaI;
    private int cantidadCamaD;
    private int cantidadCamaKS;
}
