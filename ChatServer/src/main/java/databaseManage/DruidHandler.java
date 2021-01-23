package databaseManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库操作类
 * @author Administrator
 */
public class DruidHandler {

	/**
	 * 登录验证
	 * @param con
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public boolean login(Connection con, User user) throws SQLException {
		boolean flag;
		String sql = "select * from user where userName = ? and password = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) 
			flag = true;
		else
			flag = false;
		rs.close();
		pstmt.close();
		return flag;
	}

	/**
	 * 注册
	 * @param con
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public boolean register(Connection con, User user) throws SQLException {
		//查询是否存在该用户
		String sql = "select * from user where userName = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return false;
		//插入数据
		sql = "insert into user values(null, ?, ?, ?)";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		pstmt.setString(3, user.getDate());
		pstmt.executeUpdate();
		pstmt.close();
		return true;
	}
	
}
