package supervisor.util;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import supervisor.model.SelectionEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PdfLinkingUtil {
    public static Path generateLinkedPdf(Path pdfFile, List<SelectionEntity> areas) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile.toFile())) {
            PDPage page = document.getPage(0);

            for (SelectionEntity area : areas) {
                PDAnnotationLink link = new PDAnnotationLink();

                float x = (float) Math.round(area.getX() * 1000.0) / 1000.0f;
                float y = (float) Math.round(area.getY() * 1000.0) / 1000.0f;
                float width = (float) Math.round(area.getWidth() * 1000.0) / 1000.0f;
                float height = (float) Math.round(area.getHeight() * 1000.0) / 1000.0f;

                System.out.println(x);
                System.out.println(y);
                System.out.println(width);
                System.out.println(height);

                PDRectangle rect = new PDRectangle(x, y, width, height);
                link.setRectangle(rect);

                PDActionURI action = new PDActionURI();
                action.setURI(area.getUrl());
                link.setAction(action);

                page.getAnnotations().add(link);
            }


            Path tempFile = Files.createTempFile("linked-", ".pdf");
            document.save(tempFile.toFile());
            return tempFile;
        }
    }
}
