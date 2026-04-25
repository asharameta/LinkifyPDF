const API_BASE = 'http://localhost:8080';

let currentDocId = null;
let currentFilename = null;
let currentRenderTask = null;
let currentPdfDoc = null;

window.onload = async () => {
    setupActionButtons();
    await loadDocumentList();
};

function setupActionButtons() {
    document.getElementById('edit-btn').addEventListener('click', () => {
        if (!currentDocId) return;
        window.location.href = `/Pages/HomePage?docId=${currentDocId}`;
    });

    document.getElementById('download-btn').addEventListener('click', () => {
        if (!currentDocId || !currentFilename) return;
        const a = document.createElement('a');
        a.href = `${API_BASE}/documents/${currentDocId}/file`;
        a.download = currentFilename;
        a.click();
    });

    document.getElementById('delete-btn').addEventListener('click', async () => {
        if (!currentDocId) return;
        if (!confirm(`Delete "${currentFilename}"? This cannot be undone.`)) return;
        try {
            const res = await fetch(`${API_BASE}/documents/${currentDocId}`, { method: 'DELETE' });
            if (!res.ok) throw new Error(`Server returned ${res.status}`);
            document.querySelector(`.doc-card[data-id="${currentDocId}"]`)?.remove();
            showPreviewEmpty();
            currentDocId = null;
            currentFilename = null;
            if (document.querySelectorAll('.doc-card').length === 0) {
                document.getElementById('doc-list').innerHTML = '<p class="no-docs">No documents yet. Upload one from the home page.</p>';
            }
        } catch (err) {
            showError('Failed to delete document: ' + err.message);
        }
    });
}

async function loadDocumentList() {
    const listSpinner = document.getElementById('list-spinner');
    listSpinner.style.display = 'block';

    try {
        const res = await fetch(`${API_BASE}/documents`);
        if (!res.ok) throw new Error(`Server returned ${res.status}`);
        const docs = await res.json();

        listSpinner.style.display = 'none';
        const docList = document.getElementById('doc-list');
        docList.innerHTML = '';

        if (!docs || docs.length === 0) {
            docList.innerHTML = '<p class="no-docs">No documents yet. Upload one from the home page.</p>';
            return;
        }

        docs.forEach(doc => {
            const entity = doc.pdfEntity;
            const card = createDocCard(entity);
            docList.appendChild(card);
        });
    } catch (err) {
        listSpinner.style.display = 'none';
        showError('Failed to load documents: ' + err.message);
    }
}

function createDocCard(entity) {
    const card = document.createElement('div');
    card.classList.add('doc-card');
    card.dataset.id = entity.id;

    const uploadDate = entity.uploadedAt
        ? new Date(entity.uploadedAt).toLocaleDateString()
        : 'Unknown date';

    const displayName = entity.filename.replace(/\.[^/.]+$/, '');
    card.innerHTML = `
        <div class="doc-card-name" title="${displayName}">${displayName}</div>
        <div class="doc-card-meta">
            <span>${entity.pageCount} page${entity.pageCount !== 1 ? 's' : ''}</span>
            <span>Uploaded ${uploadDate}</span>
        </div>
    `;

    card.addEventListener('click', () => selectDocument(entity, card));
    return card;
}

async function selectDocument(entity, card) {
    document.querySelectorAll('.doc-card').forEach(c => c.classList.remove('active'));
    card.classList.add('active');

    currentDocId = entity.id;
    currentFilename = entity.filename;

    showPreviewContent();
    document.getElementById('preview-filename').textContent = entity.filename;

    const spinner = document.getElementById('preview-spinner');
    spinner.style.display = 'block';

    try {
        const res = await fetch(`${API_BASE}/documents/${entity.id}/file`);
        if (!res.ok) throw new Error(`Server returned ${res.status}`);
        const blob = await res.blob();
        const arrayBuffer = await blob.arrayBuffer();

        // Keep spinner visible — renderPDF will hide it once the canvas is painted
        await renderPDF(arrayBuffer, entity.selectionEntities || []);
    } catch (err) {
        spinner.style.display = 'none';
        showError('Failed to load PDF: ' + err.message);
    }
}

async function renderPDF(arrayBuffer, selections) {
    const canvas = document.getElementById('pdfCanvas');
    const overlay = document.getElementById('overlay');

    // Cancel the in-flight render and WAIT for it to actually stop.
    // Just calling cancel() is fire-and-forget; the old render keeps writing
    // to the canvas until its promise settles, which causes the "same canvas"
    // error and leaves the canvas in a corrupted / upside-down state.
    if (currentRenderTask) {
        currentRenderTask.cancel();
        try { await currentRenderTask.promise; } catch (_) { /* RenderingCancelledException — expected */ }
        currentRenderTask = null;
    }

    // Destroy the previous PDF document to free memory.
    if (currentPdfDoc) {
        currentPdfDoc.destroy();
        currentPdfDoc = null;
    }

    overlay.innerHTML = '';

    const pdfDoc = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
    currentPdfDoc = pdfDoc;

    const page = await pdfDoc.getPage(1);

    const naturalViewport = page.getViewport({ scale: 1 });
    const wrapper = document.getElementById('pdf-wrapper');
    const availableWidth  = wrapper.clientWidth  - 4;
    const availableHeight = wrapper.clientHeight - 4;
    const scale = Math.min(
        availableWidth  / naturalViewport.width,
        availableHeight / naturalViewport.height,
        2
    );
    const viewport = page.getViewport({ scale });

    // Setting canvas.width/height clears the canvas and resets the 2D context
    // (including any leftover transform from a previous render).
    canvas.width  = viewport.width;
    canvas.height = viewport.height;
    overlay.style.width  = viewport.width  + 'px';
    overlay.style.height = viewport.height + 'px';

    const ctx = canvas.getContext('2d');
    const spinner = document.getElementById('preview-spinner');
    currentRenderTask = page.render({ canvasContext: ctx, viewport });
    try {
        await currentRenderTask.promise;
        spinner.style.display = 'none';
        addHyperlinks(canvas, overlay, selections);
    } catch (e) {
        spinner.style.display = 'none';
        if (e?.name !== 'RenderingCancelledException') throw e;
        // A newer render took over — canvas will be redrawn by that task.
    } finally {
        currentRenderTask = null;
    }
}

function addHyperlinks(canvas, overlay, selections) {
    selections.forEach(sel => {
        const box = document.createElement('div');
        box.classList.add('selection-box');
        // xNorm/yNorm/wNorm/hNorm are true 0-1 fractions — scale to canvas pixels
        box.style.left   = (sel.xNorm * canvas.width)  + 'px';
        box.style.top    = (sel.yNorm * canvas.height) + 'px';
        box.style.width  = (sel.wNorm * canvas.width)  + 'px';
        box.style.height = (sel.hNorm * canvas.height) + 'px';

        const link = document.createElement('a');
        link.href = sel.linkValue;
        link.target = '_blank';
        link.rel = 'noopener noreferrer';
        link.style.cssText = 'display:block;width:100%;height:100%;';
        link.classList.add('hyperlink');

        box.appendChild(link);
        overlay.appendChild(box);
    });
}

function showPreviewContent() {
    document.getElementById('preview-empty').style.display = 'none';
    document.getElementById('preview-content').style.display = 'flex';
}

function showPreviewEmpty() {
    document.getElementById('preview-empty').style.display = 'flex';
    document.getElementById('preview-content').style.display = 'none';
    document.getElementById('overlay').innerHTML = '';
}

function showError(message) {
    const banner = document.getElementById('error-banner');
    banner.textContent = message;
    banner.style.display = 'block';
    setTimeout(() => { banner.style.display = 'none'; }, 5000);
}
