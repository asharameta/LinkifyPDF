window.onload = async () => {
    console.info("started library page!");

    createEventHandlers();
    const data = await getPDFData();
    if (data) {
        loadPDF(data);
    } else {
        console.error("No PDF data received from the backend.");
    }
};

function createEventHandlers() {

    const sendDataButton = document.querySelector('.sendData');
    if (sendDataButton) {
        sendDataButton.addEventListener('click', async () => {
            await sendDataToBackend();
        });
    } else {
        console.error("Button with class '.sendData' not found.");
    }
}

async function getPDFData() {
    try {
        const response = fetch('http://localhost:8080/pdfs')
            .then(res => res.json())
            .then(data => {
                const finalArray = data.map(element => {
                    const { pdf, selections } = element;

                    const byteCharacters = atob(pdf);
                    const byteArray = new Uint8Array([...byteCharacters].map(c => c.charCodeAt(0)));
                    const pdfBlob = new Blob([byteArray], { type: 'application/pdf' });

                    return { pdfBlob, selections };
                });

                return finalArray;
            });

        return response;
    } catch (error) {
        console.error("Error parsing response:", error);
        return null;
    }
}

async function loadPDF(pdfData) {
    pdfData.forEach(async element => {
        const { pdfBlob, selections } = element;
        const pdfData = await pdfBlob.arrayBuffer();
        const loadingTask = pdfjsLib.getDocument(pdfData);

        loadingTask.promise.then(function (pdfDocument) {
            renderPage(pdfDocument, 1).then(() => {
                console.log("Page rendered");
                addHyperlinks(selections);
            });
        }).catch(function (error) {
            console.error("Error loading PDF:", error);
        });
    });

}

async function renderPage(pdfDocument, pageNumber) {
    const page = await pdfDocument.getPage(pageNumber);

    const viewport = page.getViewport({ scale: 1.5 });
    const canvas = document.getElementById('pdfCanvas');
    const ctx = canvas.getContext('2d');

    canvas.width = viewport.width;
    canvas.height = viewport.height;

    const renderContext = {
        canvasContext: ctx,
        viewport: viewport
    };

    await page.render(renderContext).promise;
}

function getCanvasElements() {
    return {
        canvas: document.getElementById('pdfCanvas'),
        ctx: document.getElementById('pdfCanvas').getContext('2d'),
        overlay: document.getElementById('overlay'),
    };
}

function addHyperlinks(selections) {
    const { overlay } = getCanvasElements();

    if (!selections || selections.length === 0) {
        console.error("No selections found to add hyperlinks.");
        return;
    }

    selections.forEach((selection) => {
        const selectionBox = document.createElement('div');
        selectionBox.classList.add('selection-box');

        selectionBox.style.left = `${selection.x}px`;
        selectionBox.style.top = `${selection.y}px`;
        selectionBox.style.width = `${selection.width}px`;
        selectionBox.style.height = `${selection.height}px`;
        selectionBox.style.display = "block";

        const link = document.createElement("a");
        link.href = selection.url;
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
            overlay.removeChild(selectionBox);
        };

        selectionBox.appendChild(link);
        selectionBox.appendChild(closeButton);
        selectionBox.style.cursor = "grab";
        overlay.appendChild(selectionBox);
    });
}

function drawSelectionBox(box) {
    box.style.left = `${Math.min(startX, endX)}px`;
    box.style.top = `${Math.min(startY, endY)}px`;
    box.style.width = `${Math.abs(endX - startX)}px`;
    box.style.height = `${Math.abs(endY - startY)}px`;
    box.style.display = "block";
}