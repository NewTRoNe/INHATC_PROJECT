package junggoNara;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import junggoNara.JTextFieldLimit;
import junggoNara.Table;
import junggoNara.MemberDTO;
//192.168.123.179
public class Login1 extends JFrame implements ActionListener{

	private JTextField jt;
	private JPasswordField jp;
	private JButton jb1;
	private JButton jb2;
	private int inputsizex = 140;
	private int inputsizey = 125;
//	private boolean bLoginCheck;
	JScrollPane scrollPane;
    ImageIcon icon;
    JPanel background;
    
    private Image image;
	int imagewidth;
	int imageheight;
	double ratio;
    int w;
    int h;
    static String login_id;
    
	public Login1(String title) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700,700);
		setLocationRelativeTo(null);
	

		icon = new ImageIcon("./img/logintheme.png");
		background = new JPanel() {
			public void paintComponent(Graphics g) {
                
				g.drawImage(icon.getImage(), -100, 0, null);
               
				setOpaque(false); 
				super.paintComponent(g);
			}
		};
		background.setLayout(null);
		scrollPane = new JScrollPane(background);
        setContentPane(scrollPane);
        
		jt=new JTextField(30);
		jt.setDocument((new JTextFieldLimit(15)));
		jt.setBounds(350,480,inputsizex,30);
		
		jp=new JPasswordField(30);
		jp.setDocument((new JTextFieldLimit(15)));
		jp.setBounds(350,520,inputsizex,30);
		
		jb1=new JButton();
		jb1.setBounds(190,560,140,35);
		jb1.setIcon(resize("./img/loginon.png", 140));
		jb1.setPressedIcon(resize("./img/loginoff.png", 140));
		jb1.setRolloverIcon(resize("./img/loginpress.png", 140));
		jb1.addActionListener(this);
		
		jb2=new JButton();
		jb2.setBounds(350,560,140,35);
		jb2.setIcon(resize("./img/signon.png", 140));
		jb2.setPressedIcon(resize("./img/signoff.png", 140));
		jb2.setRolloverIcon(resize("./img/signpress.png", 140));
		jb2.addActionListener(this);
		
		
		background.add(jt);
		background.add(jp);
		background.add(jb1);
		background.add(jb2);
		
		setVisible(true);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new Login1("중고나라 로그인");
	}
	
	private ImageIcon resize(String filepath,double newwidth) {
		ImageIcon imic = null;
		try {
			image = ImageIO.read(new File(filepath));
			imagewidth = image.getWidth(null);
			imageheight = image.getHeight(null);
			
			
				ratio = newwidth /(double)imagewidth;
				w = (int)(imagewidth * ratio);
				h = (int)(imageheight * ratio);
			
			
			Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImage.getGraphics();
			g.drawImage(resizeImage, 0, 0, null);
			g.dispose();
			
			imic = new ImageIcon(newImage);
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return imic;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		String id=jt.getText().trim();
		String pw=jp.getText().trim();
		MemberDAO dao = new MemberDAO();
		Connection conn = dao.getConn();
		PreparedStatement ptsm;
		boolean ok=false;
		
			
			if(obj==jb1) {
				
				
				try {
					ptsm = conn.prepareStatement("select *from  tb_member where id = '"+id+"'");
					ResultSet rs = ptsm.executeQuery();
				while(rs.next())
				{
					if(id.equals(rs.getString(1)))
					{
						ok = true;
						if(pw.equals(rs.getString(2))){
							login_id = id;
							new	Table();
							System.out.println(login_id);
							this.setVisible(false);
							break;
						}
						else {
							JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다!");
						}
					}
				}
				if(id.length()==0 && pw.length()==0) {
					JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호를 입력 하셔야 됩니다.", "아이디나 비번을 입력!", JOptionPane.DEFAULT_OPTION);
					}
				else if(ok == false) {
					JOptionPane.showMessageDialog(null, "아이디가 존재하지 않습니다.");
				}
				
				conn.close();
				ptsm.close();
				rs.close();
				} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
			}
			else if(obj==jb2) {
				new MemberProc();
			}
			
		
	}
}
		
		
		
		
		
//		if(obj==jb2) {
//		MemberProc();
//			
//	}
		
//		LoginCheck();
		
	
//	private void LoginCheck() {
//		// TODO Auto-generated method stub
//		if(jt.getText().equals("test") && new String(jp.getPassword()).equals("1234")){
//            JOptionPane.showMessageDialog(null, "Success");
//           // new BasicEditor("기본에디터");
//
//           
//            // 로그인 성공이라면 에디터 띠우기
//            
//           
//            }                  
//        else{
//            JOptionPane.showMessageDialog(null, "실패하셨습니다");
//        }






