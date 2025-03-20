package Database;//有一说一，这里的包管理比你C++好多了，不像你c++这种层次的就需要去搓命令行和txt了
import Structure.User;
import Structure.Query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

//在这个层次实现对于数据库的选择以及接口的提供与使用
//默认使用的数据库是MySQL，至于后续使用别的，在这里切换即可

/*
    这里考虑由数据库策略类来实现对应的数据库操作语句的构建
    外部的业务模块只进行对应数据的传输
    每个模块的请求对应一个方法
 */

public class DatabaseContext {
    static private volatile DatabaseContext instance;   //单例策略类，如果会涉及到多个数据库时再修改，不过应该不会涉及到那么复杂
    private SQLOperations operations;                   //多态数据库实现类

    public DatabaseContext(SQLOperations operations) {
        this.operations = operations;
    }

    //有一说一，Java中的单例模式由于其的类层次架构需要在高层次上进行一些限制
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone Database.DatabaseContext instance");
    }

    private DatabaseContext() {
        operations  = MySQLOperations.getInstance();
    }

    // 获取单例实例，采用双重检查锁定来确保线程安全
    public static DatabaseContext getInstance() {
        if (instance == null) {
            synchronized (DatabaseContext.class) {
                if (instance == null) {
                    instance = new DatabaseContext();
                }
            }
        }
        return instance;
    }

    //动态切换使用的数据库。虽然目前来说没有用()
    public void setOperations(SQLOperations operations) {
        this.operations = operations;
    }

    //封装 pstmt 检测逻辑
    //需要额外的检测逻辑时再在这里面添加即可
    private boolean isValidStatement(PreparedStatement pstmt) {
        try {
            if (pstmt == null) {
                System.err.println("SQL 语句为空！");
                return false;
            }
            if (pstmt.isClosed()) {
                System.err.println("SQL 语句已经关闭！");
                return false;
            }
            // 仅在支持 getMetaData() 时检查（部分 SQL 语句可能不支持）
            pstmt.getMetaData();
            return true;
        } catch (SQLException e) {
            System.err.println("SQL 语句异常：" + e.getMessage());
            return false;
        }
    }

    // 提供统一的数据库操作接口
    private boolean create(PreparedStatement pstmt) {
        if (isValidStatement(pstmt)) {
            return operations.create(pstmt);
        }
        return false;
    }

    private boolean read(PreparedStatement pstmt) {
        if (isValidStatement(pstmt)) {
            return operations.read(pstmt);
        }
        return false;
    }

    private boolean update(PreparedStatement pstmt) {
        if (isValidStatement(pstmt)) {
            return operations.update(pstmt);
        }
        return false;
    }

    private void delete(PreparedStatement pstmt) {
        if (isValidStatement(pstmt)) {
            operations.delete(pstmt);
        }
    }

    private boolean find(PreparedStatement pstmt) {
        if (isValidStatement(pstmt)) {
            return operations.find(pstmt);
        }
        return false;
    }

    //传入账号密码以及对应的角色进行查询
    public Query loginVerfication(User user) {
        //构建对应的PreparedStatement语句交给底层执行
        String sql = "select * from users where username = ? and password = ? and role = ?";
        try(PreparedStatement pstmt = MySQLOperations.getConnection().prepareStatement(sql))
        {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            //此时已经构建完毕一个查询语句，接下来需要进行查询
            if(this.find(pstmt))
            {
                return Query.match_Suc_Login;
            }
            else
            {
                //未查询到对应的语句，需要进行注册
                String insertSQL="insert into users (username, password, role) values (?,?,?)";
                try(PreparedStatement temp = MySQLOperations.getConnection().prepareStatement(insertSQL))
                {
                    temp.setString(1, user.getUsername());
                    temp.setString(2, user.getPassword());
                    temp.setString(3, user.getRole());
                    this.create(temp);
                    return Query.match_Fail_Create;
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
            return Query.System_Error;
        }
    }

    //策略类模块测试
    public static void main(String []args)
    {
        DatabaseContext dbContext = DatabaseContext.getInstance();
        User user = new User("student1","123456","student");
        Query hasAccount=dbContext.loginVerfication(user);
        if(hasAccount==Query.match_Suc_Login)
        {
            System.out.println("finding user");
        }
        else if(hasAccount==Query.System_Error)
        {
            System.out.println("find fail");
        }
    }

}
