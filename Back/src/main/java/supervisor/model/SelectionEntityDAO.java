package supervisor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void addSelectionData(List<SelectionEntity> selections, Long documentId) throws IOException {
        String sql = "INSERT INTO selections (document_id, page_number, x_norm, y_norm, w_norm, h_norm, link_type, link_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, selections, selections.size(), (ps, selection) -> {
            ps.setLong(1, documentId);
            ps.setInt(2, selection.getPageNumber());
            ps.setDouble(3, selection.getxNorm());
            ps.setDouble(4, selection.getyNorm());
            ps.setDouble(5, selection.getwNorm());
            ps.setDouble(6, selection.gethNorm());
            ps.setString(6, selection.getLinkType().toString());
            ps.setString(6, selection.getLinkValue());
        });
    }


    public void printResultSet(ResultSet rs) throws SQLException {
        // Get the ResultSetMetaData to retrieve column information
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Iterate over all rows in the ResultSet
        while (rs.next()) {
            // Iterate over all columns in the current row
            for (int i = 1; i <= columnCount; i++) {
                // Get column name and value
                String columnName = metaData.getColumnName(i);
                Object columnValue = rs.getObject(i);

                // Print column name and its corresponding value
                System.out.println(columnName + ": " + columnValue);
            }
            System.out.println("------------------------");
        }
    }
}
