package databaseManage;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * 数据库连接池
 * @author Administrator
 */
public class DruidFactory {

	private static DruidDataSource dataSource = null; //数据源

	/**
	 * 初始化
	 * @throws Exception
	 */
	public static void init() throws Exception {
		//加载配置文件
		Properties properties = new Properties();
		InputStream in = DruidFactory.class.getClassLoader().getResourceAsStream("druid.properties");
		properties.load(in);
		dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
		in.close();
	}

	/**
	 * 获取连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		if (null == dataSource) {
			init();
		}
		return dataSource.getConnection();
	}

	public static DruidDataSource getDataSource() {
		return dataSource;
	}

}
