package supervisor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
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

    @PostMapping(value = "/pdfs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("selectionEntities") String selectionEntitiesJson,
            @RequestPart("canvasHeightInPixels") String canvasHeightStr
    ) throws IOException {
        List<SelectionEntity> selectionEntities;
        double canvasHeightInPixels;

        try {
            selectionEntities = new ObjectMapper().readValue(
                    selectionEntitiesJson,
                    new TypeReference<List<SelectionEntity>>() {}
            );
            canvasHeightInPixels = Double.parseDouble(canvasHeightStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid input data: " + e.getMessage());
        }

        PdfUploadDTO dto = new PdfUploadDTO(file, selectionEntities, canvasHeightInPixels);

        linkifyPDFService.addPdfData(dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/pdfs/{id}")
    public ResponseEntity<PdfResponseDTO> getPDFData(@PathVariable("id") long id) throws IOException {
        var pdfData = linkifyPDFService.getPdfDTO(id);

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @GetMapping("/pdfs/{id}/file")
    public ResponseEntity<InputStreamResource> getPdfFile(@PathVariable("id") long id) throws IOException {
        var pdf = linkifyPDFService.getPdfData(id);
        Path filePath = Path.of(linkifyPDFService.getPDFPath(), pdf.getFilename());

        var inputStream = new InputStreamResource(Files.newInputStream(filePath));

        return new ResponseEntity<>(inputStream, HttpStatus.OK);
    }

    @GetMapping("/pdfs")
    public ResponseEntity<List<PdfResponseDTO>> getAllPDFData() {
        List<PdfResponseDTO> pdfData = linkifyPDFService.getAllPdfDTO();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pdfData);
    }

    @DeleteMapping("/pdfs/{id}")
    public ResponseEntity<?> deletePDFData(@PathVariable Long id){
//       boolean isDeleted = linkifyPDFService.deletePdfData(id);
//       if(!isDeleted){
//           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//       }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
