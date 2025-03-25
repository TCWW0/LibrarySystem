package Database;
import Structure.Book;
import Structure.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static volatile BookDAO instance;
    private static final List<String> validColumns = List.of("title", "author", "category", "isbn");
    private final DatabaseContext dbContext;  // 依赖 DatabaseContext
    private User currentUser;

    // 私有构造函数，通过 DatabaseContext 注入依赖
    private BookDAO(DatabaseContext dbContext, User currentUser) {
        this.dbContext = dbContext;
        this.currentUser = currentUser;
    }

    // 获取单例实例（依赖 DatabaseContext 的单例）
    public static BookDAO getInstance(User user) {
        if (instance == null) {
            synchronized (BookDAO.class) {
                if (instance == null) {
                    instance = new BookDAO(DatabaseContext.getInstance(),user);
                }
            }
        }
        return instance;
    }

    public List<Book> searchBooks_Fuzzy(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = dbContext.executeQuery(pstmt)) {
                while (rs.next()) {
                    books.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[数据库错误] 模糊查询失败: " + e.getMessage());
        }
        return books;
    }

    public List<Book> searchBooks_Exact(String keyword, String category) {
        if (!validColumns.contains(category)) {
            System.err.println("Invalid column name: " + category);
            return new ArrayList<>(); // 返回空集合而非 null
        }

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " + category + " LIKE ?";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = dbContext.executeQuery(pstmt)) {
                while (rs.next()) {
                    books.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[数据库错误] 精确查询失败: " + e.getMessage());
        }
        return books;
    }

    public List<Book> showBooks_Default() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (PreparedStatement pstmt = dbContext.prepareStatement(sql);
             ResultSet rs = dbContext.executeQuery(pstmt)) {
            while (rs.next()) {
                books.add(mapBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[数据库错误] 默认查询失败: " + e.getMessage());
        }
        return books;
    }

    // 提取 ResultSet 到 Book 的映射方法
    private Book mapBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getString("isbn"),
                rs.getInt("stock")
        );
    }

    public boolean updateStock(int bookId,int delta)throws SQLException
    {
        if(delta>0&&!"admin".equals(currentUser.getRole())) throw new SQLException("权限不足,你怎么来到这里的!?");

        String sql = "UPDATE books SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setInt(1, delta);
            pstmt.setInt(2, bookId);
            return dbContext.update(pstmt);
        }
    }

    //这里还是有用的，增加这个能够在之后可能的优化，虽然这里单步操作的性能损耗是很大的
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement pstmt = dbContext.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = dbContext.executeQuery(pstmt)) {
                return rs.next() ? mapBookFromResultSet(rs) : null;
            }
        }
    }

    public static void main(String[] args) {
        // 测试代码无需修改
        User testStuUser = new User(3, "student2", "123456", "student");
        //User testUser=new User(4,"TCWW","123456","admin");
        BookDAO dao = BookDAO.getInstance(testStuUser);
        List<Book> books = dao.searchBooks_Exact("C++", "title");
        for (Book book : books) {
            System.out.println(book);
        }
    }
}