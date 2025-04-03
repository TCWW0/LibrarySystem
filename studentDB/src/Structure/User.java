package Structure;

//封装一些特殊的结构体方便参数的传递
//有一说一，这种对于结构的封装可以节省很多原子参数传递的复杂性

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private int userId = -1;
    private final String username;
    private String password;
    private String role;

    static private Map<Integer,String>idToName = new HashMap<>();

    // 构造方法
    //首次创建的新账户
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    //对于已有账户
    public User(int id,String username, String password, String role) {
        this.userId = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter 方法
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getId() {return userId;}

    // Setter 方法
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(int userId) {this.userId = userId;}

    public static void insertToMap(User user) {User.idToName.put(user.getId(), user.getUsername());}

    public static String findById(int userId) {return User.idToName.get(userId);}

    //重写 toString 方便调试
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +'\''+
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
