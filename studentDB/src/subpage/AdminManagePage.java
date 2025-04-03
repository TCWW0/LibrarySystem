package subpage;

import Database.AdminDAO;
import Database.BookDAO;
import Database.UserDAO;
import Entrance.ApplicationManager;
import Entrance.PageSwitcher;
import Structure.Book;
import Structure.User;
import factor.PageFactory;
import factor.SimplePageFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

import java.util.List;

import static javax.swing.JOptionPane.YES_OPTION;

public class AdminManagePage extends BasePage {
    private final User currentUser;
    JTabbedPane tabbedPane;
    PageFactory pageFactory;

    public AdminManagePage(User user, PageSwitcher pageSwitcher) throws SQLException {
        super(pageSwitcher);
        this.currentUser = user;
        initUniqueUI();
    }

    @Override
    protected void initUI() {pageFactory=new SimplePageFactory();}

    private void initUniqueUI() throws SQLException {
        setLayout(new BorderLayout());

        // 标签页切换
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("书籍管理", createBookManagementPanel());
        tabbedPane.addTab("用户管理", createUserManagementPanel());
        tabbedPane.addTab("借阅记录",createBorrowRecordPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBookManagementPanel() {
        // 表格
        DefaultTableModel tableModel=new DefaultTableModel(
                new Object[]{"id","书名", "作者", "类别", "ISBN","库存"}, 0
        );
        JTable bookTable = createStyledTable();
        bookTable.setModel(tableModel);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = createStyledButton("添加书籍");
        addButton.setPreferredSize(new Dimension(100,30));
        JButton deleteButton = createStyledButton("删除书籍");
        deleteButton.setPreferredSize(new Dimension(100,30));
        JButton refreshButton = createStyledButton("刷新");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        //功能绑定，启动
        addButton.addActionListener(e -> showAddBookDialog(tableModel));
        deleteButton.addActionListener(e -> {deleteSelectedBook(bookTable, tableModel);});
        refreshButton.addActionListener(e->refreshBookTable(tableModel));

        // 布局
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        //初始加载数据
        refreshBookTable(tableModel);
        return panel;
    }

    private JPanel createUserManagementPanel() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"ID", "用户名", "密码","权限"}, 0
        );
        JTable userTable = createStyledTable();
        userTable.setModel(tableModel);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteUserBtn = createStyledButton("删除用户");
        JButton editUserRoleBtn = createStyledButton("修改权限");
        JButton resetPasswordBtn = createStyledButton("重置密码");
        JButton refreshButton = createStyledButton("刷新");
        deleteUserBtn.addActionListener(e->{deleteSelectedUser(userTable, tableModel);});
        resetPasswordBtn.addActionListener(e->resetUserPassword(userTable, tableModel));
        editUserRoleBtn.addActionListener(e->updateUserRole(userTable, tableModel));
        refreshButton.addActionListener(e -> refreshUserTable(tableModel));

        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(editUserRoleBtn);
        buttonPanel.add(resetPasswordBtn);
        buttonPanel.add(refreshButton);

        // 布局
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 初始加载数据
        refreshUserTable(tableModel);

        refreshButton.addActionListener(e -> refreshBookTable(tableModel));

        return panel;
    }

    private JPanel createBorrowRecordPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel borrowPanel = pageFactory.createPage(ApplicationManager.PageType.BORROW,currentUser,pageSwitcher);
        panel.add(borrowPanel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void onPageShown() {
        // 页面显示时刷新数据
    }

    private void showAddBookDialog(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog((Frame)null, "添加新书", true);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField[] fields = {
                new JTextField(), // 书名
                new JTextField(), // 作者
                new JTextField(), // 类别
                new JTextField(), // ISBN
                new JTextField()  // 库存
        };

        String[] labels = {"书名:", "作者:", "类别:", "ISBN:", "库存:"};
        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel(labels[i]));
            dialog.add(fields[i]);
        }

        JButton submitButton = createStyledButton("提交");
        submitButton.addActionListener(e -> {
            try {
                Book book = new Book(
                        0,
                        fields[0].getText(),
                        fields[1].getText(),
                        fields[2].getText(),
                        fields[3].getText(),
                        Integer.parseInt(fields[4].getText())
                );

                if (BookDAO.getInstance(currentUser).addBook(book)) {
                    JOptionPane.showMessageDialog(dialog, "添加成功");
                    refreshBookTable(tableModel);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "库存必须是数字");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "操作失败: " + ex.getMessage());
            }
        });

        dialog.add(submitButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook(JTable bookTable, DefaultTableModel tableModel) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的书籍");
            return;
        }

        int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除《" + bookTitle + "》吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == YES_OPTION) {
            try {
                if (BookDAO.getInstance(currentUser).deleteBook(bookId)) {
                    refreshBookTable(tableModel);
                    JOptionPane.showMessageDialog(this, "删除成功");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedUser(JTable userTable,DefaultTableModel tableModel) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的用户");
            return;
        }
        int userId = (Integer) userTable.getValueAt(selectedRow, 0);
        String userName = (String) userTable.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除用户\""+userName+"\"吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == YES_OPTION) {
            try{
                if(AdminDAO.getInstance(currentUser).deleteUser(userId))
                {
                    refreshUserTable(tableModel);
                    JOptionPane.showMessageDialog(this,"删除成功");
                }
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(this,"删除失败："+ex.getMessage());
            }
        }
    }

    private void resetUserPassword(JTable userTable,DefaultTableModel tableModel) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改密码的用户");
            return;
        }
        int userId = (Integer) userTable.getValueAt(selectedRow, 0);
        String userName = (String) userTable.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要重置用户\""+userName+"\"的密码吗？",
                "确认修改",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == YES_OPTION) {
            try {
                if (AdminDAO.getInstance(currentUser).resetUserPassword(userId)) {
                    JOptionPane.showMessageDialog(AdminManagePage.this, "密码已重置为123456");
                    refreshUserTable(tableModel);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AdminManagePage.this, "操作失败: " + ex.getMessage());
            }
        }
    }

    private void updateUserRole(JTable userTable, DefaultTableModel tableModel) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改密码的用户");
            return;
        }
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);
        // 创建带下拉框的对话框
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "student"});
        roleCombo.setSelectedItem(currentRole);

        int result = JOptionPane.showConfirmDialog(
                AdminManagePage.this,
                roleCombo,
                "选择新权限",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newRole = (String) roleCombo.getSelectedItem();
                if (AdminDAO.getInstance(currentUser).updateUserRole(userId, newRole)) {
                    JOptionPane.showMessageDialog(AdminManagePage.this, "权限修改成功");
                    refreshUserTable(tableModel);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AdminManagePage.this, "操作失败: " + ex.getMessage());
            }
        }
    }

    //从数据库再拉取一遍数据
    private void refreshBookTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Book> books = BookDAO.getInstance(currentUser).showBooks_Default();
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

//    private void toggleUserStatus(JTable userTable, DefaultTableModel tableModel) {
//        int selectedRow = userTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "请选择用户");
//            return;
//        }
//
//        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
//        String username = (String) tableModel.getValueAt(selectedRow, 1);
//        boolean currentStatus = "激活".equals(tableModel.getValueAt(selectedRow, 3));
//
//        try {
//            if (UserDAO.getInstance().toggleUserStatus(userId, !currentStatus)) {
//                refreshUserTable(tableModel);
//                JOptionPane.showMessageDialog(this, "状态已更新");
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
//        }
//    }

    private void refreshUserTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            List<User> users = AdminDAO.getInstance(currentUser).getAllUsers();
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        "******",
                        //user.getPassword(),
                        user.getRole(),
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame frame = new JFrame("Library Admin System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            User testUser=new User(4,"TCWW","123456","admin");
            try {
                frame.setContentPane(new AdminManagePage(testUser, null));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            frame.setVisible(true);
        });
    }
}
