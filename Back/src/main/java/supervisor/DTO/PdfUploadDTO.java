package supervisor.DTO;

import org.springframework.web.multipart.MultipartFile;
import supervisor.model.SelectionEntity;

import java.util.List;

public class PdfUploadDTO {
    private MultipartFile file;
    private List<SelectionEntity> selectionEntities;
    private double canvasHeightInPixels;

    public PdfUploadDTO(MultipartFile file, List<SelectionEntity> selectionEntities) {
        this.file = file;
        this.selectionEntities = selectionEntities;
    }


    public PdfUploadDTO(){}

    public PdfUploadDTO(MultipartFile file, List<SelectionEntity> selectionEntities, double canvasHeightInPixels) {
        this.file = file;
        this.selectionEntities = selectionEntities;
        this.canvasHeightInPixels = canvasHeightInPixels;
    }

    public void setSelectionEntities(List<SelectionEntity> selectionEntities) {
        this.selectionEntities = selectionEntities;
    }

    public List<SelectionEntity> getSelectionEntities() {
        return selectionEntities;
    }

    public double getCanvasHeightInPixels() {
        return canvasHeightInPixels;
    }

    public void setCanvasHeightInPixels(double canvasHeightInPixels) {
        this.canvasHeightInPixels = canvasHeightInPixels;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "PdfDTO {\n" +
                "  file=" + file + ",\n" +
                "  selectionEntities=" + (selectionEntities != null ? selectionEntities : "null") + ",\n" +
                "  canvasHeightInPixels=" + canvasHeightInPixels + "\n" +
                '}';
    }
}
