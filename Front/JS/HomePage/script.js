let isSelecting = false;
let startX, startY, endX, endY;
let pdfInstance = null;
let currentPage = 1;
let renderTask = null;
let currentSelectionBox = null;
let hyperlinks = [];

window.onload = () => {
    console.info("started home page!");

    createContextMenu();
    createEventHandlers();
};

function createContextMenu() {
    const contextMenu = document.createElement("div");
    contextMenu.id = "context-menu";

    const editOption = document.createElement("div");
    editOption.innerText = "Edit Link";
    editOption.classList.add("context-menu-item");
    editOption.onclick = () => editHyperlink(contextMenu.targetBox);

    contextMenu.appendChild(editOption);
    document.body.appendChild(contextMenu);

    document.addEventListener("click", () => (contextMenu.style.display = "none"));

    window.contextmenu = contextMenu;
}

function createEventHandlers() {
    const { canvas, overlay } = getCanvasElements();

    const sendDataButton = document.querySelector('.sendData');
    if (sendDataButton) {
        sendDataButton.addEventListener('click', async () => {
            console.info("Button clicked!");
            await sendDataToBackend();
        });
    } else {
        console.error("Button with class '.sendData' not found.");
    }

    document.querySelector('#upload-pdf-file').addEventListener('change', function (event) {
        console.info("File upload triggered");
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
        if (event.button === 2) return;
        if (event.target !== overlay) return;
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

    overlay.addEventListener('mouseup', () => {
        if (!isSelecting || !currentSelectionBox) return;
        isSelecting = false;
        addHyperlink(currentSelectionBox);
        currentSelectionBox = null;
    });

    document.addEventListener("contextmenu", (event) => {
        const targetBox = event.target.closest(".hyperlink-container");
        if (!targetBox) return;

        event.preventDefault();

        let contextMenu = document.getElementById("context-menu");
        if (!contextMenu) {
            createContextMenu();
            contextMenu = document.getElementById("context-menu");
        }

        contextMenu.targetBox = targetBox;

        contextMenu.style.left = `${event.pageX}px`;
        contextMenu.style.top = `${event.pageY}px`;
        contextMenu.style.display = "block";
    });
}

function loadPDF(pdfData) {
    pdfjsLib.getDocument({ data: pdfData }).promise
        .then((pdf) => {
            pdfInstance = pdf;
            console.log("PDF loaded successfully");
            renderPage(1);
        })
        .catch((error) => {
            console.error("Error loading PDF:", error);
        });
}

function showContextMenu(event, box) {
    const menu = window.contextMenu;
    menu.style.left = `${event.pageX}px`;
    menu.style.top = `${event.pageY}px`;
    menu.style.display = "block";
    menu.targetBox = box;
}

function editHyperlink(box) {
    let newUrl = prompt("Edit the hyperlink URL:", box.dataset.url);
    if (newUrl) {
        newUrl = processUrl(newUrl);
        box.dataset.url = newUrl;
        box.querySelector("a").href = newUrl;
    }
    window.contextmenu.style.display = "none";
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

function addHyperlink(selectionBox) {
    const url = prompt("Enter the hyperlink URL:");
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

    const closeButton = document.createElement("button");
    closeButton.innerHTML = "❌";
    closeButton.classList.add("close-btn");
    closeButton.style.position = "absolute";
    closeButton.style.background = "transparent";
    closeButton.style.top = "0px";
    closeButton.style.right = "0px";
    closeButton.style.zIndex = "2";
    closeButton.onclick = function (e) {
        e.stopPropagation();
        const { overlay } = getCanvasElements();
        overlay.removeChild(selectionBox);
        selectedAreas = selectedAreas.filter(h => h !== selectionBox);
    };

    selectionBox.appendChild(link);
    selectionBox.appendChild(closeButton);
    selectionBox.style.cursor = "grab";

    selectedAreas.push({
        url: url,
        x: parseFloat(selectionBox.style.left),
        y: parseFloat(selectionBox.style.top),
        width: parseFloat(selectionBox.style.width),
        height: parseFloat(selectionBox.style.height),
    });

    hyperlinks.push(selectionBox);
    makeDraggable(selectionBox);
}

function makeDraggable(element) {
    let offsetX, offsetY, isDragging = false;

    element.addEventListener('mousedown', (e) => {
        if (e.target.tagName === "A" || e.target.classList.contains('close-btn')) return;
        isDragging = true;
        const rect = element.getBoundingClientRect();
        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;
        element.style.cursor = "grabbing";
        e.preventDefault();
    });

    document.addEventListener('mousemove', (e) => {
        if (!isDragging) return;
        const overlay = getCanvasElements().overlay;
        const overlayRect = overlay.getBoundingClientRect();
        let newLeft = e.clientX - overlayRect.left - offsetX;
        let newTop = e.clientY - overlayRect.top - offsetY;
        element.style.left = newLeft + 'px';
        element.style.top = newTop + 'px';
    });

    document.addEventListener('mouseup', () => {
        if (isDragging) {
            isDragging = false;
            element.style.cursor = "grab";
        }
    });
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
    console.log("Page rendered");
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
    
    console.table(formData);
    try {
        await fetch('http://localhost:8080/pdfData', {
            method: "POST",
            body: formData,
        })
        .then(res => res.text())
        .then(console.log)
        .catch(err => console.error("Error sending data:", err));
    } catch (error) {
        console.error("Error sending data:", error);
    }
}