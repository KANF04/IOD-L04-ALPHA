function uploadFile() {
    const file = document.getElementById('fileInput').files[0];
    if (!file) { alert("Wybierz plik!"); return; }

    const checkedOptions = Array.from(document.querySelectorAll('input[name="option"]:checked'))
                                .map(cb => cb.value);

    const reportDiv = document.getElementById('report');
    reportDiv.innerHTML = '';

    checkedOptions.forEach(option => {
        let url = '';
        let text = '';
        switch(option) {
            case '0':
                url = '/calculateArea';
                text = "Powierzchnia:";
                break;
            case '1':
                url = '/calculateVolume';
                text = "Objętość:";
                break;
            case '2':
                url = '/calculateLuminosity';
                text = "Oświetlenie:";
                break;
            case '3':
                url = '/calculateHeating';  // ← NOWY ENDPOINT
                text = "Ogrzewanie:";
                break;
        }

        const formData = new FormData();
        formData.append("file", file);

        fetch(url, { method: 'POST', body: formData })
            .then(response => response.json())
            .then(data => {
                let html = `<h3>${text}</h3><pre>${JSON.stringify(data, null, 2)}</pre>`;
                reportDiv.innerHTML += html;
            })
            .catch(err => {
                console.error(err);
                reportDiv.innerHTML += `<p style="color:red">Błąd przy ${option}</p>`;
            });
    });
}
