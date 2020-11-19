package junggoNara;
//글쓰기 창
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import junggoNara.JTextFieldLimit;

public class PostWrite_Test extends JFrame implements ActionListener, FocusListener, KeyListener{
   private int labelx = 40;
   private int inputx = 130;
   
   private int titley = 30;
   private int contexty = 60;
   
   private int labelsizex = 100;
   private int labelsizey = 100;
   
   private int inputsizex = 350;
   private int inputsizey = 25;
   
   private int imgpathy = 510;
   private int uploady = 550;
   
   private int comboX = 160;
   private int comboY = -10;
   
   private String filePath;
   
   private JTextField uploadpath;
   private JTextArea contextinput;
   JButton upload;
   private JTextField postinput;
   private String path = System.getProperty("/main/");
   private JFileChooser chooser = new JFileChooser(path);
   private JTextField imgpath;
   
   private int postlit = 20;
   private int conlit = 650;
   
   BufferedImage image = null;
   private int imagewidth;
   private int imageheight;
   private double ratio;
   private int w;
   private int h;
   
   JScrollPane scrollPane;
    ImageIcon icon;
    JPanel p;
    JButton img;
    JButton cancel;
   JLabel imageLabel = new JLabel();
   String contstr = "거래하고자 하는 물품, 교환장소, 연락처 등 \n거래에 도움이 될 만한 내용을 작성해주세요.";
   
   DefaultComboBoxModel areamodel = new DefaultComboBoxModel();
   DefaultComboBoxModel collemodel = new DefaultComboBoxModel();
   JComboBox areacombo = null; 
   JComboBox collecombo = null;
private Connection con;
private PreparedStatement ps;
private ResultSet rs;
private Table tb;
   
   public PostWrite_Test(){
      setTitle("새 글");
      setSize(500, 650);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      icon = new ImageIcon("img/newpost.png");
        //배경 Panel 생성후 컨텐츠페인으로 지정      
      p = new JPanel() {
         public void paintComponent(Graphics g) {
                
            g.drawImage(icon.getImage(), -140, 0, null);
               
            setOpaque(false); 
            super.paintComponent(g);
         }
      };
      p.setLayout(null);
      
      setpost();
      setcontext();
      setimg();
      setupload();
      setcombo();
      
      scrollPane = new JScrollPane(p);
        setContentPane(scrollPane);
        
      p.requestFocus();
      setVisible(true);
      setResizable(false);
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
      collecombo = new JComboBox(collemodel);
      
      areacombo.setBounds(inputx+75, titley, 100, 20);
      collecombo.setBounds(inputx+248, titley, 100, 20);
      
      p.add(areacombo);
      p.add(collecombo);
   }

   private void setupload() {
      upload = new JButton();      
      upload.setBounds(240,uploady,100,25);
      upload.setIcon(resize("./img/submiton.png", 100));
      upload.setPressedIcon(resize("./img/submitoff.png", 100));
      upload.setRolloverIcon(resize("./img/submitpress.png", 100));
      
      upload.addActionListener(this);
      
      cancel = new JButton();      
      cancel.setBounds(350,uploady,100,25);
      cancel.setIcon(resize("./img/cancelon.png", 100));
      cancel.setPressedIcon(resize("./img/canceloff.png", 100));
      cancel.setRolloverIcon(resize("./img/cancelpress.png", 100));
      
      cancel.addActionListener(this);
      p.add(upload);
      p.add(cancel);
   }
   private void setimg() {
      img = new JButton();
      img.setBounds(labelx-20,imgpathy,100,25);
      img.setIcon(resize("./img/imgon.png", 100));
      img.setPressedIcon(resize("./img/imgoff.png", 100));
      img.setRolloverIcon(resize("./img/imgpress.png", 100));
      img.addActionListener(this);
      
      imgpath = new JTextField();
      imgpath.setBounds(inputx, imgpathy, inputsizex,inputsizey);
      imgpath.setEditable(false);

      p.add(img);

      p.add(imgpath);
   }
   private void setcontext() {
      
      
      
      contextinput = new JTextArea
            (contstr);
      contextinput.setDocument(new JTextFieldLimit(conlit));
      contextinput.setLineWrap(true);
      contextinput.addFocusListener(this);
      JScrollPane cjp = new JScrollPane(contextinput,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      cjp.setBounds(inputx, contexty+40, inputsizex, 400);
      
      //contextinput.addKeyListener(this);
      
      
      p.add(cjp);
      
   }
   
   private void setpost() {
      
      postinput = new JTextField();
      postinput.setDocument((new JTextFieldLimit(postlit)));
      postinput.setBounds(inputx, titley+40, inputsizex, inputsizey);
      postinput.setLocation(inputx, titley+40);
   
      p.add(postinput);
   }
   
   private void insertPost(){
          
        //화면에서 사용자가 입력한 내용을 얻는다.
       PostDTO dto = getViewData1();
        PostDAO dao = new PostDAO();       
        boolean ok = dao.insertPost(dto);
       
        if(ok){
            JOptionPane.showMessageDialog(this, "글등록이 완료되었습니다.");
            dispose();
            new Table();
        }
   }
        public PostDTO getViewData1(){
            
            //화면에서 사용자가 입력한 내용을 얻는다.
            PostDTO dto = new PostDTO();
            
            String id = Login1.login_id;
            String title = postinput.getText();
            String content =contextinput.getText();
            String college = (String) collecombo.getSelectedItem();
        
            //dto에 담는다.
            
            dto.setContent(content);
            dto.setTitle(title);
            dto.setId(id);
            dto.setCollege(college);
           
            return dto;
        }
   @Override
   public void actionPerformed(ActionEvent e) {
      Object obj = e.getSource();
      
      if(obj == upload) {
    	 if(collecombo.getSelectedItem()==null)
    	 {
    		 JOptionPane.showMessageDialog(null, "위치가 입력되지 않았습니다");
    	 }
    	 else if(postinput.getText().equals(""))
         {
            JOptionPane.showMessageDialog(null, "제목이 입력되지 않았습니다.");
            if (contextinput.getText().equals(""))
            {
               JOptionPane.showMessageDialog(null, "내용을 입력되지 않았습니다.");
            }
         }else {
        	 insertPost();
         }
      }
      else if(obj == img) {
         FileNameExtensionFilter filter = new FileNameExtensionFilter
               ("JPG & GIF Images", "jpg","gif");
         chooser.setFileFilter(filter);
         
         int ret = chooser.showOpenDialog(null);
         if(ret != JFileChooser.APPROVE_OPTION) {
            return;
         }
         
         String filePath = chooser.getSelectedFile().getPath();
         imgpath.setText(filePath);
      }
      else if(obj == cancel) {
        	this.dispose();
        	new Table();
      }
      else if(obj == areacombo) {
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
   }
   @Override
   public void focusGained(FocusEvent e) {
      if(contextinput.getText().equals(contstr)) 
      {
         contextinput.setText("");
      }
   }
   @Override
   public void focusLost(FocusEvent e) {
      // TODO Auto-generated method stub
   

      if(contextinput.getText().equals(""))
      {
         contextinput.setText(contstr);
      }
   }

   @Override
   public void keyTyped(KeyEvent e) {
   }

   @Override
   public void keyPressed(KeyEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // TODO Auto-generated method stub
      
   }
}
