window.onload = async () => {
    console.info("started library page!");

    const data = await getPDFData();
    if (data) {
        loadPDF(data);
    } else {
        console.error("No PDF data received from the backend.");
    }
};

async function getPDFData() {
    try {
        const response = fetch('http://localhost:8080/pdfData')
            .then(res => res.json())
            .then(data => {
                const finalArray = data.map(element => {
                    const { pdf, selections } = element;

                    const byteCharacters = atob(pdf);
                    const byteArray = new Uint8Array([...byteCharacters].map(c => c.charCodeAt(0)));
                    const pdfBlob = new Blob([byteArray], { type: 'application/pdf' });

                    return {pdfBlob, selections};
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
    document.getElementById("overlay").style.display = "block";

    pdfData.forEach(async element => {
        const { pdfBlob } = element;
        const pdfData = await pdfBlob.arrayBuffer();
        const loadingTask = pdfjsLib.getDocument(pdfData);

        loadingTask.promise.then(function (pdfDocument) {
            console.log("PDF loaded");

            document.getElementById("overlay").style.display = "none";

            renderPage(pdfDocument, 1);
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
    console.log(`Page ${pageNumber} rendered`);
}

function getCanvasElements() {
    return {
        canvas: document.getElementById('pdfCanvas'),
        ctx: document.getElementById('pdfCanvas').getContext('2d'),
        overlay: document.getElementById('overlay'),
    };
}
