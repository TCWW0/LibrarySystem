package Database;

import Structure.User;
import Structure.Query;
import java.sql.*;

public class LoginDAO {
    private final DatabaseContext dbContext;

    public LoginDAO(DatabaseContext dbContext) {
        this.dbContext = dbContext;
    }

    // 登录验证逻辑,在这里就完全对于整个User结构的更新
    public Query loginVerification(User user) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            if (findUser(pstmt, user)) {

                return Query.match_Suc_Login;
            } else {
                return createNewUser(user);
            }
        } catch (SQLException e) {
            System.err.println("[登录失败] " + e.getMessage());
            return Query.System_Error;
        }
    }

    // 创建新用户
    private Query createNewUser(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        //这里必须使用俩个参数的版本，不然在创建后面的获取自增ID时会报错，而且会插入错误的数据
        try (PreparedStatement pstmt = dbContext.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            if (dbContext.create(pstmt)) {
                // 获取自增的 user_id 并设置到 User 对象
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
                return Query.match_Fail_Create;
            } else {
                return Query.System_Error;
            }
        } catch (SQLException e) {
            System.err.println("[用户创建失败] " + e.getMessage());
            return Query.System_Error;
        }
    }

    // 查询用户是否存在,顺便丢进去记录状态
    private boolean findUser(PreparedStatement pstmt,User user) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                return true;
            }
            return false;
        }
    }
}
