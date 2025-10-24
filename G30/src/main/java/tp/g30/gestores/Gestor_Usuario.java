/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.gestores;

import tp.g30.dto.HuespedDTO;
import tp.g30.clases.Huesped;
import tp.g30.clases.Direccion;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import tp.g30.dao.HuespedDaoArchivos;
import tp.g30.dao.UsuarioDaoArchivos;
import tp.g30.enums.TipoDocumento;
import tp.g30.dao.ReservaDaoArchivos;
import tp.g30.excepciones.HuespedConReservasExcepcion;

/**
 *
 * @author juanc
 */

public class Gestor_Usuario{

    private HuespedDaoArchivos huespedDao;
    private UsuarioDaoArchivos usuarioDao;
    private ReservaDaoArchivos reservaDao;

    private final Scanner scanner = new Scanner(System.in);

    // --- CAMBIO: Constructor actualizado para recibir todas las dependencias necesarias ---
    public Gestor_Usuario(HuespedDaoArchivos huespedDao, UsuarioDaoArchivos usuarioDao, ReservaDaoArchivos reservaDao) {
        this.huespedDao = huespedDao;
        this.usuarioDao = usuarioDao;
        this.reservaDao = reservaDao;
    }

    public void modificar_huesped(Huesped huesped) {
        while (true) {
            System.out.println("\n¿Qué desea hacer con " + huesped.getNombre() + " " + huesped.getApellido() + "?");
            System.out.println("1- Modificar atributos del huésped");
            System.out.println("2- Dar de baja a este huésped (CU11)");
            System.out.println("3- Volver al menú principal");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    modificar_atributos(huesped);
                    return;
                case "2":
                    System.out.print("¡ATENCIÓN! Esta acción es permanente. ¿Está seguro que desea eliminar a este huésped? (S/N): ");
                    String confirmacion = scanner.nextLine().trim().toUpperCase();
                    if (confirmacion.equals("S")) {
                        try {
                            darBajaHuesped(huesped);
                            System.out.println("ÉXITO: El huésped ha sido eliminado correctamente.");
                            return;
                        } catch (HuespedConReservasExcepcion e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Operación cancelada.");
                    }
                    break;
                case "3":
                    System.out.println("Operación cancelada. Volviendo al menú principal.");
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }
    
    public void darBajaHuesped(Huesped huespedAEliminar) throws HuespedConReservasExcepcion {
        if (reservaDao.tieneReservas(huespedAEliminar)) {
            throw new HuespedConReservasExcepcion("El huésped no puede ser eliminado pues tiene reservas asociadas.");
        }
        huespedDao.eliminar(huespedAEliminar);
    }
    
    public void modificar_atributos(Huesped huesped) {
    HuespedDTO huespedOriginal = new HuespedDTO(huesped);
    HuespedDTO huespedModificado = new HuespedDTO(huesped);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    System.out.println("Para modificar un campo del Huesped ingrese el nuevo valor cuando se le pida");
    System.out.println("Si no quiere modificar algun valor, simplemente oprima (Enter) y se conservara el valor anterior:\n");

    Scanner scanner = new Scanner(System.in); // local, no se cierra
    String input;

    // Nombre
    System.out.print("Ingresar nuevo Nombre (" + huespedOriginal.getNombre() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setNombre(input.isEmpty() ? huespedOriginal.getNombre() : input);

    // Apellido
    System.out.print("Ingresar nuevo Apellido (" + huespedOriginal.getApellido() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setApellido(input.isEmpty() ? huespedOriginal.getApellido() : input);

    // Teléfono
    System.out.print("Ingresar nuevo Telefono (" + huespedOriginal.getTelefono() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setTelefono(input.isEmpty() ? huespedOriginal.getTelefono() : input);

    // Email
    System.out.print("Ingresar nuevo Email (" + huespedOriginal.getEmail() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setEmail(input.isEmpty() ? huespedOriginal.getEmail() : input);

    // Ocupación
    System.out.print("Ingresar nueva Ocupacion (" + huespedOriginal.getOcupacion() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setOcupacion(input.isEmpty() ? huespedOriginal.getOcupacion() : input);

    // --- Tipo Documento y Número Documento con validación de duplicados ---
    boolean repetir;
    do {
        repetir = false;

        // Tipo Documento
        System.out.print("Ingresar nuevo Tipo Documento (" + huespedOriginal.getTipo_documento() + "): ");
        input = scanner.hasNextLine() ? scanner.nextLine() : "";
        if (input.isEmpty()) {
            huespedModificado.setTipo_documento(huespedOriginal.getTipo_documento());
        } else {
            try {
                huespedModificado.setTipo_documento(TipoDocumento.valueOf(input.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Valor inválido, se conserva el original.");
                huespedModificado.setTipo_documento(huespedOriginal.getTipo_documento());
            }
        }

        // Número Documento
        System.out.print("Ingresar nuevo Numero Documento (" + huespedOriginal.getNum_documento() + "): ");
        input = scanner.hasNextLine() ? scanner.nextLine() : "";
        if (input.isEmpty()) {
            huespedModificado.setNum_documento(huespedOriginal.getNum_documento());
        } else {
            try {
                huespedModificado.setNum_documento(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, se conserva el original.");
                huespedModificado.setNum_documento(huespedOriginal.getNum_documento());
            }
        }

        // --- Validación de duplicados SOLO si cambió tipo o número ---
        if (!(huespedOriginal.getTipo_documento().equals(huespedModificado.getTipo_documento()) &&
            huespedOriginal.getNum_documento() == huespedModificado.getNum_documento())) {

            // Usamos tu función de búsqueda
            HuespedDTO busqueda = new HuespedDTO("", "", huespedModificado.getTipo_documento(),
                                                 String.valueOf(huespedModificado.getNum_documento()));
            List<Huesped> duplicados = huespedDao.buscar_huespedes(busqueda);

            if (!duplicados.isEmpty()) {
                System.out.println("¡CUIDADO! El tipo y número de documento ya existen en el sistema");
                System.out.println("1- ACEPTAR IGUALMENTE");
                System.out.println("2- CORREGIR");

                String opcion = scanner.hasNextLine() ? scanner.nextLine() : "2";
                if (opcion.equals("2")) {
                    repetir = true; // vuelve a pedir tipo y número
                }
            }
        }
    } while (repetir);

    // CUIT
    System.out.print("Ingresar nuevo CUIT (" + huespedOriginal.getCuit() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    if (input.isEmpty()) {
        huespedModificado.setCuit(huespedOriginal.getCuit());
    } else {
        try {
            huespedModificado.setCuit(Long.parseLong(input));
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se conserva el original.");
            huespedModificado.setCuit(huespedOriginal.getCuit());
        }
    }

    // Fecha de nacimiento
    System.out.print("Ingresar nueva Fecha Nacimiento (" + huespedOriginal.getFecha_nacimiento() + ") [yyyy-MM-dd]: ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    if (input.isEmpty()) {
        huespedModificado.setFecha_nacimiento(huespedOriginal.getFecha_nacimiento());
    } else {
        try {
            huespedModificado.setFecha_nacimiento(LocalDate.parse(input, formatter));
        } catch (Exception e) {
            System.out.println("Formato inválido, se conserva el original.");
            huespedModificado.setFecha_nacimiento(huespedOriginal.getFecha_nacimiento());
        }
    }

    // Dirección
    Direccion dir = new Direccion();

    System.out.print("Ingresar nueva Calle (" + huespedOriginal.getDireccion().getCalle() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    dir.setCalle(input.isEmpty() ? huespedOriginal.getDireccion().getCalle() : input);

    System.out.print("Ingresar nuevo Numero (" + huespedOriginal.getDireccion().getNumero() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    if (input.isEmpty()) {
        dir.setNumero(huespedOriginal.getDireccion().getNumero());
    } else {
        try {
            dir.setNumero(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se conserva el original.");
            dir.setNumero(huespedOriginal.getDireccion().getNumero());
        }
    }

    System.out.print("Ingresar nuevo Departamento (" + huespedOriginal.getDireccion().getDepartamento() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    dir.setDepartamento(input.isEmpty() ? huespedOriginal.getDireccion().getDepartamento() : input);

    System.out.print("Ingresar nuevo Piso (" + huespedOriginal.getDireccion().getPiso() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    if (input.isEmpty()) {
        dir.setPiso(huespedOriginal.getDireccion().getPiso());
    } else {
        try {
            dir.setPiso(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se conserva el original.");
            dir.setPiso(huespedOriginal.getDireccion().getPiso());
        }
    }

    System.out.print("Ingresar nuevo Codigo Postal (" + huespedOriginal.getDireccion().getCodigoPostal() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    if (input.isEmpty()) {
        dir.setCodigoPostal(huespedOriginal.getDireccion().getCodigoPostal());
    } else {
        try {
            dir.setCodigoPostal(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se conserva el original.");
            dir.setCodigoPostal(huespedOriginal.getDireccion().getCodigoPostal());
        }
    }

    System.out.print("Ingresar nueva Localidad (" + huespedOriginal.getDireccion().getLocalidad() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    dir.setLocalidad(input.isEmpty() ? huespedOriginal.getDireccion().getLocalidad() : input);

    System.out.print("Ingresar nueva Provincia (" + huespedOriginal.getDireccion().getProvincia() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    dir.setProvincia(input.isEmpty() ? huespedOriginal.getDireccion().getProvincia() : input);

    System.out.print("Ingresar nuevo Pais (" + huespedOriginal.getDireccion().getPais() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    dir.setPais(input.isEmpty() ? huespedOriginal.getDireccion().getPais() : input);

    huespedModificado.setDireccion(dir);

        // Nacionalidad
    System.out.print("Ingresar nueva Nacionalidad (" + huespedOriginal.getNacionalidad() + "): ");
    input = scanner.hasNextLine() ? scanner.nextLine() : "";
    huespedModificado.setNacionalidad(input.isEmpty() ? huespedOriginal.getNacionalidad() : input);

    // --- Guardar cambios ---
    huespedDao.modificar_huesped(huespedOriginal, huespedModificado);
    System.out.println("La operación ha culminado con éxito");
}

    private HuespedDTO ingresar_datos_huesped(){
        String apellido;
        String nombre;
        TipoDocumento tipoDocumento;
        String numeroDocumento;
        int cuit = -1;
        LocalDate fechaNacimiento;
        String calle;
        int numero;
        String departamento = "";
        int piso = 0;
        int codigoPostal;
        String localidad ;
        String provincia;
        String pais;
        String telefono;
        String email = "";
        String ocupacion ;
        String nacionalidad;
    //ingreso de datos por consola y validacion basica (usa el scanner de instancia)
        System.out.println("Ingrese el apellido del huésped:");

        apellido = scanner.nextLine();
        while (apellido.isBlank()) {
        System.out.print("Apellido no puede estar vacío. Ingrese apellido: ");
        apellido = scanner.nextLine();
        }

        System.out.println("Ingrese el nombre del huésped:");
        nombre = scanner.nextLine();
        while (nombre.isBlank()) {
        System.out.print("Nombre no puede estar vacío. Ingrese nombre: ");
        nombre = scanner.nextLine();
        }

        System.out.println("Ingrese el tipo de documento del huésped (DNI, LE, LC, PASAPORTE, OTRO):");
        while(true) {
            String tipoDocInput = scanner.nextLine().toUpperCase();
            try {
                tipoDocumento = TipoDocumento.valueOf(tipoDocInput);
                break; // Salir del bucle si la conversión es exitosa
            } catch (IllegalArgumentException e) {
                System.out.print("Tipo de documento inválido. Ingrese un tipo válido (DNI, LE, LC, PASAPORTE, OTRO): ");
            }
        }

        System.out.println("Ingrese el número de documento del huésped:");
        numeroDocumento = scanner.nextLine();
        while(numeroDocumento.isBlank()) {
        System.out.print("Número de documento no puede estar vacío. Ingrese número de documento: ");
        numeroDocumento = scanner.nextLine();
        }

        System.out.println("Ingrese el CUIT del huésped (opcional):");
        String cuitInput = scanner.nextLine();
        if (!cuitInput.isBlank()) {
            cuit = Integer.parseInt(cuitInput);
        }
        else {
            cuit = -1;
        }

        System.out.println("Ingrese la fecha de nacimiento del huésped (YYYY-MM-DD):");
        while(true) {
            String fechaInput = scanner.nextLine();
            try {
                fechaNacimiento = LocalDate.parse(fechaInput);
                if (fechaNacimiento.isAfter(LocalDate.now()) || fechaNacimiento.toString().isBlank()) {
                    System.out.print("Fecha de nacimiento inválida. Ingrese una fecha válida (YYYY-MM-DD): ");
                } else {
                    break; // Salir del bucle si la fecha es válida
                }
            } catch (Exception e) {
                System.out.print("Formato inválido. Ingrese la fecha de nacimiento en formato YYYY-MM-DD: ");
            }
        }
        System.out.println("Ingrese la calle del huésped:");
        calle = scanner.nextLine();
        while (calle.isBlank()) {
        System.out.print("Calle no puede estar vacío. Ingrese calle: ");
        calle = scanner.nextLine();
        }

        System.out.println("Ingrese el número de la calle del huésped:");
        String numeroInput = scanner.nextLine();
        while(true) {
            try {
                numero = Integer.parseInt(numeroInput);
                if (numero <= 0) {
                    System.out.print("Número inválido. Ingrese un número de calle válido: ");
                    numeroInput = scanner.nextLine();
                } else {
                    break; // Salir del bucle si el número es válido
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número de calle válido: ");
                numeroInput = scanner.nextLine();
            }
        }
        
        System.out.println("Ingrese el departamento del huésped (opcional):");
        departamento = scanner.nextLine();
        if(departamento.isBlank()){
            departamento = "0";
        }

        System.out.println("Ingrese el piso del huésped (opcional):");
        String pisoInput = scanner.nextLine();
        if (!pisoInput.isBlank()) {
            piso = Integer.parseInt(pisoInput);
        }
        else {
            piso = 0;
        }

        System.out.println("Ingrese el código postal del huésped:");
        String codigoPostalInput = scanner.nextLine();
        while(true) {
            try {
                codigoPostal = Integer.parseInt(codigoPostalInput);
                if (codigoPostal <= 0 || Integer.toString(codigoPostal).isBlank()) {
                    System.out.print("Código postal inválido. Ingrese un código postal válido: ");
                    codigoPostalInput = scanner.nextLine();
                } else {
                    break; // Salir del bucle si el código postal es válido
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un código postal válido: ");
                codigoPostalInput = scanner.nextLine();
            }
        }
        System.out.println("Ingrese la localidad del huésped:");
        localidad = scanner.nextLine();
        while (localidad.isBlank()) {
        System.out.print("Localidad no puede estar vacío. Ingrese localidad: ");    
        localidad = scanner.nextLine();
        }
        System.out.println("Ingrese la provincia del huésped:");
        provincia = scanner.nextLine();
        while(provincia.isBlank()) {
        System.out.print("Provincia no puede estar vacío. Ingrese provincia: ");
        provincia = scanner.nextLine();
        }

        System.out.println("Ingrese el país del huésped:");
        pais = scanner.nextLine();
        while(pais.isBlank()) {
        System.out.print("País no puede estar vacío. Ingrese país: ");
        pais = scanner.nextLine();
        }

        System.out.println("Ingrese el teléfono del huésped:");
        telefono = scanner.nextLine();
        while(telefono.isBlank()) {
        System.out.print("Teléfono no puede estar vacío. Ingrese teléfono: ");
        telefono = scanner.nextLine();
        }

        System.out.println("Ingrese el email del huésped (opcional):");
        email = scanner.nextLine();
        if(email.isBlank()){
            email = "-";
        }

        System.out.println("Ingrese la ocupación del huésped:");
        ocupacion = scanner.nextLine();
        while(ocupacion.isBlank()) {
        System.out.print("Ocupación no puede estar vacío. Ingrese ocupación: ");
        ocupacion = scanner.nextLine();
        }

        System.out.println("Ingrese la nacionalidad del huésped:");
        nacionalidad = scanner.nextLine();
        while(nacionalidad.isBlank()) {
        System.out.print("Nacionalidad no puede estar vacío. Ingrese nacionalidad: ");
        nacionalidad = scanner.nextLine();
        }

        HuespedDTO nuevoHuesped = new HuespedDTO(telefono, email, ocupacion, apellido, nombre, tipoDocumento, Integer.parseInt(numeroDocumento), cuit, fechaNacimiento, new Direccion(calle, numero, departamento, piso, codigoPostal, localidad, provincia, pais), nacionalidad);
        return nuevoHuesped;
    }
    
    public void dar_alta_huesped(){
        while(true){
        System.out.println("Ingrese los datos del nuevo huésped:");
        HuespedDTO nuevoHuesped = ingresar_datos_huesped();
        while(huespedDao.existe_documento(nuevoHuesped.getTipo_documento(), nuevoHuesped.getNum_documento())){
            System.out.println("¡CUIDADO! El tipo y número de documento ya existen en el sistema");
            System.out.println("Aceptar igualmente o Corregir (S/N):");
            String decision;
            decision = scanner.nextLine();
            if(decision.equalsIgnoreCase("S")){
                break;
            }
            else{
                System.out.println("Corrija los datos del huésped.");
                        System.out.println("Ingrese el tipo de documento del huésped (DNI, LE, LC, PASAPORTE, OTRO):");
                        while(true) {
                            String tipoDocInput = scanner.nextLine().toUpperCase();
                            try {
                                nuevoHuesped.setTipo_documento(TipoDocumento.valueOf(tipoDocInput));
                                break; // Salir del bucle si la conversión es exitosa
                            } catch (IllegalArgumentException e) {
                                System.out.print("Tipo de documento inválido. Ingrese un tipo válido (DNI, LE, LC, PASAPORTE, OTRO): ");
                            }
                        }
                        
                        System.out.println("Ingrese el número de documento del huésped:");
                        String numeroDocumento = scanner.nextLine();
                            while(numeroDocumento.isBlank()) {
                                System.out.print("Número de documento no puede estar vacío. Ingrese número de documento: ");
                                numeroDocumento = scanner.nextLine(); 
                            }
                        nuevoHuesped.setNum_documento(Integer.parseInt(numeroDocumento));

            }
        }
        huespedDao.registrar_huesped(nuevoHuesped);
        System.out.println("Huésped "+ nuevoHuesped.getNombre() + nuevoHuesped.getApellido() +" ha sido satisfactoriamente cargado al sistema. ¿Desea cargar otro? (S/N)");
        String decision1;
        decision1 = scanner.nextLine().trim();
        if(decision1.equalsIgnoreCase("N") ){
            break;
        }
    }    
    }
    
    public void buscar_huespedes(String nombre, String apellido, TipoDocumento tipoDocumento, String numeroDocumento){
        if(nombre == null){
            nombre = "";
        }
        if(apellido == null){
            apellido = "";
        }
        if(numeroDocumento == null){
            numeroDocumento = "-1";
        }
        
        HuespedDTO huespedBusqueda = new HuespedDTO(nombre, apellido, tipoDocumento, numeroDocumento);
        List<Huesped> lista_huespedes = huespedDao.buscar_huespedes(huespedBusqueda);
        if(lista_huespedes == null || lista_huespedes.isEmpty()){
        dar_alta_huesped();
        }
        else{
        System.out.println("Huéspedes encontrados: " + lista_huespedes.size());
        int i = 1;
        for (Huesped huesped : lista_huespedes) {
            System.out.println(i + ". " + huesped.getNombre() + " " + huesped.getApellido() + " - " + huesped.getTipoDocumento() + " " + huesped.getNumDocumento());
            i++;
        }
        System.out.println("Seleccione el número del huésped o presione ENTER para dar de alta uno nuevo:");
        String entrada;
        entrada = scanner.nextLine();

        if (entrada.isBlank()) {
            System.out.println("No se seleccionó ningún huésped. Ejecutando alta...");
            dar_alta_huesped(); // CU9
            return;
        }
         try {
        int seleccion = Integer.parseInt(entrada);
        if (seleccion >= 1 && seleccion <= lista_huespedes.size()) {
            Huesped huespedSeleccionado = lista_huespedes.get(seleccion - 1);
            System.out.println("Huésped seleccionado: " + huespedSeleccionado.getNombre() + " " + huespedSeleccionado.getApellido());
            modificar_huesped(huespedSeleccionado);  // CU10
        } else {
            System.out.println("Selección fuera de rango. Ejecutando alta...");
            dar_alta_huesped(); // CU9
        }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Ejecutando alta...");
            dar_alta_huesped(); // CU9
        }
        }
    }
    
    public boolean autenticar_conserje() {
    System.out.print("Usuario (CONSERJE): ");
    String user = scanner.nextLine().trim();

    System.out.print("Contraseña: ");
    String pass = scanner.nextLine().trim();

        boolean valido = usuarioDao.validarCredenciales(user, pass);

        if (valido) {
            System.out.println("Conserje autenticado correctamente.");
            return true;
        } else {
            System.out.println("Usuario o contraseña incorrectos o inactivo.");
            return false;
        }
    }
    
    
}

