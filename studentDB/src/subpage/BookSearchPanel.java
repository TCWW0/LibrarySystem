package subpage;

import Database.BookDAO;
import Structure.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


//这里其实就暴露了继承之于组合的巨大劣势
/*  在这里，由于继承的关系，所以BookSearchPanel拥有一系列的控件属性
    但是，我们在设计时，调用部分方法时，由于是直接继承下来的
    而且继承的还是外部库的东西
    所以在方法的使用上的可读性是相当的差的
    除非你对继承的类的方法十足的了解
    或者说你想在看代码的时候还想要去不断查找帮助文档

    所以在之后的设计中，我还是得坚持我的考虑
    使用组合关系的设计来代替继承关系的设计
 */
public class BookSearchPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;

    public BookSearchPanel() {
        bookDAO =BookDAO.getInstance();

        setLayout(new BorderLayout());

        // 搜索栏
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("关键字:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 表格初始化
        String[] columnNames = {"ID", "书名", "作者", "类别", "ISBN", "库存"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        showDefault();

        // 绑定查询事件
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });
    }

    // 查询并更新表格
    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入关键字", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //默认使用模糊查询，之后需要在这里进行优化
        List<Book> books = bookDAO.searchBooks_Fuzzy(keyword);
        tableModel.setRowCount(0);  // 清空表格数据

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getIsbn(),
                    book.getStock()
            });
        }
    }

    private void showDefault()
    {
        List<Book> books = bookDAO.showBooks_Default();
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getIsbn(),
                    book.getStock()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Search System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            frame.setContentPane(new BookSearchPanel());
            frame.setVisible(true);
        });
    }
}


