package supervisor.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PDFData {
    private MultipartFile pdf;
    private List<Selection> selections;


    public PDFData(MultipartFile pdf, List<Selection>  selections){
        this.pdf = pdf;
        this.selections = selections;
    }

    // Getters and Setters
    public MultipartFile getPdf() {
        return pdf;
    }

    public String getPDF_PATH() {
        return "C:/Users/admin/Desktop/lllll/LinkifyPDF/Back/wwwroot/pdfs";
    }

    public void setPdf(MultipartFile pdf) {
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
        return "PDFData{" +"\n"+
                "url: "+pdf.getOriginalFilename()+"\n"+
                selections.toString() +"\n"+
                '}';
    }
}

