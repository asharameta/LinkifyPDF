package supervisor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import supervisor.model.PDFData;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@Controller
public class LinkifyPDFController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello LinkifyPDF!";
    }

    @RequestMapping(value = "/send_pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> selections(@RequestBody PDFData pdfToAdd) {
        System.out.println(pdfToAdd.toString());
        return new ResponseEntity<>(pdfToAdd, HttpStatus.CREATED);
    }
}
