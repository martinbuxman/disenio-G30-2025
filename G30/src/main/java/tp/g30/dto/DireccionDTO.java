package tp.g30.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class DireccionDTO {
    
    // CAMPOS OBLIGATORIOS (requieren data)
    @NotBlank(message = "La calle es obligatoria.")
    private String calle;
    
    // Es un primitivo (int), pero Spring lo recibe del JSON. Usamos @Min.
    // Para validar que no sea 0, si es que 0 no es un número de calle válido. 
    // Si permites 0 como valor si no se ingresa, la validación se complica.
    // Asumiremos que el número de calle debe ser positivo.
    @Min(value = 1, message = "El número de calle debe ser mayor a 0.")
    private int numero;
    
    @NotNull(message = "El Código Postal es obligatorio.")
    private Integer codigoPostal; // Debe ser Integer para que @NotNull funcione si es omitido
    
    @NotBlank(message = "La localidad es obligatoria.")
    private String localidad;
    
    @NotBlank(message = "La provincia es obligatoria.")
    private String provincia;
    
    @NotBlank(message = "El país es obligatorio.")
    private String pais;

    // CAMPOS OPCIONALES (departamento y piso)
    private String departamento;
    
    // No requiere anotación si el valor por defecto se maneja en el constructor/setter
    // Si el JSON no lo envía, se inicializa a 0 si usas int.
    @Min(value = 0, message = "El piso no puede ser negativo.")
    private int piso; 

    // Constructor completo
    public DireccionDTO(String calle, int numero, String departamento, int piso, Integer codigoPostal, String localidad, String provincia, String pais) {
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
        this.piso = piso;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
    }
    
    public DireccionDTO() {
        this.numero = 0; 
        this.piso = 0; 
    }
    
    // Getters y Setters

    // GETTERS 
    public String getCalle() { return calle; }
    public int getNumero() { return numero; }
    public Integer getCodigoPostal() { return codigoPostal; }
    public String getLocalidad() { return localidad; }
    public String getProvincia() { return provincia; }
    public String getPais() { return pais; }
    public String getDepartamento() { return departamento; }
    public int getPiso() { return piso; }

    // SETTERS
    public void setCalle(String calle) { this.calle = calle; }
    public void setNumero(int numero) { this.numero = numero; }
    public void setCodigoPostal(Integer cp) { this.codigoPostal = cp; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public void setPais(String pais) { this.pais = pais; }

    public void setDepartamento(String depa) { this.departamento = depa; }

    public void setPiso(int piso) { this.piso = piso; }
}