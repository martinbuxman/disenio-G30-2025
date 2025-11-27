// ==========================================
// 1. INICIALIZACIÓN Y VARIABLES
// ==========================================
const modalOcupacionElement = document.getElementById('modalOcupacion');
const modalOcupacion = modalOcupacionElement ? new bootstrap.Modal(modalOcupacionElement) : null;
const modalDecisionElement = document.getElementById('modalDecision');
const modalDecision = modalDecisionElement ? new bootstrap.Modal(modalDecisionElement) : null;
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

//buscar huespedes en el backend
async function buscarHuespedesBackend(apellido, nombre, tipoDoc, nroDoc) {
    const params = new URLSearchParams();
    if (apellido) params.append("apellido", apellido);
    if (nombre) params.append("nombre", nombre);
    if (tipoDoc) params.append("tipo_documento", tipoDoc);
    if (nroDoc) params.append("num_documento", nroDoc);

    const url = `/api/huespedes/buscar?${params.toString()}`;
    console.log("Buscando en:", url);

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("Error en la búsqueda");
        return await response.json(); // Retorna la lista de huéspedes
    } catch (error) {
        console.error(error);
        return [];
    }
}
function renderTablaResultados(huespedes, callbackSeleccion) {
    if (!huespedes || huespedes.length === 0) {
        return '<div class="alert alert-warning">No se encontraron resultados.</div>';
    }

    let html = `
    <div class="table-responsive mt-2 border rounded">
        <table class="table table-sm table-hover mb-0">
            <thead class="table-light">
                <tr>
                    <th>Apellido y Nombre</th>
                    <th>Documento</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>`;

    huespedes.forEach(h => {
        html += `
            <tr>
                <td>${h.apellido}, ${h.nombre}</td>
                <td>${h.tipoDocumento} ${h.numDocumento}</td>
                <td>
                    <button class="btn btn-sm btn-success btn-seleccionar-huesped" 
                        data-id="${h.id}" 
                        data-nombre="${h.nombre}" 
                        data-apellido="${h.apellido}"
                        data-doc="${h.numDocumento}"
                        data-tipo="${h.tipoDocumento}">
                        Seleccionar
                    </button>
                </td>
            </tr>`;
    });

    html += `</tbody></table></div>`;
    return html;
}

function renderTablaResultadosAcomp(huespedes, targetRowId) {
    // Añadimos un botón para cerrar la lista de resultados
    const closeButton = `<button type="button" class="btn-close" 
                         onclick="document.getElementById('${targetRowId}').querySelector('.acomp-results-container').innerHTML = ''"></button>`;

    if (!huespedes || huespedes.length === 0) {
        return `
        <div class="card p-2 my-1 shadow-lg bg-light" style="min-width: 350px;">
            <div class="d-flex justify-content-between align-items-center">
                <h6 class="small text-danger mb-0">No se encontraron resultados.</h6>
                ${closeButton}
            </div>
        </div>`;
    }

    let html = `
    <div class="card p-2 my-1 shadow-lg bg-white" style="min-width: 450px; max-height: 250px; overflow-y: auto;">
        <div class="d-flex justify-content-between align-items-center mb-1 border-bottom pb-1">
            <h6 class="small text-primary mb-0">Resultados encontrados (${huespedes.length}):</h6>
            ${closeButton}
        </div>
        <ul class="list-group list-group-flush small">`;

    huespedes.forEach(h => {
        const huespedData = JSON.stringify({
            id: h.id, 
            nombre: h.nombre, 
            apellido: h.apellido, 
            numDocumento: h.numDocumento, 
            tipoDocumento: h.tipoDocumento
        }).replace(/"/g, '&quot;');
        
        html += `
            <li class="list-group-item d-flex justify-content-between align-items-center p-1">
                ${h.apellido}, ${h.nombre} 
                <span class="text-muted small">${h.numDocumento}</span>
                <button type="button" class="btn btn-sm btn-success py-0 px-2" 
                    onclick='seleccionarAcompanante(${huespedData}, "${targetRowId}")'>
                    Seleccionar
                </button>
            </li>`;
    });

    html += `</ul></div>`;
    return html;
}

// --- LÓGICA DE SELECCIÓN PARA EL ACOMPAÑANTE ---
window.seleccionarAcompanante = function (huesped, rowId) {
    const row = document.getElementById(rowId);
    if (!row) return;

    // Rellenar los inputs de la fila específica (usando los 4 campos)
    row.querySelector('select[name="tipoDocumentoAcomp"]').value = huesped.tipoDocumento;
    row.querySelector('input[name="numDocumentoAcomp"]').value = huesped.numDocumento;
    row.querySelector('input[name="apellidoAcomp"]').value = huesped.apellido;
    row.querySelector('input[name="nombreAcomp"]').value = huesped.nombre;
    
    // Limpiar resultados
    row.querySelector('.acomp-results-container').innerHTML = '';
    
    // Almacenar el ID del huésped para el envío final al backend
    let idInput = row.querySelector('input[name="idAcomp"]');
    if (!idInput) {
         idInput = document.createElement('input');
         idInput.type = 'hidden';
         idInput.name = 'idAcomp';
         row.cells[0].appendChild(idInput);
    }
    idInput.value = huesped.id;
};


// --- FUNCIÓN DE BÚSQUEDA Y SELECCIÓN PARA ACOMPAÑANTES ---
async function handleAcompananteSearch(event) {
    const btn = event.currentTarget;
    const rowId = btn.dataset.rowId;
    const row = document.getElementById(rowId);
    
    // Obtener los inputs de la fila
    const tipoDoc = row.querySelector('select[name="tipoDocumentoAcomp"]').value.trim();
    const nroDoc = row.querySelector('input[name="numDocumentoAcomp"]').value.trim();
    const apellido = row.querySelector('input[name="apellidoAcomp"]').value.trim();
    const nombre = row.querySelector('input[name="nombreAcomp"]').value.trim();
    // El contenedor de resultados ya no está en la misma celda, sino posicionado en la última
    const resultsContainer = row.querySelector('.acomp-results-container');
    
    // Ya NO SE VALIDA: Si todos están vacíos, buscarHuespedesBackend devolverá todos los huéspedes.
    
    // Interfaz de carga
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
    resultsContainer.innerHTML = ''; // Limpiar resultados anteriores

    // Buscar
    const resultados = await buscarHuespedesBackend(apellido, nombre, tipoDoc, nroDoc);

    // Renderizar Resultados
    resultsContainer.innerHTML = renderTablaResultadosAcomp(resultados, rowId);
    
    // Restaurar botón
    btn.disabled = false;
    btn.innerHTML = '🔍';
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
        
        if (seleccionActual.size > 0 || Object.keys(selectionState).length > 0) {
            limpiarSeleccionVisualActual(); 
        }
        
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

function limpiarSeleccionVisualActual() {
  
    seleccionActual.forEach((rango, roomNumber) => {
        const cellsToClear = document.querySelectorAll(`.availability-cell[data-room="${roomNumber}"]`);
        
        cellsToClear.forEach(cell => {
            if (cell.classList.contains('status-ocupada') || cell.classList.contains('selection-start')) {
                const originalStatusKey = cell.dataset.status; 
                const statusMapping = STATUS_MAPPING[originalStatusKey] || STATUS_MAPPING['LIBRE'];

                cell.classList.remove('status-ocupada');
                cell.classList.remove('selection-start'); 
                Object.values(STATUS_MAPPING).forEach(mapping => cell.classList.remove(mapping.class));
                cell.classList.add(statusMapping.class);
                cell.innerText = statusMapping.label; 
            }
        });
    });
    
    // 2. Limpiar el estado de selección de datos
    seleccionActual.clear();
    selectionState = {}; // Limpia el click inicial pendiente en CUALQUIER habitación
    
    const errorMessageElement = document.getElementById('error-message'); 
    if (errorMessageElement) {
        errorMessageElement.classList.add('d-none');
    }
    actualizarListaSeleccionModal();
}

function obtenerDatosDeOcupacion() {
    if (seleccionActual.size === 0) {
        alert("Error: No hay habitaciones seleccionadas.");
        return null;
    }
    
    const roomNumber = seleccionActual.keys().next().value;
    const rango = seleccionActual.get(roomNumber);
    
    const idResponsable = document.getElementById('idResponsableInput').value;
    const infoResponsable = document.getElementById('infoResponsable');
    
    if (!idResponsable || infoResponsable.classList.contains('d-none')) {
        alert("Por favor, seleccione un Huésped Responsable.");
        return null;
    }

    let responsableDTO;

    if (idResponsable) {
        responsableDTO = { id: parseInt(idResponsable) }; 
    } else {
        responsableDTO = {
            apellido: document.getElementById('apellidoResponsableDisplay').textContent || '',
            nombre: document.getElementById('nombreResponsableDisplay').textContent || '',
            tipoDocumento: document.getElementById('tipoDocumentoResponsable').value || 'DNI',
            numeroDocumento: document.getElementById('numDocumentoResponsable').value || '',

            telefono: document.getElementById('telResponsableInput') ? document.getElementById('telResponsableInput').value : 'N/A',
            email: document.getElementById('emailResponsableInput') ? document.getElementById('emailResponsableInput').value : 'N/A@hotel.com',
            ocupacion: document.getElementById('ocupacionResponsableInput') ? document.getElementById('ocupacionResponsableInput').value : 'Turista',
            condicionIVA: document.getElementById('condicionIVAResponsableSelect') ? document.getElementById('condicionIVAResponsableSelect').value : 'CONSUMIDOR_FINAL', 
            id: null
        };
    }
    
    const huespedes = [responsableDTO]; 
    const filasAcomp = document.querySelectorAll('#tablaAcompanantes tbody tr');
    
    filasAcomp.forEach(row => {
        const idInput = row.querySelector('input[name="idAcomp"]');
        const acompId = idInput ? idInput.value : null;

        let acompData;
        
        if (acompId) {
            acompData = { id: parseInt(acompId) }; 
        } else {
            acompData = {
                apellido: row.querySelector('input[name="apellidoAcomp"]').value,
                nombre: row.querySelector('input[name="nombreAcomp"]').value,
                tipoDocumento: row.querySelector('select[name="tipoDocumentoAcomp"]').value,
                numeroDocumento: row.querySelector('input[name="numDocumentoAcomp"]').value,
                telefono: 'N/A', 
                email: 'N/A@hotel.com',
                ocupacion: 'Acompañante',
                condicionIVA: 'CONSUMIDOR_FINAL', 
                id: null,
            };
        }
        
        if ((acompData.apellido && acompData.apellido.trim()) || (acompData.numeroDocumento && acompData.numeroDocumento.trim())) {
            huespedes.push(acompData);
        }
    });

    const estadiaDTO = {
        habitacion: {
            numeroHabitacion: parseInt(roomNumber),
            tipo: roomTypesMap[roomNumber] || 'Estándar', 
            historiaEstados: [
                {
                    estado: 'OCUPADA', 
                    fechaInicio: rango.inicio, 
                    fechaFin: rango.fin
                }
            ]
        },
        huespedes: huespedes,
        fecha_check_in: rango.inicio 
    };
    
    return estadiaDTO;
}
function showDecisionModal() {
    if (!modalDecision) return;

    let totalGuardadas = reservasConfirmadas.length;
    if (ultimaOcupacionGuardada) {
        totalGuardadas += 1;
    }
    
    const contador = document.getElementById('contadorHabitacionesPendientes');
    if(contador) {
        contador.textContent = totalGuardadas;
    }
    
    modalDecision.show();
}
// Función auxiliar para limpiar y reestablecer la grilla
function limpiarGrillaYSeleccion() {
    // 1. Limpiar el estado de selección (mapa y visual)
    limpiarSeleccionVisualActual(); // Usa la nueva función global
    

    const errorMessageElement = document.getElementById('error-message'); 
    if (errorMessageElement) {
        errorMessageElement.classList.add('d-none');
    }
}


// --- FUNCIÓN FINAL DE ENVÍO ---
const API_URL_OCUPACION = '/api/ocupacion/checkin'; 

async function enviarOcupacionesAlBackend(ocupaciones) {
    if (ocupaciones.length === 0) {
        alert("No hay ocupaciones para enviar.");
        return;
    }
    
    console.log("Iniciando envío al backend de:", ocupaciones);

    try {
        const response = await fetch(API_URL_OCUPACION, {
            method: 'POST', 
            headers: {
                'Content-Type': 'application/json', 
            },
            body: JSON.stringify(ocupaciones) 
        });


        if (response.ok) {
            const result = await response.text();
            
            console.log("✅ Éxito al guardar las ocupaciones. Respuesta del servidor:", result);
            
            reservasConfirmadas = [];
            seleccionActual.clear(); 
            alert(`🎉 ¡Check-in completado exitosamente para ${ocupaciones.length} habitación(es)!`);

            window.location.reload(); 
        } else {
            const errorText = await response.text();
            console.error(`❌ Error al enviar ocupaciones. Código: ${response.status}`, errorText);
            alert(`Hubo un error (${response.status}) al intentar ocupar las habitaciones. Consulte la consola para más detalles.`);
        }
    } catch (error) {
        console.error("❌ Error de red/conexión:", error);
        alert("No se pudo conectar con el servidor para realizar el check-in.");
    }
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
            seleccionActual.forEach((rango, hab) => {
                const li = document.createElement('li');
                li.textContent = `Habitación ${hab}: ${rango.inicio} al ${rango.fin}`;
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
const btnBuscarResp = document.getElementById('btnBuscarResponsable');
if (btnBuscarResp) {
    btnBuscarResp.addEventListener('click', async function () {
        const apellido = document.getElementById('busqApellido').value;
        const nombre = document.getElementById('busqNombre').value;
        const tipoDoc = document.getElementById('busqTipoDoc').value;
        const nroDoc = document.getElementById('busqNroDoc').value;

        // 1. Buscar
        const resultados = await buscarHuespedesBackend(apellido, nombre, tipoDoc, nroDoc);

        // 2. Renderizar Resultados
        const contenedorResultados = document.getElementById('resultadosBusquedaResponsable'); // Asegúrate de crear este div en tu HTML
        if (contenedorResultados) {
            contenedorResultados.innerHTML = renderTablaResultados(resultados);

            // 3. Asignar eventos a los botones "Seleccionar"
            contenedorResultados.querySelectorAll('.btn-seleccionar-huesped').forEach(btn => {
                btn.addEventListener('click', function () {
                    const id = this.dataset.id;
                    const nombreCompleto = `${this.dataset.apellido}, ${this.dataset.nombre}`;
                    const doc = `${this.dataset.tipo} ${this.dataset.doc}`;

                    // Rellenar datos del responsable
                    document.getElementById('infoResponsable').classList.remove('d-none');
                    document.getElementById('nombreResponsable').textContent = `${nombreCompleto} (${doc})`;
                    document.getElementById('idResponsableInput').value = id;
                    
                    // Limpiar resultados
                    contenedorResultados.innerHTML = '';
                });
            });
        }
    });
}

// --- BOTÓN AGREGAR ACOMPAÑANTE ---
const btnAgregarAcomp = document.getElementById('btnAgregarAcompananteRow');
if (btnAgregarAcomp) {
    btnAgregarAcomp.addEventListener('click', function () {
        const tbody = document.querySelector('#tablaAcompanantes tbody');
        const row = document.createElement('tr');
        
        const rowId = `acomp-row-${Date.now()}`;
        row.id = rowId;

        row.innerHTML = `
            <td>
                <select class="form-select form-select-sm" name="tipoDocumentoAcomp">
                    <option value="DNI">DNI</option>
                    <option value="PASAPORTE">PASAPORTE</option>
                    <option value="LE">LE</option>
                    <option value="LC">LC</option>
                    <option value="OTRO">OTRO</option>
                </select>
            </td>
            <td><input type="text" class="form-control form-control-sm" placeholder="Número" name="numDocumentoAcomp"></td>
            <td><input type="text" class="form-control form-control-sm" placeholder="Apellido" name="apellidoAcomp"></td>
            <td><input type="text" class="form-control form-control-sm" placeholder="Nombre" name="nombreAcomp"></td>
            
            <td class="position-relative"> 
                <div class="d-flex gap-1 justify-content-center">
                    <button type="button" class="btn btn-sm btn-outline-primary btn-search-acomp" data-row-id="${rowId}">🔍</button>
                    <button type="button" class="btn btn-danger btn-sm" onclick="this.closest('tr').remove()">X</button>
                </div>
                <div class="acomp-results-container" data-row-id="${rowId}" 
                     style="position: absolute; 
                            top: 0; /* Anclado arriba de la celda */
                            right: 100%; /* Posicionado a la izquierda de la celda para no chocar con el botón X */
                            transform: translateY(-50%); /* Ajuste vertical para centrar con el botón */
                            z-index: 1050; /* Alto z-index para que esté sobre otros elementos */
                            min-width: 450px;">
                </div> 
            </td>
        `;
        tbody.appendChild(row);
        
        row.querySelector('.btn-search-acomp').addEventListener('click', handleAcompananteSearch);
    });
}
let ultimaOcupacionGuardada = null; 

const btnDecisionVolverCarga = document.getElementById('btnDecisionVolverCarga');
if (btnDecisionVolverCarga) {
    btnDecisionVolverCarga.addEventListener('click', function() {
        modalDecision.hide();
    });
}

const btnDecisionSeguir = document.getElementById('btnDecisionSeguir');
if (btnDecisionSeguir) {
    btnDecisionSeguir.addEventListener('click', function() {
        modalDecision.hide();
        if (ultimaOcupacionGuardada) {
            reservasConfirmadas.push(ultimaOcupacionGuardada);
            ultimaOcupacionGuardada = null;
        }
        if(modalOcupacion) modalOcupacion.hide();
        limpiarGrillaYSeleccion(); 
        
    });
}
const btnDecisionSalir = document.getElementById('btnDecisionSalir');
if (btnDecisionSalir) {
    btnDecisionSalir.addEventListener('click', function() {
        modalDecision.hide();
        if (ultimaOcupacionGuardada) {
            reservasConfirmadas.push(ultimaOcupacionGuardada);
            ultimaOcupacionGuardada = null;
        }
        if(modalOcupacion) modalOcupacion.hide();
        enviarOcupacionesAlBackend(reservasConfirmadas);
    });
}



// --- BOTÓN CONFIRMAR FINAL
const btnConfirmarFinal = document.getElementById('btnConfirmarOcupacionFinal');
if(btnConfirmarFinal) {
    btnConfirmarFinal.addEventListener('click', function() {
        const nuevaOcupacion = obtenerDatosDeOcupacion();
        if (!nuevaOcupacion) return; 
        
        ultimaOcupacionGuardada = nuevaOcupacion; 
        showDecisionModal();
    });
}