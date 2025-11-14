package tp.g30.clases;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Divisa {
    @Id
    private String tipo;
    private double cotizacion;
}
