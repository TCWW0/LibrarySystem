package factor;

import Entrance.ApplicationManager;
import Structure.User;
import subpage.BasePage;
import Entrance.PageSwitcher;

import java.sql.SQLException;

public interface PageFactory {
    BasePage createPage(ApplicationManager.PageType pageType, User CurrentUser,PageSwitcher pageSwitcher);
}
