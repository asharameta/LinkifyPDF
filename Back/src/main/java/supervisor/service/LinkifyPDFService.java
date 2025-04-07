package supervisor.service;

import org.springframework.stereotype.Service;
import supervisor.model.PDFData;
import supervisor.model.PDFDataDAO;

import java.io.IOException;
import java.util.List;

@Service
public class LinkifyPDFService {
    private final PDFDataDAO pdfData;

    public LinkifyPDFService(PDFDataDAO pdfData) {
        this.pdfData = pdfData;
    }

    public List<PDFData> getAllPdfData(){
        return pdfData.getAllPDFData();
    }

    public PDFData getPdfData(int id){
        return pdfData.getPDFData(id);
    }

    public void addPdfData(PDFData data) throws IOException {
        pdfData.addPDFData(data);
    }

    public void deletePdfData(int id){
        pdfData.deletePDFData(id);
    }
}
