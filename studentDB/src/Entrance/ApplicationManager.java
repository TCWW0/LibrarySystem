package Entrance;

import Structure.User;
import subpage.*;
import factor.*;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/*
    对于SWing中的JFrame和JPanel
    JFrame就像QT中的QMainWindow，负责一个窗口的管理
    JPanel就是一系列子控件的父基类，作用类似于QWidget
    虽然也不像，应该这里只是一个控件，负责各个子组件的管理，即普通组件的容器
 */

public class ApplicationManager implements LoginSuccessListener,PageSwitcher {

    public enum PageType {
        SEARCH,
        BORROW
    }

    private final JFrame mainFrame;                                 //顶层窗口
    private final CardLayout cardLayout;                            //页切换容器
    private final JPanel mainPanel;                                 //普通的控件容器
    private final LoginMenu loginMenu;                             //由于该类使用组合设计，所以单独拎出来
    private final Map<PageType, BasePage> pages = new EnumMap<>(PageType.class);        //管理全部的页面，页仓
    private final PageFactory pageFactory = new SimplePageFactory();

    public ApplicationManager() {
        mainFrame = new JFrame("Library Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);
        mainFrame.setLocationRelativeTo(null);

        //创建主控件管理，使用cardlayout布局管理页
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainFrame.add(mainPanel);

        //初始时只显示登录页面，不需要显示该主窗口
        mainFrame.setVisible(false);

        loginMenu = new LoginMenu();
        loginMenu.setLoginSuccessListener(this);
    }

    //这里实现页面切换逻辑,
    /*
        先判断页表中是否存在对应的页
        存在则取出并刷新显式，否则报错
     */
    public void showPage(PageType pageType) {
        if (!pages.containsKey(pageType)) {
            JOptionPane.showMessageDialog(mainFrame, "页面未初始化");
            return;
        }

        BasePage page = pages.get(pageType);
        page.onPageShown();                     //刷新对应的页面数据

        cardLayout.show(mainPanel, pageType.toString());        //注意标识符匹配
    }

    public void showMainFrame() {
        mainFrame.setVisible(true);
    }

    //回调方法，通过这个来联动非控件的登录页面和控件页面
    //需要注意的是，在此时就传递了接下来所有的子页面所对应的拥有者
    public void loginSuccess(User user) {
        System.out.println("Login success");
        System.out.println("用户 " + user.getUsername() + " 登录成功！");

        loginMenu.closeLoginWindow();

        //这里是回调的注册逻辑
        registerPages(user);

        this.mainFrame.setVisible(true);

        showPage(PageType.SEARCH);
    }

    //接下来所有在BasePage类派生出来的类都需要再这里进行注册使用(使用工厂)
    //由于现有的登录页面设计为了组合结构，所以无法在这里嵌入
    //对于现有的查询页面，目前设计只有一个简单的查询demo，之后会嵌入到BasePage控件中进行管理
    public void registerPages(User user)
    {
        // 创建并存储页面对象,这里的类修改了下继承关系使得对这里的使用兼容
        //BasePage searchPage = new BookSearchPanel(user,this);
        BasePage searchPage = pageFactory.createPage(PageType.SEARCH,user,this);
        pages.put(PageType.SEARCH, searchPage);

        BasePage borrowPage = pageFactory.createPage(PageType.BORROW, user,this);
        pages.put(PageType.BORROW, borrowPage);

        //确保所有页面都被添加到 mainPanel，否则 CardLayout 无法切换
        mainPanel.add(searchPage, PageType.SEARCH.toString());
        mainPanel.add(borrowPage, PageType.BORROW.toString());

    }

    //复用原有的代码实现页面的切换
    @Override
    public void switchToPage(PageType pageType) {
        showPage(pageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            () -> {
            ApplicationManager manager = new ApplicationManager();
            }
        );
    }
}
