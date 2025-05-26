package supervisor.DTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import supervisor.model.SelectionEntity;

import java.util.Arrays;
import java.util.List;

public class PdfDTO {
    private Long id;
    private String fileName;
    private String downloadUrl;
    private List<SelectionEntity> selectionEntities;
    private double canvasHeightInPixels;

    public PdfDTO(String fileName, List<SelectionEntity> selectionEntities) {
        this.fileName = fileName;
        this.selectionEntities = selectionEntities;
    }

    public PdfDTO(String fileName, List<SelectionEntity> selectionEntities, double canvasHeightInPixels) {
        this.fileName = fileName;
        this.selectionEntities = selectionEntities;
        this.canvasHeightInPixels = canvasHeightInPixels;
    }

    public void setSelectionEntities(List<SelectionEntity> selectionEntities) {
        this.selectionEntities = selectionEntities;
    }

    public List<SelectionEntity> getSelectionEntities() {
        return selectionEntities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public double getCanvasHeightInPixels() {
        return canvasHeightInPixels;
    }

    public void setCanvasHeightInPixels(double canvasHeightInPixels) {
        this.canvasHeightInPixels = canvasHeightInPixels;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String toString() {
        return "PdfDTO{" + "\n" +
                "id: "+ id+"\n"+
                "filename: "+ fileName+"\n"+
                "downloadUrl: "+ downloadUrl+"\n"+
                '}';
    }
}
