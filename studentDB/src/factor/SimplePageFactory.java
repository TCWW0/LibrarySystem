package factor;

import Entrance.PageSwitcher;
import Entrance.ApplicationManager;
import Structure.User;
import subpage.*;

import javax.swing.*;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;


//项目结构相对简单，没有必要使用工厂来为难自己，简单工厂够用了
public class SimplePageFactory implements PageFactory {

    @Override
    public BasePage createPage(ApplicationManager.PageType pageType, User currentUser, PageSwitcher pageSwitcher){
        try{
            switch (pageType) {
                case SEARCH:
                    return new BookSearchPanel(currentUser, pageSwitcher);
                case BORROW:
                    return BorrowPage.getInstance(currentUser, pageSwitcher);
                case ADMIN_MANAGE: // 新增管理员页面类型
                    if (!"admin".equals(currentUser.getRole())) {
                        return null; // 或者抛出自定义异常
                    }
                    return new AdminManagePage(currentUser, pageSwitcher);
                case STU_MANAGER:
                    if (!"student".equals(currentUser.getRole())) {
                        return null;
                    }
                    return new StudentPage(currentUser, pageSwitcher);
                default:
                    throw new IllegalArgumentException("未知界面类型");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"数据库错误"+e.getMessage());
            return null;
        }
    }
}
