package factor;

import Entrance.PageSwitcher;
import Entrance.ApplicationManager;
import Structure.User;
import subpage.AdminManagePage;
import subpage.BasePage;
import subpage.BookSearchPanel;
import subpage.BorrowPage;


//项目结构相对简单，没有必要使用工厂来为难自己，简单工厂够用了
public class SimplePageFactory implements PageFactory {


    @Override
    public BasePage createPage(ApplicationManager.PageType pageType, User currentUser, PageSwitcher pageSwitcher) {
        switch (pageType) {
            case SEARCH:
                return new BookSearchPanel(currentUser, pageSwitcher);
            case BORROW:
                return BorrowPage.getInstance(currentUser, pageSwitcher);
            case ADMIN_MANAGE: // 新增管理员页面类型
                if ("admin".equals(currentUser.getRole())) {
                    return new AdminManagePage(currentUser, pageSwitcher);
                } else {
                    //throw new AccessDeniedException("权限不足");
                }
            default:
                throw new IllegalArgumentException("未知界面类型");
        }
    }
}
