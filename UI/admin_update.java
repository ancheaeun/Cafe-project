package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import DB.connection;
import UI.admin_add.east;
import UI.admin_add.south;
import UI.admin_add.west;

public class admin_update extends JFrame{
	JTable jt;
	Vector<Vector<String>> rowdata = new Vector<Vector<String>>();	//행
	Vector<String> coldata = new Vector<String>();	//열
	Vector<String> temp;							//선택된 테이블 메뉴 저장하는 벡터
	JTextField tf[] = new JTextField[2];	//메뉴명 , 가격 적는 칸(east)
	JComboBox<String> types;				//분류			 (east)
	JLabel menu_img;	//저장할 이미지							 (east)
	public admin_update() {
		setTitle("메뉴 수정");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(1, 2));
		
		add(new west());
		add(new east());
		
		setSize(1000,400);
		setVisible(true);
	}
	class west extends JPanel{
		String colli[] = {"분류","메뉴명","가격"};
		public west() {
			setLayout(new BorderLayout());
			for(int i=0;i<colli.length;i++) {		//메뉴 항목 저장
				coldata.add(colli[i]);
			}
			get_m_info();							//메뉴 정보 저장	-> 이후 테이블에 저장 후 올리기	
			DefaultTableModel tm = new DefaultTableModel(rowdata, coldata);
			jt = new JTable(tm);
			
			JScrollPane scroll = new JScrollPane(jt);

			add(new w_north(),BorderLayout.NORTH);
			add(scroll,BorderLayout.CENTER);
			scroll.setPreferredSize(new Dimension(400, 200));
			
				
			jt.addMouseListener(new jt_mouse());		//테이블 셀 선택시 그에 해당하는 메뉴 정보 가져오기
		}
		class jt_mouse extends MouseAdapter{
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = jt.getSelectedRow();
				temp = new Vector<String>();
				temp.addAll(rowdata.get(row));		//->이 내용을 옆 패널에 추가
				
				int index = 0;
				if(temp.get(0).equals("음료")) {
					index = 0;
				}else if(temp.get(0).equals("푸드")){
					index = 1;
				}else if(temp.get(0).equals("상품")) {
					index = 2;
				}
				
				types.setSelectedIndex(index);		//종류 바꾸기
				tf[0].setText(temp.get(1));			//메뉴
				tf[1].setText(temp.get(2));			//가격	
				
				String filePath = "C:\\Users\\SJCOM\\Desktop\\cafe\\cafeproject\\DataFiles\\이미지\\"+temp.get(1)+".jpg";	//파일경로 가져오기
				try {
					BufferedImage image;

					File imageFile = new File(filePath);	//파일경로로 부터 이미지 가져오기
					image = ImageIO.read(imageFile);
					
					menu_img.setIcon(new ImageIcon(image));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		class w_north extends JPanel{
			JTextField tff;
			public w_north() {
				String jcombo_li[] = {"전체", "음료", "푸드", "상품"};
				JComboBox<String> list = new JComboBox<String>(jcombo_li);
				JButton find = new JButton("찾기");
				tff = new JTextField(20);
				JLabel la = new JLabel("검색");
				
				add(la);
				add(list);
				add(tff);
				add(find);
				
				int num = list.getSelectedIndex();
				String type = list.getItemAt(num);
				find.addActionListener(new find_act(type));
			}
			class find_act implements ActionListener{
				String finding,type;
				public find_act(String type) {
					this.type = type;
				}
				public void actionPerformed(java.awt.event.ActionEvent e) {
					finding = tff.getText();
					Connection con = null;
					Statement stmt = null;
					ResultSet rs = null;
					String ins = "select m_group, m_name, m_price from menu where m_group = '" + type + "' and m_name like '%" + finding + "%'";	//종류 선택시
					String ins1 = "select m_group, m_name, m_price from menu where m_name like '%" + finding + "%'";								//전체 선택시
					String selins=null;
					
					try {
						rowdata.removeAllElements();		//테이블에 있는 내용 전체 삭제
						DefaultTableModel model = (DefaultTableModel) jt.getModel();	//jtable 초기화하기(새로고침)
						model.setRowCount(0);
						
						if(type.equals("전체")) {
							selins = ins1;
						}else {
							selins = ins;
						}
						
						con = connection.makeConnection("coffee_an");
						stmt = con.createStatement();
						rs = stmt.executeQuery(selins);
						
						while(rs.next()) {
							Vector<String> finds = new Vector<String>();
							for(int i=0;i<3;i++) {
								finds.add(rs.getString(i+1));
							}
							rowdata.add(finds);
						}
						jt.updateUI();
						
					}
					catch(SQLException e1) {
						System.out.println("검색 DB오류: "+e1);
					}
				};
			}
		}
		public void get_m_info() {		//메뉴 정보 가져오기
			Connection con = connection.makeConnection("coffee_an");
			PreparedStatement psmt = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			String u_no;
			
			try {
				String ins = "select m_group, m_name, m_price from menu";	//메뉴 정보 가져오기
				stmt = con.createStatement();	
				rs = stmt.executeQuery(ins);
				
				while(rs.next()) {
					Vector<String> get_m_infos = new Vector<String>();
					for(int i=0;i<3;i++) {
						get_m_infos.add(rs.getString(i+1));
					}
					rowdata.add(get_m_infos);
				}
			}
			catch(SQLException e) {
				System.out.println("DB오류: "+e);
			}
		}
	}
	class east extends JPanel{
		public east() {
			add(new e_center(),BorderLayout.CENTER);
			add(new e_south(),BorderLayout.SOUTH);

		}
		class e_center extends JPanel{
			public e_center() {
				setLayout(new GridLayout(1, 2));
				add(new e_west());
				add(new e_east());
			}
		}
		class e_east extends  JPanel{
			public e_east() {
				setLayout(new BorderLayout(10,5));
				String list = "사 진 등 록";
				JButton btn = new JButton(list);
				Border bor = new EtchedBorder(EtchedBorder.RAISED);
					
				menu_img = new JLabel();
				menu_img.setBorder(bor);
				add(menu_img,BorderLayout.CENTER);
				add(btn,BorderLayout.SOUTH);
				menu_img.setPreferredSize(new Dimension(200, 200));
				
				btn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser chooser = new JFileChooser();	
						int ret = chooser.showOpenDialog(null);		
						
						if(ret != JFileChooser.APPROVE_OPTION) {
							JOptionPane.showMessageDialog(null, "사진을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
						}
						
						try {
							String filePath = chooser.getSelectedFile().getPath();	//파일경로 가져오기
							BufferedImage image;
							File imageFile = new File(filePath);	//파일경로로 부터 이미지 가져오기
							image = ImageIO.read(imageFile);
							
							menu_img.setIcon(new ImageIcon(image));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		}
		class e_west extends JPanel{
			public e_west() {	
				setLayout(new GridLayout(3,2,20,10));
				String list[] = {"분류","메뉴명","가격"}; 
				String type[] = {"음료","푸드","상품"};
				types = new JComboBox<String>(type);
				JLabel la[] = new JLabel[list.length];
				for(int i=0;i<list.length;i++) {
					la[i] = new JLabel(list[i]);
				}
				for(int i=0;i<2;i++) {
					tf[i] = new JTextField(10);
				}
				
				for(int i=0,j=0,k=0;i<6;i++) {
					if(i==1) {
						add(types);
					}
					if(i%2==0) {
						add(la[k]);
						k++;
					}
					else if(i==3||i==5){
						add(tf[j]);
						j++;
					}
				}
			}
		}
		class e_south extends JPanel{
			public e_south() {
				String list[] = {"삭제","수정" ,"취소"};
				JButton btn[] = new JButton[list.length];
				for(int i=0;i<list.length;i++) {
					btn[i] = new JButton(list[i]);
					add(btn[i]);
					btn[i].addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String btn = e.getActionCommand();
							
							switch(btn) {
							case "삭제":{
								if(tf[0].getText().equals("")||tf[1].getText().equals("")) {
									JOptionPane.showMessageDialog(null, "삭제할 메뉴를 선택해주세요!");
								}else {
									delete_m();		//메뉴 삭제
									set_new_jt();	//jtable 재 생성
									JOptionPane.showMessageDialog(null, "삭제되었습니다.");
								}
								break;
							}
							case "수정":{
								if(tf[0].getText().equals("")||tf[1].getText().equals("")) {
									JOptionPane.showMessageDialog(null, "수정할 메뉴를 선택해주세요!");
								}else {
									update_m();		//메뉴 업데이트
									set_new_jt();	//jtable 재 생성
								}
								break;
							}
							case "취소":
								dispose();
								new admin();
								break;
							}
							
						}
					});
				}
			}
			public boolean isNumeric(String string) {			//숫자인지 아닌지 확인하는 함수
			    int intValue;

			    System.out.println(String.format("Parsing string: \"%s\"", string));

			    if(string == null || string.equals("")) {
			        System.out.println("String cannot be parsed, it is null or empty.");
			        return false;
			    }

			    try {
			        intValue = Integer.parseInt(string);
			        return true;
			    } catch (NumberFormatException e) {
			        System.out.println("Input String cannot be parsed to Integer.");
			    }
			    return false;
			}
			public void update_m() {	//메뉴 수정
				Connection con = null;
				Statement stmt = null;
				int rs;				
				int num = types.getSelectedIndex();
				String g_type = types.getItemAt(num);
				
				if(tf[0].getText().equals("")||tf[1].getText().equals("")) {
					JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.");
				}else if(isNumeric(tf[1].getText())==false) {
					JOptionPane.showMessageDialog(null, "가격을 다시 입력해주세요.");
					tf[1].setText("");
				}else if(check_dup() == false) {	//메뉴이름이 중복인지 아닌지 확인하는 함수
					JOptionPane.showMessageDialog(null, "이미 존재하는 메뉴명입니다.");
					tf[0].setText("");
				}else {
					try {
						con = connection.makeConnection("coffee_an");
						
						System.out.println(g_type);
						System.out.println(tf[0].getText());
						System.out.println(tf[1].getText());
						String ins = "update menu set m_group = '"+ g_type +"', m_name = '"+ tf[0].getText()+"',m_price = "+tf[1].getText()
								+ " where m_name = '"+temp.get(1)+"'";
						stmt = con.createStatement();
						rs = stmt.executeUpdate(ins);
						
						if(rs == 1) {
							System.out.println("수정 성공");
						}else {System.out.println("수정 실패");}
						DefaultTableModel model = (DefaultTableModel) jt.getModel();	//jtable 초기화하기(새로고침)
						model.setRowCount(0);
						
						jt.updateUI();
					}catch(SQLException e) {
						System.out.println("메뉴 수정 DB오류 : "+e);
					}
				}
				
				
			}
			public boolean check_dup() {	//메뉴이름이 중복인지 아닌지 확인하는 함수
				Connection con = null;
				Statement stmt = null;
				ResultSet rs;
				String dup_m;
				
				try {
					con = connection.makeConnection("coffee_an");
					String ins = "select m_name from menu where m_name like '"+ tf[0].getText()+"'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(ins);
					
					rs.next();
					dup_m = rs.getString(1);
				}catch(SQLException e) {
					return true;
				}
				return false;
			}
			public void delete_m() {	//메뉴 삭제
				Connection con = null;
				Statement stmt = null;
				int rs;				
				
				try {
					con = connection.makeConnection("coffee_an");
					String ins = "delete from menu where m_group = '"+ temp.get(0)+"' and m_name = '"+ temp.get(1)+"' and m_price = '"+temp.get(2)+"'";
					stmt = con.createStatement();
					rs = stmt.executeUpdate(ins);
					
					if(rs == 1) {
						System.out.println("삭제 성공");
					}else {System.out.println("삭제 실패");}
					DefaultTableModel model = (DefaultTableModel) jt.getModel();	//jtable 초기화하기(새로고침)
					model.setRowCount(0);
					
					jt.updateUI();
				}catch(SQLException e) {
					System.out.println("장바구니 선택삭제 DB오류 : "+e);
				}
				
			}
			public void set_new_jt() {
				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;
				String ins = "select m_group, m_name, m_price from menu";

				try {
					rowdata.removeAllElements();		//테이블에 있는 내용 전체 삭제
					DefaultTableModel model = (DefaultTableModel) jt.getModel();	//jtable 초기화하기(새로고침)
					model.setRowCount(0);
					
					con = connection.makeConnection("coffee_an");
					stmt = con.createStatement();
					rs = stmt.executeQuery(ins);
					
					while(rs.next()) {
						Vector<String> finds = new Vector<String>();
						for(int i=0;i<3;i++) {
							finds.add(rs.getString(i+1));
						}
						rowdata.add(finds);
					}
					jt.updateUI();
					
				}
				catch(SQLException e1) {
					System.out.println("검색 DB오류: "+e1);
				}
			}
		}
	}
	public static void main(String[]args) {
		new admin_update();
	}
}
