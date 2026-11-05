package UI;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DB.connection;


public class join extends JFrame{
	JTextField name_tf = new JTextField(20);	//이름
	JTextField id_tf = new JTextField(20);		//아이디
	JTextField pw_tf = new JTextField(20);		//비밀번호
	public join() {
		setTitle("회원가입");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new join_north(),BorderLayout.NORTH);
		add(new join_center(),BorderLayout.CENTER);
		add(new join_south(),BorderLayout.SOUTH);
		
		setSize(300,220);
		setVisible(true);
	}
	class join_north extends JPanel{		//정보(이름, 아이디, 비밀번호) 입력 텍스트창
		String li[] = {"이름     ","아이디  ","비밀번호"};
		JLabel la[] = new JLabel[li.length];
		public join_north() {	
			setLayout(new GridLayout(3,1));
			add(new Namepanel());
			add(new Idpanel());
			add(new Pwpanel());
		}
		class Namepanel extends JPanel{		//이름 입력텍스트
			public Namepanel() {
				la[0] = new JLabel(li[0]);
				add(la[0]);
				add(name_tf);
			}
		}
		class Idpanel extends JPanel{		//아이디 입력 텍스트
			public Idpanel() {
				la[1] = new JLabel(li[1]);
				add(la[1]);
				add(id_tf);
			}
		}
		class Pwpanel extends JPanel{		//비밀번호 입력텍스트
			public Pwpanel() {
				la[2] = new JLabel(li[2]);
				add(la[2]);
				add(pw_tf);
			}
		}
	}
	JComboBox<String> year;
	JComboBox<String> month;
	JComboBox<String> day;
	class join_center extends JPanel{
		public join_center() {
			Calendar cal = Calendar.getInstance();
			int calyear = cal.get(Calendar.YEAR);
			String li[] = {"생년월일","년","월","일"};
			JLabel la[] = new JLabel[li.length];
			year = new JComboBox<String>();
			month = new JComboBox<String>();
			day = new JComboBox<String>();
			
			for(int i=0;i<=calyear-1900;i++) {		//년도 1900~현재년도(2024)
				year.addItem(Integer.toString(i+1900));
			}
			for(int i=0;i<12;i++) {					//월(1~12)
				month.addItem(Integer.toString(i+1));
			}
			for(int i=0;i<31;i++) {					//초기 날짜(1~31)
				day.addItem(Integer.toString(i+1));
			}
			for(int i=0;i<li.length;i++) {
				la[i] = new JLabel(li[i]);
			}
			
			add(la[0]);
			add(year);
			add(la[1]);
			add(month);
			add(la[2]);
			add(day);
			add(la[3]);
			
			year.addActionListener(new take_day());
			month.addActionListener(new take_day());
		}
		class take_day implements ActionListener{		//날짜 가져오기
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> gbox = (JComboBox<String>) e.getSource();
				Vector<String> daylist = new Vector<String>();
				String index = gbox.getSelectedItem().toString();
				int index2 = Integer.parseInt(index);
				
				int year = 0,month = 0;
				if(index2>=1900) {		 			//년도가 1900년도부터 시작
					year = index2;
				}else if(index2>=1||index2<=12){	//월이 12월까지 있음
					month = index2;
				}
				
				GregorianCalendar cld = new GregorianCalendar(year,month-1,1);	//월별 마지막날짜 알아내기
				int maxday = cld.getActualMaximum(Calendar.DAY_OF_MONTH);
//				System.out.println(maxday);

				for(int i=1;i<=maxday;i++) {		//벡터에 날짜 저장
					daylist.add(Integer.toString(i));
				}
				day.removeAllItems();				//초기에 저장되어있던 일자 삭제
				for(int i=0;i<daylist.size();i++) {	//벡터에 저장된 날짜들을 아이템박스(day)에 넣기
					day.addItem(daylist.get(i));
				}
			}
		}
	}
	
	class join_south extends JPanel{		//가입완료, 종료 버튼
		public join_south() {
			String li[] = {"가입완료","종료"};
			JButton btn[] = new JButton[li.length];
			
			for(int i=0;i<li.length;i++) {
				btn[i] = new JButton(li[i]);
				add(btn[i]);
				btn[i].addActionListener(new btn_act());
			}
		}
		class btn_act implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String st = e.getActionCommand();
				
				switch(st) {
				case "가입완료": {
					String join_name = name_tf.getText();
					String join_id = id_tf.getText();
					String join_pw = pw_tf.getText();
					join_chk(join_name,join_id,join_pw); //중복확인,빈칸확인 체크 함수
					break;		
				}
				case "종료": dispose();	new login(); 
				}
			};
		}
	}
	public void join_chk(String name, String id, String pw) {
		if(name.equals("")||id.equals("")||pw.equals("")) {		//빈칸확인
			JOptionPane.showMessageDialog(null, "누락된 항목이 있습니다.");
		}else {
			Connection con = connection.makeConnection("");
			PreparedStatement psmt = null;
			Statement stmt = null;
			
			try {
				stmt = con.createStatement();
				stmt.executeUpdate("use coffee_an");
				psmt = con.prepareStatement("select u_name from user where u_id = ?");		//중복확인
				psmt.setString(1, id);
				
				ResultSet rs = psmt.executeQuery();
				if(rs.next()) {
					JOptionPane.showMessageDialog(null, "아이디가 중복되었습니다.");
					id_tf.setText("");			
				}
				else {																		//중복 없음 -> 회원가입
					ResultSet u_no = stmt.executeQuery("SELECT u_no FROM user ORDER BY u_no DESC LIMIT 1");
					u_no.next();
					String BD = take_bd();
					String No = Integer.toString(u_no.getInt(1)+1);
					psmt = con.prepareStatement("insert into user values(?,?,?,?,?,0,'일반')");
					psmt.setString(1, No); 			//가장 최근 회원번호+1
					psmt.setString(2, id);			//아이디	
					psmt.setString(3, pw);			//비밀번호
					psmt.setString(4, name);		//이름
					psmt.setString(5, BD);			//생년월일
					
					int joinrs = psmt.executeUpdate();
					if(joinrs==1) {
						JOptionPane.showMessageDialog(null, "가입이 완료되었습니다.");
						dispose();
						new login();
					}
					else {
						JOptionPane.showMessageDialog(null, "가입실패, 관리자를 통해 확인하세요");
					}
				}
			}
			catch (SQLException e) {
				System.out.println("DB오류");
				System.out.println(e);
			}
		}
		
	}
	
	public String take_bd() {		//가입 생년월일 가져오기
		String uyear,umonth,uday;
		String ins_bd;
		uyear = year.getSelectedItem().toString();
		umonth = month.getSelectedItem().toString();
		uday = day.getSelectedItem().toString();
		if(Integer.parseInt(umonth)>=10&&Integer.parseInt(uday)>=10) {
			ins_bd = uyear+"-"+umonth+"-"+uday;
		}else if(Integer.parseInt(umonth)>=10&&Integer.parseInt(uday)<10) {
			ins_bd = uyear+"-"+umonth+"-0"+uday;
		}else {
			ins_bd = uyear+"-0"+umonth+"-0"+uday;
		}
		System.out.println(ins_bd);
		return ins_bd;
	}

	public static void main(String[]args) {
		new join();
	}
}
