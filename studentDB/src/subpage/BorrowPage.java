package subpage;

import Database.BookDAO;
import Database.BorrowDAO;
import Database.DatabaseContext;
import Database.UserDAO;
import Entrance.ApplicationManager;
import Entrance.PageSwitcher;
import Structure.Book;
import Structure.BorrowRecord;
import Structure.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

//每个页面需要绑定一些基本信息，这里指的是对应的使用者信息
public class BorrowPage extends BasePage {
    private final User currentUser;
    private final BorrowDAO borrowDAO;
    private JTable borrowTable;
    private DefaultTableModel tableModel;
    private static volatile BorrowPage instance;

    private BorrowPage(User user, PageSwitcher pageSwitcher){
        super(pageSwitcher);
        this.currentUser = user;
        this.borrowDAO=BorrowDAO.getInstance();
        initUI();
        initUserTable();
        recordQuery();
    }

    //只有在管理员登录时才加载一次映射，否则只添加一个自己的映射
    private void initUserTable(){
        if(currentUser.getRole().equals("student"))User.insertToMap(currentUser);
        else if (currentUser.getRole().equals("admin"))
        {
            UserDAO.getInstance().getAllUsers();
            BookDAO.getInstance(currentUser).getAllBooks();
        }
    }

    public static BorrowPage getInstance(User user, PageSwitcher pageSwitcher) throws SQLException {
        if (instance == null) {
            synchronized (BorrowPage.class) {
                if (instance == null) {
                    instance = new BorrowPage(user, pageSwitcher);
                }
            }
        }
        return instance;
    }

    //每次刷新当前用户的借阅记录
    @Override
    public void onPageShown() {
        recordQuery();
    }

    //初始化子控件
    @Override
    protected void initUI() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 右对齐

        //初始化标题
        JLabel title=new JLabel("借阅记录喵");
        title.setFont(titlefont);
        add(title, BorderLayout.NORTH);

        //记录表格
        tableModel=new DefaultTableModel(
                new Object[]{"借阅ID","借阅人", "图书名", "借阅日期", "应还日期", "归还日期"}, 0
        );
        borrowTable=createStyledTable();
        //注意这里需要在隐藏的代码调用前调用，否则空指针喵
        borrowTable.setModel(tableModel);
        //存在对应的ID列但是被隐藏,注意位置避免空指针
        borrowTable.getColumnModel().getColumn(0).setMinWidth(0); // 隐藏ID列
        borrowTable.getColumnModel().getColumn(0).setMaxWidth(0);
        borrowTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane1=new JScrollPane(borrowTable);

        add(scrollPane1, BorderLayout.CENTER);

        //对应的操作按钮
        JButton returnBookBtn = createStyledButton("归还图书");
        returnBookBtn.addActionListener(e->handleReturnBook());
        btnPanel.add(returnBookBtn);

        JButton refreshBtn = createStyledButton("刷新");
        refreshBtn.addActionListener(e->{
            loadBorrowRecords();
        });
        btnPanel.add(refreshBtn);
//添加返回按钮
//        JButton backBtn=createStyledButton("返回查询页面");
//        backBtn.addActionListener(e -> {
//            if(pageSwitcher!=null){
//                pageSwitcher.switchToPage(ApplicationManager.PageType.SEARCH);
//            } else {
//                JOptionPane.showMessageDialog(this,"还没实现抱歉喵");
//            }
//        });
//        add(backBtn, BorderLayout.EAST);
//        btnPanel.add(backBtn,BorderLayout.SOUTH);

        this.add(btnPanel, BorderLayout.SOUTH);
    }

    private void handleReturnBook() {
        int selectedRow = borrowTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要归还的记录喵", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object returnData = borrowTable.getValueAt(selectedRow, 5);
        // 如果 returnData 不是字符串，说明记录中存放的是日期，意味着已经归还
        if (!(returnData instanceof String) || !Objects.equals(returnData, "未归还")) {
            JOptionPane.showMessageDialog(this, "已归还别扒拉","警告",JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int borrowId = (int) borrowTable.getValueAt(selectedRow, 0); // 第一列是借阅ID，注意被隐藏
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

    //需要注意的是，这里的方法实现是基于当前登录系统的用户进行一个查询的
    private void loadBorrowRecords() {
        recordQuery();
    }

    private void recordQuery() {
        DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
        model.setRowCount(0); // 清空旧数据
        List<BorrowRecord> records=null;
        if(currentUser.getRole().equals("student"))records = borrowDAO.getBorrowRecords(currentUser.getId());
        else if (currentUser.getRole().equals("admin"))records = borrowDAO.getAllBorrowRecords();
        else records = borrowDAO.getBorrowRecords(1);//这里理论上不可能进入，但一定进入了有对应的显式也好定位
        for (BorrowRecord record : records) {
            model.addRow(new Object[]{
                    record.getId(),
                    User.findById(record.getUserId()),      //替代下面的语句
                    //this.currentUser.getUsername(),
                    Book.findById(record.getBookId()),
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
            User testUser = new User(3,"TCWW", "123456","admin");
            User testUser1 = new User(4,"test1", "123456","student");

            // 创建窗口
            JFrame frame = new JFrame("Borrow Page Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            BorrowPage borrowPage=new BorrowPage(testUser,null);
            frame.setContentPane(borrowPage);

            borrowPage.recordQuery();

            // 显示窗口
            frame.setVisible(true);
        });
    }
}
