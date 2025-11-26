package tp.g30.clases;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

    @OneToMany (cascade = CascadeType.ALL)
    @JoinColumn(name = "habitacion_id")
    private List<EstadoHabitacion> historiaEstados;

}
