const formHuesped = document.getElementById('formAltaHuesped');

formHuesped.addEventListener('submit', function(e) {
    e.preventDefault();

    const formData = new FormData(formHuesped);

    // Convertir a JSON anidado para direccion
    const data = {
        nombre: formData.get('nombre'),
        apellido: formData.get('apellido'),
        tipo_documento: formData.get('tipo_documento'),
        num_documento: parseInt(formData.get('num_documento')),
        cuit: parseInt(formData.get('cuit')),
        fecha_nacimiento: formData.get('fecha_nacimiento'),
        direccion: {
            calle: formData.get('direccion.calle'),
            numero: parseInt(formData.get('direccion.numero')),
            departamento: formData.get('direccion.departamento'),
            piso: parseInt(formData.get('direccion.piso')) || 0,
            codigo_postal: parseInt(formData.get('direccion.codigo_postal')) || 0,
            localidad: formData.get('direccion.localidad'),
            provincia: formData.get('direccion.provincia'),
            pais: formData.get('direccion.pais')
        },
        nacionalidad: formData.get('nacionalidad'),
        telefono: formData.get('telefono'),
        email: formData.get('email'),
        ocupacion: formData.get('ocupacion')
    };

    fetch('/api/huespedes/alta', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(resp => resp.json())
    .then(json => {
        document.getElementById('mensajeHuesped').textContent =
            `Huésped ${json.nombre} ${json.apellido} dado de alta!`;
        formHuesped.reset();
    })
    .catch(err => {
        document.getElementById('mensajeHuesped').textContent = 'Error: ' + err;
    });
});
