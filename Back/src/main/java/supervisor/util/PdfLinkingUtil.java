package supervisor.util;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import supervisor.DTO.ExistingLinkDTO;
import supervisor.model.SelectionEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfLinkingUtil {
    public static Path generateLinkedPdf(Path pdfFile, List<SelectionEntity> areas) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile.toFile())) {
            // Remove all existing URI annotations so edits/deletes are reflected
            for (PDPage p : document.getPages()) {
                List<PDAnnotation> filtered = p.getAnnotations().stream()
                        .filter(a -> !(a instanceof PDAnnotationLink la && la.getAction() instanceof PDActionURI))
                        .collect(Collectors.toList());
                p.setAnnotations(filtered);
            }

            Map<Integer, List<SelectionEntity>> byPage = areas.stream()
                    .collect(Collectors.groupingBy(SelectionEntity::getPageNumber));

            byPage.forEach((pageNum, pageAreas) -> {
                PDPage page = document.getPage(pageNum);
                PDRectangle mediaBox = page.getMediaBox();

                pageAreas.forEach(area -> {
                    PDRectangle rectangle = getPdRectangle(mediaBox, area);
                    PDAnnotationLink link = new PDAnnotationLink();
                    link.setRectangle(rectangle);

                    PDActionURI action = new PDActionURI();
                    action.setURI(area.getLinkValue());
                    link.setAction(action);

                    try {
                        page.getAnnotations().add(link);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            Path tempFile = Files.createTempFile(pdfFile.getParent(), "linked-", ".pdf");
            document.save(tempFile.toFile());
            return tempFile;
        }
    }

    public static List<ExistingLinkDTO> extractExistingLinks(InputStream pdfStream) throws IOException {
        List<ExistingLinkDTO> result = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(pdfStream.readAllBytes())) {
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage page = document.getPage(i);
                PDRectangle mediaBox = page.getMediaBox();
                float pageW = mediaBox.getWidth();
                float pageH = mediaBox.getHeight();

                for (PDAnnotation ann : page.getAnnotations()) {
                    if (!(ann instanceof PDAnnotationLink link)) continue;
                    PDAction action = link.getAction();
                    if (!(action instanceof PDActionURI uriAction)) continue;

                    String uri = uriAction.getURI();
                    String linkType = uri.startsWith("mailto:") ? "MAILTO"
                                    : uri.startsWith("tel:")    ? "TEL"
                                    : "URL";

                    PDRectangle r = link.getRectangle();
                    double xNorm = r.getLowerLeftX()  / pageW;
                    double yNorm = (pageH - r.getLowerLeftY() - r.getHeight()) / pageH;
                    double wNorm = r.getWidth()  / pageW;
                    double hNorm = r.getHeight() / pageH;

                    result.add(new ExistingLinkDTO(i, xNorm, yNorm, wNorm, hNorm, linkType, uri));
                }
            }
        }
        return result;
    }

    private static PDRectangle getPdRectangle(PDRectangle mediaBox, SelectionEntity area) {
        float pageW = mediaBox.getWidth();
        float pageH = mediaBox.getHeight();

        // xNorm/yNorm/wNorm/hNorm are true 0-1 fractions (top-left origin).
        // PDF coordinate origin is bottom-left, so Y must be flipped.
        float llx = (float) (area.getxNorm() * pageW);
        float lly = (float) (pageH - (area.getyNorm() + area.gethNorm()) * pageH);
        float w   = (float) (area.getwNorm() * pageW);
        float h   = (float) (area.gethNorm() * pageH);

        return new PDRectangle(llx, lly, w, h);
    }
}


