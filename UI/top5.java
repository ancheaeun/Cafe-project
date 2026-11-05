package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DB.connection;

public class top5 extends JFrame{
	JComboBox<String> type;
	Vector<String> top5_list = new Vector<String>();	//메뉴 리스트
	Vector<String> top5_lic = new Vector<String>();		//메뉴 개별 갯수
	t_center tc;
	Container c = getContentPane();
	
	public top5(String uid, String upw) {
		setTitle("인기상품 top5");
		addWindowListener(new close_act(uid,upw));		//창을 끌때 이벤트 발생
		tc = new t_center("음료");
		
		c.add(new t_north(),BorderLayout.NORTH);
		c.add(tc,BorderLayout.CENTER);
		
		setSize(400,500);
		setVisible(true);
	}
	class close_act extends WindowAdapter{		//X버튼을 눌렀을때 꺼지지 않고 STARBOX(Main)이 실행되도록 한다.
		String uid,upw;
		public close_act(String uid,String upw) {
			this.uid = uid;
			this.upw = upw;
		}
		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
			new Main(uid,upw);
		}
	}
	class t_north extends JPanel{		//인기상품 종류 선택 콤보박스
		public t_north() {
			String li[] = {"음료","푸드","상품"};	//콤보박스 리스트
			String title = "인기상품 TOP5";
			
			JLabel la = new JLabel(title);
			setBackground(Color.LIGHT_GRAY);
			type = new JComboBox<String>(li);
			
			add(type);
			add(la);
			type.addActionListener(new sel_type());
		}
		class sel_type implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				//선택한 그룹 확인하기
				JComboBox<String> com = (JComboBox<String>)e.getSource();
				int sum = com.getSelectedIndex();
				String types = com.getItemAt(sum).toString();		

				c.remove(tc);
				System.out.println(111);
				tc = new t_center(types);
				
				c.add(tc,BorderLayout.CENTER);
				c.revalidate();
			}
		}
	}
	class t_center extends JPanel{		//인기상품 표 생성
		String types;
		public t_center(String type) {
			this.types = type;
			System.out.println(types);
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.black);
			g.drawLine(50, 50, 50, 350);
			int leng = 250;		//250이 최대 길이, 메뉴당 막대 그래프
			int starty = 80;	//막대그래프 시작 높이
			
			Color colors[] = {Color.red,Color.orange,Color.yellow,Color.green,Color.blue};

			int percent[] = set_graph(types);		//각 메뉴의 퍼센트 가져오기(100퍼센트 기준)	
			int temp = leng / percent[0];		//1퍼센트당 길이를 측정
			
			for(int i=0;i<colors.length;i++) {
				int lengths = temp * percent[i];	//1퍼센트당 길이중 각 메뉴의 퍼센트로 길이 계산하기
				if(lengths <=250) {
					String top_menu = top5_list.get(i);		//메뉴당 이름과 구매 갯수

					g.setColor(colors[i]);
					g.fillRect(50, starty, lengths, 20);	//막대 그래프
					g.setColor(Color.black);
					g.drawString(top_menu, 50, starty+40);	//메뉴 이름과 구매 갯수
					starty+=55;
				}
			}
		}	
		public int[] set_graph(String type) {		//그래프 그리는 함수
			//선택한 그룹당 전체 카운트 수 가져오기 -> 퍼센트 계산하기
			int allcount = get_allcount(type);
			//선택한 그룹의 인기메뉴 5개 뽑아오기
			Vector<String> top5_menu = get_top5count(type);
			//메뉴별 갯수 가지고 있는 벡터 -> top5_lic
			
			//퍼센트 계산하는 함수(각 메뉴당)
			int percent[] = set_per(allcount,top5_lic);
			
			return percent;
		}
		public int[] set_per(int allc,Vector<String> lic) {				//퍼센트 계산하는 함수
			int percent[] = new int[lic.size()];
			int temp;
			for(int i=0;i<lic.size();i++) {
				temp = Integer.parseInt(lic.get(i));
				percent[i] = temp * 100 / allc;
			}
			return percent;
		}
		public int get_allcount(String type) {				//메뉴 총 갯수 가져오기
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String ins = "select sum(getsu) from (select m_no, count(m_no) as getsu from orderlist where o_group = '" + type + "' group by m_no order by count(m_no) desc limit 5) m";
			int count = 0;
			
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				
				count = rs.getInt(1);
			}catch(SQLException e) {
				System.out.println("전체 갯수 DB오류: "+e);
			}
			return count;
		}
		public Vector<String> get_top5count(String type) {		//top5 메뉴 가져오는 함수 , string배열 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String ins = "select m_no,count(m_no) from orderlist where o_group = '"+type+"' group by m_no order by count(m_no) desc limit 5";
			String m_names[] = new String[5]; 
			String m_no[] = new String[5];
			String temp = null;
			//vector -> top5_list , top5_lic
			
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				int i=0;
				
				if(top5_list.isEmpty()==false) {
					top5_list.removeAllElements();
					top5_lic.removeAllElements();
				}
				
				while(rs.next()) {
					m_names[i] = get_top5_m_name(rs.getString(1));			//메뉴 이름 가져오는 함수
					temp = m_names[i] + " - " + rs.getString(2) + "개";
					top5_list.add(temp);								//메뉴 이름 + 갯수
					top5_lic.add(rs.getString(2));						//메뉴 개별 갯수
					i++;
				}
			}catch(SQLException e) {
				System.out.println("전체 갯수 DB오류: "+e);
			}
			return top5_list;
		}
		public String get_top5_m_name(String m_no) {			//메뉴 이름 가져오는 함수
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			String ins = "select m_name from menu where m_no = '"+m_no+"'";
			String m_name = null;
			
			try {
				con = connection.makeConnection("coffee_an");
				stmt = con.createStatement();
				rs = stmt.executeQuery(ins);
				rs.next();
				
				m_name = rs.getString(1);
			}catch(SQLException e) {
				System.out.println("전체 갯수 DB오류: "+e);
			}
			
			return m_name;
		}
	}
	public static void main(String args[]) {
		new top5("coffee1","1111");
	}
}
