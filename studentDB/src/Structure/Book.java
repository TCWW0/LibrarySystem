package Structure;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.util.HashMap;
import java.util.Map;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private int stock;

    public Book(int id, String title, String author, String category, String isbn, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.stock = stock;
    }

    //这里会在程序查询页面第一次显示时进行一次懒加载
    static private Map<Integer, String> idToName = new HashMap<>();
    // Getter 和 Setter 方法
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getIsbn() { return isbn; }
    public int getStock() { return stock; }

    public static void insertToMap(Book book) {
        Book.idToName.put(book.getId(),book.getTitle());
    }

    public static String findById(int id) {
        return Book.idToName.get(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "title: "+ getTitle()
                + ", author: "+ getAuthor()
                + ", category: "+ getCategory()
                + ", isbn: "+ getIsbn();
    }
}
