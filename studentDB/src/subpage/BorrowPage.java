package subpage;

import Entrance.ApplicationManager;
import Entrance.PageSwitcher;
import Structure.User;

import javax.swing.*;
import java.awt.*;

//每个页面需要绑定一些基本信息，这里指的是对应的使用者信息
public class BorrowPage extends BasePage {
    private final User currentUser;

    public BorrowPage(User user, PageSwitcher pageSwitcher) {
        super(pageSwitcher);
        this.currentUser = user;
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
    }

    public static void main(String[]args)
    {
        SwingUtilities.invokeLater(() -> {
            // 创建测试用户
            User testUser = new User("testUser", "12345","admin");

            // 创建窗口
            JFrame frame = new JFrame("Borrow Page Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            // 添加 BorrowPage 到窗口
            BorrowPage borrowPage = new BorrowPage(testUser,null);
            frame.setContentPane(borrowPage);

            // 显示窗口
            frame.setVisible(true);
        });
    }
}
