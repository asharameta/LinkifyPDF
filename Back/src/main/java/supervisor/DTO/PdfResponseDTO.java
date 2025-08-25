package supervisor.DTO;

import supervisor.model.DocumentEntity;

public class PdfResponseDTO {
    private String pathToFile;
    private DocumentEntity documentEntity;
    //private List<SelectionEntity> selectionEntities;

    public PdfResponseDTO(String pathToFile, DocumentEntity documentEntity) {
        this.pathToFile = pathToFile;
        this.documentEntity = documentEntity;
    }

    public DocumentEntity getPdfEntity() {
        return documentEntity;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void setPdfEntity(DocumentEntity documentEntity) {
        this.documentEntity = documentEntity;
    }

//    public List<SelectionEntity> getSelectionEntities() {
//        return selectionEntities;
//    }
//
//    public void setSelectionEntities(List<SelectionEntity> selectionEntities) {
//        this.selectionEntities = selectionEntities;
//    }
}
