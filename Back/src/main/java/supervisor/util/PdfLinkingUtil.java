package supervisor.util;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import supervisor.model.SelectionEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PdfLinkingUtil {
    public static Path generateLinkedPdf(Path pdfFile, List<SelectionEntity> areas, double canvasHeightInPixels) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile.toFile())) {
            PDPage page = document.getPage(0);
            // Get PDF page dimensions in points
            PDRectangle mediaBox = page.getMediaBox();

            areas.forEach(area -> {
                PDRectangle rectangle = getPdRectangle(mediaBox, area, canvasHeightInPixels);
                // Create the hyperlink annotation
                PDAnnotationLink link = new PDAnnotationLink();
                link.setRectangle(rectangle);

                // Set the hyperlink action
                PDActionURI action = new PDActionURI();
                action.setURI(area.getLinkValue());
                link.setAction(action);

                // Add the annotation to the page
                try {
                    page.getAnnotations().add(link);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Path tempFile = Files.createTempFile(pdfFile.getParent(), "linked-", ".pdf");
            document.save(tempFile.toFile());
            return tempFile;
        }
    }

    private static PDRectangle getPdRectangle(PDRectangle mediaBox, SelectionEntity area, double canvasHeightInPixels) {
        float pdfPageHeight = mediaBox.getHeight(); // e.g., 792 for US Letter
        float pdfPageWidth = mediaBox.getWidth();   // e.g., 612 for US Letter

        // Calculate scaling factor (assuming canvas displays the full page height)
        float scale = (float) (pdfPageHeight / canvasHeightInPixels);

        // Transform canvas coordinates to PDF coordinates
        float pdfLowerLeftX = (float) (area.getxNorm() * scale);
        float pdfLowerLeftY = (float) (pdfPageHeight - (area.getyNorm() + area.gethNorm()) * scale);
        float pdfUpperRightX = (float) ((area.getxNorm() + area.getwNorm()) * scale);
        float pdfUpperRightY = (float) (pdfPageHeight - area.getyNorm() * scale);

        // Create PDRectangle for the hyperlink
        return new PDRectangle(pdfLowerLeftX, pdfLowerLeftY,
                pdfUpperRightX - pdfLowerLeftX,
                pdfUpperRightY - pdfLowerLeftY);
    }
}


