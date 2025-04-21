package supervisor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.util.List;

public class PdfEntityDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PdfEntityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PDFEntity> RowMapper = (rs, rowNum) -> {
        PDFEntity c = new PDFEntity();
        c.setId(rs.getLong("id"));
        c.setFilename(rs.getString("filename"));
        c.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return c;
    };

    public List<PDFEntity> getAllPDFData(){
        String sql = "SELECT * " +
                     "FROM pdfs";
        return jdbcTemplate.query(sql, RowMapper);
    }

    public PDFEntity getPDFData(Long id){
        String sql = "SELECT * " +
                      "FROM pdfs " +
                      "WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, RowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addPDFData(PDFEntity data) throws IOException {
//        MultipartFile pdfFile = data.getPdf();
//
//        Path destinationDir = Paths.get(data.getPDF_PATH());
//        if (Files.notExists(destinationDir)) {
//            Files.createDirectories(destinationDir);
//        }
//
//        String filename = Objects.requireNonNull(pdfFile.getOriginalFilename());
//        Path finalPath = destinationDir.resolve(filename);
//        Files.copy(pdfFile.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);
//
//        tempPDF.add(data);
    }

    public void deletePDFData(int id){
//        PdfEntity toDelete = getPDFData(id);
//        if(toDelete == null || tempPDF.isEmpty() || !tempPDF.contains(toDelete)) {
//            return false;
//        }
//        tempPDF.remove(id);
//        return true;
    }
}
