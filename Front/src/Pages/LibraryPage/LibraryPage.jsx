import "./style.css";
import "./script.js";

const LibraryPage = () => {
    <>  
        <h1>YOUR PDF FILE: </h1>
    <input type="button" className="downloadData" id="download-data-btn" value="DOWNLOAD" />

    <div className="container">
        <div className="pdf-wrapper" style="position: relative;">
            <canvas id="pdfCanvas"></canvas>
            <div id="overlay"></div>
        </div>
    </div>
    </>
}

export default LibraryPage;