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
        SelectionEntity c = new SelectionEntity();
        c.setId(rs.getLong("id"));
        c.setUrl(rs.getString("url"));
        c.setX(rs.getDouble("x"));
        c.setY(rs.getDouble("y"));
        c.setHeight(rs.getDouble("height"));
        c.setWidth(rs.getDouble("width"));

        return c;
    };

    public List<SelectionEntity> getAllSelectionData() {
        String sql = "SELECT * FROM selections";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SelectionEntity dto = new SelectionEntity();
            dto.setId(rs.getLong("id"));
            dto.setUrl(rs.getString("url"));
            dto.setX(rs.getDouble("x"));
            dto.setY(rs.getDouble("y"));
            dto.setWidth(rs.getDouble("width"));
            dto.setHeight(rs.getDouble("height"));
            return dto;
        });
    }

    public List<SelectionEntity> getSelectionData(Long id){
        String sql = "SELECT * " +
                "FROM selections " +
                "WHERE pdf_id = ?";

        return jdbcTemplate.query(sql, RowMapper, id);
    }

    public void addSelectionData(List<SelectionEntity> selections, Long pdfId) throws IOException {
        String sql = "INSERT INTO selections (pdf_id, url, x, y, width, height) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, selections, selections.size(), (ps, selection) -> {
            ps.setLong(1, pdfId);
            ps.setString(2, selection.getUrl());
            ps.setDouble(3, selection.getX());
            ps.setDouble(4, selection.getY());
            ps.setDouble(5, selection.getWidth());
            ps.setDouble(6, selection.getHeight());
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
