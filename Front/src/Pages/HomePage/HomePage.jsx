
import style from "./style.module.css";
import "./script.js";

const HomePage = () => {
    return (
        <>  
        <div className={style['liquid-bg']}></div>
            
            <div className={style['main-content']}>
                <div className={style.container}>
                    <div className={style['main-board']}>
                        <div className={style['pdf-wrapper']}>
                            <canvas id={style.pdfCanvas}></canvas>
                            <div id={style.overlay}></div>
                        </div>
                        <div className={style['upload-container']} id={style['drop-zone']}>
                        <input type="file" id={style['upload-pdf-file']} accept="application/pdf" hidden />
                        <label htmlFor="upload-pdf-file" className={style['upload-label']}>
                            <i className={`${style['upload-icon']} fa-solid fa-file`}></i>

                            <div className={style['upload-text']}>
                            <strong>Click to upload</strong><br/>
                            or drag & drop a PDF here
                            </div>
                        </label>
                        </div>

                        <input type="button" className={style.sendData} id={style['send-data-btn']} value="SEND" />

                    </div>
                </div>
            </div>
        </>
    );

}

export default HomePage;