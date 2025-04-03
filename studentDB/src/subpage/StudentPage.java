package subpage;

import Entrance.ApplicationManager;
import Entrance.PageSwitcher;
import Structure.User;
import factor.SimplePageFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

//在这里去集成所有的用户页面交给对应的显示
public class StudentPage extends BasePage {

    private final User currentUser;
    private JTabbedPane tabbedPane;
    private SimplePageFactory pageFactory;

    public StudentPage(User user,PageSwitcher pageSwitcher) throws SQLException {
        super(pageSwitcher);
        this.currentUser = user;
        pageFactory = new SimplePageFactory();
        initSubPage();
    }

    @Override
    public void onPageShown() {

    }

    @Override
    protected void initUI() {}

    private void initSubPage() throws SQLException {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // 添加子页面作为标签页
        tabbedPane.addTab("图书查询", createSearchPanel());
        tabbedPane.addTab("借阅记录", createBorrowRecordPanel());
        //tabbedPane.addTab("个人信息", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchPanel = pageFactory.createPage(ApplicationManager.PageType.SEARCH,currentUser,pageSwitcher);
        panel.add(searchPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBorrowRecordPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel borrowPanel = pageFactory.createPage(ApplicationManager.PageType.BORROW,currentUser,pageSwitcher);
        panel.add(borrowPanel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame frame = new JFrame("Library Admin System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            User testUser=new User(3,"TCWW","123456","admin");
            try {
                frame.setContentPane(new StudentPage(testUser, null));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            frame.setVisible(true);
        });
    }
}
