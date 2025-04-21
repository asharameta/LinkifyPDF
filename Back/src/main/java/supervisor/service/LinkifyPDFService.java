package supervisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import supervisor.DTO.PdfDTO;
import supervisor.exception.PdfEntityNoContentException;
import supervisor.exception.PdfEntityNotFoundException;
import supervisor.mapper.PDFDataMapper;
import supervisor.model.PDFEntity;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkifyPDFService {
    @Value("${pdf.storage.path}")
    private String pdfStoragePath;
    private final PdfEntityDAO pdfDAO;
    private final SelectionEntityDAO selectionEntityDAO;

    public LinkifyPDFService(PdfEntityDAO pdfDAO, SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
        this.pdfDAO = pdfDAO;
    }

    public List<PDFEntity> getAllPdfData(){
        return pdfDAO.getAllPDFData();
    }
    public PDFEntity getPdfData(Long id){
        return pdfDAO.getPDFData(id);
    }

    private String getPDFPath() {
        return pdfStoragePath;
    }

    private byte[] retrievePDF(String fileName) throws IOException {
        Path sourcePath = Paths.get(getPDFPath()+"/"+fileName);
        return Files.readAllBytes(sourcePath);
    }

    public List<PdfDTO> getAllPdfDTO(){
        List<byte[]> pdfBytes = pdfDAO.getAllPDFData().stream().map(pdfEntity -> {
            try {
                return retrievePDF(pdfEntity.getFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).toList();

        if(pdfBytes.isEmpty()){
            throw new PdfEntityNoContentException();
        }
        return PDFDataMapper.convertToDTOList(selectionEntityDAO.getAllSelectionData(), pdfBytes);
    }

    public PdfDTO getPdfDTO(Long id) throws IOException {
        PDFEntity PDFEntity = getPdfData(id);

        if(PDFEntity==null){
            throw new PdfEntityNotFoundException(id);
        }
        var selectionEntity = PDFEntity.getSelections();

        byte[] pdfBytes = retrievePDF(PDFEntity.getFilename());

        return PDFDataMapper.convertToDTO(selectionEntity, pdfBytes);
    }

    public void addPdfData(PDFEntity data) throws IOException {
        //pdfData.addPDFData(data);
    }

    public void deletePdfData(Long id){
        //return pdfData.deletePDFData(id);
    }
}
