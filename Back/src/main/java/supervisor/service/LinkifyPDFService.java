package supervisor.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
import supervisor.exception.PdfEntityNotFoundException;
import supervisor.mapper.PDFDataMapper;
import supervisor.model.DocumentEntity;
import supervisor.model.DocumentEntityDAO;
import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;
import supervisor.util.PdfLinkingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class LinkifyPDFService {
    private static final Logger log = LoggerFactory.getLogger(LinkifyPDFService.class);

    @Value("${spring.servlet.multipart.location}")
    private String pdfStoragePath;
    private final DocumentEntityDAO pdfDAO;
    private final SelectionEntityDAO selectionEntityDAO;

    public LinkifyPDFService(DocumentEntityDAO pdfDAO, SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
        this.pdfDAO = pdfDAO;
    }

    public List<DocumentEntity> getAllPdfData(){
        return pdfDAO.getAllDocuments();
    }

    public DocumentEntity getPdfData(Long id){
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
        var pdfs = pdfDAO.getAllDocuments();

        return PDFDataMapper.convertToDTOList(getPDFPath(), pdfs);
    }

    public PdfResponseDTO getPdfDTO(Long id) throws IOException {
        DocumentEntity DocumentEntity = getPdfData(id);

        if(DocumentEntity ==null){
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

        DocumentEntity documentEntity = PDFDataMapper.convertToEntity(data, LocalDateTime.now());
        List<SelectionEntity> selectionEntities = data.getSelectionEntities();

        Path filePath = Paths.get(getPDFPath(), documentEntity.getFilename());

        documentEntity.setStoragePath(getPDFPath());
        documentEntity.setMimeType("application/pdf");
        try (PDDocument pdf = Loader.loadPDF(filePath.toFile())) {
            documentEntity.setPageCount(pdf.getNumberOfPages());
        }

        Path modifiedPdfPath = PdfLinkingUtil.generateLinkedPdf(filePath, selectionEntities);
        log.info("Generated linked PDF at: {}", modifiedPdfPath);
        Files.move(modifiedPdfPath, filePath, StandardCopyOption.REPLACE_EXISTING);

        Long pdfId = pdfDAO.addDocument(documentEntity);
        selectionEntityDAO.addSelectionData(selectionEntities, pdfId);
    }

    @Transactional
    public void updatePdfData(Long id, List<SelectionEntity> selectionEntities) throws IOException {
        DocumentEntity doc = getPdfData(id);
        if (doc == null) throw new PdfEntityNotFoundException(id);

        Path filePath = Paths.get(getPDFPath(), doc.getFilename());
        Path modifiedPdfPath = PdfLinkingUtil.generateLinkedPdf(filePath, selectionEntities);
        Files.move(modifiedPdfPath, filePath, StandardCopyOption.REPLACE_EXISTING);

        selectionEntityDAO.deleteSelectionData(id);
        selectionEntityDAO.addSelectionData(selectionEntities, id);
    }

    public void deletePdfData(Long id) throws IOException {
        DocumentEntity doc = getPdfData(id);
        if (doc == null) {
            throw new PdfEntityNotFoundException(id);
        }
        Files.deleteIfExists(Paths.get(getPDFPath(), doc.getFilename()));
        pdfDAO.deletePDFData(id);
    }
}
