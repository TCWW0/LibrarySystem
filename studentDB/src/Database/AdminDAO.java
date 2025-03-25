package Database;

import Structure.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    private final DatabaseContext dbContext;  // 依赖 DatabaseContext
    private static volatile AdminDAO instance;
    private User currentUser;

    private AdminDAO(User currentUser) {
        this.dbContext = DatabaseContext.getInstance();
        this.currentUser = currentUser;
    }

    public static AdminDAO getInstance(User currentUser) {
        if (instance == null) {
            synchronized (AdminDAO.class) {
                if (instance == null) {
                    instance = new AdminDAO(currentUser);
                }
            }
        }
        return instance;
    }

    public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        String checkSql = "SELECT id, username,password, role FROM users";
        try (PreparedStatement checkStmt = dbContext.prepareStatement(checkSql);
                ResultSet rs = checkStmt.executeQuery()) {
            while(rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(user);
            }
        }
        return users;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String deleteSql = "DELETE FROM users WHERE id = ?";
        try(PreparedStatement pstmt = dbContext.prepareStatement(deleteSql))
        {
            pstmt.setInt(1, userId);
            return dbContext.delete(pstmt);
        }
    }

    public boolean updateUserRole(int userId, int role) throws SQLException {
        String updateSql = "UPDATE users SET role = ? WHERE id = ?";
        try(PreparedStatement pstmt = dbContext.prepareStatement(updateSql)){
            pstmt.setInt(1, role);
            pstmt.setInt(2, userId);
            return dbContext.update(pstmt);
        }
    }

    public boolean resetUserPassword(int userId) throws SQLException {
        String defaultPassword = "123456";
        String resetPasswordSql = "UPDATE users SET password = ? WHERE id = ?";
        try(PreparedStatement pstmt = dbContext.prepareStatement(resetPasswordSql)){
            pstmt.setString(1, defaultPassword);
            pstmt.setInt(2, userId);
            return dbContext.update(pstmt);
        }
    }

    public boolean updateUserRole(int userId, String newRole) throws SQLException {
        String updateSql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbContext.prepareStatement(updateSql)) {
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            return dbContext.update(pstmt);
        }
    }
}
