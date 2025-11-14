package tp.g30.clases;

import jakarta.persistence.Entity;
import lombok.Data;

import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Efectivo extends Pago {
	private double montoDePago;
}
