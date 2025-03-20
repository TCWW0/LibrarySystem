package subpage;

import Structure.Query;
import Database.DatabaseContext;
import Structure.User;


import javax.swing.*;
import java.awt.*;

/*
    组合优于继承
    与其使用继承使得类内的方法很混乱，不如使用组合来分离职责
    这里其实也是一种习惯吧，这里最好是将各个成员丢到函数中让GC进行管理
    不过QT中就是这么写的，都一样，理解就好
 */

/*
    这里需要对于SWing中的一些东西进行熟悉
    对于布局类GridBagConstraints，其使用的是类似于网格布局的格式
    其网格可以具体到某行某列，在这其中，使用期对应的类方法可以设置改类的一些属性
    在一些控件的使用中，通过添加方法add可以实现对于该控件在主窗口上的位置摆放
 */

/*
    在本项目中，我们对于SQL语句的构建其实存在一些问题
    我在这里其实将语句的构建职责交给了对应的请求发出类(业务层)
    这也就意味着在较浅的层次程序就可以进行数据库表的窥探
    这里对于一些攻击来说是危险的
    后续考虑进一步将这里的构建职责进行解耦
 */

/*  在该项目中，我是顺便进行从c++到java的熟悉的
    这里的构造对于一个cpper来说是很不适的
    虽然说GC之前就已经了解过了，但是这里还是得跟QT联动一下
    这里其实就类似于QT中关于对象树的管理机制
    这里将对应的对象挂到对应的对象树上，然后就将对应对象的内存管理交给容器
    这里也是如此，因此，这也是为什么对于一些对象，在函数死亡后仍能够显示的原因

 */


public class LoginMenu {

    /*
    由于C++的习惯，我其实很讨厌这里去转换为局部变量去给GC管理
     */
    private LoginMenuListener listener;

    private DatabaseContext db;                         //保留一个策略类的使用
    private JFrame myFrame;
    private JTextField usernameField;                   //储存输入的账号数据
    private JPasswordField passwordField;               //储存输入的密码数据
    private JComboBox<String> roleSelector;             //身份选择框
    private JButton loginButton, cancelButton;

    private LoginSuccessListener successListener = null;       //登录回调的类的使用

    public LoginMenu() {
        db= DatabaseContext.getInstance();               //获取策略接口
        setBackground();                                //初始化窗口
        addLoginItem();                                 //将对应的控件添加
    }

    public void setBackground()
    {
        myFrame = new JFrame("用户登录");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setSize(300, 350);
        myFrame.setLocationRelativeTo(null); // 居中显示
        myFrame.setLayout(new GridBagLayout());             //使用网格布局
    }

    /*
    GridBagConstraints中的inserts成员的作用是来控制一个空间周围的间隔
    这也是基于网格才能实现的
    这里的gridx和gridy都是基于逻辑上的相对位置的
     */

    public void addLoginItem()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);            //该类的insets属性是一个控件周边的间隔大小

        //身份选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        String[]roles={"student","admin"};
        roleSelector = new JComboBox<>(roles);
        customizeComboBox(roleSelector);
        myFrame.add(roleSelector, gbc);

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 1;
        myFrame.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        myFrame.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        myFrame.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        myFrame.add(passwordField, gbc);


        // 登录按钮
        gbc.gridx=0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        loginButton = new JButton("登录");
        myFrame.add(loginButton, gbc);

        //绑定输入信息处理
        loginButton.addActionListener(e -> handleSQL());

        // 取消按钮
        gbc.gridx = 1;
        cancelButton = new JButton("取消");
        myFrame.add(cancelButton, gbc);

        cancelButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(cancelButton);
            if (frame != null) {
                frame.dispose(); // 仅关闭当前窗口
            }
        });


        myFrame.setVisible(true);
    }

    //这个函数负责我们SQL语句的构建，不应该暴露给外界
    //考虑先在这里进行一次基础的检测，排除一些恶意的账号密码
    private void handleSQL()
    {
        String username=usernameField.getText();
        String password=String.valueOf(passwordField.getPassword());
        String role=roleSelector.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名或密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (username.length() < 3 || username.length() > 20 || password.length() < 6 || password.length() > 20) {
            JOptionPane.showMessageDialog(null, "用户名或密码长度不合法！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            JOptionPane.showMessageDialog(null, "用户名只能包含字母、数字和下划线！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //使用账号密码创建User对象来进行进一步的操作
        User user=new User(username,password,role);
        Query loginAddition=db.loginVerfication(user);
        //此语句之后已经登录成功，通知监听器，让监听器通知其他监听者
        handleLogin(loginAddition);
        //System.out.println(user.toString());
        if(listener!=null)
        {
            listener.onLoginSuccess(user);
        }
    }

    public void handleLogin(Query loginQuery)
    {
        if(loginQuery==Query.System_Error)
        {
            JOptionPane.showMessageDialog(null,"发生系统错误","error",JOptionPane.ERROR_MESSAGE);
        }
        else if(loginQuery==Query.match_Suc_Login)
        {
            JOptionPane.showMessageDialog(null,"成功登录账户","success",JOptionPane.INFORMATION_MESSAGE);
            if(successListener!=null){
                String username=usernameField.getText();
                String password=String.valueOf(passwordField.getPassword());
                String role=roleSelector.getSelectedItem().toString();
                User user=new User(username,password,role);
                successListener.loginSuccess(user);
            }
        }
        else if(loginQuery==Query.match_Fail_Create)
        {
            JOptionPane.showMessageDialog(null,"初次登录，已自动创建账户","warning",JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void customizeComboBox(JComboBox<?> comboBox) {
        // 设置字体
        comboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        // 透明背景 + 自定义颜色
        comboBox.setOpaque(false);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);

        // 设置 UIManager 来移除蓝色默认样式
        UIManager.put("ComboBox.selectionBackground", Color.WHITE);
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", Color.BLACK);
    }

    public interface LoginMenuListener{
        void onLoginSuccess(User user);
    }

    public void setLoginSuccessListener(LoginSuccessListener successListener) {
        this.successListener = successListener;
    }

    public void showLoginWindow()
    {
        this.myFrame.setVisible(true);
    }

    public void closeLoginWindow()
    {
        this.myFrame.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginMenu::new);
    }
}
