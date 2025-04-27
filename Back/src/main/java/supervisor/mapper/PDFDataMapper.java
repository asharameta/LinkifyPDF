package supervisor.mapper;

import supervisor.DTO.PdfDTO;
import supervisor.model.PDFEntity;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDataMapper {

    public static List<PdfDTO> convertToDTOList(List<List<SelectionEntity>> selectionEntity, List<byte[]> base64Pdf) {
        var res =  selectionEntity.stream()
                .map(entity -> {
                    return convertToDTO(entity, base64Pdf.get(selectionEntity.indexOf(entity)));
                })
                .collect(Collectors.toList());

        return res;
    }

    public static PdfDTO convertToDTO(List<SelectionEntity> selectionEntity, byte[] base64Pdf) {
        PdfDTO dto = new PdfDTO();
        dto.setPdf(base64Pdf);
        dto.setSelections(selectionEntity);
        return dto;
    }

    public static PDFEntity convertToEntity(PdfDTO dtoData, LocalDateTime uploadedAt){
        PDFEntity pdfEntity = new PDFEntity();
        pdfEntity.setFilename(dtoData.getPdfName());
        pdfEntity.setUploadedAt(uploadedAt);

        return  pdfEntity;
    }
}
