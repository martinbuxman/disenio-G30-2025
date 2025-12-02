package tp.g30.clases;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(mappedBy = "reserva", cascade = jakarta.persistence.CascadeType.ALL)
    private List<ReservaHabitacion> listaHabitacionesRerservadas;
    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huespedPrincipal;

}
