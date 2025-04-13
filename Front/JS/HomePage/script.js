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
        overlay.removeChild(contextMenu.targetBox);
        selectedAreas = selectedAreas.filter(
            area => area !== contextMenu.targetBox
        );
        hyperlinks = hyperlinks.filter(
            h => h !== contextMenu.targetBox
        );
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

    const sendDataButton = document.querySelector('.sendData');
    if (sendDataButton) {
        sendDataButton.addEventListener('click', async () => {
            await sendDataToBackend();
        });
    } else {
        console.error("Button with class '.sendData' not found.");
    }


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

    overlay.addEventListener('mousedown', (event) => {
        if (event.button === 2 || event.target !== overlay) return;
        isSelecting = true;
        const rect = overlay.getBoundingClientRect();
        startX = event.clientX - rect.left;
        startY = event.clientY - rect.top;
        currentSelectionBox = createSelectionBox();
    });

    overlay.addEventListener('mousemove', (event) => {
        if (!isSelecting || !currentSelectionBox) return;
        const rect = overlay.getBoundingClientRect();
        endX = event.clientX - rect.left;
        endY = event.clientY - rect.top;
        drawSelectionBox(currentSelectionBox);
    });

    overlay.addEventListener('mouseup', async () => {
        if (!isSelecting || !currentSelectionBox) return;
        isSelecting = false;
        await addHyperlink(currentSelectionBox);
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
    pdfjsLib.getDocument({ data: pdfData }).promise
        .then((pdf) => {
            pdfInstance = pdf;
            renderPage(1);
            updateVisibility();
        })
        .catch((error) => {
            console.error("Error loading PDF:", error);
        });
}

function updateVisibility() {
    const pdfWrapper = document.querySelector(".pdf-wrapper");
    const sendDataButton = document.querySelector("#send-data-btn");
    const uploadContainer = document.querySelector(".upload-container");
    sendDataButton.style.display = "block";
    uploadContainer.style.display = "none";
    pdfWrapper.style.display = "block";
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
    
    const { x, y } = getSelectionCoords();
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

async function addHyperlink(selectionBox) {
    const { x, y } = getSelectionCoords();
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

    selectedAreas.push({
        url: url,
        x: parseFloat(selectionBox.style.left),
        y: parseFloat(selectionBox.style.top),
        width: parseFloat(selectionBox.style.width),
        height: parseFloat(selectionBox.style.height),
    });

    hyperlinks.push(selectionBox);
}

async function renderPage(pageNumber) {
    const { canvas, ctx, overlay } = getCanvasElements();
    const page = await pdfInstance.getPage(pageNumber);
    const scale = 1.5;
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

    const formData = new FormData();
    formData.append("file", file);
    formData.append("json", new Blob(
        [JSON.stringify(selectedAreas)],
        { type: "application/json" }
    ));

    try {
        await fetch('http://localhost:8080/pdfs', {
            method: "POST",
            body: formData,
        })
            .catch(err => console.error("Error sending data:", err));
    } catch (error) {
        console.error("Error sending data:", error);
    }
}

function getSelectionCoords() {
    const sel = window.getSelection();
    if (!sel.rangeCount) return { x: window.innerWidth / 2, y: window.innerHeight / 2 };
    const range = sel.getRangeAt(0).cloneRange();
    const rect = range.getBoundingClientRect();
    return { x: rect.left + window.scrollX, y: rect.bottom + window.scrollY };
  }