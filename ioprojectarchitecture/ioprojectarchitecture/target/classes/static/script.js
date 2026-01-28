function renderJson(data) {
    if (data === null) return '<i>null</i>';

    if (typeof data !== 'object') {
        return `<span>${data}</span>`;
    }

    if (Array.isArray(data)) {
        if (data.length === 0) return '<i>(pusta lista)</i>';

        return `
            <ul>
                ${data.map(item => `<li>${renderJson(item)}</li>`).join('')}
            </ul>
        `;
    }

    let html = '<table class="json-table">';
    for (const key in data) {
        html += `
            <tr>
                <td class="key">${key}</td>
                <td class="value">${renderJson(data[key])}</td>
            </tr>
        `;
    }
    html += '</table>';
    return html;
}


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

        const formData = new FormData();
        formData.append("file", file);

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

                const heatingLimit = parseFloat(document.getElementById('heatingLimit').value);
                if (!isNaN(heatingLimit)) {
                    formData.append("heatingLimit", heatingLimit);
                }
                break;
        }

        fetch(url, { method: 'POST', body: formData })
            .then(response => response.json())
            .then(data => {
                reportDiv.innerHTML += `
                                    <div class="report-card">
                                        <h3>${text}</h3>
                                        ${renderJson(data)}
                                    </div>
                                `;
            })
            .catch(err => {
                console.error(err);
                reportDiv.innerHTML +=
                `
                                    <div class="report-card" style="color:red">
                                        Błąd przy ${text}
                                    </div>
                                `;
            });
    });
}
