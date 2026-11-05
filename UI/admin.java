package UI;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class admin extends JFrame{
	public admin() {
		setTitle("관리자 메뉴");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(3,1));
		JButton btn[] = new JButton[3];
		String list[] = {"메뉴 등록","메뉴 관리","로그아웃"};
		
		for(int i=0;i<3;i++) {
			btn[i] = new JButton(list[i]);
			add(btn[i]);
			btn[i].addActionListener(new btn_act());
		}
		
		setSize(300,200);
		setVisible(true);
	}
	class btn_act implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String btn = e.getActionCommand();
			
			switch(btn) {
			case "메뉴 등록":dispose(); new admin_add(); break;
			case "메뉴 관리":dispose(); new admin_update(); break;
			case "로그아웃": dispose(); new login(); break;
			}
		}
	}
	public static void main(String args[]) {
		new admin();
	}
}
