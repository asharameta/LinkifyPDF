package supervisor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import supervisor.model.PDFData;
import supervisor.model.Selection;
import supervisor.service.LinkifyPDFService;

import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@Controller
public class LinkifyPDFController {
    private final LinkifyPDFService linkifyPDFService;

    public LinkifyPDFController(LinkifyPDFService linkifyPDFService) {
        this.linkifyPDFService = linkifyPDFService;
    }



    @GetMapping("/hello")
    public String sayHello() {
        return "Hello LinkifyPDF!";
    }

    @PostMapping("/pdfData")
    @ResponseBody
    public ResponseEntity<?> addPDFData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("json") List<Selection> jsonData
    ) {
        var pdfData = new PDFData(file, jsonData);
        linkifyPDFService.addPdfData(pdfData);
        return new ResponseEntity<>(pdfData, HttpStatus.CREATED);
    }

    @GetMapping("/pdfData/{id}")
    public ResponseEntity<?> getPDFData(@PathVariable int id){
        var pdfToReturn = linkifyPDFService.getPdfData(id);

        if (pdfToReturn == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pdfToReturn, HttpStatus.OK);
    }

    @GetMapping("/pdfData")
    public ResponseEntity<?> getAllPDFData(){
        var pdfToReturn = linkifyPDFService.getAllPdfData();

        if (pdfToReturn.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pdfToReturn, HttpStatus.OK);
    }

    @DeleteMapping("/pdfData/{id}")
    public ResponseEntity<?> deletePDFData(@PathVariable int id){
        //#TODO
        return null;
    }

}
