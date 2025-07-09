package supervisor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
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

    public List<PDFEntity> getAllPdfWithSelections() {
        String sql = "SELECT s.*, p.id as pdf_id, p.filename, p.uploaded_at " +
                "FROM selections s " +
                "JOIN pdfs p ON s.pdf_id = p.id";

        Map<Long, PDFEntity> pdfMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long pdfId = rs.getLong("pdf_id");

            // Create PDFDto if not already in map
            PDFEntity pdfDto = pdfMap.computeIfAbsent(pdfId, id -> {
                PDFEntity dto = new PDFEntity();
                dto.setId(pdfId);
                try {
                    dto.setFilename(rs.getString("filename"));
                    dto.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                dto.setSelectionEntities(new ArrayList<>());
                return dto;
            });

            // Create and add SelectionDto
            SelectionEntity selection = new SelectionEntity();
            selection.setId(rs.getLong("id"));
            selection.setUrl(rs.getString("url"));
            selection.setX(rs.getDouble("x"));
            selection.setY(rs.getDouble("y"));
            selection.setWidth(rs.getDouble("width"));
            selection.setHeight(rs.getDouble("height"));

            pdfDto.getSelectionEntities().add(selection);
        });

        return new ArrayList<>(pdfMap.values());
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

    public Long addPDFData(PDFEntity data) throws IOException {
        String sql = "INSERT INTO pdfs (filename, uploaded_at) VALUES (?, ?) RETURNING id";

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                data.getFilename(),
                Timestamp.valueOf(data.getUploadedAt())
        );
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
