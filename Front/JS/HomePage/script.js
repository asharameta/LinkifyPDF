let isSelecting = false;
let startX, startY, endX, endY;
let pdfInstance = null;
let currentPage = 1;
let renderTask = null;
let currentSelectionBox = null;
let hyperlinks = [];
let editingDocId = null;

window.onload = async () => {
    createContextMenu();
    createEventHandlers();

    const params = new URLSearchParams(window.location.search);
    const docId = params.get('docId');
    if (docId) {
        await loadDocumentForEditing(parseInt(docId, 10));
    }
};

async function loadDocumentForEditing(docId) {
    editingDocId = docId;
    document.getElementById('send-data-btn').textContent = 'Update';

    document.querySelector('.upload-container').style.display = 'none';
    document.getElementById('edit-loading').style.display = 'flex';

    try {
        const [metaRes, fileRes] = await Promise.all([
            fetch(`http://localhost:8080/documents/${docId}`),
            fetch(`http://localhost:8080/documents/${docId}/file`),
        ]);
        if (!metaRes.ok) throw new Error(`Metadata fetch failed (${metaRes.status})`);
        if (!fileRes.ok) throw new Error(`File fetch failed (${fileRes.status})`);

        const meta = await metaRes.json();
        const savedSelections = meta.pdfEntity?.selectionEntities ?? [];

        const blob = await fileRes.blob();
        const arrayBuffer = await blob.arrayBuffer();

        const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
        pdfInstance = pdf;
        await renderPage(1);

        document.getElementById('edit-loading').style.display = 'none';
        updateVisibility();

        if (savedSelections.length > 0) {
            renderExistingLinks(savedSelections);
            showToast(`Loaded ${savedSelections.length} saved link${savedSelections.length !== 1 ? 's' : ''}`, 'info');
        }
    } catch (e) {
        console.error('Failed to load document for editing:', e);
        document.getElementById('edit-loading').style.display = 'none';
        showToast('Failed to load document: ' + e.message, 'error');
    }
}

function createContextMenu() {
    const contextMenu = document.createElement("div");
    contextMenu.id = "context-menu";

    const editOption = document.createElement("div");
    editOption.innerText = "Edit Link";
    editOption.classList.add("context-menu-item");
    editOption.onclick = async () => await editHyperlink(contextMenu.targetBox);

    const deleteOption = document.createElement("div");
    deleteOption.innerText = "Delete Link";
    deleteOption.classList.add("context-menu-item");
    deleteOption.onclick = () => {
        const { overlay } = getCanvasElements();
        const box = contextMenu.targetBox;
        overlay.removeChild(box);
        const idx = parseInt(box.dataset.areaIndex, 10);
        selectedAreas.splice(idx, 1);
        hyperlinks = hyperlinks.filter(h => h !== box);
        // Re-index remaining boxes
        hyperlinks.forEach((h, i) => { h.dataset.areaIndex = i; });
    };

    const cancelOption = document.createElement("div");
    cancelOption.innerText = "Cancel";
    cancelOption.classList.add("context-menu-item");
    cancelOption.onclick = () => {
        contextMenu.style.display = "none";
    };

    contextMenu.appendChild(editOption); // Already existing
    contextMenu.appendChild(deleteOption);
    contextMenu.appendChild(cancelOption);
    document.body.appendChild(contextMenu);

    document.addEventListener("click", () => (contextMenu.style.display = "none"));

    window.customContextMenu = contextMenu;
}

function createEventHandlers() {
    const { overlay } = getCanvasElements();

    document.getElementById('send-data-btn').addEventListener('click', async () => {
        await sendDataToBackend();
    });

    document.getElementById('merge-btn').addEventListener('click', () => {
        mergeLinks(50);
    });

    document.getElementById('reset-btn').addEventListener('click', () => {
        const prompt = document.getElementById('inline-prompt');
        if (prompt && prompt.style.display !== 'none') {
            document.getElementById('inline-prompt-cancel').click();
        }
        const { overlay } = getCanvasElements();
        overlay.querySelectorAll('.selection-box').forEach(b => b.remove());
        overlay._existingLinks = null;
        selectedAreas = [];
        hyperlinks = [];
    });

    document.getElementById('new-pdf-btn').addEventListener('click', () => {
        const { overlay } = getCanvasElements();
        overlay.querySelectorAll('.selection-box').forEach(b => b.remove());
        overlay._existingLinks = null;
        selectedAreas = [];
        hyperlinks = [];
        pdfInstance = null;
        currentPage = 1;
        document.getElementById('upload-pdf-file').value = '';
        const pdfWrapper = document.querySelector('.pdf-wrapper');
        const uploadContainer = document.querySelector('.upload-container');
        const actionBar = document.getElementById('action-bar');
        pdfWrapper.style.display = 'none';
        uploadContainer.style.display = '';
        actionBar.style.display = 'none';
    });


    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('upload-pdf-file');

    ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, e => {
            e.preventDefault();
            dropZone.classList.add('dragover');
        });
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, e => {
            e.preventDefault();
            dropZone.classList.remove('dragover');
        });
    });

    dropZone.addEventListener('drop', e => {
        const files = e.dataTransfer.files;
        if (files.length > 0 && files[0].type === "application/pdf") {
            fileInput.files = files;
            // Optional: Trigger a change event if needed
            fileInput.dispatchEvent(new Event('change'));
        }
    });

    fileInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (file && file.type === "application/pdf") {
            const reader = new FileReader();
            reader.readAsArrayBuffer(file);
            reader.onload = function () {
                loadPDF(reader.result, file);
            };
        } else {
            alert("Please upload a valid PDF file.");
        }
    });

    overlay.addEventListener('pointerdown', (event) => {
        if (event.button === 2 || event.target !== overlay) return;
        overlay.setPointerCapture(event.pointerId);
        isSelecting = true;
        const rect = overlay.getBoundingClientRect();
        startX = event.clientX - rect.left;
        startY = event.clientY - rect.top;
        endX = startX;
        endY = startY;
        currentSelectionBox = createSelectionBox();
    });

    overlay.addEventListener('pointermove', (event) => {
        if (!isSelecting || !currentSelectionBox) return;
        const rect = overlay.getBoundingClientRect();
        endX = event.clientX - rect.left;
        endY = event.clientY - rect.top;
        drawSelectionBox(currentSelectionBox);
    });

    overlay.addEventListener('pointerup', async (event) => {
        if (!isSelecting || !currentSelectionBox) return;
        isSelecting = false;
        const w = Math.abs(endX - startX);
        const h = Math.abs(endY - startY);
        if (w < 8 || h < 8) {
            currentSelectionBox.remove();
            currentSelectionBox = null;
            return;
        }
        await addHyperlink(currentSelectionBox, event);
        currentSelectionBox = null;
    });

    document.addEventListener("contextmenu", (event) => {
        const targetBox = event.target.closest(".selection-box");

        if (targetBox) {
            event.preventDefault(); // Only prevent default if clicking on a box
            let contextMenu = window.customContextMenu;
            if (!contextMenu) {
                createContextMenu();
                contextMenu = window.customContextMenu;
            }

            contextMenu.targetBox = targetBox;
            contextMenu.style.left = `${event.pageX}px`;
            contextMenu.style.top = `${event.pageY}px`;
            contextMenu.style.display = "block";
        } else {
            // Hide the custom menu if open
            const menu = window.customContextMenu;
            if (menu) menu.style.display = "none";
        }
    });
}

function loadPDF(pdfData, rawFile) {
    const spinner = document.getElementById('pdf-spinner');
    spinner.style.display = 'block';
    pdfjsLib.getDocument({ data: pdfData }).promise
        .then(async (pdf) => {
            pdfInstance = pdf;
            await renderPage(1);
            updateVisibility();
            if (rawFile) await analyzeExistingLinks(rawFile);
        })
        .catch((error) => {
            console.error("Error loading PDF:", error);
            spinner.style.display = 'none';
        });
}

async function analyzeExistingLinks(file) {
    try {
        const form = new FormData();
        form.append('file', file);
        const res = await fetch('http://localhost:8080/pdf/analyze', { method: 'POST', body: form });
        if (!res.ok) return;
        const links = await res.json();
        if (links.length === 0) return;
        renderExistingLinks(links);
        showToast(`${links.length} existing link${links.length > 1 ? 's' : ''} found and highlighted`, 'info');
    } catch (e) {
        console.warn('Could not analyze existing links:', e);
    }
}

function renderExistingLinks(links) {
    const { canvas, overlay } = getCanvasElements();

    // Register all pages' links into selectedAreas so they survive a Send
    links.forEach(l => {
        l._areaIndex = selectedAreas.length;
        selectedAreas.push({
            linkValue: l.linkValue,
            linkType:  l.linkType,
            pageNumber: l.pageNumber,
            xNorm: l.xNorm * canvas.width,
            yNorm: l.yNorm * canvas.height,
            wNorm: l.wNorm * canvas.width,
            hNorm: l.hNorm * canvas.height,
        });
    });

    overlay._existingLinks = links;

    // Render visual boxes for the current page
    links
        .filter(l => l.pageNumber === currentPage - 1)
        .forEach(l => {
            const box = makeExistingLinkBox(l, canvas, overlay);
            hyperlinks.push(box);
        });
}

function makeExistingLinkBox(l, canvas, overlay) {
    const box = document.createElement('div');
    box.classList.add('selection-box');
    box.dataset.existing = 'true';
    box.style.left   = `${l.xNorm * canvas.width}px`;
    box.style.top    = `${l.yNorm * canvas.height}px`;
    box.style.width  = `${l.wNorm * canvas.width}px`;
    box.style.height = `${l.hNorm * canvas.height}px`;
    box.style.cursor = 'grab';
    box.title        = l.linkValue;
    box.dataset.url  = l.linkValue;
    box.dataset.areaIndex = l._areaIndex;

    const link = document.createElement('a');
    link.href = l.linkValue;
    link.target = '_blank';
    link.classList.add('hyperlink');
    link.style.cssText = 'width:100%;height:100%;position:absolute;background-color:transparent;z-index:1;';
    box.appendChild(link);

    overlay.appendChild(box);
    return box;
}

window._existingLinksForPage = function(pageIndex) {
    const { canvas, overlay } = getCanvasElements();
    overlay.querySelectorAll('.selection-box[data-existing]').forEach(b => {
        hyperlinks = hyperlinks.filter(h => h !== b);
        b.remove();
    });
    const links = overlay._existingLinks;
    if (!links) return;
    links
        .filter(l => l.pageNumber === pageIndex)
        .forEach(l => {
            const box = makeExistingLinkBox(l, canvas, overlay);
            hyperlinks.push(box);
        });
};

function updateVisibility() {
    document.querySelector('.pdf-wrapper').style.display = 'block';
    document.querySelector('.upload-container').style.display = 'none';
    document.getElementById('action-bar').style.display = 'flex';
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} visible`;
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => {
        toast.classList.remove('visible');
    }, 3500);
}

function showContextMenu(event, box) {
    const menu = window.customContextMenu;
    menu.style.left = `${event.pageX}px`;
    menu.style.top = `${event.pageY}px`;
    menu.style.display = "block";
    menu.targetBox = box;
}

async function editHyperlink(box) {
    if (!box) return;
    const rect = box.getBoundingClientRect();
    const x = rect.left + window.scrollX;
    const y = rect.bottom + window.scrollY;
    let newUrl = await customPrompt("Enter URL", box.dataset.url, x, y);
    if (newUrl) {
        newUrl = processUrl(newUrl);
        box.dataset.url = newUrl;
        box.title = newUrl;
        const link = box.querySelector("a");
        if (link) link.href = newUrl;
        const idx = parseInt(box.dataset.areaIndex, 10);
        if (!isNaN(idx) && selectedAreas[idx]) selectedAreas[idx].linkValue = newUrl;
    }
    const menu = window.customContextMenu;
    if (menu) menu.style.display = "none";
}

function processUrl(url) {
    if (!url.match(/^(http:\/\/|https:\/\/|mailto:|tel:)/)) {
        return "http://" + url;
    }
    return url;
}

function createSelectionBox() {
    const { overlay } = getCanvasElements();
    const selectionBox = document.createElement('div');
    selectionBox.classList.add('selection-box');
    overlay.appendChild(selectionBox);
    return selectionBox;
}

function drawSelectionBox(box) {
    box.style.left = `${Math.min(startX, endX)}px`;
    box.style.top = `${Math.min(startY, endY)}px`;
    box.style.width = `${Math.abs(endX - startX)}px`;
    box.style.height = `${Math.abs(endY - startY)}px`;
    box.style.display = "block";
}

let selectedAreas = [];

async function addHyperlink(selectionBox, mouseEvent) {
    const x = mouseEvent ? mouseEvent.pageX : window.innerWidth / 2;
    const y = mouseEvent ? mouseEvent.pageY : window.innerHeight / 2;
    const url = processUrl(await customPrompt("Enter URL", "", x, y));
    if (!url) {
        selectionBox.remove();
        return;
    }

    const link = document.createElement("a");

    link.href = url;
    link.target = "_blank";
    link.classList.add("hyperlink");
    link.style.width = "100%";
    link.style.height = "100%";
    link.style.position = "absolute";
    link.style.backgroundColor = "transparent";
    link.style.zIndex = "1";

    selectionBox.appendChild(link);
    selectionBox.style.cursor = "grab";
    selectionBox.dataset.url = url;

    const areaData = {
        linkValue: url,
        linkType: "URL",
        pageNumber: currentPage - 1,
        xNorm: parseFloat(selectionBox.style.left),
        yNorm: parseFloat(selectionBox.style.top),
        wNorm: parseFloat(selectionBox.style.width),
        hNorm: parseFloat(selectionBox.style.height),
    };
    selectionBox.dataset.areaIndex = selectedAreas.length;
    selectedAreas.push(areaData);

    hyperlinks.push(selectionBox);
}

async function renderPage(pageNumber) {
    const spinner = document.getElementById('pdf-spinner');
    const { canvas, ctx, overlay } = getCanvasElements();
    const page = await pdfInstance.getPage(pageNumber);
    const naturalViewport = page.getViewport({ scale: 1 });
    const mainBoard = document.querySelector('.main-board');
    const availableWidth = (mainBoard ? mainBoard.clientWidth : window.innerWidth) - 60;
    const availableHeight = (mainBoard ? mainBoard.clientHeight : window.innerHeight) - 120;
    const scaleW = availableWidth / naturalViewport.width;
    const scaleH = availableHeight / naturalViewport.height;
    const scale = Math.min(scaleW, scaleH, 2);
    const viewport = page.getViewport({ scale });

    canvas.width = viewport.width;
    canvas.height = viewport.height;
    overlay.style.width = `${viewport.width}px`;
    overlay.style.height = `${viewport.height}px`;

    const renderContext = {
        canvasContext: ctx,
        viewport: viewport
    };

    if (renderTask) {
        renderTask.cancel();
    }

    renderTask = page.render(renderContext);
    await renderTask.promise;
    spinner.style.display = 'none';
}

function getCanvasElements() {
    return {
        canvas: document.getElementById('pdfCanvas'),
        ctx: document.getElementById('pdfCanvas').getContext('2d'),
        overlay: document.getElementById('overlay')
    };
}

async function sendDataToBackend() {
    const { canvas } = getCanvasElements();
    const cw = canvas.width;
    const ch = canvas.height;
    const normalizedAreas = selectedAreas.map(a => ({
        ...a,
        xNorm: a.xNorm / cw,
        yNorm: a.yNorm / ch,
        wNorm: a.wNorm / cw,
        hNorm: a.hNorm / ch,
    }));

    if (editingDocId) {
        try {
            const res = await fetch(`http://localhost:8080/documents/${editingDocId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(normalizedAreas),
            });
            if (res.ok) {
                showToast('Document updated! Changes saved to your Library.', 'success');
            } else {
                showToast(`Update failed (${res.status}). Please try again.`, 'error');
            }
        } catch (error) {
            console.error('Error updating document:', error);
            showToast('Could not reach the server. Is the backend running?', 'error');
        }
        return;
    }

    const file = document.querySelector('#upload-pdf-file').files[0];
    if (!file) {
        alert("Please upload a PDF file first.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("json", new Blob(
        [JSON.stringify(normalizedAreas)],
        { type: "application/json" }
    ));

    try {
        const res = await fetch('http://localhost:8080/documents', {
            method: "POST",
            body: formData,
        });
        if (res.ok) {
            showToast('PDF saved successfully! View it in My Library.', 'success');
        } else {
            showToast(`Upload failed (${res.status}). Please try again.`, 'error');
        }
    } catch (error) {
        console.error("Error sending data:", error);
        showToast('Could not reach the server. Is the backend running?', 'error');
    }
}

function getSelectionCoords() {
    const sel = window.getSelection();
    if (!sel.rangeCount) return { x: window.innerWidth / 2, y: window.innerHeight / 2 };
    const range = sel.getRangeAt(0).cloneRange();
    const rect = range.getBoundingClientRect();
    return { x: rect.left + window.scrollX, y: rect.bottom + window.scrollY };
}

function rectsClose(a, b, tolerance) {
    const dx = Math.max(0, Math.max(a.xNorm, b.xNorm) - Math.min(a.xNorm + a.wNorm, b.xNorm + b.wNorm));
    const dy = Math.max(0, Math.max(a.yNorm, b.yNorm) - Math.min(a.yNorm + a.hNorm, b.yNorm + b.hNorm));
    return Math.sqrt(dx * dx + dy * dy) <= tolerance;
}

function mergeLinks(tolerance = 50) {
    if (selectedAreas.length === 0) {
        showToast('No selections to merge.', 'info');
        return;
    }

    const before = selectedAreas.length;

    // Iteratively union pairs that share url+page and are within tolerance
    let changed = true;
    while (changed) {
        changed = false;
        outer: for (let i = 0; i < selectedAreas.length; i++) {
            for (let j = i + 1; j < selectedAreas.length; j++) {
                const a = selectedAreas[i], b = selectedAreas[j];
                if (a.linkValue !== b.linkValue || a.pageNumber !== b.pageNumber) continue;
                if (!rectsClose(a, b, tolerance)) continue;

                // Merge b into a
                const x = Math.min(a.xNorm, b.xNorm);
                const y = Math.min(a.yNorm, b.yNorm);
                selectedAreas[i] = {
                    linkValue:  a.linkValue,
                    linkType:   a.linkType,
                    pageNumber: a.pageNumber,
                    xNorm: x,
                    yNorm: y,
                    wNorm: Math.max(a.xNorm + a.wNorm, b.xNorm + b.wNorm) - x,
                    hNorm: Math.max(a.yNorm + a.hNorm, b.yNorm + b.hNorm) - y,
                };
                selectedAreas.splice(j, 1);
                changed = true;
                break outer;
            }
        }
    }

    // Rebuild DOM from merged selectedAreas
    const { overlay } = getCanvasElements();
    overlay.querySelectorAll('.selection-box, .existing-link-box').forEach(b => b.remove());
    overlay._existingLinks = null;
    hyperlinks = [];

    const pageIndex = currentPage - 1;
    selectedAreas.forEach((a, idx) => {
        a._areaIndex = idx; // keep in sync for non-visible pages
        if (a.pageNumber !== pageIndex) return;

        const box = document.createElement('div');
        box.classList.add('selection-box');
        box.style.left   = `${a.xNorm}px`;
        box.style.top    = `${a.yNorm}px`;
        box.style.width  = `${a.wNorm}px`;
        box.style.height = `${a.hNorm}px`;
        box.style.cursor = 'grab';
        box.dataset.url  = a.linkValue;
        box.dataset.areaIndex = idx;

        const link = document.createElement('a');
        link.href = a.linkValue;
        link.target = '_blank';
        link.classList.add('hyperlink');
        link.style.cssText = 'width:100%;height:100%;position:absolute;background-color:transparent;z-index:1;';
        box.appendChild(link);

        overlay.appendChild(box);
        hyperlinks.push(box);
    });

    const after = selectedAreas.length;
    const merged = before - after;
    if (merged === 0) {
        showToast('No links close enough to merge.', 'info');
    } else {
        showToast(`Merged ${merged} pair${merged !== 1 ? 's' : ''} → ${after} link${after !== 1 ? 's' : ''} remaining.`, 'success');
    }
}