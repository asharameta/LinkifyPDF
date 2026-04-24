let isSelecting = false;
let startX, startY, endX, endY;
let pdfInstance = null;
let currentPage = 1;
let renderTask = null;
let currentSelectionBox = null;
let hyperlinks = [];

window.onload = () => {
    createContextMenu();
    createEventHandlers();
};

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

    document.getElementById('reset-btn').addEventListener('click', () => {
        const { overlay } = getCanvasElements();
        overlay.querySelectorAll('.selection-box').forEach(b => b.remove());
        selectedAreas = [];
        hyperlinks = [];
    });

    document.getElementById('new-pdf-btn').addEventListener('click', () => {
        const { overlay } = getCanvasElements();
        overlay.querySelectorAll('.selection-box').forEach(b => b.remove());
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
                loadPDF(reader.result);
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

function loadPDF(pdfData) {
    const spinner = document.getElementById('pdf-spinner');
    spinner.style.display = 'block';
    pdfjsLib.getDocument({ data: pdfData }).promise
        .then((pdf) => {
            pdfInstance = pdf;
            renderPage(1);
            updateVisibility();
        })
        .catch((error) => {
            console.error("Error loading PDF:", error);
            spinner.style.display = 'none';
        });
}

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
        box.querySelector("a").href = newUrl;
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
    const file = document.querySelector('#upload-pdf-file').files[0];
    if (!file) {
        alert("Please upload a PDF file first.");
        return;
    }
    const { canvas } = getCanvasElements();
    const formData = new FormData();

    formData.append("file", file);
    formData.append("json", new Blob(
        [JSON.stringify(selectedAreas)],
        { type: "application/json" }
    ));
    formData.append("canvasHeight", JSON.stringify(canvas.offsetHeight));



    console.info("Sending data to backend:", {
        file: formData.get("file"),
        json: formData.get("json"),
        offsetHeight: formData.get("canvasHeight")
    });

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