package supervisor.mapper;

import supervisor.DTO.PdfDTO;
import supervisor.model.PDFEntity;
import supervisor.model.SelectionEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDataMapper {

    public static List<PdfDTO> convertToDTOList(List<SelectionEntity> selectionEntityList, List<byte[]> base64Pdf) {
        return selectionEntityList.stream()
                .map(entity -> {
                    return convertToDTO(entity, base64Pdf.get(selectionEntityList.indexOf(entity)));
                })
                .collect(Collectors.toList());
    }

    public static PdfDTO convertToDTO(SelectionEntity selectionEntity, byte[] base64Pdf) {
        PdfDTO dto = new PdfDTO();
        try {
            dto.setPdf(Arrays.toString(base64Pdf));
        } catch (Exception e) {
            // handle the exception or log it
            e.printStackTrace();
            dto.setPdf(null); // or throw an exception if necessary
        }
        dto.setSelections(selectionEntity);
        return dto;
    }
}
