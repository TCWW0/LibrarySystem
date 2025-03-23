package subpage;

import Database.BorrowDAO;
import Database.DatabaseContext;
import Entrance.ApplicationManager;
import Entrance.PageSwitcher;
import Structure.Book;
import Structure.BorrowRecord;
import Structure.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

//每个页面需要绑定一些基本信息，这里指的是对应的使用者信息
public class BorrowPage extends BasePage {
    private final User currentUser;
    private final BorrowDAO borrowDAO;
    private JTable borrowTable;
    private DefaultTableModel tableModel;

    public BorrowPage(User user, PageSwitcher pageSwitcher) {
        super(pageSwitcher);
        this.currentUser = user;
        this.borrowDAO=new BorrowDAO(DatabaseContext.getInstance());
        initUI();
    }

    @Override
    public void onPageShown() {

    }

    //初始化子控件
    @Override
    protected void initUI() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 右对齐

        //初始化标题
        JLabel title=new JLabel("借阅记录喵");
        title.setFont(titlefont);
        add(title, BorderLayout.NORTH);

        //借阅表格
        JTable table=new JTable();
        JScrollPane scrollPane=new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //操作按钮
        JButton returnBtn=createStyledButton("归还图书喵");
        //add(returnBtn, BorderLayout.SOUTH);
        btnPanel.add(returnBtn,BorderLayout.SOUTH);

        //添加返回按钮
        JButton backBtn=createStyledButton("返回查询页面");
        backBtn.addActionListener(e -> {
            if(pageSwitcher!=null){
                pageSwitcher.switchToPage(ApplicationManager.PageType.Search);
            } else {
                JOptionPane.showMessageDialog(this,"还没实现抱歉喵");
            }
        });
        //add(backBtn, BorderLayout.EAST);
        btnPanel.add(backBtn,BorderLayout.SOUTH);

        this.add(btnPanel, BorderLayout.SOUTH);

        //记录表格
        tableModel=new DefaultTableModel(
                new Object[]{"借阅ID", "图书ID", "借阅日期", "应还日期", "归还日期"}, 0
        );
        borrowTable=createStyledTable();
        borrowTable.setModel(tableModel);
        JScrollPane scrollPane1=new JScrollPane(borrowTable);

        add(scrollPane1, BorderLayout.CENTER);

        //对应的操作按钮
        JButton returnBookBtn = createStyledButton("归还图书喵");
        returnBookBtn.addActionListener(e->handleReturnBook());
    }

    private void handleReturnBook() {
        int selectedRow = borrowTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要归还的记录喵", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int borrowId = (int) borrowTable.getValueAt(selectedRow, 0); // 第一列是借阅ID
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要归还这本图书吗？",
                "确认归还",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return borrowDAO.returnBook(borrowId); // 在后台线程执行归还
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(BorrowPage.this, "归还成功喵！");
                            onPageShown(); // 刷新表格
                        } else {
                            JOptionPane.showMessageDialog(BorrowPage.this, "归还失败喵，请检查记录", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(BorrowPage.this, "错误喵: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void loadBorrowRecords() {
        DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
        model.setRowCount(0); // 清空旧数据

        List<BorrowRecord> records = borrowDAO.getBorrowRecords(currentUser.getId());
        for (BorrowRecord record : records) {
            model.addRow(new Object[]{
                    record.getId(),
                    record.getBookId(),
                    record.getBorrowDate(),
                    record.getDueDate(),
                    record.getReturnDate() == null ? "未归还" : record.getReturnDate()
            });
        }
    }

    public static void main(String[]args)
    {
        SwingUtilities.invokeLater(() -> {
            // 创建测试用户
            User testUser = new User(4,"TCWW", "123456","admin");

            // 创建窗口
            JFrame frame = new JFrame("Borrow Page Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            // 添加 BorrowPage 到窗口
            BorrowPage borrowPage = new BorrowPage(testUser,null);
            frame.setContentPane(borrowPage);

            borrowPage.loadBorrowRecords();

            // 显示窗口
            frame.setVisible(true);
        });
    }
}
