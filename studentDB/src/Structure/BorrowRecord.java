package Structure;

import java.util.Date;

public class BorrowRecord {
    private int id;
    private int userId;
    private int bookId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String bookName;

    public BorrowRecord(int id, int userId, int bookId, Date borrowDate, Date dueDate, Date returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getBookId() { return bookId; }
    public Date getBorrowDate() { return borrowDate; }
    public Date getDueDate() { return dueDate; }
    public Date getReturnDate() { return returnDate; }


    public String toString() {
        return "BorrowRecord{"+
                "user_id: "+getUserId()
                +", book_id: "+getBookId()
                +", borrow_date: "+getBorrowDate()
                +", due_date: "+getDueDate()
                +", return_date: "+getReturnDate();
    }
}
