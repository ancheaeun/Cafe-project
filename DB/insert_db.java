package DB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class insert_db {
	public insert_db() {
		Connection con = null;
		PreparedStatement psmt = null;
		Scanner s;
		StringTokenizer list = null;
		
		String root = "C:\\Users\\SJCOM\\Desktop\\cafe\\cafeproject\\DataFiles\\";
		String txt[] = {"menu","user","orderlist"};	 //가져올 DB내용 텍스트 제목
		String ins[] = {"insert into menu values(?,?,?,?)",	//DB에 넣을 내용(한줄씩 가져오기)
				"insert into user values(?,?,?,?,?,?,?)",
				"insert into orderlist values(?,?,?,?,?,?,?,?,?)"};
		
		con = connection.makeConnection("coffee_an");
		
		try {
			for(int i=0;i<ins.length;i++) {
				psmt = con.prepareStatement(ins[i]);
				s = new Scanner(new FileInputStream(root+txt[i]+".txt"));	//s=읽어들인 텍스트 전체
				s.nextLine();	//첫 줄은 목록, 읽을 필요 없음(한줄만 읽는 함수) 저장x
				
				while(s.hasNext()) {	//텍스트의 내용이 있을시(한줄씩)	
					String nlist = s.nextLine();			//한 줄을 nlist에 저장
					list = new StringTokenizer(nlist,"\t");	//한 줄을 list에 저장(list는 StringTokenizer)
					int j = 1, k = 0;
					
					while(list.hasMoreTokens()) {			//list의 내용이(토큰이)남아있으면 반복
						String count = list.nextToken();	//하나씩 db에 저장
						//orderlist.txt파일의 6번째 목록(사이즈) 부분에 사이즈가 안들어가면 공백을 추가
						//txt파일 순서가 menu -> user -> orderlist 순이기 때문에 3번째 txt파일을 읽을때(i가 2일때)실행
						if(i==2 && j==6 && !count.equals("M") && !count.equals("L")) {	
							k = j + 1;
							psmt.setString(j, "");			//사이즈가 들어가있지 않으면 공백을 추가
							psmt.setString(k, count);		//count의 값은 6번째 값을 가지고 있기 때문에 6번째 공백을 추가하고 7번째에 count의 값을 넣어준다.
							j = k;
						}else {
							psmt.setString(j, count);
						}
						j++;
					}
					int re = psmt.executeUpdate();
					if(re == 1) {
					}else System.out.println("삽입실패");
				}
				System.out.println("삽입성공");
			}
		}
		catch(SQLException e) {
			System.out.println(e);
			System.out.println("SQL 오류");
		}
		catch(FileNotFoundException e) {
			System.out.println("파일을 찾지 못했습니다.");
		}
	}
	public static void main(String []args) {
		new create_db();
		new insert_db();
	}
}