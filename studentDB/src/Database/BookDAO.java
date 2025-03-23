package Database;
import Structure.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//查询书籍的子页面底层数据库实现
public class BookDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tcww3498";
    private static BookDAO instance;
    // 允许的列名，防止 SQL 注入
    private static List<String> validColumns = List.of("title", "author", "category", "isbn");

    // 获取数据库连接
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 查询书籍,模糊搜索，每一个属性中都进行换一次模糊查询
    //输入关键字，在书籍的每一个属性进行一次近似地查询属性的
    public List<Book> searchBooks_Fuzzy(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String queryParam = "%" + keyword + "%";
            stmt.setString(1, queryParam);
            stmt.setString(2, queryParam);
            stmt.setString(3, queryParam);

            ResultSet rs = stmt.executeQuery();
            //对于查询到的序列进行储存到一个列表中返回
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("isbn"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks_Exact(String keyword,String category) {
        if (!validColumns.contains(category)) {
            System.out.println("Invalid column name: " + category);
            return null; // 直接返回空列表，避免 SQL 注入
        }

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " + category + " LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String queryParam = "%" + keyword + "%";
            stmt.setString(1, queryParam);

            ResultSet rs = stmt.executeQuery();
            //对于查询到的序列进行储存到一个列表中返回
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("isbn"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> showBooks_Default() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try(Connection connection=getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("isbn"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    private BookDAO() {}

    public static BookDAO getInstance() {
        if (instance == null) {
            synchronized (BookDAO.class) {
                if (instance == null) {
                    instance = new BookDAO();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        BookDAO dao = BookDAO.getInstance();
        //List<Book> books = dao.searchBooks_Fuzzy("计算机");
        List<Book> books = dao.searchBooks_Exact("C++","title");
        for (Book book : books) {
            System.out.println(book.toString());
        }
    }
}
