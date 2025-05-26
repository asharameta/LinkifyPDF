package supervisor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import supervisor.DTO.PdfDTO;
import supervisor.model.SelectionEntity;

import java.io.IOException;
import java.util.List;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import supervisor.service.LinkifyPDFService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@Controller
public class LinkifyPDFController {
    private final LinkifyPDFService linkifyPDFService;

    public LinkifyPDFController(LinkifyPDFService linkifyPDFService) {
        this.linkifyPDFService = linkifyPDFService;
    }

    @PostMapping(value = "/pdfs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<PdfDTO> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("json") List<SelectionEntity> selectionEntityList,
            @RequestPart("canvasHeight") String canvasHeight
    ) throws IOException {
        linkifyPDFService.saveUploadedPdf(file);

        PdfDTO pdfDTO = new PdfDTO(file.getOriginalFilename(),selectionEntityList, Float.parseFloat(canvasHeight));
        linkifyPDFService.addPdfData(pdfDTO);
        return new ResponseEntity<>(pdfDTO, HttpStatus.CREATED);
    }

    @GetMapping("/pdfs/{id}")
    public ResponseEntity<PdfDTO> getPDFData(@PathVariable("id") long id) throws IOException {
        var pdfData = linkifyPDFService.getPdfDTO(id);


        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @GetMapping("/pdfs")
    public ResponseEntity<List<PdfDTO>> getAllPDFData() {
        List<PdfDTO> pdfData = linkifyPDFService.getAllPdfDTO();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pdfData);
        //return new ResponseEntity<>(pdfData, HttpStatus.OK);
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
