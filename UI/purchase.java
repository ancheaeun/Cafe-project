package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import DB.connection;

public class purchase extends JFrame{
	JTable jt;
	String uid, upw;	//고객 아이디 비번
	String u_name;		//고객 이름
	int chk_sel;		//삭제시 선택한 열 
	Vector<String> get_menu;	//삭제시 선택한 열의 메뉴 이름
	Vector<Vector<String>> rowdata = new Vector<Vector<String>>();	//행
	Vector<String> coldata = new Vector<String>();	//열
	String sel_m_no;
	public purchase(String uid,String upw) {	//고객 아이디, 비번
		setTitle("장바구니");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.uid = uid;
		this.upw = upw;
		
		add(new pur_north(uid,upw),BorderLayout.NORTH);
		add(new pur_center(),BorderLayout.CENTER);
		add(new pur_south(),BorderLayout.SOUTH);
		
		setSize(750,500);
		setVisible(true);
	}
	public void addtable() {	//테이블에 정보 추가함수
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String ins = "select m_no, s_price, s_count, s_size, s_amount from shopping";
			con = connection.makeConnection("coffee_an");
			stmt = con.createStatement();
			rs = stmt.executeQuery(ins);
			
			while(rs.next()) {
				Vector<String> addcol = new Vector<String>();
				for(int i=0;i<5;i++) {
					if(i==0) {
						String m_no = rs.getString(i+1);
						String m_name = get_m_name(m_no);	//메뉴 이름 가져오는 함수
						addcol.add(m_name);
					}else {
						addcol.add(rs.getString(i+1));
					}
				}
				
				rowdata.add(addcol);
			}
		}
		catch(SQLException e) {
			System.out.println("장바구니 추가 DB오류: "+e);
		}
	}
	public String get_m_name(String m_no) {	//메뉴 이름 가져오는 함수
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String m_name = null;	//메뉴 이름
		String ins = null;
		
		try {
			con = connection.makeConnection("coffee_an");
			stmt = con.createStatement();
			ins = "select m_name from menu where m_no = '"+m_no+"'";
			rs = stmt.executeQuery(ins);
			
			rs.next();
			m_name = rs.getString(1);		//메뉴 이름 
//			System.out.println(m_name);
		}
		catch(SQLException e) {
			System.out.println(e);
		}
		return m_name;
	}
	public void delete_shop() {		//shopping내용 전체 삭제 함수
		Connection con = null;
		Statement stmt = null;
		PreparedStatement psmt = null;
		int res = 0; 
//		ResultSet rs = null;
		
		try{
			String ins = "delete from shopping";
			con = connection.makeConnection("coffee_an");
			psmt = con.prepareStatement(ins);
			res = psmt.executeUpdate();
			
			if(res==1) {
				System.out.println("삭제 완료");
			}				
		}
		catch(SQLException e) {
			System.out.println("장바구니 추가 DB오류: "+e);
		}
	}
	public String getu_name(String uid,String upw) {		//고객 이름 가져오는 함수
		String u_name = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			String ins = "select u_name from user where u_id = '"+uid+"' and u_pw = '"+upw+"'";
			con = connection.makeConnection("coffee_an");
			stmt = con.createStatement();
			rs = stmt.executeQuery(ins);
			
			rs.next();
			u_name = rs.getString(1);
		}
		catch(SQLException e) {
			System.out.println("고객이름찾기DB오류 : "+e);
		}
		return u_name;
	}
	class pur_north extends JPanel{
		public pur_north(String uid,String upw) {		
			u_name = getu_name(uid,upw);		//고객 이름
			String text = u_name + "님 장바구니";
			JLabel la = new JLabel(text);
			
			add(la);
		}
	}
	class pur_center extends JPanel{
		String colli[] = {"메뉴명","가격","수량","사이즈","금액"};
		public pur_center() {
			for(int i=0;i<colli.length;i++) {
				coldata.add(colli[i]);
			}
			DefaultTableModel tm = new DefaultTableModel(rowdata, coldata);
			jt = new JTable(tm);
			
			JScrollPane scroll = new JScrollPane(jt);
			scroll.setPreferredSize(new Dimension(700, 300));
			add(scroll);
			
			jt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					//선택한 내용 저장할 변수 하나 필요
					chk_sel = jt.getSelectedRow();		//선택한 열 번호
//					System.out.println(chk_sel);
//					get_menu = rowdata.get(chk_sel);	//선택한 열의 내용 다 가져오기
//					System.out.println(get_menu);
				}
			});
			
			addtable();
		}
		
	}
	class pur_south extends JPanel{		//구매 삭제 닫기 버튼
		public pur_south() {
			String btn_li[] = {"구매","삭제","닫기"};
			JButton btn[] = new JButton[btn_li.length];
			for(int i=0;btn_li.length>i;i++) {	//버튼 만들기
				btn[i] = new JButton(btn_li[i]);
				add(btn[i]);
				btn[i].addActionListener(new pur_action());
			}
			
			boolean shop = check_shop();	//장바구니 데이터 유무 확인 함수
			if(shop==false) {	//장바구니 데이터가 없을 경우
				btn[0].setEnabled(false);
				btn[1].setEnabled(false);
			}else {
				for(int i=0;i<btn_li.length;i++) {
					btn[i].setEnabled(true);
				}
			}
		}
		public boolean check_shop() {	//장바구니 데이터 유무 확인 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				String ins = "select * from shopping";
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				
				
				if(rs.next()==false) {
					return false;	//데이터가 비어있을 경우
				}
			}catch(SQLException e) {
				System.out.println("데이터 유무 확인 DB오류: "+e);
			}
			return true;	//데이터가 있을 경우
		}
		public String get_u_no() {		//고객 번호 가져오는 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String u_no = null;
			String ins = "select u_no from user where u_id ='"+uid + "' and u_pw = '"+upw+"'";
			try {
				System.out.println(uid + " "+ upw);
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
//				rs.next();
				
				while(rs.next()) {
					u_no = rs.getString(1);
					System.out.println(u_no);
				}
				
			}
			catch(SQLException e) {
				System.out.println("11"+ e);
			}
			return u_no;
		}
		public String get_m_no(int row) {		//메뉴 번호 가져오는 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String m_no = null;
			String result = (String) jt.getValueAt(row, 0);

			String ins = "select m_no from menu where m_name = '" + result + "'";
			
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				
				rs.next();
				m_no = rs.getString(1);
//				System.out.println(m_no);
			}
			catch(SQLException e) {
				System.out.println("메뉴 번호 가져오기 DB오류 : "+e);
			}
			return m_no;
		}
		public String get_m_type(int row,String m_no) {		//메뉴 종류 가져오는 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String m_type = null;

			String ins = "select m_group from menu where m_no = '" + m_no + "'";
			
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				
				rs.next();
				m_type = rs.getString(1);
//				System.out.println(m_type);
			}
			catch(SQLException e) {
				System.out.println("메뉴 종류 가져오기 DB오류 : "+e);
			}
			return m_type;
		}
		public String getdate() {	//현재 날짜 가져오기
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR); 
			int month = now.get(Calendar.MONTH) +1; 
			int day = now.get(Calendar.DAY_OF_MONTH); 
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			String noww = sdf.format(now.getTime());
			
			return noww;
			
		}
		public String get_order_no() {	//현재까지 저장되어 있는 구매 번호 가져오기(가장 최근)
			Connection con = connection.makeConnection("coffee_an");
			Statement stmt = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			String p_no = null;
			
			try {
				String ins = "select o_no from orderlist order by o_no desc limit 1";
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				
				if(rs.next()==true) {
					p_no = Integer.toString(rs.getInt(1)+1);
				}
			}catch(SQLException e) {
				System.out.println("DB오류 : "+e);
			}
			return p_no;
		}
		public void set_order(String m_no,String type,String date,String o_no, String o_size,String o_price,String o_count,String o_amount,String u_no) {
			Connection con = connection.makeConnection("coffee_an");
			Statement stmt = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			String point = null;			//가져온 회원 포인트
			
			System.out.println(m_no + " " + type + " " + date + " " + o_no + " " + o_size + " " + o_price + " " + o_count + " " + o_amount + " " + u_no);
				//구매이후 구매한 내역을 가지고 orderlist에 구매내역 추가하기
			try {					
				String ins = "insert into orderlist values(?,?,?,?,?,?,?,?,?)";
				psmt = con.prepareStatement(ins);
				psmt.setString(1, o_no);	//구매 번호->현재 데이터베이스 값 가져와서 추가
				psmt.setString(2, date);	//구매 날짜->오늘 날짜
				psmt.setString(3, u_no);	//고객번호	->u_no
				psmt.setString(4, m_no);		//메뉴번호->no
				psmt.setString(5, type);	//구매 종류->
				psmt.setString(6, o_size);	//사이즈 ->sizes
				psmt.setString(7, o_price);	//개당 가격 -> cost
				psmt.setString(8, o_count);	//구매 갯수
				psmt.setString(9, o_amount);	//총 가격
				
				psmt.executeUpdate();
				System.out.println(11);
				//여기부터 시작 왜 insert가 안돼냐 이거
			}
			catch(SQLException eee) {
				System.out.println("DB오류 : "+eee);
			}
		}
		public void pur_shop() {	
			Connection con = connection.makeConnection("coffee_an");
			Statement stmt = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			String point = null;			//가져온 회원 포인트
			String sum = null;
			int totalcost = 0;					//결제할 총 가격
			int rowcount = jt.getRowCount();	//열의(메뉴의) 총 갯수
			for(int i=0;i<rowcount;i++) {
				sum = (String) jt.getValueAt(i, 4);
				totalcost += Integer.parseInt(sum);
			}
			System.out.println(totalcost);
			
			
			try {							//고객 회원 포인트
				String ins = "select u_point from user where u_id = '"+ uid + "' and u_pw = '"+upw+"'";
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				point = rs.getString(1);
			}
			catch(SQLException e) {
				System.out.println("DB오류: "+e);
			}
			
			//결제수단
			int yn = JOptionPane.showConfirmDialog(null, "회원님의 총 포인트 : " + point +"결제하시겠습니까?","결제수단",JOptionPane.YES_NO_OPTION);
			if(yn == JOptionPane.YES_OPTION) {
				//포인트로 결제시 포인트 차감(update)
				int u_point = Integer.parseInt(point) - totalcost;	//결제 후 남은 포인트
				try {
					String ins = "update user set u_point = " + Integer.toString(u_point) +" where u_id = '"+ uid + "' and u_pw = '"+upw+"'";
					psmt = con.prepareStatement(ins);
					psmt.executeUpdate();
				}
				catch(SQLException ee) {
					System.out.println("DB오류 : "+ ee);
				}
//				업데이트 후 다시 joptionpane 결제 완료창 띄우기
				JOptionPane.showMessageDialog(null, "포인트로 결제 완료되었습니다. 남은포인트 : " + u_point);					
			}
			
			
			String u_no = get_u_no();			//고객 번호 가져오는 함수(1번)
			System.out.println(uid + " " +upw);
			for(int i=0;i<rowcount;i++) {
				String m_no = get_m_no(i);			//메뉴 번호 가져오는 함수
				String m_type = get_m_type(i,m_no);	//메뉴 종류 가져오는 함수
				String date = getdate();		//구매시각(현 시간)가져오는 함수
				String o_no = get_order_no();	//데이터베이스에 가장 마지막으로 저장된 구매 번호 가져오기
				String o_size = (String) jt.getValueAt(i, 3);	//구매 사이즈
				String o_price = (String) jt.getValueAt(i, 1);	//개당 가격
				String o_count = (String) jt.getValueAt(i, 2);	//구매 갯수
				String o_amount = (String) jt.getValueAt(i, 4);	//구매 총 가격
				set_order(m_no,m_type,date,o_no,o_size,o_price,o_count,o_amount,u_no);		//구매 후 orderlist 에 추가하기
				System.out.println(12);
			}
			
			
			delete_shop();	//구매 이후 실행(장바구니 전체 삭제)
			
			//등급 측정하기
			String total = checkgroup();
			update_group(total, uid);
			
		}
		public void update_group(String total,String u_id) {	//등급 상승하기
			int bronze = 300000;	//브론즈 등급 조건
			int silver = 500000;	//실버 등급 조건
			int gold = 800000;		//골드 등급 조건
			int u_total = Integer.parseInt(total);	//고객 총 결제 금액
			String u_group = null;		//변경될 등급
			String r_group = null;		//현재 고객 등급
			String ins = "";
			Connection con = null;
			PreparedStatement psmt = null;
			Statement stmt = null;
			int re = 0;
			ResultSet rs = null;
			
			try{		//현재등급과 변경등급이 같은지 확인하기
				ins = "select u_group from user where u_id = '"+u_id+"'";
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				
				rs.next();
				r_group = rs.getString(1);
				
			}catch(SQLException e) {
				System.out.println("등급상승 확인DB오류 : "+e);
			}
			
			
			if(u_total>gold) {
				u_group = "Gold";
			}else if(u_total>silver) {
				u_group = "Silver";
			}else u_group = "Bronze";
			
			if(r_group.equals(u_group)) {	//현재 등급이 변경등급과 다를경우 실행
				
			}else {		
				try {
					ins = "update user set u_grade = '"+u_group+"' where u_id = '"+u_id+"'";
					con = connection.makeConnection("coffee_an");
					psmt = con.prepareStatement(ins);
					re = psmt.executeUpdate();
					
					if(re == 1) {
						JOptionPane.showMessageDialog(null, "축하합니다! 회원님의 등급이 "+u_group + "으로 상승했습니다.");
					}
				}
				catch(SQLException ee) {
					System.out.println("등급상승DB오류 : "+ee);
				}
			}
		}
		public String checkgroup() {		//등급 확인하기
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String total = null;
			
			try {
				con = connection.makeConnection("coffee_an");
				String u_no = get_u_no();
				String ins = "select sum(o_amount) from orderlist where u_no = '" + u_no + "'";
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				total = rs.getString(1);
			}
			catch(SQLException e1) {
				System.out.println("DB오류 : "+e1);
			}
			return total;
		}
		public void pur_delete() {	//장바구니 선택 삭제
			Connection con = null;
			Statement stmt = null;
			int rs;
			int num = chk_sel+1;
			String selection = Integer.toString(num);
			
//			System.out.println(chk_sel);
			if(chk_sel==0) {
				JOptionPane.showMessageDialog(null, "삭제할 주문을 선택하세요");
				return;
			}
			
			try {
				con = connection.makeConnection("coffee_an");
				String ins = "delete from shopping where s_no = " + num;
				stmt = con.createStatement();
				rs = stmt.executeUpdate(ins);
				
				if(rs == 1) {
					System.out.println("삭제 성공");
					update_s_no();
				}else {System.out.println("삭제 실패");}
				DefaultTableModel model = (DefaultTableModel) jt.getModel();	//jtable 초기화하기(새로고침)
				model.setRowCount(0);
				addtable();
				
			}catch(SQLException e) {
				System.out.println("장바구니 선택삭제 DB오류 : "+e);
			}
			
		}
		public void update_s_no() {
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			int re;
			
			String ins = "set @count = 0";
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				re = stmt.executeUpdate(ins);
				
				ins = "update shopping set s_no = @count:=@count+1";
				re = stmt.executeUpdate(ins);
				
			}
			catch(SQLException e) {
				System.out.println("테이블 정렬 DB오류 : "+e);
			}
		}
		class pur_action implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				String in = e.getActionCommand();
				
				switch(in) {
				case "구매":	pur_shop(); break;
				case "삭제":	pur_delete(); break;
				case "닫기": dispose(); new Main(uid,upw);
				}
			}
		}
	}
}
