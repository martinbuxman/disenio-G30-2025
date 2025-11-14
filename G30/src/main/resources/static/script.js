document.addEventListener('DOMContentLoaded', function() {
    const formHuesped = document.getElementById('formAltaHuesped');

    formHuesped.addEventListener('submit', function(e) {
        e.preventDefault();

        // 1. Limpiar todos los mensajes y estilos de error previos
        limpiarErrores(); 
        
        const formData = new FormData(formHuesped);
        
        // Convertir a JSON anidado para direccion
        const cuitValue = formData.get('cuit');
        const data = {
            nombre: formData.get('nombre'),
            apellido: formData.get('apellido'),
            tipo_documento: formData.get('tipo_documento'),
            // Asegurarse de que los números sean Long/Integer y no strings vacíos si son opcionales
            num_documento: parseInt(formData.get('num_documento')),
            cuit: cuitValue && cuitValue.trim() !== '' ? parseInt(cuitValue) : null,
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
                return resp.json(); // Respuesta 200 OK
            } else if (resp.status === 400) {
                // Errores de validación
                const errorData = await resp.json();
                mostrarErroresEnPantalla(errorData);
                throw new Error('Errores de validación encontrados.'); 
            } else {
                // Errores del servidor (500) u otros
                const text = await resp.text();
                throw new Error(`Error HTTP ${resp.status}: ${text.substring(0, 100)}...`);
            }
        })
        .then(json => {
            // Lógica de éxito (200 OK)
            const mensajeGeneral = document.getElementById('mensajeHuesped');
            
            mensajeGeneral.innerHTML = `Huésped <strong>${json.nombre} ${json.apellido}</strong> dado de alta con éxito!`;
            
            // Estilo de éxito (verde)
            mensajeGeneral.classList.remove('alert-danger');
            mensajeGeneral.classList.add('alert-success');
            
            formHuesped.reset();
        })
        .catch(err => {
            // Lógica para capturar errores que NO son 400
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

    // --------------------------------------------------------------------------------

    /**
     * Limpia los estilos de borde rojo de los inputs y el mensaje general.
     */
    function limpiarErrores() {
        // Limpia el borde rojo de todos los campos de formulario
        document.querySelectorAll('input, select').forEach(el => {
            el.classList.remove('is-invalid'); 
        });
        
        // Limpia el contenedor de mensajes
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