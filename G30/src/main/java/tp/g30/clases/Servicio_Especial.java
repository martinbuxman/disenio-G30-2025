package tp.g30.clases;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class Servicio_Especial extends Consumo {
    private String descripcion;
}
