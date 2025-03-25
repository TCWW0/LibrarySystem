package subpage;

import Entrance.PageSwitcher;

import Entrance.ApplicationManager;

//对于该类，这是重新开始接下来的类设计的起点
/*
    对于之前的登录页面和查询页面，其一个是组合模式，一个是直接的继承设计
    俩者都是对于一些从C++向Java语法的一种熟悉
    在此处，开始更加严格规定接下来的类的规范设计

    要求:1~使用继承结构来个性化控件，这种继承结构使得页面可以使得其被主控件ApplicationManager进行管理
        2~要求每个实现类中都有着接口规范，也就是接下来的此基页类
        3~每个页面都需要保留一个对于页管理器的引用
 */

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public abstract class BasePage extends JPanel{

    //格式化文本,统一字体的配置
    protected Font titlefont = new Font("微软雅黑", Font.BOLD, 14);
    protected Font btnFont = new Font("微软雅黑", Font.BOLD, 14);
    protected Font bodyFont = new Font("宋体",Font.PLAIN,12);
    protected static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);

    protected PageSwitcher pageSwitcher;                    //页面切换回调接口

    protected static final Color BACKGROUND_COLOR = Color.BLACK; // 页面背景色
    protected static final Color BORDER_COLOR = Color.WHITE;    // 边框颜色
    protected static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12); // 默认字体
    protected static final int BORDER_RADIUS = 20; // 圆角半径

    public BasePage(PageSwitcher pageSwitcher) {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(240,240,240));

        this.pageSwitcher = pageSwitcher;
        initUI();
    }

    public void setSwitcher(PageSwitcher pageSwitcher) {
        this.pageSwitcher = pageSwitcher;
    }

    //页面切换时需要的数据刷新
    public abstract void onPageShown();
    //ABC类接口,每个层次注意自己该层次的控件格式设置即可，但注意风格统一
    protected abstract void initUI();

    //这里可以看做是格式化工厂，通过传入数据给你输出相同样式的控件
    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 背景颜色调整为浅灰色系
                if (getModel().isPressed()) {
                    g2.setColor(new Color(200, 200, 200)); // 按下状态
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(240, 240, 240)); // 悬停状态
                } else {
                    g2.setColor(Color.WHITE);          // 默认状态
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 180, 180)); // 中灰色边框
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
                g2.dispose();
            }
        };

        // 文字颜色调整
        button.setForeground(Color.BLACK);
        button.setFont(DEFAULT_FONT);

        // 其他样式保持不变
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(8, 20, 8, 20));

        button.setPreferredSize(new Dimension(100,30));

        return button;
    }

    // 创建统一风格的文本框
    protected JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. 绘制白色圆角背景
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 2. 绘制浅灰色边框
                g2.setColor(new Color(200, 200, 200)); // 浅灰色边框
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };

        // 3. 设置文字和光标颜色
        textField.setForeground(Color.BLACK);      // 黑色文字
        textField.setCaretColor(new Color(80, 80, 80)); // 深灰色光标

        // 4. 确保背景绘制有效
        textField.setOpaque(false); // 保持半透明以显示自定义背景

        textField.setFont(DEFAULT_FONT);

        setPreferredSize(new Dimension(200,25));

        return textField;
    }

    // 创建统一风格的标签
    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(DEFAULT_FONT);
        return label;
    }

    // 创建统一风格的表格
    // 美化后的表格控件
    protected JTable createStyledTable() {
        JTable table = new JTable() {
            //类似于责任链的拦截
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setFont(DEFAULT_FONT);
        table.setOpaque(true);
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(DEFAULT_FONT);

        //设置边框格式
        table.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x333333), 1), // 1像素深灰色边框
                BorderFactory.createEmptyBorder(5, 5, 5, 5)            // 5像素内边距
        ));

        // 禁止用户拖动列
        table.getTableHeader().setReorderingAllowed(false);

        // 禁止调整列宽
        table.getTableHeader().setResizingAllowed(false);

        return table;
    }

    //类似于QT的那种，这里的控件格式控制还是使用责任链模式设计的，所以使用重写可以来设置格式
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆角背景
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 圆角半径为20

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20); // 圆角边框
        g2.dispose();
    }

    //设置自己的美化格式
    protected void beautifyPanel() {
        setBackground(Color.BLACK); // 设置页面背景为黑色
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 上下左右内边距为10
    }
}

