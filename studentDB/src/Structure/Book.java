package Structure;

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

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getIsbn() { return isbn; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        return "Book{" +
                "title: "+ getTitle()
                + ", author: "+ getAuthor()
                + ", category: "+ getCategory()
                + ", isbn: "+ getIsbn();
    }
}
