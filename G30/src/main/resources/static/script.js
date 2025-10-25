document.getElementById('pingBtn').addEventListener('click', () => {
    fetch('/api/ping')
        .then(response => response.json())
        .then(data => {
            document.getElementById('resultado').textContent = data.msg;
        })
        .catch(err => {
            document.getElementById('resultado').textContent = 'Error: ' + err;
        });
});