package tp.g30.clases;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Tarjeta_Credito extends Tarjeta {
    
}
