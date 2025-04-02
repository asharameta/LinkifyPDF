let isSelecting = false;
let startX, startY, endX, endY;
let pdfInstance = null;
let currentPage = 1;
let renderTask = null; // Stores active render task

window.onload = () => {
    console.info("started!");

    createEventHandlers();
};

function createEventHandlers() {
    const { canvas } = getCanvasElements();

    // Handle File Upload
    document.getElementById('upload-pdf-file').addEventListener('change', function (event) {
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

    // Mouse Events for Selection
    canvas.addEventListener('mousedown', (event) => {
        isSelecting = true;
    const rect = canvas.getBoundingClientRect();
    startX = event.clientX - rect.left;
    startY = event.clientY - rect.top;
    });

    canvas.addEventListener('mousemove', (event) => {
        if (!isSelecting) return;

    const rect = canvas.getBoundingClientRect();
    endX = event.clientX - rect.left;
    endY = event.clientY - rect.top;

    requestAnimationFrame(() => {
        drawSelection();
    });
    });

    canvas.addEventListener('mouseup', () => {
        isSelecting = false;
        addHyperlink(startX, startY, endX - startX, endY - startY);
    });
}


// Draw Selection Box (Does NOT Re-render PDF)
function drawSelection() {
    const { canvas, ctx } = getCanvasElements();
    ctx.clearRect(0, 0, canvas.width, canvas.height); // Keep PDF intact
    renderPage(currentPage).then(() => {
        ctx.strokeStyle = "red";
        ctx.lineWidth = 2;
        ctx.setLineDash([5, 5]); // Dashed Border
        ctx.strokeRect(startX, startY, endX - startX, endY - startY);
    });
}

// Load & Render PDF
async function loadPDF(pdfData) {
    pdfInstance = await pdfjsLib.getDocument({ data: pdfData }).promise;
    renderPage(currentPage);
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
    overlay.style.position = 'absolute';
    overlay.style.top = canvas.offsetTop + 'px';
    overlay.style.left = canvas.offsetLeft + 'px';

    const renderContext = {
        canvasContext: ctx,
        viewport: viewport
    };

    // Cancel previous render task if it exists
    if (renderTask) {
        renderTask.cancel();
    }

    renderTask = page.render(renderContext);
    await renderTask.promise; // Wait for rendering to complete
}



// Add Hyperlink as an Overlay
function addHyperlink(x, y, width, height) {
    const { overlay } = getCanvasElements();
    const url = prompt("Enter the hyperlink URL:");
    if (!url) return;

    const link = document.createElement("a");
    link.href = url;
    link.target = "_blank";
    link.classList.add("hyperlink");
    link.style.left = `${x}px`;
    link.style.top = `${y}px`;
    link.style.width = `${width}px`;
    link.style.height = `${height}px`;

    overlay.appendChild(link);
}


function getCanvasElements() {
    return {
        canvas: document.getElementById('pdfCanvas'),
        ctx: document.getElementById('pdfCanvas').getContext('2d'),
        overlay: document.getElementById('overlay')
    }
}