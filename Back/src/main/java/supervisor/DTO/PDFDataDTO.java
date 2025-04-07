package supervisor.DTO;

import supervisor.model.Selection;
import java.util.List;

public class PDFDataDTO {
    private String pdf;
    private List<Selection> selections;

    public PDFDataDTO() {
    }

    public String getPdf(){
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public List<Selection> getSelections() {
        return selections;
    }

    public void setSelections(List<Selection> selections) {
        this.selections = selections;
    }

    public String toString() {
        return "PDFData{" + "\n" +
                "pdf: " + pdf + "\n" +
                selections.toString() + "\n" +
                '}';
    }
}
