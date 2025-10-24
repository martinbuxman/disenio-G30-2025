package tp.g30.excepciones;

/*
  Excepción personalizada que se lanza cuando se intenta eliminar un huésped
  que tiene reservas asociadas. Esto nos permite manejar errores de negocio
  de forma limpia.
 */
public class HuespedConReservasExcepcion extends Exception {
    public HuespedConReservasExcepcion(String message) {
        super(message);
    }
}
