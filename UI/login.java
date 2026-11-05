package UI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DB.connection;

public class login extends JFrame{
	JTextField id_tf = new JTextField(15);		//id 텍스트
	JPasswordField pw_tf = new JPasswordField(15);	//pw 텍스트
	public login() {
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new login_north(),BorderLayout.NORTH);
		add(new login_center(),BorderLayout.CENTER);
		add(new login_south(),BorderLayout.SOUTH);
		add(new login_east(),BorderLayout.EAST);
		
		setSize(300,200);
		setVisible(true);
	}
	class login_north extends JPanel{	//로고
		public login_north() {
			String st = new String("STARBOX");
			JLabel la = new JLabel(st);
			la.setFont(new Font("SansSerif", Font.BOLD, 25));
			add(la);
		}
	}
	class login_east extends JPanel{	//로그인 버튼
		public login_east() {
			String li = "로그인";
			JButton btn = new JButton(li);
			btn.setPreferredSize(new Dimension(70, 70));
			add(btn);
			btn.addActionListener(new login_act());
		}
	}
	class login_act implements ActionListener{
		public void actionPerformed(ActionEvent e) {			
			String id = id_tf.getText();
			String pw = new String(pw_tf.getPassword());
			login_chk(id,pw);	//로그인 확인 함수
		};
	}
	public void login_chk(String id,String pw) {	//로그인 확인 함수
		if(id.equals("")||pw.equals("")) {
			JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.");
			id_tf.setText("");
			pw_tf.setText("");
		}else {
			Connection con = connection.makeConnection("");
			PreparedStatement psmt = null;
			Statement stmt = null;
			
			try {
				stmt = con.createStatement();
				stmt.executeUpdate("use coffee_an");
				psmt = con.prepareStatement("select u_name from user where u_id = ? and u_pw = ?");
				if(id.equals("admin")&&pw.equals("1234")) {		//관리자로 로그인 시
					dispose();
					new admin();
				}else {											//일반 회원으로 로그인시
					psmt.setString(1, id);
					psmt.setString(2, pw);
					
					ResultSet rs = psmt.executeQuery();
					if(rs.next()) {
						dispose();
						new Main(id,pw);	//회원정보 확인을 위한 id,pw 보내주기
					}else {	
						JOptionPane.showMessageDialog(null, "회원정보가 틀립니다.다시입력해주세요.");
						id_tf.setText("");
						pw_tf.setText("");
					}
				}
			}
			catch (SQLException e) {
				System.out.println("DB오류");
			}
		}
	}
	
	class login_center extends JPanel{	//ID,PW 입력 텍스트
		String li[] = {"  ID :","PW :"};
		JLabel la[] = new JLabel[li.length];
		public login_center() {
			add(new idpanel());
			add(new pwpanel());
		}
		
		class idpanel extends JPanel{	//IDpanel
			public idpanel() {
				la[0] = new JLabel(li[0]);
				add(la[0]);
				add(id_tf);
			}
		}
		class pwpanel extends JPanel{	//PWpanel
			public pwpanel() {
				la[1] = new JLabel(li[1]);
				add(la[1]);
				add(pw_tf);
			}
		}
	}
	class login_south extends JPanel{	//회원가입,종료 버튼
		public login_south() {
			String li[] = {"회원가입","종료"};
			JButton btn[] = new JButton[li.length];
			for(int i=0;i<li.length;i++) {
				btn[i] = new JButton(li[i]);
				add(btn[i]);
				btn[i].addActionListener(new sou_act());
			}
		}
	}
	class sou_act implements ActionListener{	//회원가입,종료 버튼 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			String st = e.getActionCommand();
			
			switch(st) {
			case "회원가입": new join(); break;
			case "종료": dispose();
			}
		}
	}
	public static void main(String[]args) {
		new login();
	}
}
