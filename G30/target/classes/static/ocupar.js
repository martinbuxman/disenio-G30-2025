// ==========================================
// 1. INICIALIZACIÓN Y VARIABLES
// ==========================================
const modalOcupacionElement = document.getElementById('modalOcupacion');
const modalOcupacion = modalOcupacionElement ? new bootstrap.Modal(modalOcupacionElement) : null;
let selectionState = {};
let roomTypesMap = {}; 
let reservasConfirmadas = [];

let seleccionActual = new Map();

const STATUS_MAPPING = {
    'LIBRE': { class: 'status-libre', label: 'L' },
    'OCUPADA': { class: 'status-ocupada', label: 'O' },
    'RESERVADA': { class: 'status-reservada', label: 'R' },
    'FUERA_DE_SERVICIO': { class: 'status-fuera_servicio', label: 'F' }
};

// ==========================================
// 2. UTILIDADES
// ==========================================
function normalizeDate(date) { return date.toISOString().substring(0, 10); }

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
    const dayName = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'][date.getDay()];
    return `${dayName}, ${date.getDate()}/${date.getMonth() + 1}`;
}

// ==========================================
// 3. GRILLA (CU05)
// ==========================================
function getRoomStatusForDate(room, dateStr) {
    let currentStatus = 'LIBRE'; 
    if(!room.historiaEstados) return 'LIBRE';
    for (const estado of room.historiaEstados) {
        const fInicio = estado.fechaInicio; 
        const fFin = estado.fechaFin; 
        if (dateStr >= fInicio && dateStr <= fFin) {
            currentStatus = estado.estado;
            break; 
        }
    }
    return currentStatus;
}

async function fetchRoomAvailability(startDate, endDate) {
    const url = `/api/reservas/disponibilidad?fechaDesde=${startDate}&fechaHasta=${endDate}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error(await response.text());
    return response.json();
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
            cell.dataset.id = `${room.numeroHabitacion}|${dateStr}`;
            
            cell.onclick = function() {
                window.handleCellClick(room.numeroHabitacion, dateStr, statusKey, this);
            };
            dataRow.appendChild(cell);
        });

        occupancyGrid.appendChild(dataRow);
    });
}
// ==========================================
// 4. LÓGICA DE SELECCIÓN (RANGO DE OCUPACIÓN - TWO-CLICK)
// ==========================================

function actualizarEstadoBotonSiguiente() {
    const btn = document.getElementById('btnVerificarSeleccion');
    if (btn) {
        btn.disabled = seleccionActual.size === 0;
    }
}
window.handleCellClick = function (roomNumber, dateClicked, status, cellElement) {
    const errorMessageElement = document.getElementById('error-message'); 
    if (status === 'OCUPADA' || status === 'FUERA_DE_SERVICIO') {
        alert(`No puedes iniciar una selección en una fecha que está ${status}.`);
        return;
    }

    if (!selectionState[roomNumber]) {
        limpiarSeleccionVisual(roomNumber);
        
        selectionState[roomNumber] = dateClicked;
        cellElement.classList.add('selection-start');

    } else {       
        const fechaInicioStr = selectionState[roomNumber];
        const fechaFinStr = dateClicked;

        let d1 = new Date(fechaInicioStr + 'T00:00:00');
        let d2 = new Date(fechaFinStr + 'T00:00:00');
        
        if (d1 > d2) { 
             [d1, d2] = [d2, d1]; 
        } 
        
        const datesToCheck = getDatesInRange(d1, d2); 
        datesToCheck.push(d2);

        let rangoValido = true;
        
        for (let date of datesToCheck) {
            const dateString = normalizeDate(date);
            const cell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${dateString}"]`);
            
            if (cell && (cell.dataset.status === 'OCUPADA' || cell.dataset.status === 'FUERA_DE_SERVICIO')) {
                rangoValido = false;
                if (errorMessageElement) {
                    errorMessageElement.textContent = `Error: El rango incluye fechas OCUPADAS o FUERA DE SERVICIO (ej: ${dateString}).`;
                }
                break; 
            }
        }

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
        
        const inicioStr = normalizeDate(d1); 
        const finStr = normalizeDate(d2); 
        
        seleccionActual.set(roomNumber, { inicio: inicioStr, fin: finStr });
        
        const celdasASeleccionar = [];
        datesToCheck.forEach(date => {
            const dateString = normalizeDate(date);
            const cell = document.querySelector(`.availability-cell[data-room="${roomNumber}"][data-date="${dateString}"]`);
            if (cell) {
                celdasASeleccionar.push(cell);
            }
        });

        celdasASeleccionar.forEach(c => {
            c.classList.remove('selection-start'); 
            
            Object.values(STATUS_MAPPING).forEach(mapping => c.classList.remove(mapping.class));
            
            c.classList.add('status-ocupada'); 
            c.innerText = 'O';
        });
        
        if (errorMessageElement) {
            errorMessageElement.textContent = `Rango seleccionado para Habitación ${roomNumber}: ${inicioStr} al ${finStr} (${datesToCheck.length} días).`;
            errorMessageElement.classList.remove('d-none', 'alert-danger');
            errorMessageElement.classList.add('alert', 'alert-success');
        }

        delete selectionState[roomNumber];
        actualizarListaSeleccionModal();
    }
}

function limpiarSeleccionVisual(roomNumber) {
    seleccionActual.delete(roomNumber);
    const cellsToClear = document.querySelectorAll(`.availability-cell[data-room="${roomNumber}"].status-ocupada`);
    
    cellsToClear.forEach(cell => {
        const originalStatusKey = cell.dataset.status; 
        const statusMapping = STATUS_MAPPING[originalStatusKey] || STATUS_MAPPING['LIBRE'];

        cell.classList.remove('status-ocupada');
        cell.classList.remove('selection-start'); 
        
        Object.values(STATUS_MAPPING).forEach(mapping => cell.classList.remove(mapping.class));
        cell.classList.add(statusMapping.class);
        
        cell.innerText = statusMapping.label; 
    });
}



function actualizarListaSeleccionModal() {
    const listaHtml = document.getElementById('listaSeleccionModal');
    if(listaHtml) {
        listaHtml.innerHTML = '';
        seleccionActual.forEach((rango, roomNumber) => {
            const li = document.createElement('li');
            li.textContent = `Habitación ${roomNumber}: ${rango.inicio} al ${rango.fin}`;
            listaHtml.appendChild(li);
        });
    }
    actualizarEstadoBotonSiguiente();
}

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

// ==========================================
// 5. FUNCIONES GLOBALES Y EVENTOS
// ==========================================

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

// --- BOTÓN SIGUIENTE (ABRIR MODAL) ---
const btnVerificar = document.getElementById('btnVerificarSeleccion');
if(btnVerificar) {
    btnVerificar.addEventListener('click', function() {
        const listaHtml = document.getElementById('listaSeleccionModal');
        if(listaHtml) {
            listaHtml.innerHTML = '';
            Array.from(seleccionActual).sort().forEach(item => {
                const [hab, fecha] = item.split('|');
                const li = document.createElement('li');
                li.textContent = `Habitacion ${hab} - Fecha ${fecha}`;
                listaHtml.appendChild(li);
            });
        }
        const dniInput = document.getElementById('buscarDniResponsable');
        if(dniInput) dniInput.value = '';
        const infoResp = document.getElementById('infoResponsable');
        if(infoResp) infoResp.classList.add('d-none');
        document.querySelector('#tablaAcompanantes tbody').innerHTML = '';
        
        if(modalOcupacion) modalOcupacion.show();
    });
}

// --- BOTÓN BUSCAR RESPONSABL ---
const btnBuscarResp = document.getElementById('btnBuscarResponsable');
if(btnBuscarResp) {
    btnBuscarResp.addEventListener('click', async function() {
        const dni = document.getElementById('buscarDniResponsable').value;
        if(!dni) { alert("Ingrese DNI"); return; }
        
        // Simulación
        document.getElementById('infoResponsable').classList.remove('d-none');
        document.getElementById('nombreResponsable').textContent = "Huesped Encontrado (DNI " + dni + ")";
        document.getElementById('idResponsableInput').value = "1"; 
    });
}

// --- BOTÓN AGREGAR ACOMPAÑANTE ---
const btnAgregarAcomp = document.getElementById('btnAgregarAcompananteRow');
if(btnAgregarAcomp) {
    btnAgregarAcomp.addEventListener('click', function() {
        const tbody = document.querySelector('#tablaAcompanantes tbody');
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><input type="number" class="form-control form-control-sm" placeholder="DNI"></td>
            <td><input type="text" class="form-control form-control-sm" placeholder="Nombre"></td>
            <td><button type="button" class="btn btn-danger btn-sm" onclick="this.closest('tr').remove()">X</button></td>
        `;
        tbody.appendChild(row);
    });
}

// --- BOTÓN CONFIRMAR FINAL ---
const btnConfirmarFinal = document.getElementById('btnConfirmarOcupacionFinal');
if(btnConfirmarFinal) {
    btnConfirmarFinal.addEventListener('click', function() {
        alert("Datos listos para enviar al Backend.\nItems: " + seleccionActual.size);
        if(modalOcupacion) modalOcupacion.hide();
    });
}