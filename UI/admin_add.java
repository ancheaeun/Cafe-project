package UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.mysql.cj.util.StringUtils;

public class admin_add extends JFrame{
	JLabel menu_img;	//저장할 이미지
	JTextField tf[] = new JTextField[2];	//메뉴명 , 가격 적는 칸
	JComboBox<String> types;				//분류	
	public admin_add() {
		setTitle("메뉴 추가");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10,5));
		
		add(new east(),BorderLayout.EAST);
		add(new west(),BorderLayout.CENTER);
		add(new south(),BorderLayout.SOUTH);
		
		setSize(400,300);
		setVisible(true);
	}
	class east extends JPanel{
		public east() {
			setLayout(new BorderLayout(10,5));
			String list = "사 진 등 록";
			JButton btn = new JButton(list);
			Border bor = new EtchedBorder(EtchedBorder.RAISED);
				
			menu_img = new JLabel();
			menu_img.setBorder(bor);
			add(menu_img,BorderLayout.CENTER);
			add(btn,BorderLayout.SOUTH);
			
			
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();	
					int ret = chooser.showOpenDialog(null);		
					
					if(ret != JFileChooser.APPROVE_OPTION) {
						JOptionPane.showMessageDialog(null, "사진을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
					}
					
					String filePath = chooser.getSelectedFile().getPath();	//파일경로 가져오기
					
					
					try {
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
	class west extends JPanel{
		public west() {
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
	class south extends JPanel{
		public south() {
			String list[] = {"등록" ,"취소"};
			JButton btn[] = new JButton[list.length];
			for(int i=0;i<list.length;i++) {
				btn[i] = new JButton(list[i]);
				add(btn[i]);
				btn[i].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String btn = e.getActionCommand();
						
						switch(btn) {
						case "등록":{
							save_menu();
						}
						case "취소":
							dispose();
							new admin();
						}
						
					}
				});
			}
		}
	}
	public void save_menu() {	//메뉴 등록하는 함수
		String m_name = tf[0].getText();		//메뉴명 가져오기
		String cost = tf[1].getText();			//메뉴 가격 가져오기
		int num = types.getSelectedIndex();
		String type = types.getItemAt(num);		//분류 가져오기
		
		if(m_name.equals("")||cost.equals("")) {
			JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.", "경고", JOptionPane.WARNING_MESSAGE);
			return;
		}
		boolean res = StringUtils.isStrictlyNumeric(cost);	//숫자인지 아닌지 확인하기
		if(res == false) {
			JOptionPane.showMessageDialog(null, "가격은 숫자로 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
			return;
		}
		//대충 데이터베이스로 중복 확인하기(완전히 똑같은게 아니라 큰 타이틀이 같은지)
//		if(m_name) {
//			
//		}
		
		//메뉴 등록하기
	}
	public static void main(String[]args) {
		new admin_add();
	}
}
