package supervisor.DTO;

import supervisor.model.PDFEntity;
import supervisor.model.SelectionEntity;

import java.util.List;

public class PdfResponseDTO {
    private String pathToFile;
    private PDFEntity pdfEntity;
    //private List<SelectionEntity> selectionEntities;

    public PdfResponseDTO(String pathToFile, PDFEntity pdfEntity) {
        this.pathToFile = pathToFile;
        this.pdfEntity = pdfEntity;
    }

    public PDFEntity getPdfEntity() {
        return pdfEntity;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void setPdfEntity(PDFEntity pdfEntity) {
        this.pdfEntity = pdfEntity;
    }

//    public List<SelectionEntity> getSelectionEntities() {
//        return selectionEntities;
//    }
//
//    public void setSelectionEntities(List<SelectionEntity> selectionEntities) {
//        this.selectionEntities = selectionEntities;
//    }
}
