package subpage;

import Entrance.PageSwitcher;
import Structure.User;
import javax.swing.*;
import java.awt.*;

public class AdminManagePage extends BasePage {
    public AdminManagePage(User user, PageSwitcher pageSwitcher) {
        super(pageSwitcher);
        initUI();
    }

    @Override
    protected void initUI() {
        JButton addBookBtn = createStyledButton("书籍入库");
        addBookBtn.addActionListener(e -> openAddBookDialog());
        add(addBookBtn, BorderLayout.NORTH);
    }

    private void openAddBookDialog() {
        // 实现书籍入库逻辑
    }

    @Override
    public void onPageShown() {}
}
