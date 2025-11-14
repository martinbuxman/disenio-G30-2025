
package tp.g30.clases;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Entity;
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Cheque extends Pago {
    private Long numeroCheque;
    private String bancoEmisor;
    private String tipo;
    private LocalDate fechaCobro;
}
