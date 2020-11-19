package junggoNara;
//메인 게시판 창
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
;
public class Table extends JFrame implements MouseListener, ActionListener{
	JLayeredPane c;
	Connection con = null;
	PreparedStatement ps = null; 
    ResultSet rs = null;
	DefaultComboBoxModel areamodel = new DefaultComboBoxModel();
	DefaultComboBoxModel collemodel = new DefaultComboBoxModel();
	JComboBox areacombo = null; 
	JComboBox collecombo = null;
	JTable table = null;
	int comboy = 110;
	int leftx = 77; 
	int rightx = 442; 
	BufferedImage image = null;
	private int imagewidth;
	private int imageheight;
	private double ratio;
	private int w;
	private int h;
	JScrollPane scrollPane;
    ImageIcon icon;
    JPanel background;
	private JButton corr;
	private JButton btn;

	private DefaultTableModel tbmodel;
	private Vector v;
	private Vector cols;
	private PostDAO dao;
	public Table(){
		setTitle("게시판");
		setSize(700, 680);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		cols = getColumn();
		dao = new PostDAO();
		v = dao.getTableList();
		
		
		tbmodel = new DefaultTableModel(v, cols) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};
		
		

		icon = new ImageIcon("./img/rain.png");
		        //배경 Panel 생성후 컨텐츠페인으로 지정      
		background = new JPanel() {
		public void paintComponent(Graphics g) {
		                
		g.drawImage(icon.getImage(), -140, 0, null);
		               
		setOpaque(false); //그림을 표시하게 설정,투명하게 조절
		super.paintComponent(g);
		     }
		};
		background.setLayout(null);
		setbutton();
		settable();
		setcombo();
		JLabel idlabel = new JLabel(Login1.login_id);
		idlabel.setText(Login1.login_id);
		idlabel.setBounds(410, 10, 100, 30);
		background.add(idlabel);
		scrollPane = new JScrollPane(background);
        setContentPane(scrollPane);
		setResizable(false);
		setVisible(true);
		
	}
	
	public Vector getColumn(){
	      Vector col = new Vector();
	      col.add("게시번호");
	      col.add("제목");
	      col.add("작성자");
	      col.add("작성일");
	     
	      return col;
	  }
	private void setbutton() {
		// TODO Auto-generated method stub
		corr = new JButton();
		btn = new JButton();
		btn.setIcon(resize("./img/newon.jpg",100));
		btn.setPressedIcon(resize("./img/newpress.jpg",100));
		btn.setRolloverIcon(resize("./img/newoff.jpg",100));
		btn.setBounds(rightx+50, 580, 75, 25);

		corr.setIcon(resize("./img/corron.png",150));
		corr.setPressedIcon(resize("./img/corrpress.png",150));
		corr.setRolloverIcon(resize("./img/corroff.png",150));
		corr.setBounds(leftx+25, 580, 150, 25);
		
		
		btn.addActionListener(this);
		corr.addActionListener(this);
		
		background.add(btn);
		background.add(corr);

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

	private void setcombo() {
		
	    
	    String sql = "select distinct area from area order by area";
	    
	    try {
	    	con = new PostDAO().getConn();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			areamodel.addElement("전체");
			while(rs.next())
			{
				areamodel.addElement(rs.getString("area"));
			}
			
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}finally {
			try {
				con.close();
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
		}
	   
	    
		areacombo = new JComboBox(areamodel);
		areacombo.addActionListener(this);
		areacombo.setBounds(leftx+30, comboy, 100, 20);
		collecombo = new JComboBox(collemodel);
		collecombo.addActionListener(this);
		collecombo.setBounds(rightx+30, comboy, 100, 20);
		background.add(areacombo);
		background.add(collecombo);
	}
	private void settable() {
		
		JPanel tableup = new JPanel();
		tableup.setBounds(leftx+26, comboy+25, 467, 430);
		table = new JTable(tbmodel);
		table.setBackground(new Color(252,239,209));
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);

		table.addMouseListener(this); // 게시물, 작성자 클릭시에 다음 창으로 넘김
		
		table.getColumn("게시번호").setPreferredWidth(10);
		table.getColumn("제목").setPreferredWidth(160); //크기 조정
		table.getColumn("작성자").setPreferredWidth(25);
		table.getColumn("작성일").setPreferredWidth(25);
	
		JScrollPane sp = new JScrollPane(table);
		DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
		tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = table.getColumnModel();

		tcmSchedule.getColumn(0).setCellRenderer(tScheduleCellRenderer);
		tcmSchedule.getColumn(2).setCellRenderer(tScheduleCellRenderer);
		tcmSchedule.getColumn(3).setCellRenderer(tScheduleCellRenderer);
				
		background.add(tableup);
		tableup.add(sp);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
			
	}
	@Override
	public void mousePressed(MouseEvent e) {
		 int r = table.getSelectedRow();
		if(e.getClickCount()>1) {
		      int post_id = (int) table.getValueAt(r, 0);
		      //System.out.println("id="+id);
		      new TableRead(post_id);
		}
	}	//테이블 특정 위치 클릭시 그에 맞는 이벤트 설정
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == corr)
		{
			new MemberProc(Login1.login_id);
		}
		if(obj == btn)
		{
			new PostWrite_Test();
			dispose();
		}
		else if(obj == areacombo)
		{
			Connection con = new PostDAO().getConn();
			String area = (String) areacombo.getSelectedItem();
			String sql = "select college from area where area='" + area + "' order by college";
			collemodel.removeAllElements();
			try {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next())
				{
					collemodel.addElement(rs.getString("college"));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			finally {
				try {
					con.close();
					ps.close();
					rs.close();
				} catch (SQLException e1) {
					// TODO 자동 생성된 catch 블록
					e1.printStackTrace();
				}
				
			}
			
		}
		else if(obj == collecombo)
		{
			String college;
			if( (String)areacombo.getSelectedItem()=="전체")
			{
				v = dao.getTableList();
				tbmodel.setDataVector(v, cols);
			}
			else {
				college = (String) collecombo.getSelectedItem();
				System.out.println(college);
				v = dao.getTableList(college);
				tbmodel.setDataVector(v, cols);
			}
			
					
		}
		
	}

}