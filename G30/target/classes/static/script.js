const modalAdvertenciaElement = document.getElementById('modalAdvertencia');
const modalConfirmarSalidaElement = document.getElementById('modalConfirmarSalida');
const modalHuespedGuardadoElement = document.getElementById('modalHuespedGuardado');

const modalAdvertencia = modalAdvertenciaElement 
    ? new bootstrap.Modal(modalAdvertenciaElement) 
    : null;
    
const modalConfirmarSalida = modalConfirmarSalidaElement 
    ? new bootstrap.Modal(modalConfirmarSalidaElement) 
    : null;
    
const modalHuespedGuardado = modalHuespedGuardadoElement 
    ? new bootstrap.Modal(modalHuespedGuardadoElement) 
    : null;

let datosHuespedPendientes = null; 
const STATUS_MAPPING = {
    'LIBRE': { class: 'status-libre', label: 'L' },
    'OCUPADA': { class: 'status-ocupada', label: 'O' },
    'RESERVADA': { class: 'status-reservada', label: 'R' },
    'FUERA_DE_SERVICIO': { class: 'status-fuera_servicio', label: 'F' }
};

function normalizeDate(date) {
    return date.toISOString().substring(0, 10);
}

function getDatesInRange(startDate, endDate) {
    const dates = [];
    let currentDate = new Date(startDate);
    const end = new Date(endDate);
    end.setDate(end.getDate() + 1); 

    while (currentDate < end) {
        dates.push(new Date(currentDate));
        currentDate.setDate(currentDate.getDate() + 1);
    }
    return dates;
}

function formatDateLabel(date) {
    const dayName = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'][date.getDay()];
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${dayName}, ${day}/${month}`;
}

function getRoomStatusForDate(room, dateStr) {
    let currentStatus = 'LIBRE'; 
    for (const estado of room.historiaEstados) {
        const fechaInicio = new Date(estado.fechaInicio + 'T00:00:00');
        const fechaFin = new Date(estado.fechaFin + 'T00:00:00');
        const targetDate = new Date(dateStr + 'T00:00:00');

        if (targetDate >= fechaInicio && targetDate < fechaFin) {
            currentStatus = estado.estado;
            break; 
        }
    }
    return currentStatus;
}


function limpiarErrores() {
    document.querySelectorAll('input, select').forEach(el => {
        el.classList.remove('is-invalid'); 
        const feedbackEl = el.nextElementSibling;
        if (feedbackEl && feedbackEl.classList.contains('invalid-feedback')) {
             feedbackEl.textContent = '';
        }
    });
    
    const mensajeGeneral = document.getElementById('mensajeHuesped');
    if (mensajeGeneral) {
        mensajeGeneral.textContent = '';
        mensajeGeneral.classList.remove('alert-danger', 'alert-success');
        mensajeGeneral.innerHTML = ''; 
    }
}

/**
 * @param {Object} errores - Objeto JSON con {campo: mensaje, ...}
 */
function mostrarErroresEnPantalla(errores) {

    limpiarErrores(); 

    const mensajeGeneral = document.getElementById('mensajeHuesped');
    let listaErroresHTML = '<strong>Errores encontrados:</strong>'; 
    
    if (mensajeGeneral) {
        mensajeGeneral.classList.add('alert', 'alert-danger');
        listaErroresHTML += '<ul>';
    }

    let primerCampoConError = null;

    for (const campo in errores) {
        if (errores.hasOwnProperty(campo)) {
            const mensaje = errores[campo];
            
            const inputElement = document.querySelector(`[name="${campo}"]`);
            if (inputElement) {
                inputElement.classList.add('is-invalid'); 
                
                if (!primerCampoConError) {
                    primerCampoConError = inputElement;
                }

                const feedbackElement = inputElement.nextElementSibling;
                if (feedbackElement && feedbackElement.classList.contains('invalid-feedback')) {
                    feedbackElement.textContent = mensaje;
                } else {
                    let nombreCampoAmigable = campo.replace('direccion.', '').replace('_', ' ');
                    nombreCampoAmigable = nombreCampoAmigable.charAt(0).toUpperCase() + nombreCampoAmigable.slice(1);
                    if (mensajeGeneral) listaErroresHTML += `<li>${nombreCampoAmigable}: ${mensaje}</li>`;
                }
            } else {
                let nombreCampoAmigable = campo.replace('direccion.', '').replace('_', ' ');
                nombreCampoAmigable = nombreCampoAmigable.charAt(0).toUpperCase() + nombreCampoAmigable.slice(1);
                if (mensajeGeneral) listaErroresHTML += `<li>${nombreCampoAmigable}: ${mensaje}</li>`;
            }
        }
    }
    
    if (mensajeGeneral) {
        listaErroresHTML += '</ul>';
        mensajeGeneral.innerHTML = listaErroresHTML; 
        
        if (primerCampoConError) {
             primerCampoConError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else {
            mensajeGeneral.scrollIntoView({ behavior: 'smooth' });
        }
    }
}


async function fetchRoomAvailability(startDate, endDate) {
    const RELATIVE_PATH = '/api/habitaciones/disponibilidad';
    const url = `${RELATIVE_PATH}?fechaDesde=${startDate}&fechaHasta=${endDate}`;
    
    console.log("Iniciando conexión con backend (Disponibilidad):", url);

    try {
        const response = await fetch(url, { method: 'GET', headers: { 'Content-Type': 'application/json' } });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText || 'Error desconocido del servidor.'}`);
        }
        return response.json();

    } catch (error) {
        console.error("Error al obtener la disponibilidad:", error);
        throw new Error(`No se pudo conectar con el servidor backend. Error: ${error.message}.`);
    }
}

function generateAvailabilityGrid(dates, rooms) {
    const occupancyGrid = document.getElementById('availability-timeline');
    occupancyGrid.innerHTML = '';
    
    if (rooms.length === 0) {
        occupancyGrid.innerHTML = '<div class="text-center p-4 text-muted">No se encontraron habitaciones disponibles.</div>';
        return;
    }
    
    const headerRow = document.createElement('div');
    headerRow.classList.add('grid-header-row');
    
    const cornerCell = document.createElement('div');
    cornerCell.classList.add('grid-cell', 'top-left-corner');
    cornerCell.textContent = 'Fecha \\ Hab.';
    headerRow.appendChild(cornerCell);

    rooms.forEach(room => {
        const roomCell = document.createElement('div');
        roomCell.classList.add('grid-cell');
        roomCell.textContent = room.numeroHabitacion; 
        headerRow.appendChild(roomCell);
    });

    occupancyGrid.appendChild(headerRow);

    dates.forEach(date => {
        const dataRow = document.createElement('div');
        dataRow.classList.add('grid-data-row');
        const dateStr = normalizeDate(date); 
        
        const dateLabelCell = document.createElement('div');
        dateLabelCell.classList.add('grid-cell', 'date-label-cell');
        dateLabelCell.textContent = formatDateLabel(date);
        dataRow.appendChild(dateLabelCell);

        rooms.forEach(room => {
            const statusKey = getRoomStatusForDate(room, dateStr); 
            const status = STATUS_MAPPING[statusKey] || STATUS_MAPPING['LIBRE']; 
            
            const cell = document.createElement('div');
            cell.classList.add('grid-cell', 'availability-cell', status.class);
            cell.setAttribute('title', `Hab. ${room.numeroHabitacion} - ${statusKey} el ${dateStr}`);
            cell.innerHTML = status.label; 
            
            cell.onclick = () => {
                window.handleCellClick(room.numeroHabitacion, dateStr, statusKey);
            };

            dataRow.appendChild(cell);
        });

        occupancyGrid.appendChild(dataRow);
    });
}
window.showRoomAvailability = async function () {
    const fechaDesdeInput = document.getElementById('fechaDesde');
    const fechaHastaInput = document.getElementById('fechaHasta');
    const errorMessage = document.getElementById('error-message');
    const loadingMessage = document.getElementById('loading-message');
    const dateSummary = document.getElementById('date-summary');
    const availabilitySection = document.getElementById('room-availability-section');

    errorMessage.classList.add('d-none');
    errorMessage.classList.remove('alert-success', 'alert-danger', 'alert-info');
    loadingMessage.classList.add('d-none');
    availabilitySection.style.display = 'none';

    if (!fechaDesdeInput.value || !fechaHastaInput.value) {
        errorMessage.textContent = "Por favor, complete ambas fechas.";
        errorMessage.classList.remove('d-none');
        errorMessage.classList.add('alert-danger');
        return;
    }
    
    const fechaDesde = new Date(fechaDesdeInput.value + 'T00:00:00');
    const fechaHasta = new Date(fechaHastaInput.value + 'T00:00:00');

    if (isNaN(fechaDesde) || isNaN(fechaHasta) || fechaDesde >= fechaHasta) {
        errorMessage.textContent = "La 'Fecha Desde' debe ser estrictamente anterior a la 'Fecha Hasta'.";
        errorMessage.classList.remove('d-none');
        errorMessage.classList.add('alert-danger');
        return;
    }
    
    try {
        loadingMessage.classList.remove('d-none');

        const dateFromStr = normalizeDate(fechaDesde);
        const dateToStr = normalizeDate(fechaHasta);
        
        const roomData = await fetchRoomAvailability(dateFromStr, dateToStr);
        
        loadingMessage.classList.add('d-none');

        const dates = getDatesInRange(fechaDesde, fechaHasta);
        const totalRooms = roomData.length;
        const timelineContainer = document.getElementById('availability-timeline');
        

        const roomColumnWidths = `repeat(${totalRooms}, 70px)`;
        timelineContainer.style.gridTemplateColumns = `minmax(120px, 120px) ${roomColumnWidths}`;
        dateSummary.innerHTML = `Período: <strong>${dateFromStr}</strong> al <strong>${dateToStr}</strong> (${dates.length} días)`;
        availabilitySection.style.display = 'block';

        generateAvailabilityGrid(dates, roomData);

    } catch (error) {
        loadingMessage.classList.add('d-none');
        errorMessage.textContent = `Error de conexión/respuesta: ${error.message}. Asegúrese de que su servidor Spring Boot esté corriendo y el endpoint sea accesible.`;
        errorMessage.classList.remove('d-none');
        errorMessage.classList.add('alert-danger');
    }
}


window.handleCellClick = function (roomNumber, date, status) {
    console.log(`Seleccionada Habitación ${roomNumber} el día ${date}. Estado actual: ${status}`);
    const message = `¡Celda seleccionada!\nHabitación: ${roomNumber}\nFecha: ${date}\nEstado: ${status}`;
    const errorMessageElement = document.getElementById('error-message');
    
    if (errorMessageElement) {
        errorMessageElement.textContent = message;
        errorMessageElement.classList.remove('d-none', 'alert-danger', 'alert-info');
        errorMessageElement.classList.add('alert', 'alert-success');
    }
}


document.addEventListener('DOMContentLoaded', function() {
    const formHuesped = document.getElementById('formAltaHuesped');
    const btnConfirmarGuardado = document.getElementById('btnConfirmarGuardado');
    const cuerpoModal = document.getElementById('cuerpoModalAdvertencia');
    const btnConfirmarSalida = document.getElementById('btnConfirmarSalida');
    const cuerpoModalGuardadoExito = document.getElementById('cuerpoModalGuardadoExito');
    const btnSalirAHomedeExito = document.getElementById('btnSalirAHomedeExito');
    const inputTipoDocumento = document.querySelector('[name="tipo_documento"]');
    const formSeleccion = document.getElementById('formSeleccion');
    
    if (formSeleccion) {
        formSeleccion.addEventListener('submit', function(event) {
            event.preventDefault(); 
            const selectedHuesped = document.querySelector('input[name="huespedIdSeleccionado"]:checked');

            if (selectedHuesped) {
                const huespedId = selectedHuesped.value;
                console.log('✅ Huésped seleccionado (ID: ' + huespedId + '). Redirigiendo a / para iniciar reserva.');
                window.location.href = '/'; 
            } else {
                console.log('➡️ Huésped NO seleccionado. Redirigiendo a /huespedes/alta');
                window.location.href = '/huespedes/alta';
            }
        });
    }

    if(formHuesped){
        formHuesped.addEventListener('submit', function(e) {
            e.preventDefault();

            limpiarErrores(); 
            
            const formData = new FormData(formHuesped);
            
            const cuitValue = formData.get('cuit');
            const data = {
                nombre: formData.get('nombre'),
                apellido: formData.get('apellido'),
                tipo_documento: formData.get('tipo_documento'),
                num_documento: parseInt(formData.get('num_documento') || 0),
                cuit: cuitValue && cuitValue.trim() !== '' ? parseInt(cuitValue) : 0,
                fecha_nacimiento: formData.get('fecha_nacimiento'),
                direccion: {
                    calle: formData.get('direccion.calle'),
                    numero: parseInt(formData.get('direccion.numero') || 0),
                    departamento: formData.get('direccion.departamento'),
                    piso: parseInt(formData.get('direccion.piso') || 0),
                    codigoPostal: (function(){ const cp = formData.get('direccion.codigoPostal'); return cp && cp.trim() !== '' ? parseInt(cp,10) : null; })(),
                    localidad: formData.get('direccion.localidad'),
                    provincia: formData.get('direccion.provincia'),
                    pais: formData.get('direccion.pais')
                },
                nacionalidad: formData.get('nacionalidad'),
                telefono: formData.get('telefono'),
                email: formData.get('email'),
                ocupacion: formData.get('ocupacion'),
                condicionIVA: formData.get('condicionIVA')
            };

            fetch('/api/huespedes/alta', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
            .then(async resp => {
                if (resp.ok) {
                    return resp.json();
                } else if (resp.status === 400) {
                    const errorData = await resp.json();
                    mostrarErroresEnPantalla(errorData); 
                    throw new Error('Errores de validación encontrados.'); 
                } else if (resp.status === 409) { 
                    const mensajeAdvertencia = await resp.text();
                    
                    datosHuespedPendientes = data; 
                    
                    if (cuerpoModal && modalAdvertencia) {
                        cuerpoModal.textContent = mensajeAdvertencia;
                        modalAdvertencia.show();
                    }
                    
                    throw new Error('Conflicto de documento. Esperando confirmación.');
                } else {
                    const text = await resp.text();
                    throw new Error(`Error HTTP ${resp.status}: ${text.substring(0, 100)}...`);
                }
                })
            .then(json => {
                formHuesped.reset();
                limpiarErrores(); 
                
                if (cuerpoModalGuardadoExito && modalHuespedGuardado) {
                    cuerpoModalGuardadoExito.innerHTML = `El huésped <strong>${json.nombre} ${json.apellido}</strong> ha sido cargado correctamente.`;
                    modalHuespedGuardado.show();
                }
            })
            .catch(err => {
                if (err.message !== 'Errores de validación encontrados.' && document.getElementById('mensajeHuesped')) {
                    const mensajeGeneral = document.getElementById('mensajeHuesped');
                    
                    limpiarErrores();
                    mensajeGeneral.classList.remove('alert-success');
                    mensajeGeneral.classList.add('alert-danger');
                    mensajeGeneral.classList.remove('d-none'); 
                    
                    mensajeGeneral.innerHTML = '<strong>Error de Sistema:</strong> ' + err.message;
                }
                console.error('Error completo:', err);
            });
        });
    }

    if(btnSalirAHomedeExito){
        btnSalirAHomedeExito.addEventListener('click', function() {
            window.location.href = '/'; 
        });
    }

    if(btnConfirmarSalida){
        btnConfirmarSalida.addEventListener('click', function() {
            if (modalConfirmarSalida) modalConfirmarSalida.hide(); 
            window.location.href = '/';
        });
    }

    if(btnConfirmarGuardado){
        btnConfirmarGuardado.addEventListener('click', function() {
        if (datosHuespedPendientes) {
            if (modalAdvertencia) modalAdvertencia.hide();
            
            fetch('/api/huespedes/alta?forzar=true', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosHuespedPendientes)
            })
            .then(async resp => {
                if (resp.ok) {
                    return resp.json();
                }
                const errorText = await resp.text();
                throw new Error(`Error ${resp.status} al guardar forzado: ${errorText}`);
            })
            .then(json => {
                formHuesped.reset();
                limpiarErrores(); 
                
                if (cuerpoModalGuardadoExito && modalHuespedGuardado) {
                    cuerpoModalGuardadoExito.innerHTML = `El huésped <strong>${json.nombre} ${json.apellido}</strong> ha sido cargado correctamente.`;
                    modalHuespedGuardado.show();
                }
                
                datosHuespedPendientes = null;
            })
            .catch(err => {
                const mensajeGeneral = document.getElementById('mensajeHuesped');
                mensajeGeneral.innerHTML = '<strong>Error Crítico al reintentar:</strong> ' + err.message;
                mensajeGeneral.classList.remove('alert-success');
                mensajeGeneral.classList.add('alert-danger');
                mensajeGeneral.classList.remove('d-none'); 
                console.error('Error al reenviar:', err);
            });
        }
    });
    }

    const modalAdvertenciaElement = document.getElementById('modalAdvertencia');
    if(modalAdvertenciaElement){
        modalAdvertenciaElement.addEventListener('hidden.bs.modal', function () {
            if (inputTipoDocumento) {
                inputTipoDocumento.classList.add('is-invalid');
                
                inputTipoDocumento.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    }
});