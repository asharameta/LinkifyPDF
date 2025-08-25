package supervisor.mapper;

import supervisor.DTO.PdfResponseDTO;
import supervisor.DTO.PdfUploadDTO;
import supervisor.model.DocumentEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDataMapper {

    public static List<PdfResponseDTO> convertToDTOList(String pathToFile, List<DocumentEntity> groupedSelections) {
        if (groupedSelections == null) {
            throw new IllegalArgumentException("Input lists cannot be null");
        }


        return groupedSelections.stream().map(groupedSelection -> convertToDTO(pathToFile, groupedSelection))
                .collect(Collectors.toList());
    }

    public static PdfResponseDTO convertToDTO(String pathToFile, DocumentEntity pdfEntities) {
        return new PdfResponseDTO(pathToFile, pdfEntities);
    }

    public static DocumentEntity convertToEntity(PdfUploadDTO dtoData, LocalDateTime uploadedAt){
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFilename(dtoData.getFile().getOriginalFilename());
        documentEntity.setUploadedAt(uploadedAt);

        return documentEntity;
    }
}
