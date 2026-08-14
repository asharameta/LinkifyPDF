package supervisor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/Pages/HomePage";
    }

    @GetMapping("/Pages/HomePage")
    public String homePage() {
        return "forward:/Pages/HomePage/index.html";
    }

    @GetMapping("/Pages/LibraryPage")
    public String libraryPage() {
        return "forward:/Pages/LibraryPage/index.html";
    }

    @GetMapping("/Pages/OptimizerPage")
    public String optimizerPage() {
        return "forward:/Pages/OptimizerPage/index.html";
    }
}
