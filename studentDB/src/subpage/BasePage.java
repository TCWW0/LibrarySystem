package subpage;

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

public class BasePage extends JPanel implements Page{

    ApplicationManager app;

    public BasePage(ApplicationManager appManager) {
        this.app = appManager;
    }

    @Override
    public void initialize() {

    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
