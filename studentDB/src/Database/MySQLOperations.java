package Database;

import java.sql.*;

/*
    这一层是我希望的数据库操作层，对于数据库的操作语句由其来进行构建和执行
    至于参数这一块，有上下文来进行从UI层往这一层中的传输
 */

//特化对应的数据库操作
public class MySQLOperations implements SQLOperations {
    private static volatile MySQLOperations instance;
    private static Connection connection ;

    private static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Tcww3498";

    //方法复用
    private boolean executeUpdate(PreparedStatement pstmt) {
        try {
            pstmt.executeUpdate();
            System.out.println("SQL 执行成功！");
            return true;
        } catch (SQLException e) {
            System.err.println("SQL 执行失败！");
            e.printStackTrace();
            return false;
        }
    }

    // 私有构造函数（单例模式）
    private MySQLOperations() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("MySQL 数据库连接成功！");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL 数据库连接失败！");
        }
    }

    // 获取单例实例
    public static MySQLOperations getInstance() {
        if (instance == null) { // 第一次检查
            synchronized (MySQLOperations.class) {
                if (instance == null) { // 第二次检查
                    instance = new MySQLOperations();
                }
            }
        }
        return instance;
    }

    // 获取数据库连接
    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败，尝试重新连接...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }


    @Override
    public boolean create(PreparedStatement pstmt) {
        return executeUpdate(pstmt);
    }

    @Override
    public boolean read(PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("查询到数据：" + rs.getString(1)); // 这里可以修改为适合你的字段
            }
        } catch (SQLException e) {
            System.err.println("SQL 查询失败！");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PreparedStatement pstmt) {
        return executeUpdate(pstmt);
    }

    @Override
    public boolean delete(PreparedStatement pstmt) {
        return executeUpdate(pstmt);
    }

    @Override
    public boolean find(PreparedStatement pstmt)
    {
        try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
            return rs.next(); // 只需检查是否有查询结果
        } catch (SQLException e) {
            System.err.println("查询失败：" + e.getMessage());
            e.printStackTrace();
            return false; // 发生异常时，返回 false
        }
    }

    @Override
    public boolean close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                instance = null; // 释放单例对象
                System.out.println("数据库连接已关闭！");
            } catch (SQLException e) {
                System.err.println("关闭数据库连接失败！");
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }


    //单元模块测试
    public static void main(String[] args) throws SQLException {
        SQLOperations db=MySQLOperations.getInstance();
        //查找操作成功
//        String findSql="select * from users";
//        PreparedStatement pstmt=connection.prepareStatement(findSql);
//        if(db.find(pstmt))
//            System.out.println("查询成功");

        //插入操作成功
//        String insertSql="insert into users (username, password, role) values (?, ?, ?)";
//        PreparedStatement pstmt=connection.prepareStatement(insertSql);
//        pstmt.setString(1,"TCWW");
//        pstmt.setString(2,"Tcww3498");
//        pstmt.setString(3,"admin");
//        if(db.create(pstmt))
//            System.out.println("用户创建成功");

        //删除操作测试通过
//        String sql = "DELETE FROM users WHERE username=?";
//        PreparedStatement pstmt = connection.prepareStatement(sql);
//        pstmt.setString(1, "Tcww");
//
//        if (db.delete(pstmt)) {
//            System.out.println("删除操作成功");
//        } else {
//            System.out.println("删除操作失败");
//        }

        //更新测试通过
//        String updateSql="update users set password=? where username=?";
//        PreparedStatement pstm = connection.prepareStatement(updateSql);
//        pstm.setString(1, "123456");
//        pstm.setString(2, "student1");
//        if(db.update(pstm)) {
//            System.out.println("更新操作成功");
//        }

    }

}
