package tp.g30.excepciones; // Crea este paquete si no existe

public class DocumentoDuplicadoException extends RuntimeException {
    public DocumentoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}