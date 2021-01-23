package databaseManage;

/**
 * 用户类
 * @author Administrator
 */
public class User {
	
	private int userId; //id
	private String userName; //用户名
	private String password; //密码
	private String date; //注册时间
	
	public User() {}
	
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	
	public User(String userName, String password, String date) {
		this.userName = userName;
		this.password = password;
		this.date = date;
	}

	public User(int userId, String userName, String password, String date) {
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.date = date;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", password=" + password + ", date=" + date + "]";
	}
	
}
