document.addEventListener('DOMContentLoaded', function () {
    const volSelect = document.getElementById('vol');
    const classeSelect = document.getElementById('classe');
    const nombrePlacesInput = document.getElementById('nombrePlaces');
    const prixTotalInput = document.getElementById('prixTotal');

    if (!classeSelect || !prixTotalInput) {
        return;
    }

    async function refreshPrix() {
        const idVol = volSelect ? volSelect.value : null;
        const classe = classeSelect.value;
        const nombrePlaces = nombrePlacesInput ? nombrePlacesInput.value : null;

        if (!idVol || !classe || !nombrePlaces) {
            return;
        }

        try {
            const url = `/reservations/prix?idVol=${encodeURIComponent(idVol)}` +
                `&classe=${encodeURIComponent(classe)}` +
                `&nombrePlaces=${encodeURIComponent(nombrePlaces)}`;

            const resp = await fetch(url, {
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!resp.ok) {
                return;
            }

            const data = await resp.json();
            if (data && data.prixTotal != null) {
                prixTotalInput.value = data.prixTotal;
            }
        } catch (e) {
            // ignore
        }
    }

    if (volSelect) {
        volSelect.addEventListener('change', refreshPrix);
    }
    classeSelect.addEventListener('change', refreshPrix);
    if (nombrePlacesInput) {
        nombrePlacesInput.addEventListener('input', refreshPrix);
        nombrePlacesInput.addEventListener('change', refreshPrix);
    }

    refreshPrix();
});

document.addEventListener('DOMContentLoaded', function () {
    function normalizeText(s) {
        return (s || '').toString().toLowerCase().trim();
    }

    function buildPerColumnFilter(table, tableIndex) {
        const thead = table.querySelector('thead');
        const headerCells = thead ? Array.from(thead.querySelectorAll('th')) : [];
        if (headerCells.length === 0) {
            return;
        }

        const tbody = table.querySelector('tbody');
        if (!tbody) {
            return;
        }

        const panel = document.createElement('div');
        panel.className = 'card mb-3';

        const panelHeader = document.createElement('div');
        panelHeader.className = 'card-header d-flex justify-content-between align-items-center';

        const title = document.createElement('div');
        title.className = 'fw-semibold';
        title.textContent = 'Filtres';

        const toggleBtn = document.createElement('button');
        toggleBtn.type = 'button';
        toggleBtn.className = 'btn btn-sm btn-outline-secondary';
        toggleBtn.textContent = 'Masquer';

        panelHeader.appendChild(title);
        panelHeader.appendChild(toggleBtn);

        const panelBody = document.createElement('div');
        panelBody.className = 'card-body';

        const row = document.createElement('div');
        row.className = 'row g-2';

        const inputIds = [];
        headerCells.forEach((th, idx) => {
            const col = document.createElement('div');
            col.className = 'col-12 col-md-6 col-lg-4';

            const label = document.createElement('label');
            label.className = 'form-label';
            label.textContent = (th.textContent || '').trim() || ('Colonne ' + (idx + 1));

            const input = document.createElement('input');
            input.type = 'text';
            input.className = 'form-control';
            input.placeholder = 'Filtrer...';
            input.id = `table-filter-${tableIndex}-${idx}`;

            inputIds.push(input.id);

            col.appendChild(label);
            col.appendChild(input);
            row.appendChild(col);
        });

        const actions = document.createElement('div');
        actions.className = 'd-flex gap-2 justify-content-end mt-3';

        const applyBtn = document.createElement('button');
        applyBtn.type = 'button';
        applyBtn.className = 'btn btn-primary';
        applyBtn.textContent = 'Filtrer';

        const resetBtn = document.createElement('button');
        resetBtn.type = 'button';
        resetBtn.className = 'btn btn-outline-secondary';
        resetBtn.textContent = 'Reinitialiser';

        actions.appendChild(resetBtn);
        actions.appendChild(applyBtn);

        panelBody.appendChild(row);
        panelBody.appendChild(actions);

        panel.appendChild(panelHeader);
        panel.appendChild(panelBody);

        function getFilters() {
            return inputIds.map(id => normalizeText(document.getElementById(id).value));
        }

        function cellText(rowEl, idx) {
            const cells = rowEl.querySelectorAll('td');
            if (idx < 0 || idx >= cells.length) {
                return '';
            }
            return normalizeText(cells[idx].innerText);
        }

        function applyFilter() {
            const filters = getFilters();
            const rows = Array.from(tbody.querySelectorAll('tr'));

            rows.forEach(r => {
                const ok = filters.every((f, idx) => {
                    if (!f) {
                        return true;
                    }
                    return cellText(r, idx).includes(f);
                });
                r.style.display = ok ? '' : 'none';
            });
        }

        function resetFilter() {
            inputIds.forEach(id => {
                const el = document.getElementById(id);
                if (el) {
                    el.value = '';
                }
            });
            applyFilter();
        }

        applyBtn.addEventListener('click', applyFilter);
        resetBtn.addEventListener('click', resetFilter);

        inputIds.forEach(id => {
            const el = document.getElementById(id);
            if (!el) {
                return;
            }
            el.addEventListener('keydown', function (e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    applyFilter();
                }
            });
        });

        toggleBtn.addEventListener('click', function () {
            const hidden = panelBody.style.display === 'none';
            panelBody.style.display = hidden ? '' : 'none';
            toggleBtn.textContent = hidden ? 'Masquer' : 'Afficher';
        });

        const responsive = table.closest('.table-responsive');
        if (responsive) {
            responsive.parentNode.insertBefore(panel, responsive);
        } else {
            table.parentNode.insertBefore(panel, table);
        }
    }

    const tables = Array.from(document.querySelectorAll('table.table'));
    tables.forEach((t, i) => buildPerColumnFilter(t, i));
});
