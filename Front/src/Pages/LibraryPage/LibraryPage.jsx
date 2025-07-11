import style from "./style.module.css";
import "./script.js";

const LibraryPage = () => {
        return (
    <>  
            <h1>YOUR PDF FILE: </h1>
            <input type="button" className={style.downloadData} id={style['download-data-btn']} value="DOWNLOAD" />

            <div className={style.container}>
                <div className={['pdf-wrapper']} style={{position: 'relative'}}>
                    <canvas id={style.pdfCanvas}></canvas>
                    <div id={style.overlay}></div>
                </div>
            </div>
    </>
        );
}

export default LibraryPage;