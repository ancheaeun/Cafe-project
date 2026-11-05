package DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class create_db {
	public create_db() {
		Connection con;
		Statement st = null;
		String create_db = "create database if not exists coffee_an";
		String create_table[] = {"create table if not exists menu(m_no int primary key not null auto_increment,"
				+ "m_group varchar(10), m_name varchar(30), m_price int)",
				
				"create table if not exists user(u_no int primary key not null auto_increment, u_id varchar(20),"
				+ "u_pw varchar(4), u_name varchar(5), u_bd varchar(14), u_point int, u_grade varchar(10))",
				
				"create table if not exists orderlist(o_no int primary key not null auto_increment,o_date Date,"
				+ " u_no int, m_no int, o_group varchar(10),o_size varchar(1), o_price int, o_count int, o_amount int, "
				+ "foreign key(u_no) references user(u_no) on update cascade, foreign key(m_no) references menu(m_no) on update cascade)",
				
				"create table if not exists shopping(s_no int primary key not null auto_increment,"
				+ "m_no int, s_price int, s_count int, s_size varchar(1), s_amount int, foreign key(m_no) references menu(m_no) on update cascade)"};
		
		try {
			con = connection.makeConnection("");	//데이터베이스 연결
			st = con.createStatement();				//데이터베이스 명령어 사용(?)
			st.executeUpdate(create_db);			//데이터베이스 생성
			st.executeUpdate("use coffee_an");		//데이터베이스 사용
			
			for(int i=0;i<create_table.length;i++) {
				st.executeUpdate(create_table[i]);	//데이터베이스-테이블 생성
			}
			System.out.println("만들기 성공");
		}
		catch(SQLException e){
			System.out.println(e);
			System.out.println("SQL오류");
		}
	}
	public static void main(String []args) {
//		new create_db();
	}
}