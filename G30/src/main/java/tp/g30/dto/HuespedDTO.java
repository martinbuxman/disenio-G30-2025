
package tp.g30.dto;

import java.time.LocalDate;
import tp.g30.enums.CondicionIVA;
import tp.g30.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class HuespedDTO extends PersonaDTO {
    private long id = 0; 
    @NotBlank(message = "El teléfono no puede estar vacío.")
    private String telefono;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "La ocupación es obligatoria.")
    private String ocupacion;

    private CondicionIVA condicionIVA;
    public HuespedDTO(String nombre, String apellido, TipoDocumento tipoDocumento, String numeroDocumento) {
        super(apellido, nombre, tipoDocumento, numeroDocumento);
    }
    public HuespedDTO(String telefono, String email, String ocupacion, CondicionIVA condicionIVA,
                      String apellido, String nombre, TipoDocumento tipo_documento, long num_documento,
                      long cuit, LocalDate fecha_nacimiento, DireccionDTO direccion,  String nacionalidad) {
        super(apellido, nombre, tipo_documento, num_documento, cuit, fecha_nacimiento, direccion, nacionalidad);
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.condicionIVA =  condicionIVA;
    }
    public HuespedDTO(){
    }
    public HuespedDTO(HuespedDTO huesped){
        super(huesped.getApellido(), huesped.getNombre(), huesped.getTipoDocumento(), huesped.getNumDocumento(),
              huesped.getCUIT(), huesped.getFechaNacimiento(), huesped.getDIRECCION(), huesped.getNacionalidad());
        this.telefono = huesped.getTelefono();
        this.email = huesped.getEmail();
        this.ocupacion = huesped.getOcupacion();
    }
    

    public String getTelefono(){
        return  this.telefono;
    }
    public String getEmail(){
        return  this.email;
    }
    public CondicionIVA getCondicionIVA(){
        return this.condicionIVA;
    }
    public String getOcupacion(){
        return  this.ocupacion;
    }
    public DireccionDTO getDIRECCION(){
        return super.getDireccion();
    }
    public TipoDocumento getTipoDocumento(){
        return super.getTipo_documento();
    }
    public long getNumDocumento(){
        return super.getNum_documento();
    }
    public long getCUIT(){
        return super.getCuit();
    }
    public LocalDate getFechaNacimiento(){
        return super.getFecha_nacimiento();
    }
    public String getApellido(){
        return super.getApellido();
    }
    public String getNombre(){
        return super.getNombre();
    }
    public String getNacionalidad(){
        return super.getNacionalidad();
    }
    
    //SETTERS
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setOcupacion(String ocupacion){
        this.ocupacion = ocupacion;
    }   
    public void setDireccion(DireccionDTO direccion){
        super.setDireccion(direccion);
    }
    public void setCondicionIVA(CondicionIVA condicionIVA){
        this.condicionIVA = (condicionIVA != null) ? condicionIVA : CondicionIVA.CONSUMIDOR_FINAL;
    }

}
