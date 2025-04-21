package supervisor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

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

    public List<SelectionEntity> getAllSelectionData(){
        String sql = "SELECT * " +
                "FROM selections ";
        return jdbcTemplate.query(sql, RowMapper);
    }

    public SelectionEntity getSelectionData(Long id){
        String sql = "SELECT * " +
                "FROM selections " +
                "WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, RowMapper, id);
    }
}
