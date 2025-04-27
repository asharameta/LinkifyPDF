package supervisor.DTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import supervisor.model.SelectionEntity;

import java.util.Arrays;
import java.util.List;

public class PdfDTO {
    private String pdfName;
    private byte[] pdf;
    private List<SelectionEntity> selectionEntities;

    public PdfDTO(){}

    public PdfDTO(String pdfName, byte[] pdf, List<SelectionEntity> selectionEntity) throws JsonProcessingException {
        this.pdfName = pdfName;
        this.pdf = pdf;
        this.selectionEntities = selectionEntity;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public byte[] getPdf(){
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public List<SelectionEntity> getSelections() {
        return selectionEntities;
    }

    public void setSelections(List<SelectionEntity> selectionEntities) {
        this.selectionEntities = selectionEntities;
    }

    public String toString() {
        return "PDFData{" + "\n" +
                "filename: "+ pdfName+"\n"+
                "pdf: " + Arrays.toString(pdf) + "\n" +
                selectionEntities + "\n" +
                '}';
    }
}
