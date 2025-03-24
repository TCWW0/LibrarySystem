package Database;

import Structure.BorrowRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    private final DatabaseContext dbContext;
    private static volatile BorrowDAO instance;

    private BorrowDAO(DatabaseContext dbContext) {
        this.dbContext = dbContext;
    }

    public static BorrowDAO getInstance() {
        if (instance == null) {
            synchronized (BorrowDAO.class) {
                if (instance == null) {
                    instance = new BorrowDAO(DatabaseContext.getInstance());
                }
            }
        }
        return instance;
    }

    // 借阅图书
    public boolean borrowBook(int userId, int bookId, int borrowDays) {
        String sql = "INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date) " +
                "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY))";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, borrowDays);
            return dbContext.create(pstmt); // 复用 DatabaseContext 的 create 方法
        } catch (SQLException e) {
            System.err.println("[借阅失败] " + e.getMessage());
            return false;
        }
    }

    // 获取用户借阅记录
    public List<BorrowRecord> getBorrowRecords(int userId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ?";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = dbContext.executeQuery(pstmt)) { // 复用 executeQuery
                while (rs.next()) {
                    records.add(mapRecord(rs));
                }
            }
            return records;
        } catch (SQLException e) {
            System.err.println("[查询失败] " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 归还图书（无需直接操作 Connection）
    public boolean returnBook(int borrowId) {
        try {
            dbContext.beginTransaction(); // 开启事务

            // 1. 更新借阅记录
            String updateBorrow = "UPDATE borrow_records SET return_date = NOW() WHERE id = ?";
            try (PreparedStatement pstmt = dbContext.prepareStatement(updateBorrow)) {
                pstmt.setInt(1, borrowId);
                dbContext.update(pstmt); // 复用 update 方法
            }

            // 2. 更新库存
            String updateStock = "UPDATE books SET stock = stock + 1 WHERE id = " +
                    "(SELECT book_id FROM borrow_records WHERE id = ?)";
            try (PreparedStatement pstmt = dbContext.prepareStatement(updateStock)) {
                pstmt.setInt(1, borrowId);
                dbContext.update(pstmt); // 复用 update 方法
            }

            dbContext.commitTransaction(); // 提交事务
            return true;
        } catch (SQLException e) {
            dbContext.rollbackTransaction(); // 回滚事务
            System.err.println("[归还失败] " + e.getMessage());
            return false;
        }
    }

    // ResultSet 到 BorrowRecord 的映射
    private BorrowRecord mapRecord(ResultSet rs) throws SQLException {
        return new BorrowRecord(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getDate("borrow_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date")
        );
    }

    public static void main(String[] args) {
        // 创建数据库上下文
        DatabaseContext dbContext = DatabaseContext.getInstance();
        BorrowDAO borrowDAO = new BorrowDAO(dbContext);

        // 假设用户ID为1，书籍ID为2，借阅7天
        int userId = 4;
        int bookId = 2;
        int borrowDays = 7;

//        System.out.println("=== 测试借阅书籍 ===");
//        boolean borrowSuccess = borrowDAO.borrowBook(userId, bookId, borrowDays);
//        System.out.println("借阅结果: " + (borrowSuccess ? "成功" : "失败"));

        System.out.println("=== 查询借阅记录 ===");
        List<BorrowRecord> records = borrowDAO.getBorrowRecords(userId);
        if (records.isEmpty()) {
            System.out.println("当前无借阅记录");
        } else {
            for (BorrowRecord record : records) {
                System.out.println(record.toString());
            }
        }

//        if (!records.isEmpty()) {
//            int borrowId = records.getFirst().getId();
//            System.out.println("=== 测试归还书籍 ===");
//            boolean returnSuccess = borrowDAO.returnBook(borrowId);
//            System.out.println("归还结果: " + (returnSuccess ? "成功" : "失败"));
//
//            System.out.println("=== 归还后再次查询借阅记录 ===");
//            List<BorrowRecord> updatedRecords = borrowDAO.getBorrowRecords(userId);
//            for (BorrowRecord record : updatedRecords) {
//                System.out.println(record);
//            }
//        }
    }
}
