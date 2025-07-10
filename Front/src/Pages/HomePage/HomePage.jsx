
import "./style.css";
import "./script.js";

const HomePage = () => {
    return (
        <>  
        <div className="liquid-bg"></div>
            
            <div classNameName="main-content">
                <div className="container">
                    <div className="main-board">
                        <div className="pdf-wrapper">
                            <canvas id="pdfCanvas"></canvas>
                            <div id="overlay"></div>
                        </div>
                        <div className="upload-container" id="drop-zone">
                        <input type="file" id="upload-pdf-file" accept="application/pdf" hidden />
                        <label for="upload-pdf-file" className="upload-label">
                            <i className="fa-solid fa-file upload-icon"></i>

                            <div className="upload-text">
                            <strong>Click to upload</strong><br/>
                            or drag & drop a PDF here
                            </div>
                        </label>
                        </div>

                        <input type="button" className="sendData" id="send-data-btn" value="SEND" />

                    </div>
                </div>
            </div>
        </>
    );

}

export default HomePage;