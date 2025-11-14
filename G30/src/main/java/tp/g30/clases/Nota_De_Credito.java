package tp.g30.clases;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Nota_De_Credito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroNota;

    @OneToOne
    private Responsable_de_pago responsableDePago;

    private double importeNeto;
    private double importeTotal;
    private double iva;

    @OneToMany
    @JoinColumn(name = "nota_de_credito_id")
    private List<Factura> facturas;
}
