package supervisor.controller;

import jakarta.mail.MessagingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import supervisor.model.PDFData;
import supervisor.model.Selection;

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
    public ResponseEntity<?> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("json") List<Selection> jsonData
    ) throws IOException {
        PDFData pdfData = new PDFData(file, jsonData);
        linkifyPDFService.addPdfData(pdfData);
        return new ResponseEntity<>(pdfData, HttpStatus.CREATED);
    }

    @GetMapping("/pdfs/{id}")
    public ResponseEntity<?> getPDFData(@PathVariable int id){
        var pdfData = linkifyPDFService.getPdfDTO(id);

        if (pdfData == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @GetMapping("/pdfs")
    public ResponseEntity<?> getAllPDFData() throws IOException, MessagingException {
        var pdfData = linkifyPDFService.getAllPdfDTO();

        if (pdfData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @DeleteMapping("/pdfs/{id}")
    public ResponseEntity<?> deletePDFData(@PathVariable int id){
       boolean isDeleted = linkifyPDFService.deletePdfData(id);
       if(!isDeleted){
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
