package supervisor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SelectionEntityDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SelectionEntityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SelectionEntity> RowMapper = (rs, rowNum) -> {
        SelectionEntity selection = new SelectionEntity();
        selection.setId(rs.getLong("id"));
        selection.setDocumentId(rs.getLong("document_id"));
        selection.setPageNumber(rs.getInt("page_number"));
        selection.setxNorm(rs.getDouble("x_norm"));
        selection.setyNorm(rs.getDouble("y_norm"));
        selection.setwNorm(rs.getDouble("w_norm"));
        selection.sethNorm(rs.getDouble("h_norm"));
        selection.setLinkType(LinkType.valueOf(rs.getString("link_type")));
        selection.setLinkValue(rs.getString("link_value"));

        return selection;
    };

    public List<SelectionEntity> getAllSelectionData() {
        String sql = "SELECT * FROM selections";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SelectionEntity dto = new SelectionEntity();
            dto.setId(rs.getLong("id"));
            dto.setPageNumber(rs.getInt("page_number"));
            dto.setxNorm(rs.getDouble("x_norm"));
            dto.setyNorm(rs.getDouble("y_norm"));
            dto.setwNorm(rs.getDouble("w_norm"));
            dto.sethNorm(rs.getDouble("h_norm"));
            dto.setLinkType(LinkType.valueOf(rs.getString("link_type")));
            dto.setLinkValue(rs.getString("link_value"));
            return dto;
        });
    }

    public List<SelectionEntity> getSelectionData(Long id){
        String sql = "SELECT * " +
                "FROM selections " +
                "WHERE document_id = ?";

        return jdbcTemplate.query(sql, RowMapper, id);
    }

    public void deleteSelectionData(Long documentId) {
        jdbcTemplate.update("DELETE FROM selections WHERE document_id = ?", documentId);
    }

    public void addSelectionData(List<SelectionEntity> selections, Long documentId) {
        String sql = "INSERT INTO selections (document_id, page_number, x_norm, y_norm, w_norm, h_norm, link_type, link_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, selections, selections.size(), (ps, selection) -> {
            ps.setLong(1, documentId);
            ps.setInt(2, selection.getPageNumber());
            ps.setDouble(3, selection.getxNorm());
            ps.setDouble(4, selection.getyNorm());
            ps.setDouble(5, selection.getwNorm());
            ps.setDouble(6, selection.gethNorm());
            ps.setString(7, selection.getLinkType().toString());
            ps.setString(8, selection.getLinkValue());
        });
    }



}
