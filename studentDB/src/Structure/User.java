package Structure;

//封装一些特殊的结构体方便参数的传递
//有一说一，这种对于结构的封装可以节省很多原子参数传递的复杂性

public class User {
    private final String username;
    private String password;
    private String role;

    // 构造方法
    public User(String username, String password, String role) {
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

    // Setter 方法（如果需要修改用户信息）
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    //重写 toString 方便调试
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
