package supervisor.DTO;

import supervisor.model.SelectionEntity;
import java.util.List;

public class PdfDTO {
    private String pdf;
    private SelectionEntity selectionEntities;

    public PdfDTO() {
    }

    public String getPdf(){
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public SelectionEntity getSelections() {
        return selectionEntities;
    }

    public void setSelections(SelectionEntity selectionEntities) {
        this.selectionEntities = selectionEntities;
    }

    public String toString() {
        return "PDFData{" + "\n" +
                "pdf: " + pdf + "\n" +
                selectionEntities.toString() + "\n" +
                '}';
    }
}
