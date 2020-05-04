package recipebook.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides helper methods common to all DAO classes.
 */
public class IdExtractor {

    /**
     * Returns the id of the item inserted into the database.
     *
     * @param pstmt Prepared Statement to insert the item.
     * @return Id of the inserted item.
     * @throws SQLException
     */
    public int getCreatedItemIdFromDatabase(PreparedStatement pstmt) throws SQLException {
        int id = 0;
        ResultSet resultSet = pstmt.getGeneratedKeys();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }

    /**
     * Returns the id of the item inserted into a list.
     *
     * @param items List of items the created item was inserted into.
     * @return Id of the inserted item.
     */
    public int getCreatedItemIdFromList(List<? extends Object> items) {
        return items.size() + 1;
    }
}
