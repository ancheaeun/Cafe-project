package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import DB.connection;

public class history extends JFrame{
	JTable jt;
	Vector<Vector<String>> rowdata = new Vector<Vector<String>>();	//행
	Vector<String> coldata = new Vector<String>();	//열
	public history(String u_name, String u_id,String u_pw) {		//회원 이름 가져오기
		setTitle("구매내역");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(new his_north(u_name),BorderLayout.NORTH);
		add(new his_center(u_name,u_id,u_pw),BorderLayout.CENTER);
		add(new his_south(u_id, u_pw),BorderLayout.SOUTH);
		
		setSize(750,600);
		setVisible(true);	
	}
	class his_north extends JPanel{
		public his_north(String u_name) {
			String name = u_name;
			JLabel la = new JLabel(name+"회원님 구매 내역");
			add(la);
		}
	}
	class his_center extends JPanel{
		String colli[] = {"구매일자","메뉴명","가격","사이즈","수량","총금액"};
		public his_center(String name, String id, String pw) {		//아이디, 비번, 이름을 이용해서 구매내역 불러오기
			gethistory(name,id,pw);
			for(int i=0;i<colli.length;i++) {
				coldata.add(colli[i]);
			}
			DefaultTableModel tm = new DefaultTableModel(rowdata, coldata);
			jt = new JTable(tm);
			
			JScrollPane scroll = new JScrollPane(jt);
			scroll.setPreferredSize(new Dimension(700, 400));
			add(scroll);
		}
		public void gethistory(String name, String id, String pw) {		//구매내역 불러오기(회원 번호 가져오기)
			Connection con = connection.makeConnection("coffee_an");
			PreparedStatement psmt = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			String u_no;
			
			try {
				String ins = "select u_no from user where u_id = '"+ id +"' and u_pw = '"+pw+"'";	//회원 번호 가져오기
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				u_no = rs.getString(1);
				
				ins = "select o_date, m_no, o_price, o_size, o_count, o_amount from orderlist where u_no = '"+u_no+"'";		//회원 번호를 이용한 구매내역 불러오기
				rs = stmt.executeQuery(ins);
				while(rs.next()) {
					Vector<String> shop_history = new Vector<String>();
					for(int i=0;i<6;i++) {
						shop_history.add(rs.getString(i+1));
					}
//					System.out.println(shop_history);
					rowdata.add(shop_history);
				}
			}
			catch(SQLException e) {
				System.out.println("DB오류: "+e);
			}
		}
	}
	class his_south extends JPanel{
		String uid,upw;
		public his_south(String id,String pw) {
			this.uid = id;
			this.upw = pw;
			String st1 = "총 결제 금액";
			JLabel la = new JLabel(st1);
			JTextField tf = new JTextField(20);
			JButton btn = new JButton("닫기");
			tf.setEnabled(false);		//tf수정 불가
			
			Connection con = connection.makeConnection("coffee_an");
			PreparedStatement psmt = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			String u_no, u_asum;
			
			try {
				String ins = "select u_no from user where u_id = '"+ id +"' and u_pw = '"+pw+"'";	//회원 번호 가져오기
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				u_no = rs.getString(1);
				
				ins = "select sum(o_amount) from orderlist where u_no = '" +u_no+"'"; 
				rs = stmt.executeQuery(ins);
				rs.next();
				u_asum = rs.getString(1);
				tf.setText(u_asum);
				
			}
			catch(SQLException e) {
				System.out.println("DB 오류: "+e);
			}
			
			add(la);
			add(tf);
			add(btn);
			btn.addActionListener(new btn_act());
		}	
		class btn_act implements ActionListener{
			public void actionPerformed(java.awt.event.ActionEvent e) {
				dispose();
				new Main(uid,upw);
			};
		}
	}
}
