package supervisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supervisor.DTO.PdfDTO;
import supervisor.exception.PdfEntityNoContentException;
import supervisor.exception.PdfEntityNotFoundException;
import supervisor.mapper.PDFDataMapper;
import supervisor.model.PDFEntity;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntityDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    private byte[] readPDF(String fileName) throws IOException {
        Path sourcePath = Paths.get(getPDFPath()+"/"+fileName);
        return Files.readAllBytes(sourcePath);
    }

    private void writePDF(byte[] pdfBytes, String filename) throws IOException {
        Path destinationDir = Paths.get(getPDFPath());
        if (Files.notExists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        Path filePath = destinationDir.resolve(filename);
        Files.write(filePath, pdfBytes);
    }


    public List<PdfDTO> getAllPdfDTO(){
        List<byte[]> pdfBytes = pdfDAO.getAllPDFData().stream().map(pdfEntity -> {
            try {
                return readPDF(pdfEntity.getFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).toList();

        if(pdfBytes.isEmpty()){
            throw new PdfEntityNoContentException();
        }

        var selections = selectionEntityDAO.getAllSelectionData();

        return PDFDataMapper.convertToDTOList(selections, pdfBytes);
    }

    public PdfDTO getPdfDTO(Long id) throws IOException {
        PDFEntity PDFEntity = getPdfData(id);

        if(PDFEntity==null){
            throw new PdfEntityNotFoundException(id);
        }
        var selectionEntity = selectionEntityDAO.getSelectionData(id);

        byte[] pdfBytes = readPDF(PDFEntity.getFilename());

        return PDFDataMapper.convertToDTO(selectionEntity, pdfBytes);
    }

    @Transactional
    public void addPdfData(PdfDTO data) throws IOException {
        var pdfBytes = data.getPdf();
        LocalDateTime writeTime = LocalDateTime.now();
        writePDF(pdfBytes, data.getPdfName());

        PDFEntity pdfEntity = PDFDataMapper.convertToEntity(data, writeTime);

        Long pdfId = pdfDAO.addPDFData(pdfEntity);
        var selectionData = data.getSelections();
        selectionEntityDAO.addSelectionData(selectionData, pdfId);
    }

    public void deletePdfData(Long id){
        //return pdfData.deletePDFData(id);
    }
}
