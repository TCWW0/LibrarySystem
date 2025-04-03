package Database;

import Structure.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static volatile UserDAO instance;
    private final DatabaseContext dbContext;

    private UserDAO(DatabaseContext dbContext) {
        this.dbContext = dbContext;
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO(DatabaseContext.getInstance());
                }
            }
        }
        return instance;
    }

    public void getAllUsers(){
        //List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, role FROM users";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql);
             ResultSet rs = dbContext.executeQuery(pstmt)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "", // 不返回密码
                        rs.getString("role")
                );
                //users.add(user);
                User.insertToMap(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //return users;
    }

//    public boolean toggleUserStatus(int userId, boolean active) throws SQLException {
//        String sql = "UPDATE users SET active = ? WHERE id = ?";
//        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
//            pstmt.setBoolean(1, active);
//            pstmt.setInt(2, userId);
//            return dbContext.update(pstmt);
//        }
//    }
}
