package junggoNara;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class TableRead extends JFrame implements ActionListener, MouseListener{
   private int labelx = 40;
   private int inputx = 100;
   private int titley = 0;
   private int contexty = 30;
   private int labelsizex = 100;
   private int labelsizey = 100;
   private int inputsizex = 350;
   private int inputsizey = 25;
   
   private int imagex = 460;
   private int ButtonY = 480;
   
   private int postlit = 30;
   private int conlit = 650;
   private int comlit = 50;
   
   private String id;   // '디비에서 읽어올' 작성한 게시물 주인의 아이디
   private String connectid;// '디비에서 읽어올' 현재 접속돼있는 아이디
   private String manager;   // 아이디가 '매니저'인 관리자 아이디 
   private int post_id;
   private JTextField postinput; //제목
   private JTextArea contextinput; //내용
  
   
   private JPanel p;
   private JPanel jp;
   private JPanel cp;
   
   private JButton toList;
   private JButton correct;
   private JButton Delete;
   private JButton Cancel;
   
   private String postTemp;
   private String contTemp;
   private JTextArea comarea;
   private JFileChooser chooser = new JFileChooser();
   String header[] = {"작성자","내용","날짜"};
   String contents[][] = { };
   DefaultTableModel tbmodel = new DefaultTableModel(contents,header){
      public boolean isCellEditable(int i, int c) {
         return false;
      }
   };
   JTable table = null;
   
   private Image image;
   int imagewidth;
   int imageheight;
   double ratio;
    int w;
    int h;

    int newwidth = 300;
    int newheight = 400;
    String mainPosition = "W";
    String imgformat = "jpg";
    private JLabel img = new JLabel();
    
   
    
   JScrollPane scrollPane;
    ImageIcon icon;
    JPanel background;
   private JButton check;
   private JButton pc;
   private JButton upCom;
   
   public TableRead(int post_id){
      setTitle("게시글 보기");
      setSize(850, 920);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      
      connectid = Login1.login_id; //접속한 id 저장
     
      this.post_id = post_id; //게시글 번호 저장
      
      jp = new JPanel();
      jp.setLayout(null);
      jp.setBounds(imagex + 60, titley, 300, 400);
   
      icon = new ImageIcon("./img/Tread.png");
      background = new JPanel() {
         public void paintComponent(Graphics g) {
                
            g.drawImage(icon.getImage(), -1015, 0, null);
               
            setOpaque(false); 
            super.paintComponent(g);
         }
      };
      background.setLayout(null);
      scrollPane = new JScrollPane(background);
        setContentPane(scrollPane);
        
      setpost();   // 작성자가 작성한 제목 관련 메소드
      setcontext();   // 작성자가 작성한 내용 관련 메소드
      PostDAO dao = new PostDAO();
      PostDTO vMem = dao.getPostDTO(post_id);
      viewPost(vMem);
      setupload();   // 우측 버튼 관련 메소드
      setcom();   //   댓글 작성 창 관련 메소드
      settab();   // 댓글 열람 창 관련 메소드
      
      
      background.add(jp.add(img));
      
      
   
      setResizable(false);
      
      setVisible(true);
      
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
   private void settab() {
      
      
      JPanel tableup = new JPanel();      // tableup = 댓글 테이블 크기,위치 할당 패널
      
      tableup.setBounds(65, 545, 729, 204);
      table = new JTable(tbmodel);      // table = 댓글 목록
      table.getTableHeader().setReorderingAllowed(false);
      table.getTableHeader().setResizingAllowed(false);
      
      table.getColumn("작성자").setPreferredWidth(30);
      table.getColumn("내용").setPreferredWidth(450);
      table.getColumn("날짜").setPreferredWidth(50);
      
      table.addMouseListener(this);
      
      JScrollPane tsp = new JScrollPane(table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      tsp.setPreferredSize(new java.awt.Dimension(730,200));
      
      background.add(tableup);   // background = 배경 사진이 삽입된 패널
      tableup.add(tsp);
   
   }
   private void setcom() {
      comarea = new JTextArea();   // 댓글 쓰는 텍스트 에이리어
      comarea.setDocument(new JTextFieldLimit(comlit));   //setdocument(jtextfieldlimit(int)) 댓글 최대 수 제한 
      comarea.setLineWrap(true);                     //자동 줄넘김
      JScrollPane csp = new JScrollPane
            (comarea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                  ,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      
      csp.setBounds(65, 760, 625, 75);
      
      upCom = new JButton();   //'등록' 버튼
      upCom.setBounds(700, 760, 100, 76);
      upCom.setIcon(resize("./img/Commenton.png", 100));
      upCom.setPressedIcon(resize("./img/Commentoff.png", 100));
      upCom.setRolloverIcon(resize("./img/Commentpress.png", 100));
      upCom.addActionListener(this);
      
      background.add(csp);
      background.add(upCom);
      
   }
   
   private void setupload() {
      toList = new JButton();      // 목록 버튼
      toList.setBounds(700,ButtonY,60,30);
      toList.setIcon(resize("./img/liston.png", 58));
      toList.setPressedIcon(resize("./img/listoff.png", 58));
      toList.setRolloverIcon(resize("./img/listpress.png", 58));
      
      toList.addActionListener(this);
      
      correct = new JButton();   // 수정 버튼
      correct.setBounds(625,ButtonY,60,30);
      correct.setIcon(resize("./img/correctin.png", 58));
      correct.setPressedIcon(resize("./img/correctoff.png", 58));
      correct.setRolloverIcon(resize("./img/correctpress.png", 58));
      correct.addActionListener(this);
      
      Cancel = new JButton();      // 취소 버튼
      Cancel.setBounds(625,ButtonY,60,30);
      Cancel.setIcon(resize("./img/Cancellon.png", 58));
      Cancel.setPressedIcon(resize("./img/Cancelloff.png", 58));
      Cancel.setRolloverIcon(resize("./img/Cancellpress.png", 58));
      Cancel.addActionListener(this);
      
      
      Delete = new JButton();      // 삭제 버튼   
      Delete.setBounds(550,ButtonY,60,30);
      Delete.setIcon(resize("./img/Deleteon.png", 60));
      Delete.setPressedIcon(resize("./img/Deleteoff.png", 60));
      Delete.setRolloverIcon(resize("./img/Deletepress.png", 60));
      Delete.addActionListener(this);
      
      
      check = new JButton();   // 확인 버튼
      check.setBounds(550,ButtonY,60,30);
      check.setIcon(resize("./img/checkon.png", 58));
      check.setPressedIcon(resize("./img/checkoff.png", 58));
      check.setRolloverIcon(resize("./img/checkpress.png", 58));
      check.addActionListener(this);
      
      pc = new JButton();      //사진 첨부 버튼
      pc.setBounds(700,ButtonY,113,30);
      pc.setIcon(resize("./img/pcon.png", 113));
      pc.setPressedIcon(resize("./img/pcoff.png", 113));
      pc.setRolloverIcon(resize("./img/pcpress.png", 113));
      pc.addActionListener(this);
      
      background.add(correct);
      background.add(toList);
      background.add(Delete);
      background.add(Cancel);
      background.add(check);
      background.add(pc);
      
      check.setVisible(false);
      Cancel.setVisible(false);
      pc.setVisible(false);
      System.out.println(id + ", "+ connectid);
      if(id == connectid || connectid == "manager") {         // 디비에서 받은 아이디와 연결된 아이디를 비교해서 버튼의 유무를 선택
         Delete.setVisible(true);
         System.out.println("생성1");
      } else {
         Delete.setVisible(false); 
         System.out.println("생성2");
      }
      if(id == connectid) {         // 디비에서 받은 아이디와 연결된 아이디를 비교해서 버튼의 유무를 선택
         correct.setVisible(true);
         System.out.println("생성3");
      } else {
         correct.setVisible(false);    
         System.out.println("생성4");
      }
   }
   private void setcontext() {
      
      
      contextinput = new JTextArea();      // 작성 글 내용 텍스트 에이리어
      contextinput.setDocument(new JTextFieldLimit(conlit));
      contextinput.setLineWrap(true);
      contextinput.setEditable(false);
      contextinput.setText("내용을 읽혀서");
      
      JScrollPane cjp = new JScrollPane(contextinput,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      cjp.setBounds(inputx, contexty+40, inputsizex, 400);
      
      background.add(cjp);
      
   }
   private void setpost() {
      
      postinput = new JTextField();      // 작성 제목 내용 텍스트 에이리어
      postinput.setDocument((new JTextFieldLimit(30)));
      
      postinput.setBounds(inputx, titley+40, inputsizex, inputsizey);
      postinput.setLocation(inputx, titley+40);
      postinput.setEditable(false);
      postinput.setText("제목을");
      
      background.add(postinput);   
      

      }
   private void insertTwit(){
       
       //화면에서 사용자가 입력한 내용을 얻는다.
      TwitDTO dto = getViewData2();
       TwitDAO dao = new TwitDAO();       
       boolean ok = dao.insertTwit(dto);
      
       if(ok){
          
           JOptionPane.showMessageDialog(this, "댓글등록이 완료되었습니다.");
          // mList.jTableRefresh();
           dispose();
          
       }else{
          
           JOptionPane.showMessageDialog(this, "댓글등록이 정상적으로 처리되지 않았습니다.");
       }
	}
       public TwitDTO getViewData2(){
           
           //화면에서 사용자가 입력한 내용을 얻는다.
           TwitDTO dto = new TwitDTO();
           
            
          
           String twit_content =comarea.getText(); //글내용
         
           
       
           //dto에 담는다.
           
           dto.setTwit_content(twit_content);
         
           
          
           return dto;
       }
   @Override
   public void actionPerformed(ActionEvent e) {
      Object obj = e.getSource();
      if(obj == upCom ) {         // obj == 댓글 등록
         if(comarea.getText().equals(""))
         {
            JOptionPane.showMessageDialog(null, "내용을 입력해주세요");
            return;
         }
         
         Date today = new Date();
          
          SimpleDateFormat date = new SimpleDateFormat("yy.MM.dd");
          SimpleDateFormat time = new SimpleDateFormat("HH:mm ");
         
         tbmodel = (DefaultTableModel) table.getModel();
         String [] add = {connectid,comarea.getText(),date.format(today)+". "+time.format(today)};
         // add 첫번째 매개변수 임의로 설정한 아이디
         
         comarea.setText("");      // 댓글 작성창 초기화
         tbmodel.addRow(add);      // 댓글 목록창에 열 추가
      }
      
      if(obj == Delete) {         // obj == 삭제
         
         
         int selected = JOptionPane.showConfirmDialog(null, "작성한 글을 삭제합니다.", 
               "경고", JOptionPane.YES_NO_OPTION, 
               JOptionPane.WARNING_MESSAGE);
         if (selected == JOptionPane.YES_OPTION) {
        	 PostDAO dao =  new PostDAO();
        	 dao.deletePost(post_id);
            JOptionPane.showMessageDialog(null, "글이 삭제 되었습니다.");
            
            dispose();
         } else {
            return;
         }
      
         
      }
      if(obj == toList) {      // obj == 목록
         int result = JOptionPane.showConfirmDialog(null, "게시글 목록으로 이동합니까?",
        		 "confirm", JOptionPane.YES_NO_OPTION);
         if(result == JOptionPane.YES_OPTION) {
        	 this.dispose();
         }
      }
      
      
      if(obj == correct) {   // obj == 수정
         
         
         postTemp = postinput.getText();
         contTemp = contextinput.getText();
         
         postinput.setEditable(true);      // 제목 텍스트 에이리어 수정 가능
         contextinput.setEditable(true);      // 내용 텍스트 에이리어 수정 가능
         UpdatePost();
         Delete.setVisible(false);
         check.setVisible(true);
         correct.setVisible(false);
         Cancel.setVisible(true);
         toList.setVisible(false);
         pc.setVisible(true);
         } 
      
      if(obj == check) {      // obj == 확인
         
            if(postinput.getText().equals(""))
            {
               JOptionPane.showMessageDialog(null, "제목이 입력되지 않았습니다.");
            } 
            else if (contextinput.getText().equals(""))
            {
               JOptionPane.showMessageDialog(null, "내용을 입력되지 않았습니다.");
            } else {
               JOptionPane.showMessageDialog(null, "게시물 수정 완료");
               PostDTO dto = getViewData(); 
               PostDAO dao = new PostDAO();
               dao.updatePost(dto);
               postinput.setEditable(false);
               contextinput.setEditable(false);
               Delete.setVisible(true);
               correct.setVisible(true);
               pc.setVisible(false);
               toList.setVisible(true);
               toList.setBounds(700,ButtonY,60,30);
            }   
      }
      if(obj == Cancel){      // obj == 취소

         int selected = JOptionPane.showConfirmDialog(null, "수정을 취소합니다.", 
               "경고", JOptionPane.YES_NO_OPTION, 
               JOptionPane.WARNING_MESSAGE);
         if (selected == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "수정이 취소되었습니다.");
            postinput.setEditable(false);
            contextinput.setEditable(false);
            Delete.setVisible(true);
            correct.setVisible(true);
            toList.setVisible(true);
            
            pc.setVisible(false);
            check.setVisible(false);
            Cancel.setVisible(false);
            
            toList.setBounds(700,ButtonY,60,30);
         } else {
            return;
         }
      
         postinput.setText(postTemp);
         contextinput.setText(contTemp);
         
         postinput.setEditable(false);
         contextinput.setEditable(false);

      }
      if(obj  == pc) {   // 디비에서 사진 어떻게 불러오는지는 모르지만 크기에 적용하는 방식에 도움이 됐으면 함 
         // obj == 사진 첨부
         FileNameExtensionFilter filter = new FileNameExtensionFilter
               ("JPG & GIF Images", "jpg","gif","png");
         chooser.setFileFilter(filter);
         
         int ret = chooser.showOpenDialog(null);
         if(ret != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, " 좇까","경고",JOptionPane.WARNING_MESSAGE);
            return;
         }   //임의로 지정한 파일 경로 탐색후 업로드 방식
         
         String filePath = chooser.getSelectedFile().getPath();
         try {
            image = ImageIO.read(new File(filePath));
            imagewidth = image.getWidth(null);
            imageheight = image.getHeight(null);
            
            
               ratio = (double)newwidth/(double)imagewidth;
               w = (int)(imagewidth * ratio);
               h = (int)(imageheight * ratio);
            
            
            Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = newImage.getGraphics();
            g.drawImage(resizeImage, 0, 0, null);
            g.dispose();
            
            ImageIcon imic = new ImageIcon(newImage);
            img.setIcon(imic);
            img.setBounds(imagex + 50, titley+70, 300, 400);
            }catch (IOException e1) {
               e1.printStackTrace();
            }                                    //이미지 업로드 확인
         
         }
   }
   @Override
   public void mouseClicked(MouseEvent e) {
      int row = table.getSelectedRow();
      int col = table.getSelectedColumn();
      Object value = table.getValueAt(row, 0);
      
      String userCheck = (String)value;      // 댓글 목록창에서 읽어온 아이디 값
      
      tbmodel = (DefaultTableModel) table.getModel();
      if(userCheck == connectid || connectid == "manager") {      //현재 접속한 아이디 비교
      int selected = JOptionPane.showConfirmDialog(null, "작성한 댓글을 삭제합니다.", 
            "경고", JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
      if (selected == JOptionPane.YES_OPTION) {
         tbmodel.removeRow(row);
      } else {
         return;
         }
      } 
      else {
    	 JOptionPane.showMessageDialog(null, "작성자가 아닙니다. 삭제할 수 없습니다.");
      }
   }
   @Override
   public void mousePressed(MouseEvent e) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public void mouseReleased(MouseEvent e) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public void mouseEntered(MouseEvent e) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public void mouseExited(MouseEvent e) {
      // TODO Auto-generated method stub
      
   }
   private void UpdatePost() {
       
       PostDTO dto = getViewData(); 
       PostDAO dao = new PostDAO();
       boolean ok = dao.updatePost(dto);
   }
   public PostDTO getViewData(){
       
       //화면에서 사용자가 입력한 내용을 얻는다.
       PostDTO dto = new PostDTO();
       
       String title = postinput.getText();
       String content =contextinput.getText();
       
       dto.setContent(content);
       dto.setTitle(title);
       dto.setPost_id(post_id);
      
       return dto;
   }
   //화면에 Db에서 가져온 내용을 불러와 화면에 입력한다.
   private void viewPost(PostDTO vMem){
       
       String id = vMem.getId();
       String title = vMem.getTitle();
       String content = vMem.getContent();
       
       
       postinput.setText(title);
       contextinput.setText(content);
       this.id = id;
       
       System.out.println( 30 + id + ", " + connectid);
   }

}