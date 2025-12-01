const modalAdvertenciaElement = document.getElementById('modalAdvertencia');
const modalConfirmarSalidaElement = document.getElementById('modalConfirmarSalida');
const modalHuespedGuardadoElement = document.getElementById('modalHuespedGuardado');
const modalCargaHuespedElement = document.getElementById('modalCargaHuesped');
const modalReservaExitosaElement = document.getElementById('modalReservaExitosa');

const modalReservaExitosa = modalReservaExitosaElement 
    ? new bootstrap.Modal(modalReservaExitosaElement) 
    : null;

const modalCargaHuesped = modalCargaHuespedElement 
    ? new bootstrap.Modal(modalCargaHuespedElement) 
    : null;

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

        if (targetDate >= fechaInicio && targetDate <= fechaFin) {
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
/**
 * @param {Object} room -
 * @param {Array<Date>} dates 
 * @returns {boolean} 
 */
function isRoomPartiallyAvailable(room, dates) {
    for (const date of dates) {
        const dateStr = normalizeDate(date); 
        if (getRoomStatusForDate(room, dateStr) === 'LIBRE') {
            return true;
        }
    }
    return false;
}

async function fetchRoomAvailability(startDate, endDate) {
    const RELATIVE_PATH = '/api/reservas/disponibilidad';
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
let selectionState = {};
let roomTypesMap = {}; 
let reservasConfirmadas = [];
function formatDateForCard(dateString) {
    const date = new Date(dateString + 'T00:00:00');
    
    const dayName = date.toLocaleDateString('es-AR', { weekday: 'short' });
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();

    const dayNameCap = dayName.charAt(0).toUpperCase() + dayName.slice(1);

    return `${dayNameCap}, ${day}/${month}/${year}`;
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
            cell.dataset.room = room.numeroHabitacion;
            cell.dataset.date = dateStr;
            cell.dataset.status = statusKey;
            cell.setAttribute('title', `Hab. ${room.numeroHabitacion} - ${statusKey} el ${dateStr}`);
            cell.innerHTML = status.label; 
            
            cell.onclick = function() {
                window.handleCellClick(room.numeroHabitacion, dateStr, statusKey, this);
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
        
        roomData.forEach(room => {
        roomTypesMap[room.numeroHabitacion] = room.tipo || 'Estándar'; 
        });
        loadingMessage.classList.add('d-none');

        const dates = getDatesInRange(fechaDesde, fechaHasta);
        const availableRooms = roomData.filter(room => isRoomPartiallyAvailable(room, dates));
        if (availableRooms.length === 0) {
            errorMessage.textContent = ` No hay habitaciones disponibles en el período ${dateFromStr} al ${dateToStr}.`;
            errorMessage.classList.remove('d-none');
            errorMessage.classList.add('alert-danger');
            availabilitySection.style.display = 'none';
            return;
        }
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
window.eliminarReserva = function(index) {
    const reservaAEliminar = reservasConfirmadas[index];
    if (reservaAEliminar) {
        const d1 = new Date(reservaAEliminar.fechaInicio + 'T00:00:00');
        const d2 = new Date(reservaAEliminar.fechaFin + 'T00:00:00');
        const fechasAEliminar = getDatesInRange(d1, d2);
        fechasAEliminar.push(d2); 

        fechasAEliminar.forEach(date => {
            const dateString = normalizeDate(date);
            const cell = document.querySelector(`.availability-cell[data-room="${reservaAEliminar.habitacion}"][data-date="${dateString}"]`);
            if (cell && cell.dataset.status === 'RESERVADA') {
                cell.classList.remove('status-reservada');
                cell.dataset.status = 'LIBRE';
                cell.innerText = STATUS_MAPPING['LIBRE'].label;
            }
        });
    }

    reservasConfirmadas.splice(index, 1);

    renderReservationCards();
};

window.handleRechazarReserva = function() {
    console.log("Rechazo/Limpieza de todas las reservas pendientes.");
    for (let i = reservasConfirmadas.length - 1; i >= 0; i--) {
        window.eliminarReserva(i);
    }

    const errorMessageElement = document.getElementById('error-message');
    if (errorMessageElement) {
        errorMessageElement.textContent = `Se limpiaron todas las selecciones de reserva.`;
        errorMessageElement.classList.remove('d-none', 'alert-success');
        errorMessageElement.classList.add('alert', 'alert-danger');
    }

    document.getElementById('room-availability-section').style.display = 'none'; 
};

window.handleAceptarReserva = function() {
    
    if (reservasConfirmadas.length === 0) {
        alert("Por favor, seleccione al menos una reserva.");
        return;
    }

    let resumenHTML = 'Reservas a confirmar: <ul class="mb-0 ps-3">';
    
    reservasConfirmadas.forEach(reserva => {
        const numero = reserva.habitacion.numeroHabitacion;
        const tipo = reserva.habitacion.tipo || 'Estándar'; 
        const inicio = reserva.fecha_inicio;
        const fin = reserva.fecha_fin;

        resumenHTML += `<li>Habitación <strong>${numero}</strong> (${tipo}): del ${inicio} al ${fin}</li>`;
    });
    resumenHTML += '</ul>';
    const modalResumen = document.getElementById('modal-resumen-reservas');
    if (modalResumen) {
        modalResumen.innerHTML = resumenHTML;
    } else {
        console.error("No se encontró el elemento 'modal-resumen-reservas' en el HTML");
    }

    const modalElement = document.getElementById('modalCargaHuesped');
    
    if (modalElement) {
        let modalInstance = bootstrap.Modal.getInstance(modalElement);
        if (!modalInstance) {
            modalInstance = new bootstrap.Modal(modalElement);
        }
        
        console.log("Mostrando modal...");
        modalInstance.show();
    } else {
        console.error("Error Crítico: No se encuentra el <div id='modalCargaHuesped'> en el HTML.");
        alert("Error interno: No se encuentra la ventana de carga de huésped.");
    }
}

function renderReservationCards() {
    const container = document.getElementById('lista-reservas-pendientes');
    const btnAceptar = document.getElementById('btnAceptarReserva'); 

    container.innerHTML = '';

    if (reservasConfirmadas.length === 0) {
        container.innerHTML = '<div class="text-muted fst-italic">No hay reservas seleccionadas aún.</div>';
        if (btnAceptar) btnAceptar.disabled = true; 
        return;
    }

    if (btnAceptar) btnAceptar.disabled = false;

    reservasConfirmadas.forEach((reserva, index) => {
        const fechaIngresoFmt = formatDateForCard(reserva.fecha_inicio);
        const fechaEgresoFmt = formatDateForCard(reserva.fecha_fin);

        const cardHTML = `
            <div class="col-md-6 col-lg-4">
                <div class="card shadow-sm border-start border-4 border-primary h-100">
                    <div class="card-body">
                        <h5 class="card-title fw-bold text-primary">
                            Habitación ${reserva.habitacion.numeroHabitacion}
                        </h5>
                        <h6 class="card-subtitle mb-3 text-muted">
                            ${reserva.habitacion.tipo}
                        </h6>
                        
                        <div class="mb-2">
                            <strong class="d-block text-secondary small">✔ Ingreso:</strong>
                            <span>${fechaIngresoFmt}, 12:00hs</span>
                        </div>
                        
                        <div class="mb-2">
                            <strong class="d-block text-secondary small">✔ Egreso:</strong>
                            <span>${fechaEgresoFmt}, 10:00hs</span>
                        </div>

                        <button onclick="eliminarReserva(${index})" class="btn btn-sm btn-outline-danger w-100 mt-2">
                            Quitar
                        </button>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += cardHTML;
    });
}

window.eliminarReserva = function(index) {
    reservasConfirmadas.splice(index, 1);
    renderReservationCards();
}

window.handleCellClick = function (roomNumber, dateClicked, status, cellElement) {
    const errorMessageElement = document.getElementById('error-message');

    if (status !== 'LIBRE') {
        alert("No puedes seleccionar una fecha que no está LIBRE.");
        return;
    }

    if (!selectionState[roomNumber]) {
        console.log(`Inicio de selección para Hab ${roomNumber} en ${dateClicked}`);
        
        selectionState[roomNumber] = dateClicked;
        
        cellElement.classList.add('selection-start');
        if (errorMessageElement) errorMessageElement.classList.add('d-none');

    } else {
        
        const fechaInicioStr = selectionState[roomNumber];
        const fechaFinStr = dateClicked;

        let d1 = new Date(fechaInicioStr + 'T00:00:00');
        let d2 = new Date(fechaFinStr + 'T00:00:00');
        if (d1 > d2) { const temp = d1; d1 = d2; d2 = temp; } 
        
        const nuevaReservaInicio = d1; 
        const nuevaReservaFin = d2; 
        const datesToCheck = getDatesInRange(d1, d2); 
        datesToCheck.push(d2); 

        let rangoValido = true;
        for (let date of datesToCheck) {
            const dateString = normalizeDate(date);
            const cell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${dateString}"]`);
            
            if (cell && (cell.dataset.status === 'OCUPADA' || cell.dataset.status === 'FUERA_DE_SERVICIO')) {
                rangoValido = false;
                if (errorMessageElement) {
                     errorMessageElement.textContent = `Error: El rango incluye fechas ya OCUPADAS o FUERA DE SERVICIO (ej: ${dateString}).`;
                }
                break; 
            }
        }

        if (errorMessageElement) errorMessageElement.classList.add('d-none');

        const indicesAEliminar = [];
        
        reservasConfirmadas.forEach((reserva, index) => {
            if (reserva.habitacion == roomNumber) {
                
                const reservaExistenteInicio = new Date(reserva.fechaInicio + 'T00:00:00');
                const reservaExistenteFin = new Date(reserva.fechaFin + 'T00:00:00');
                if (nuevaReservaInicio <= reservaExistenteInicio && nuevaReservaFin >= reservaExistenteFin) {
                    
                    indicesAEliminar.push(index);
                    const fechasAntiguas = getDatesInRange(reservaExistenteInicio, reservaExistenteFin);
                    fechasAntiguas.push(reservaExistenteFin); 
                    
                    fechasAntiguas.forEach(date => {
                        const dateString = normalizeDate(date);
                        const cell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${dateString}"]`);
                        if (cell) {
                            cell.classList.remove('status-reservada');
                            cell.dataset.status = 'LIBRE';
                            cell.innerText = STATUS_MAPPING['LIBRE'].label;
                        }
                    });
                } else if (nuevaReservaFin > reservaExistenteInicio && nuevaReservaInicio < reservaExistenteFin) {
                    rangoValido = false;
                    
                    if (errorMessageElement) {
                        errorMessageElement.textContent = `Error: Este rango se superpone parcialmente con una reserva temporal ya realizada (del ${reserva.fechaInicio} al ${reserva.fechaFin}).`;
                    }
                }
            }
        });

        if (!rangoValido) {
            const startCell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${selectionState[roomNumber]}"]`);
            if (startCell) startCell.classList.remove('selection-start');
            if (errorMessageElement) {
                errorMessageElement.classList.remove('d-none', 'alert-success');
                errorMessageElement.classList.add('alert', 'alert-danger');
            }
            delete selectionState[roomNumber];
            return; 
        }
        indicesAEliminar.sort((a, b) => b - a).forEach(index => {
            reservasConfirmadas.splice(index, 1);
        });
        
        const celdasAColorear = [];
        datesToCheck.forEach(date => {
            const dateString = normalizeDate(date);
            const cell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${dateString}"]`);
            if (cell) {
                celdasAColorear.push(cell);
            }
        });

        celdasAColorear.forEach(c => {
            c.classList.remove('selection-start'); 
            c.classList.remove('status-libre');
            c.classList.add('status-reservada'); 
            c.dataset.status = 'RESERVADA'; 
            c.innerText = 'R'; 
        });

        const inicioStr = normalizeDate(nuevaReservaInicio); 
        const finStr = normalizeDate(nuevaReservaFin); 
        const nuevaReserva = {
            habitacion: {
                numeroHabitacion: roomNumber,
                tipo: roomTypesMap[roomNumber]
            },
            fecha_inicio: inicioStr,
            fecha_fin: finStr
        };
        reservasConfirmadas.push(nuevaReserva); 
        renderReservationCards();

        if (errorMessageElement) {
            errorMessageElement.textContent = `Rango seleccionado para Habitación ${roomNumber}: ${datesToCheck.length} noches. ${indicesAEliminar.length > 0 ? `Se absorbieron ${indicesAEliminar.length} reservas anteriores.` : ''}`;
            errorMessageElement.classList.remove('d-none', 'alert-danger');
            errorMessageElement.classList.add('alert', 'alert-success');
        }
        
        delete selectionState[roomNumber];
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
    const formHuespedReserva = document.getElementById('formHuespedReserva');
    if (formHuespedReserva) {
    const btnGuardarReservaCompleta = document.getElementById('btnGuardarReservaCompleta');

    formHuespedReserva.addEventListener('submit', function(e) {
            e.preventDefault();
            if (btnGuardarReservaCompleta) {
                btnGuardarReservaCompleta.disabled = true;
                btnGuardarReservaCompleta.textContent = 'Guardando...';
            }
            const formData = {
                telefono: document.getElementById('telefonoHuesped').value,
                email: "placeholder@example.com",
                ocupacion: "N/A",
                condicionIVA: "CONSUMIDOR_FINAL",
                apellido: document.getElementById('apellidoHuesped').value,
                nombre: document.getElementById('nombreHuesped').value,
                tipo_documento: "DNI",
                numero_documento: 0,
                cuit: 0,
                fecha_nacimiento: "0001-01-01",
                direccion: {
                    calle: "N/A",
                    numero: 0,
                    departamento: "N/A",
                    piso: 0,
                    codigoPostal: 0,
                    localidad: "N/A",
                    provincia: "N/A",
                    pais: "N/A"
                },
                nacionalidad: "N/A"
            };
            const payloadFinal = {
                listaHabitacionesReservadas: reservasConfirmadas,
                huespedPrincipal: formData,
            };
            console.log("Enviando Payload Final al Backend:", payloadFinal);

            fetch('/api/reservas/confirmar', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify(payloadFinal)
            })
            .then(async response => {
                if (response.ok) {
                    return response.json(); 
                } else if (response.status === 400) {
                    const errorData = await response.json();
                    console.error('Error de validación del Backend:', errorData);
                    throw new Error(`Error de validación: ${JSON.stringify(errorData)}`);
                } else {
                    const errorText = await response.text();
                    throw new Error(`Error ${response.status} del servidor: ${errorText.substring(0, 100)}...`);
                }
            })
            .then(reservaData => { 
                console.log('--- ÉXITO: Objeto recibido:', reservaData); 
                const reservaId = reservaData.idReserva;
                if (modalCargaHuesped) {
                    modalCargaHuesped.hide();
                }
                const reservaIdMostrada = document.getElementById('reservaIdMostrada');
                if (reservaIdMostrada) {
                    reservaIdMostrada.textContent = `ID #${reservaId}`; 
                }
                setTimeout(() => {
                        if (modalReservaExitosa) {
                            modalReservaExitosa.show();
                        } else {
                            alert(`¡Reserva ID #${reservaId} guardada! (El modal de éxito falló al mostrarse)`);
                        }
                    }, 100);
                reservasConfirmadas = []; 
                celdasSeleccionadas = [];
                
            })
            .catch(err => {
                console.error('Error de Persistencia:', err);
                alert(`No se pudo completar la reserva. Ver consola para detalles. Mensaje: ${err.message}`);
            })
            .finally(() => {
                if (modalCargaHuesped) { 
                modalCargaHuesped.hide(); 
                }
                if (btnGuardarReservaCompleta) {
                    btnGuardarReservaCompleta.disabled = false;
                    btnGuardarReservaCompleta.textContent = 'Confirmar Reserva';
                }
            });
        });
    }
    if (formSeleccion) {
        formSeleccion.addEventListener('submit', function(event) {
            event.preventDefault(); 
            const selectedHuesped = document.querySelector('input[name="huespedIdSeleccionado"]:checked');

            if (selectedHuesped) {
                const huespedId = selectedHuesped.value;
                console.log(' Huésped seleccionado (ID: ' + huespedId + '). Redirigiendo a / para iniciar reserva.');
                window.location.href = '/'; 
            } else {
                console.log(' Huésped NO seleccionado. Redirigiendo a /huespedes/alta');
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
                num_documento: parseInt(formData.get('num_documento')),
                cuit: cuitValue && cuitValue.trim() !== '' ? parseInt(cuitValue) : 0,
                fecha_nacimiento: formData.get('fecha_nacimiento'),
                direccion: {
                    calle: formData.get('direccion.calle'),
                    numero: parseInt(formData.get('direccion.numero')),
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