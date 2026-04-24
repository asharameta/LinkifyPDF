package supervisor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
import supervisor.exception.PdfEntityNotFoundException;
import supervisor.model.SelectionEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import supervisor.service.LinkifyPDFService;


@RestController
public class LinkifyPDFController {
    private final LinkifyPDFService linkifyPDFService;

    public LinkifyPDFController(LinkifyPDFService linkifyPDFService) {
        this.linkifyPDFService = linkifyPDFService;
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("json") List<SelectionEntity> selectionEntities,
            @RequestPart("canvasHeight") String canvasHeight
    ) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files are accepted.");
        }

        PdfUploadDTO dto = new PdfUploadDTO(file, selectionEntities, Float.parseFloat(canvasHeight));

        linkifyPDFService.addPdfData(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<PdfResponseDTO> getPDFData(@PathVariable("id") long id) throws IOException {
        var pdfData = linkifyPDFService.getPdfDTO(id);

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @GetMapping("/documents/{id}/file")
    public ResponseEntity<InputStreamResource> getPdfFile(@PathVariable("id") long id) throws IOException {
        var pdf = linkifyPDFService.getPdfData(id);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Path.of(linkifyPDFService.getPDFPath(), pdf.getFilename());

        var inputStream = new InputStreamResource(Files.newInputStream(filePath));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdf.getFilename() + "\"")
                .body(inputStream);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<PdfResponseDTO>> getAllPDFData() {
        List<PdfResponseDTO> pdfData = linkifyPDFService.getAllPdfDTO();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pdfData);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deletePDFData(@PathVariable Long id) throws IOException {
        try {
            linkifyPDFService.deletePdfData(id);
            return ResponseEntity.noContent().build();
        } catch (PdfEntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
