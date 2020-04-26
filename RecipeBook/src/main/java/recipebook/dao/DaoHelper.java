package recipebook.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides helper methods common to all dao classes.
 */
public class DaoHelper {

    /**
     * Returns the id of the item inserted into the database.
     * @param pstmt Prepared Statement to insert the item. 
     * @return Id of the inserted item.
     * @throws SQLException 
     */
    public int getCreatedItemId(PreparedStatement pstmt) throws SQLException {
        int id = 0;
        ResultSet resultSet = pstmt.getGeneratedKeys();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }
}
