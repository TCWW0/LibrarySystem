package Entrance;

import Structure.User;
import subpage.*;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ApplicationManager implements LoginSuccessListener {

    public enum PageType {
        Search,
        CHILD
    }

    private final JFrame mainFrame;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final LoginMenu loginMenu;                             //由于该类使用组合设计，所以单独拎出来
    private final Map<PageType, BasePage> pages=new EnumMap<>(PageType.class);

    public ApplicationManager() {
        mainFrame = new JFrame("Library Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainFrame.add(mainPanel);

        BookSearchPanel bookSearchPanel = new BookSearchPanel();
        mainPanel.add(bookSearchPanel, PageType.Search.toString());
        loginMenu = new LoginMenu();
        loginMenu.setLoginSuccessListener(this);



    }

    public void addPanel(JPanel panel, PageType pageType) {
        mainPanel.add(panel, pageType.name());
    }

    public void showPage(PageType pageType) {
        cardLayout.show(mainPanel, pageType.name());
    }

    public void showMainFrame() {
        mainFrame.setVisible(true);
    }

    //回调方法，通过这个来联动非控件的登录页面和控件页面
    @Override
    public void loginSuccess(User user) {
        System.out.println("Login success");
        System.out.println("用户 " + user.getUsername() + " 登录成功！");

        loginMenu.closeLoginWindow();

        this.mainFrame.setVisible(true);

        showPage(PageType.Search);
    }

    //接下来所有在BasePage类派生出来的类都需要再这里进行注册使用
    //由于现有的登录页面设计为了组合结构，所以无法在这里嵌入
    //对于现有的查询页面，目前设计只有一个简单的查询demo，之后会嵌入到BasePage控件中进行管理
    public void registerPages()
    {

        for(Map.Entry<PageType, BasePage> entry: pages.entrySet())
        {
            mainPanel.add(entry.getValue(), entry.getKey().toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            () -> {
            ApplicationManager manager = new ApplicationManager();
            manager.mainFrame.setVisible(false);
            //manager.displayLoginMenu();
            }
        );
    }
}
