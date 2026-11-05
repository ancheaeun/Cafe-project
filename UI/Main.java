package UI;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import DB.connection;

public class Main extends JFrame{
	JScrollPane scroll;
	String u_id,u_pw;
	main_center M_Center;
	main_east M_East;
	Vector<menuPanel> menu ;
	String cusinfo[] = new String[3];
	Container c;
	public Main(String id, String pw) {
		this.u_id  = id;
		this.u_pw = pw;
		setTitle("STARBOX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		M_Center = new main_center("음료");
		M_East = new main_east();
		scroll = new JScrollPane(M_Center);
		c = getContentPane();
		c.add(new main_north(id,pw),BorderLayout.NORTH);
		c.add(scroll,BorderLayout.CENTER);
		c.add(new main_west(),BorderLayout.WEST);
		c.add(M_East,BorderLayout.EAST);		//보이지 않는 패널(아무것도 없는 창 넣어놨다가 버튼을 누르면 이벤트로 추가,sub_east)
		
		setSize(750,600);
		setVisible(true);
	}
	public void delete_shop() {			//장바구니 내용 전체 삭제 함수
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
	class main_north extends JPanel{			//회원 정보 가져오기
		public main_north(String id,String pw) {	
			setLayout(new BorderLayout());
			add(new top_nor(id,pw),BorderLayout.NORTH);
			add(new top_sou(id,pw),BorderLayout.SOUTH);
			
			
		}
		class top_nor extends JPanel{
			public top_nor(String id,String pw) {
				String li []= {"회원명: "," / 회원등급: "," / 총 누적 포인트: "};
				String info = "";
				JLabel la;
				
				Connection con = connection.makeConnection("coffee_an");
				PreparedStatement psmt = null;
				Statement stmt = null;
				
				try {
					stmt = con.createStatement();
					psmt = con.prepareStatement("select u_name,u_grade,u_point from user where u_id = '" + id + "' and u_pw = '" + pw + "'");
					ResultSet rs = psmt.executeQuery();
					
					while(rs.next()) {
						for(int i=0;i<3;i++) {
							cusinfo[i] = new String(rs.getString(i+1));
							info += li[i] + cusinfo[i];
						}
					}
					la = new JLabel(info);
					add(la);
				}
				catch (SQLException e) {
					System.out.println("DB오류");
				}
			}
		}
		class top_sou extends JPanel{
			public top_sou(String id,String pw) {
				setLayout(new FlowLayout());
				String li[] = {"구매내역","장바구니","인기상품 Top5","Logout"};
				JButton btn[] = new JButton[li.length];
				
				for(int i=0;i<li.length;i++) {
					btn[i] = new JButton(li[i]);
					add(btn[i]);
					btn[i].addActionListener(new btn_act(id,pw));
				}
			}
			class btn_act implements ActionListener{
				String uid,upw;
				public btn_act(String id, String pw) {		//구매내역 확인을 위한 아이디 비번 가져오기
					this.uid = id;
					this.upw = pw;
				}
				public void actionPerformed(ActionEvent e) {
					String b = e.getActionCommand();
					switch(b) {
					case "구매내역": dispose(); new history(cusinfo[0],uid,upw); break;
					case "장바구니": dispose(); new purchase(uid,upw); break;
					case "인기상품 Top5": dispose(); new top5(uid,upw); break;
					case "Logout": delete_shop(); dispose(); new login(); break;
					}
				};
			}
		}	
	}
	class main_center extends JPanel{	//음료
		public main_center(String type) {
			makemenu(type);
			int size = 0;
			if(menu.size()%3==0) {		//메뉴 갯수 확인 후 행열 정하기
				size = menu.size()/3;
			}else {
				size = menu.size()/3+1;
			}
			setLayout(new GridLayout(size,3,5,5));
			for(int i=0;i<menu.size();i++) {
				add(menu.get(i));
			}
		}	
		
	}
	public void makemenu(String type) {
		menu = new Vector<menuPanel>(); 
		Vector<String> m_name = new Vector<String>();
		String ins = "select m_name from menu where m_group = '"+type+"'";
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = connection.makeConnection("coffee_an");
			stmt = con.createStatement();
			rs = stmt.executeQuery(ins);

			while(rs.next()) {
				m_name.add(rs.getString(1));
				menu.add(new menuPanel(rs.getString(1)));
			}
		}
		catch (SQLException e) {
			System.out.println("DB오류: "+e);
		}	
	}
	class menuPanel extends Panel{			//메뉴 1개 만드는 클래스 (액션까지 다 )
		JLabel la;	//메뉴 사진
		JLabel na;	//메뉴 이름
		public menuPanel(String m_name) {
			setLayout(new BorderLayout());
			
			ImageIcon icon = new ImageIcon("DataFiles\\이미지\\"+m_name+".jpg");
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			ImageIcon chanIcon = new ImageIcon(changeImg);
			
			na = new JLabel(m_name);
			la = new JLabel(chanIcon);
			la.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			
			na.setHorizontalAlignment(getWidth());	//중앙 정렬
			add(na,BorderLayout.SOUTH);
			add(la,BorderLayout.CENTER);
			
			la.addMouseListener(new menu_click());
		}
		class menu_click extends MouseAdapter{
			@Override
			public void mousePressed(MouseEvent e) {		
				M_East.removeAll();
				sub_east S_East = new sub_east(na);		//결제창 만드는 패널
				M_East.add(S_East);
			}
		}
	}	
	class sub_east extends JPanel{	//결제창 만드는 패널
		String uid, upw;
		public sub_east(String id, String pw) {		//고객 아이디, 비번 가져오기
			this.uid = id;
			this.upw = pw;
		}
		JLabel name;
		String n_menu;
		public sub_east(JLabel name) {	
			this.name = name;
			setLayout(new BorderLayout());
			add(new sub_south(name),BorderLayout.SOUTH);
			add(new sub_center(),BorderLayout.CENTER);
			Main.this.setSize(1200, 600);
			c.revalidate();
		}
		class sub_south extends JPanel{
			public sub_south(JLabel name) {
				String li[] = {"장바구니 담기","구매하기"};
				JButton btn[] = new JButton[li.length];
				
				for(int i=0;i<li.length;i++) {
					btn[i] = new JButton(li[i]);
					add(btn[i]);
					btn[i].addActionListener(new btn_act(uid,upw));
				}
			}
		}
		JComboBox<String> getsu;
		JComboBox<String> size;
		String cost;
		String li[] = {"주방메뉴: ","가격: ","수량: ","사이즈: ","총 금액: "};
		String sizeli[] = {"M","L"};
		String getsuli[] = new String[10];
		JTextField tf[] = new JTextField[3];
		class sub_center extends JPanel{
			public sub_center() {
				add(new scen_left(),BorderLayout.WEST);
				add(new scen_right(),BorderLayout.EAST);
			}
			class scen_left extends JPanel{			//사진 가져오기
				public scen_left() {	
					n_menu = name.getText();
					ImageIcon icon = new ImageIcon("DataFiles\\이미지\\"+n_menu+".jpg");
					Image img = icon.getImage();
					Image changeImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
					ImageIcon chanIcon = new ImageIcon(changeImg);
					JLabel menu = new JLabel(chanIcon);
					add(menu);
				}
			}
			class scen_right extends JPanel{	
				public scen_right() {
					n_menu = name.getText();	//메뉴 이름
					setLayout(new GridLayout(5, 2, 5, 5));
					
					
					for(int i=0;i<getsuli.length;i++) {
						getsuli[i] = new String(Integer.toString(i+1));
					}
					getsu = new JComboBox<String>(getsuli);
					size = new JComboBox<String>(sizeli);
					
					JLabel la[] = new JLabel[li.length];
					for(int i=0;i<li.length;i++) {
						la[i] = new JLabel(li[i]);
					}
					for(int i=0;i<tf.length;i++) {
						tf[i] = new JTextField(10);
						tf[i].setEditable(false);
					}
					for(int i=0,j=0,k=0;i<10;i++) {
						if(i%2==0) {
							add(la[j]);
							j++;
						}
						else {
							if(i==5) add(getsu);
							else if(i==7) add(size);
							else {
								add(tf[k]);
								k++;
							}
						}
					}
					getsu.addActionListener(new cost_act());
					size.addActionListener(new cost_act());
					
					cost = menucost(n_menu);	//메뉴 가격 가져오기	
					tf[0].setText(n_menu); 		//메뉴 이름
					tf[1].setText(cost);		//메뉴 가격(개당 가격)
					int totalcost = getcost(getsu, size, cost);						//계산 총 가격 가져오기
					tf[2].setText(Integer.toString(totalcost));
					
					
				}
				class cost_act implements ActionListener{						//jcombobox 선택시 이벤트(선택시 마다 가격 가져오기)
					@Override
					public void actionPerformed(ActionEvent e) {
						int totalcost = getcost(getsu, size, cost);				//계산 총 가격 가져오기
						tf[2].setText(Integer.toString(totalcost));
					}
				}
			}
		}
		public int getcost(JComboBox getsu, JComboBox size ,String cost) {		//계산 총 가격 가져오기
			int totalcost = 0;
			int sum = getsu.getSelectedIndex();
			String items = getsu.getItemAt(sum).toString();			//선택 갯수 가져오기
			
			int si = size.getSelectedIndex();
			String sizes = size.getItemAt(si).toString();			//선택 사이즈 가져오기
			
			totalcost = Integer.parseInt(items) * Integer.parseInt(cost);			
			if(sizes.equals("L")) totalcost += 1000; 				//사이즈에 따른 계산 총 가격 (1)	
			
			switch(cusinfo[1]) {									//회원 등급에 따른 총 가격(2)
			case "Bronze": totalcost = totalcost - (int) (totalcost * (0.03));  break;
			case "Silver": totalcost = totalcost - (int) (totalcost * (0.05));	break;
			case "Gold": totalcost = totalcost - (int) (totalcost * (0.1));		break;
			default : break;
			}
			return totalcost;
		}
		public String menucost(String n_menu) {		//메뉴 가격 가져오기
			Connection con = connection.makeConnection("coffee_an");
			PreparedStatement psmt = null;
			Statement stmt = null;
			ResultSet rs = null;
			String cost = null;
			
			try {
				String ins = "Select m_price from menu where m_name = '"+n_menu+"'";
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				cost = rs.getString(1);
			}
			catch(SQLException e) {
				System.out.println("DB오류: "+e);
			}
			return cost;
		}
		
		class btn_act implements ActionListener{		//장바구니 담기 , 구매하기 버튼 액션
			public btn_act(String id, String pw) {				//이름 가져오기
				
			}
			public void actionPerformed(ActionEvent e) {
				String sel = e.getActionCommand();
				String shopnum = Integer.toString(Integer.parseInt(gets_no())+1);	//장바구니 입력번호 가져오기
//				System.out.println(shopnum);
				int totalcost = 0;
				int sum = 0;
				String items = null, sizes = null, no = null;
				
				sum = getsu.getSelectedIndex();
				items = getsu.getItemAt(sum).toString();			//선택 갯수 가져오기
				
				int si = size.getSelectedIndex();
				sizes = size.getItemAt(si).toString();			//선택 사이즈 가져오기
				totalcost = getcost(getsu, size, cost);
				no = get_m_no(tf[0].getText());
				switch(sel) {
					case "장바구니 담기": {
						Connection con = connection.makeConnection("coffee_an");
						Statement stmt = null;
						PreparedStatement psmt = null;
						ResultSet rs = null;

						try {
							psmt = con.prepareStatement("insert into shopping values(?,?,?,?,?,?)");
							psmt.setString(1, shopnum);							//1부터 시작 장바구니 갯수에따라 숫자 계속 증가
							psmt.setString(2, no);								//메뉴 이름으로 찾기
							psmt.setString(3, cost);							//메뉴 가격 가져오기
							psmt.setString(4, items);							//jcombobox(getsu)에서 가져오기
							psmt.setString(5, sizes);							//jcombobox(size)에서 가져오기
							psmt.setString(6, Integer.toString(totalcost));				//totalcost 가져오기
							
							int payrs = psmt.executeUpdate();
							if(payrs==1) {
								JOptionPane.showMessageDialog(null, "장바구니에 담았습니다.");
							}
							else {
								JOptionPane.showMessageDialog(null, "장바구니 담기 실패");
							}
						}
						catch(SQLException ee) {
							System.out.println("DB오류 : "+ee);
						}
						break;
					}
					case "구매하기": {
						totalcost = getcost(getsu, size, cost);
//						System.out.println(no + " " + items + " " + sizes);
						purchase(totalcost,no,items,sizes);		//3개 밖으로 꺼내오기
					}
				}
			};
			
			public void purchase(int totalcost, String no, String items , String sizes) {			//구매하기 함수
				//총 포인트 가져오기(u_point)
				Connection con = connection.makeConnection("coffee_an");
				Statement stmt = null;
				PreparedStatement psmt = null;
				ResultSet rs = null;
				String point = null;			//가져온 회원 포인트
				
				try {
					String ins = "select u_point from user where u_id = '"+ u_id + "' and u_pw = '"+u_pw+"'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(ins);
					rs.next();
					point = rs.getString(1);
//					System.out.println(point);
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
						String ins = "update user set u_point = " + Integer.toString(u_point) +" where u_id = '"+ u_id + "' and u_pw = '"+u_pw+"'";
						psmt = con.prepareStatement(ins);
						psmt.executeUpdate();
					}
					catch(SQLException ee) {
						System.out.println("DB오류 : "+ ee);
					}
					//업데이트 후 다시 joptionpane 결제 완료창 띄우기
					JOptionPane.showMessageDialog(null, "포인트로 결제 완료되었습니다. 남은포인트 : " + u_point);					
					
					//구매이후 구매한 내역을 가지고 orderlist에 구매내역 추가하기
					try {
						String date = getdate();	//현재 날짜 가져오기
						String p_no = getp_no();	//회원 번호 가져오기
						String set_getsu = getsu.getSelectedItem().toString();
						String type = gettype(no);
						String u_no = getu_no(u_id);
						
//						System.out.println(p_no + " " + date + " " + u_no + " " + no + " " +
//								type + " " + sizes + " " + cost + " " + set_getsu + " " + Integer.toString(totalcost));
						
						String ins = "insert into orderlist values(?,?,?,?,?,?,?,?,?)";
						psmt = con.prepareStatement(ins);
						psmt.setString(1, p_no);	//구매 번호->현재 데이터베이스 값 가져와서 추가
						psmt.setString(2, date);	//구매 날짜->오늘 날짜
						psmt.setString(3, u_no);	//고객번호	->u_no
						psmt.setString(4, no);		//메뉴번호->no
						psmt.setString(5, type);	//구매 종류->
						psmt.setString(6, sizes);	//사이즈 ->sizes
						psmt.setString(7, cost);	//개당 가격 -> cost
						psmt.setString(8, set_getsu);	//구매 갯수
						psmt.setString(9, Integer.toString(totalcost));	//총 가격
						
						psmt.executeUpdate();
					}
					catch(SQLException eee) {
						System.out.println("DB오류 : "+eee);
					}
					
				}
				//구매내역에서 구매한 총 내역을 가지고 그 값이 일정 가격을 넘으면 등급 상승
				String total = checkgroup();	//등급 확인하기
				
				//구매내역 총 결제 금액이 일정 금액을 넘어가면 등급 상승 , 이미 상승된 상태라면 상승하면 안됌
				update_group(total, u_id);
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
					String u_no = getu_no(u_id);
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
			public String getdate() {	//현재 날짜 가져오기
				Calendar now = Calendar.getInstance();
				int year = now.get(Calendar.YEAR); 
				int month = now.get(Calendar.MONTH) +1; 
				int day = now.get(Calendar.DAY_OF_MONTH); 
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				String noww = sdf.format(now.getTime());
				
				return noww;
				
			}
			public String getp_no() {	//현재까지 저장되어 있는 구매 번호 가져오기(가장 최근)
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
			public String gettype(String no) {
				Connection con = connection.makeConnection("coffee_an");
				Statement stmt = null;
				PreparedStatement psmt = null;
				ResultSet rs = null;
				String type = null;
				
				try {
					String ins = "select m_group from menu where m_no = "+ no;
					stmt = con.createStatement();
					stmt = con.createStatement();
					rs = stmt.executeQuery(ins);
					
					if(rs.next()==true) {
						type = rs.getString(1);
					}
				}
				catch(SQLException e) {
					System.out.println("DB 오류: "+e);
				}
				return type;
			}
			public String getu_no(String u_id) {
				Connection con = connection.makeConnection("coffee_an");
				Statement stmt = null;
				PreparedStatement psmt = null;
				ResultSet rs = null;
				String u_no = null;
				
				try {
					String ins = "select u_no from user where u_id = '"+ u_id+"'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(ins);
					
					if(rs.next()==true) {
						u_no = rs.getString(1);
					}
				}
				catch(SQLException e) {
					System.out.println("DB 오류: "+e);
				}
				return u_no;
			}
		}
	}
	public String get_m_no(String m_name) {				//회원 번호 가져오기
		Connection con = connection.makeConnection("coffee_an");
		PreparedStatement psmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String no = null;
		
		try {
			String ins = "Select m_no from menu where m_name = '"+m_name+"'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(ins);
			rs.next();
			no = rs.getString(1);
		}
		catch(SQLException e) {
			System.out.println("DB오류: "+e);
		}
		return no;
	}
	public String gets_no() {			//장바구니 담기(shopping) mysql 장바구니 입력 번호 가져오기
		Connection con = connection.makeConnection("coffee_an");
		Statement stmt = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String p_no = null;
	
		try {
			String ins = "select s_no from shopping order by s_no desc limit 1";
			stmt = con.createStatement();
			rs = stmt.executeQuery(ins);
			
			if(rs.next()==true) {
				p_no = rs.getString(1);
			}else p_no = "0";
		}
		catch(SQLException e) {
			System.out.println("DB오류 : "+e);
		}
		return p_no;
	}
	
	class main_west extends JPanel{
		public main_west() {
			setLayout(new BorderLayout());
			add(new btn_nor(),BorderLayout.NORTH);
		}
		class btn_nor extends JPanel{
			public btn_nor() {
				setLayout(new GridLayout(3,1));
				String li[] = {"음료","푸드","상품"};
				JButton btn[] = new JButton[li.length];
				
				for(int i=0;i<li.length;i++) {
					btn[i] = new JButton(li[i]);
					add(btn[i]);
					btn[i].addActionListener(new w_btn_act());
				}
			}
		}
		class w_btn_act implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String btn = e.getActionCommand();
				Connection con = null;
				Statement stmt = null;
				PreparedStatement psmt = null;
				ResultSet rs = null;
				String ins = "select * from menu where m_group = '";
				
				switch(btn) {
				case "음료": re("음료"); break;
				case "푸드": re("푸드"); break;
				case "상품": re("상품"); break;
				}
			};
			
		}
		public void re(String type) {
			c.remove(scroll);			//컴포넌트에 있는 스크롤팬 지우기
			M_Center = new main_center(type);	//메인 화면에 다시 메뉴 패널 얹기
			scroll = new JScrollPane(M_Center); //스크롤에 메인메뉴 올리기
			c.add(scroll,BorderLayout.CENTER);	//컴포넌트에 다시 스크롤팬 (메인메뉴 넣은) 올리기
			c.revalidate();						//컴포넌트 새로고침
			
			System.out.println(menu.size());
		}
	}
	
	class main_east extends JPanel{
		public main_east() {
			
		}
		
	}
	
	public static void main(String[]args) {
		new Main("coffee1","1111");
	}
}
