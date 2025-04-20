package supervisor.service;

import org.springframework.stereotype.Service;
import supervisor.DTO.PDFDataDTO;
import supervisor.mapper.PDFDataMapper;
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

    public List<PDFDataDTO> getAllPdfDTO(){
        return PDFDataMapper.convertToDTOList(pdfData.getAllPDFData());
    }

    public PDFData getPdfData(int id){
        return pdfData.getPDFData(id);
    }

    public PDFDataDTO getPdfDTO(int id){
        return PDFDataMapper.convertToDTO(pdfData.getPDFData(id));
    }

    public void addPdfData(PDFData data) throws IOException {
        pdfData.addPDFData(data);
    }

    public boolean deletePdfData(int id){
        return pdfData.deletePDFData(id);
    }
}
