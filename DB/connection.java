package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {
	public static Connection makeConnection(String db) {
		String url = "jdbc:mysql://localhost/"+db;
		String id = "root";
		String pass = "1234";
		Connection con= null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			
			con = DriverManager.getConnection(url,id,pass);
			System.out.println("데이터베이스 연결 성공");
		}catch (ClassNotFoundException e) {
			System.out.println("드라이브를 찾을 수 없습니다.");
		}catch (SQLException e) {
			System.out.println("SQL연결 실패");
		}
		
		return con;
	}
	public static void main(String args[]) {
//		Connection con = makeConnection("");
	}
}