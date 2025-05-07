package supervisor.mapper;

import org.springframework.core.io.InputStreamResource;
import supervisor.DTO.PdfDTO;
import supervisor.model.PDFEntity;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PDFDataMapper {

    public static List<PdfDTO> convertToDTOList(List<InputStreamResource> inputStreamResources, List<List<SelectionEntity>> groupedSelections) {
        if (inputStreamResources == null || groupedSelections == null) {
            throw new IllegalArgumentException("Input lists cannot be null");
        }

        if (inputStreamResources.size() != groupedSelections.size()) {
            throw new IllegalArgumentException("Input stream resources and grouped selections must have the same size");
        }

        return IntStream.range(0, inputStreamResources.size())
                .mapToObj(i -> convertToDTO(inputStreamResources.get(i), groupedSelections.get(i)))
                .collect(Collectors.toList());
    }

    public static PdfDTO convertToDTO(InputStreamResource inputStreamResource, List<SelectionEntity> selectionEntities) {
        return new PdfDTO(inputStreamResource.getFilename(), selectionEntities);
    }

    public static PDFEntity convertToEntity(PdfDTO dtoData, LocalDateTime uploadedAt){
        PDFEntity pdfEntity = new PDFEntity();
        pdfEntity.setFilename(dtoData.getFileName());
        pdfEntity.setUploadedAt(uploadedAt);

        return pdfEntity;
    }
}
