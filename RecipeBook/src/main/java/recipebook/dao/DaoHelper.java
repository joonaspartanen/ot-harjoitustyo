package recipebook.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoHelper {

    public int getCreatedItemId(PreparedStatement pstmt) throws SQLException {
        int id = 0;
        ResultSet resultSet = pstmt.getGeneratedKeys();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }
}
