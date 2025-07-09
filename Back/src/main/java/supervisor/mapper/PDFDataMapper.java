package supervisor.mapper;

import org.springframework.core.io.InputStreamResource;
import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
import supervisor.model.PDFEntity;
import supervisor.model.SelectionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PDFDataMapper {

    public static List<PdfResponseDTO> convertToDTOList(String pathToFile, List<PDFEntity> groupedSelections) {
        if (groupedSelections == null) {
            throw new IllegalArgumentException("Input lists cannot be null");
        }


        return groupedSelections.stream().map(groupedSelection -> convertToDTO(pathToFile, groupedSelection))
                .collect(Collectors.toList());
    }

    public static PdfResponseDTO convertToDTO(String pathToFile, PDFEntity pdfEntities) {
        return new PdfResponseDTO(pathToFile, pdfEntities);
    }

    public static PDFEntity convertToEntity(PdfUploadDTO dtoData, LocalDateTime uploadedAt){
        PDFEntity pdfEntity = new PDFEntity();
        pdfEntity.setFilename(dtoData.getFile().getOriginalFilename());
        pdfEntity.setUploadedAt(uploadedAt);

        return pdfEntity;
    }
}
