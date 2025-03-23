package subpage;

import Database.BookDAO;
import Entrance.ApplicationManager;
import Structure.Book;
import Entrance.PageSwitcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

public class BookSearchPanel extends BasePage {
    private JTextField searchField;
    private JButton searchButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;

    public BookSearchPanel(PageSwitcher pageSwitcher) {
        super(pageSwitcher);
        bookDAO = BookDAO.getInstance();

        showDefault();  //从构造中抽离，有效且合理，这里是子类的行为，不应该被放到组件的构造中
    }

    @Override
    public void onPageShown() {
        // 可在此处添加页面显示时的刷新逻辑
    }

    @Override
    protected void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 搜索框
        searchField = createStyledTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.BOTH; // 填充空间
        add(searchField, gbc);

        // 搜索按钮
        searchButton = createStyledButton("Search");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH; // 填充空间
        add(searchButton, gbc);

        // 初始化表格模型
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "书名", "作者", "分类", "ISBN", "库存"}, 0
        );
        resultTable = createStyledTable();
        resultTable.setModel(tableModel);

        // 设置表头样式
        JTableHeader header = resultTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(DEFAULT_FONT);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x333333), 1), // 1像素深灰色边框
                BorderFactory.createEmptyBorder(5, 5, 5, 5)            // 5像素内边距
        ));

        // 结果表格
        JScrollPane scrollPane = new JScrollPane(resultTable);

        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x333333), 1), // 1像素边框
                BorderFactory.createEmptyBorder(10, 10, 10, 10)        // 10像素内边距
        ));

        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        //showDefault();
        //这里保留一下，这里又是一次不注意的初始化顺序导致的错误，可以自己解开并去注释掉现有构造中的方法尝试

        // 操作按钮面板
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);

        JButton borrowBtn = createStyledButton("借阅记录");
        borrowBtn.addActionListener(e -> pageSwitcher.switchToPage(ApplicationManager.PageType.BORROW));
        actionPanel.add(borrowBtn);

        gbc.gridy = 2;
        gbc.weighty = 0.0; // 固定高度
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(actionPanel, gbc);

        // 绑定查询事件
        searchButton.addActionListener(e -> searchBooks());
    }

    // 查询并更新表格
    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入关键字", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Book> books = bookDAO.searchBooks_Fuzzy(keyword);
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

    private void showDefault() {
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
            frame.setContentPane(new BookSearchPanel(null)); // 测试时可传 null，实际使用时需实现 PageSwitcher
            frame.setVisible(true);
        });
    }
}
