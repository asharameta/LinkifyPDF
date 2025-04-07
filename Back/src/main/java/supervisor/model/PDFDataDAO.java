package supervisor.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PDFDataDAO {
    public List<PDFData> tempPDF;

    public PDFDataDAO(){
        tempPDF = new ArrayList<>();
    }

    public List<PDFData> getAllPDFData(){
        return tempPDF;
    }

    public PDFData getPDFData(int i){
        return tempPDF.get(i);
    }

    public void addPDFData(PDFData data) throws IOException {
        MultipartFile pdfFile = data.getPdf();

        Path destinationDir = Paths.get(data.getPDF_PATH());
        if (Files.notExists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        String filename = Objects.requireNonNull(pdfFile.getOriginalFilename());
        Path finalPath = destinationDir.resolve(filename);
        Files.copy(pdfFile.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);

        tempPDF.add(data);
    }

    public void deletePDFData(int id){
        tempPDF.remove(id);
    }
}
