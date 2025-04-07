package supervisor.controller;

import jakarta.mail.MessagingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import supervisor.mapper.PDFDataMapper;
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

    @PostMapping(value = "/pdfData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("json") List<Selection> jsonData
    ) throws IOException {
        System.out.println(jsonData);
        PDFData pdfData = new PDFData(file, jsonData);
        linkifyPDFService.addPdfData(pdfData);
        return new ResponseEntity<>(pdfData, HttpStatus.CREATED);
    }

    @GetMapping("/pdfData/{id}")
    public ResponseEntity<?> getPDFData(@PathVariable int id){
        var pdfData = linkifyPDFService.getPdfData(id);

        if (pdfData == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var pdfToReturn = PDFDataMapper.convertToDTO(pdfData);

        return new ResponseEntity<>(pdfToReturn, HttpStatus.OK);
    }

    @GetMapping("/pdfData")
    public ResponseEntity<?> getAllPDFData() throws IOException, MessagingException {
        var pdfData = linkifyPDFService.getAllPdfData();

        if (pdfData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var pdfToReturn = PDFDataMapper.convertToDTOList(pdfData);

        return new ResponseEntity<>(pdfToReturn, HttpStatus.OK);
    }

    @DeleteMapping("/pdfData/{id}")
    public ResponseEntity<?> deletePDFData(@PathVariable int id){
        //#TODO
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
