package tp.g30.clases;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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

    @OneToMany
    @JoinColumn(name = "habitacion_id")
    private List<Estado_Habitacion> historiaEstados;

    @ManyToMany(mappedBy = "listaHabitaciones")
    private List<Reserva> reservas;
}
