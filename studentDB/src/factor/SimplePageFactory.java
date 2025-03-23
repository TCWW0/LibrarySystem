package factor;

import Entrance.PageSwitcher;
import Entrance.ApplicationManager;
import Structure.User;
import subpage.BasePage;
import subpage.BorrowPage;


//项目结构相对简单，没有必要使用工厂来为难自己，简单工厂够用了
public class SimplePageFactory implements PageFactory {


    @Override
    public BasePage createPage(ApplicationManager.PageType pageType, User CurrentUser, PageSwitcher pageSwitcher) {
        switch (pageType) {
            case ApplicationManager.PageType.BORROW:
                return new BorrowPage(CurrentUser, pageSwitcher);

            default:
                throw new IllegalArgumentException("未知界面类型");

        }
    }
}
