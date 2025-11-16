document.addEventListener('DOMContentLoaded', function() {
    const formHuesped = document.getElementById('formAltaHuesped');
    let datosHuespedPendientes = null; 
    const btnConfirmarGuardado = document.getElementById('btnConfirmarGuardado');
    const cuerpoModal = document.getElementById('cuerpoModalAdvertencia');
    const btnConfirmarSalida = document.getElementById('btnConfirmarSalida');
    const cuerpoModalGuardadoExito = document.getElementById('cuerpoModalGuardadoExito');
    const btnSalirAHomedeExito = document.getElementById('btnSalirAHomedeExito');
    const modalAdvertenciaElement = document.getElementById('modalAdvertencia');
    const modalConfirmarSalidaElement = document.getElementById('modalConfirmarSalida');
    const modalHuespedGuardadoElement = document.getElementById('modalHuespedGuardado');
    const inputTipoDocumento = document.querySelector('[name="tipo_documento"]');
    const formSeleccion = document.getElementById('formSeleccion');
    // --- 2. Inicialización SEGURA de Modales (SOLUCIÓN al TypeError) ---
    // Solo inicializa el objeto Bootstrap Modal si el elemento HTML existe.
    const modalAdvertencia = modalAdvertenciaElement 
        ? new bootstrap.Modal(modalAdvertenciaElement) 
        : null;
        
    const modalConfirmarSalida = modalConfirmarSalidaElement 
        ? new bootstrap.Modal(modalConfirmarSalidaElement) 
        : null;
        
    const modalHuespedGuardado = modalHuespedGuardadoElement 
        ? new bootstrap.Modal(modalHuespedGuardadoElement) 
        : null;

if (formSeleccion) {
        formSeleccion.addEventListener('submit', function(event) {
        event.preventDefault(); // Detiene el envío por defecto del formulario

         const selectedHuesped = document.querySelector('input[name="huespedIdSeleccionado"]:checked');

        if (selectedHuesped) {
            const huespedId = selectedHuesped.value;
             console.log('✅ Huésped seleccionado (ID: ' + huespedId + '). Redirigiendo a / para iniciar reserva.');
            window.location.href = '/'; // Se mantiene tu lógica actual de ir a /
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
            num_documento: parseInt(formData.get('num_documento')),
            cuit: cuitValue && cuitValue.trim() !== '' ? parseInt(cuitValue) : 0,
            fecha_nacimiento: formData.get('fecha_nacimiento'),
            direccion: {
                calle: formData.get('direccion.calle'),
                numero: parseInt(formData.get('direccion.numero')),
                departamento: formData.get('direccion.departamento'),
                piso: parseInt(formData.get('direccion.piso')) || 0,
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
                
                cuerpoModal.textContent = mensajeAdvertencia;
                modalAdvertencia.show();
                
                throw new Error('Conflicto de documento. Esperando confirmación.');
            } else {
                const text = await resp.text();
                throw new Error(`Error HTTP ${resp.status}: ${text.substring(0, 100)}...`);
            }
            })
        .then(json => {
            formHuesped.reset();
            limpiarErrores(); 
            
            cuerpoModalGuardadoExito.innerHTML = `El huésped <strong>${json.nombre} ${json.apellido}</strong> ha sido cargado correctamente.`;
            
            modalHuespedGuardado.show();
        })
        .catch(err => {
            if (err.message !== 'Errores de validación encontrados.') {
                const mensajeGeneral = document.getElementById('mensajeHuesped');
                
                limpiarErrores();
                mensajeGeneral.classList.remove('alert-success');
                mensajeGeneral.classList.add('alert-danger');

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
        modalConfirmarSalida.hide(); 
        
        window.location.href = '/';
    });
}
if(btnConfirmarGuardado){
    btnConfirmarGuardado.addEventListener('click', function() {
    if (datosHuespedPendientes) {
        modalAdvertencia.hide();
        
        fetch('/api/huespedes/alta?forzar=true', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datosHuespedPendientes)
        })
        .then(async resp => {
            if (resp.ok) {
                return resp.json();
            }
            throw new Error('Error al guardar el huésped después de la confirmación.');
        })
        .then(json => {
            formHuesped.reset();
            limpiarErrores(); 
            
            cuerpoModalGuardadoExito.innerHTML = `El huésped <strong>${json.nombre} ${json.apellido}</strong> ha sido cargado correctamente.`;
            
            modalHuespedGuardado.show();
            
            datosHuespedPendientes = null;
        })
        .catch(err => {
            const mensajeGeneral = document.getElementById('mensajeHuesped');
            mensajeGeneral.innerHTML = '<strong>Error Crítico:</strong> ' + err.message;
            mensajeGeneral.classList.add('alert-danger');
            console.error('Error al reenviar:', err);
        });
    }
});
}
if(modalAdvertenciaElement){
    modalAdvertenciaElement.addEventListener('hidden.bs.modal', function () {
        if (inputTipoDocumento) {
            inputTipoDocumento.classList.add('is-invalid');
            
            // Desplazamiento
            inputTipoDocumento.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    });
}
    
    function limpiarErrores() {
        document.querySelectorAll('input, select').forEach(el => {
            el.classList.remove('is-invalid'); 
        });
        
        const mensajeGeneral = document.getElementById('mensajeHuesped');
        mensajeGeneral.textContent = '';
        mensajeGeneral.classList.remove('alert-danger', 'alert-success');
        mensajeGeneral.innerHTML = ''; 
    }

    /**
     * Muestra los errores de validación de la API en el recuadro general y resalta los campos.
     * @param {Object} errores - Objeto JSON con {campo: mensaje, ...}
     */
    function mostrarErroresEnPantalla(errores) {

        limpiarErrores(); 

        const mensajeGeneral = document.getElementById('mensajeHuesped');
        // 💡 Cambio 1: Agregar el título
        let listaErroresHTML = '<strong>Errores encontrados:</strong>'; 
        
        // Aplicar estilos del recuadro rojo
        mensajeGeneral.classList.add('alert-danger'); 

        listaErroresHTML += '<ul>';

        for (const campo in errores) {
            if (errores.hasOwnProperty(campo)) {
                const mensaje = errores[campo];
                
                // 💡 Aplicar borde rojo al input/select
                const inputElement = document.querySelector(`[name="${campo}"]`);
                if (inputElement) {
                    inputElement.classList.add('is-invalid'); 
                }

                // 💡 Cambio 2: Capitalizar la primera letra del nombre del campo
                let nombreCampoAmigable = campo.replace('direccion.', '').replace('_', ' ');
                nombreCampoAmigable = nombreCampoAmigable.charAt(0).toUpperCase() + nombreCampoAmigable.slice(1);
                
                // Agregar el error a la lista
                listaErroresHTML += `<li>${nombreCampoAmigable}: ${mensaje}</li>`;
            }
        }
        
        listaErroresHTML += '</ul>';

        // Insertar el contenido completo
        mensajeGeneral.innerHTML = listaErroresHTML; 
        
        // Desplazamiento
        mensajeGeneral.scrollIntoView({ behavior: 'smooth' });
    }
});