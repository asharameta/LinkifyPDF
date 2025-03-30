package supervisor.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkifyPDFController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello LinkifyPDF!";
    }
}
