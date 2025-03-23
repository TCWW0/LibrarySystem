package Database;

import java.sql.*;

/*
    使用策略模式来进行对于的数据库选择
    使用单例模式来进行特定数据库的实例化

    接口规范我个人不喜欢花里胡哨
    一个数据库就只需要增删改查的操作
    其他的自行进行封装即可。
 */

/*
    这里对于一个程序进行数据库操作的步骤进行分析
    1~通过简单的String类等字符串进行初步的使用占位符的sql语句构建
    2~将一般的字符串传送给PreparedStatement构建出更安全的语句
    3~此时再对于各个参数进行参数的传递，避免被大量数据注入
    4~调用语句对象的类方法executeUpdate()使得数据库底层执行改语句

    这里将语句的构建职责交给了请求发出者
    因为为了后续的可扩展，如果这里来进行数据的处理的话
    由于发出者请求的种类不同，逻辑是会以指数级爆炸的
    所以这里就只关注如何执行对应的sql语句即可
 */

//接口规范
public interface SQLOperations {
    boolean create(PreparedStatement pstmt);               //增加数据
    boolean find(PreparedStatement pstmt);                 //查找数据是否存在
    boolean read(PreparedStatement pstmt);                //读取数据
    boolean update(PreparedStatement pstmt);               //更新数据
    boolean delete(PreparedStatement pstmt);              //删除数据
    boolean close();                                       //关闭方法

    Connection getConnection()throws SQLException;
}
