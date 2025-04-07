package supervisor.mapper;

import supervisor.DTO.PDFDataDTO;
import supervisor.model.PDFData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDataMapper {

    public static List<PDFDataDTO> convertToDTOList(List<PDFData> pdfDataList) {
        return pdfDataList.stream()
                .map(PDFDataMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public static PDFDataDTO convertToDTO(PDFData data) {
        PDFDataDTO dto = new PDFDataDTO();
        try {
            byte[] pdfBytes = Files.readAllBytes(Path.of(data.getPDF_PATH()+"\\"+data.getPdf().getOriginalFilename()));
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            dto.setPdf(base64Pdf);
        } catch (Exception e) {
            // handle the exception or log it
            e.printStackTrace();
            dto.setPdf(null); // or throw an exception if necessary
        }
        dto.setSelections(data.getSelections());
        return dto;
    }
}
