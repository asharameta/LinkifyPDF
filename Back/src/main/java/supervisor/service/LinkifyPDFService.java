package supervisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
import supervisor.exception.PdfEntityNoContentException;
import supervisor.exception.PdfEntityNotFoundException;
import supervisor.mapper.PDFDataMapper;
import supervisor.model.PDFEntity;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;
import supervisor.util.PdfLinkingUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class LinkifyPDFService {
    @Value("${spring.servlet.multipart.location}")
    private String pdfStoragePath;
    private final PdfEntityDAO pdfDAO;
    private final SelectionEntityDAO selectionEntityDAO;

    public LinkifyPDFService(PdfEntityDAO pdfDAO, SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
        this.pdfDAO = pdfDAO;
    }

    public List<PDFEntity> getAllPdfData(){
        return pdfDAO.getAllPdfWithSelections();
    }

    public PDFEntity getPdfData(Long id){
        return pdfDAO.getPDFData(id);
    }

    public String getPDFPath() {
        return pdfStoragePath;
    }

    private InputStreamResource readPDF(String fileName) throws IOException {
        Path filePath = Paths.get(getPDFPath(), fileName);

        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on server");
        }

        return new InputStreamResource(Files.newInputStream(filePath));
    }

    public void saveUploadedPdf(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid or null filename");
        }

        Path destination = Paths.get(getPDFPath()).resolve(Objects.requireNonNull(originalFilename));

        if (Files.exists(destination) && Files.isDirectory(destination)) {
            throw new IllegalArgumentException("Destination path is a directory: " + destination);
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    }


    public List<PdfResponseDTO> getAllPdfDTO(){
        //var selections = selectionEntityDAO.getAllSelectionData();
        var pdfs = pdfDAO.getAllPdfWithSelections();

        return PDFDataMapper.convertToDTOList(getPDFPath(), pdfs);
    }

    public PdfResponseDTO getPdfDTO(Long id) throws IOException {
        PDFEntity PDFEntity = getPdfData(id);

        if(PDFEntity==null){
            throw new PdfEntityNotFoundException(id);
        }
        var selectionEntity = selectionEntityDAO.getSelectionData(id);
        var pdfEntity = pdfDAO.getPDFData(id);
        pdfEntity.setSelectionEntities(selectionEntity);

        return PDFDataMapper.convertToDTO(getPDFPath(), pdfEntity);
    }

    @Transactional
    public void addPdfData(PdfUploadDTO data) throws IOException {
        saveUploadedPdf(data.getFile());

        PDFEntity pdfEntity = PDFDataMapper.convertToEntity(data, LocalDateTime.now());
        List<SelectionEntity> selectionEntities = data.getSelectionEntities();

        Path filePath = Paths.get(getPDFPath(), pdfEntity.getFilename());
        Path modifiedPdfPath = PdfLinkingUtil.generateLinkedPdf(filePath, selectionEntities, data.getCanvasHeightInPixels());
        System.out.println(modifiedPdfPath);
        Files.copy(Files.newInputStream(modifiedPdfPath), modifiedPdfPath, StandardCopyOption.REPLACE_EXISTING);

        Long pdfId = pdfDAO.addPDFData(pdfEntity);
        selectionEntityDAO.addSelectionData(selectionEntities, pdfId);
    }

    public void deletePdfData(Long id){
        //return pdfData.deletePDFData(id);
    }
}
