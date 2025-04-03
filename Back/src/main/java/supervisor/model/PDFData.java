package supervisor.model;

import java.util.List;

public class PDFData {
    private String pdf;
    private List<Selection> selections;

    // Getters and Setters
    public String getPdf() {
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

    @Override
    public String toString() {
        return "PDFData{" +
                "url here = " +"\n"+
                selections.toString() +
                '}';
    }
}