package tp.g30.clases;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Tarjeta extends Pago {
    private int numeroTarjeta;
    private String nombreTitular;
    private LocalDate fechaVencimiento;
    private String redDePago;
}
