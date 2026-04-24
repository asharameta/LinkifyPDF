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
public class DocumentEntityDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DocumentEntityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DocumentEntity> RowMapper = (rs, rowNum) -> {
        DocumentEntity doc = new DocumentEntity();
        doc.setId(rs.getLong("id"));
        doc.setFilename(rs.getString("filename"));
        doc.setStoragePath(rs.getString("storage_path"));
        doc.setMimeType(rs.getString("mime_type"));
        doc.setPageCount(rs.getInt("page_count"));
        doc.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return doc;
    };

    public List<DocumentEntity> getAllDocuments() {
        String sql = "SELECT d.id AS doc_id, d.filename, d.storage_path, d.mime_type, d.page_count, d.sha256_hex, d.uploaded_at, " +
                "s.id AS sel_id, s.page_number, s.x_norm, s.y_norm, s.w_norm, s.h_norm, s.link_type, s.link_value " +
                "FROM documents d " +
                "LEFT JOIN selections s ON s.document_id = d.id";

        Map<Long, DocumentEntity> pdfMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long pdfId = rs.getLong("doc_id");

            DocumentEntity pdfDto = pdfMap.computeIfAbsent(pdfId, id -> {
                DocumentEntity dto = new DocumentEntity();
                dto.setId(pdfId);
                try {
                    dto.setFilename(rs.getString("filename"));
                    dto.setStoragePath(rs.getString("storage_path"));
                    dto.setMimeType(rs.getString("mime_type"));
                    dto.setPageCount(rs.getInt("page_count"));
                    dto.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                dto.setSelectionEntities(new ArrayList<>());
                return dto;
            });

            long selId = rs.getLong("sel_id");
            if (!rs.wasNull()) {
                SelectionEntity selection = new SelectionEntity();
                selection.setId(selId);
                selection.setPageNumber(rs.getInt("page_number"));
                selection.setxNorm(rs.getDouble("x_norm"));
                selection.setyNorm(rs.getDouble("y_norm"));
                selection.setwNorm(rs.getDouble("w_norm"));
                selection.sethNorm(rs.getDouble("h_norm"));
                selection.setLinkType(LinkType.valueOf(rs.getString("link_type")));
                selection.setLinkValue(rs.getString("link_value"));
                pdfDto.getSelectionEntities().add(selection);
            }
        });

        return new ArrayList<>(pdfMap.values());
    }


    public DocumentEntity getPDFData(Long id){
        String sql = "SELECT * " +
                      "FROM documents " +
                      "WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, RowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long addDocument(DocumentEntity data) throws IOException {
        String sql = "INSERT INTO documents (filename, storage_path, mime_type, page_count, sha256_hex, uploaded_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                data.getFilename(),
                data.getStoragePath(),
                data.getMimeType(),
                data.getPageCount(),
                data.getSha256Hex(),
                Timestamp.valueOf(data.getUploadedAt())
        );
    }

    public void deletePDFData(Long id) {
        jdbcTemplate.update("DELETE FROM documents WHERE id = ?", id);
    }
}
