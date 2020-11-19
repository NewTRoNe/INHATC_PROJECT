package junggoNara;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import java.awt.event.*;
 
public class MemberProc extends JFrame implements ActionListener {
   
   
    JPanel p;
    JTextField tfId, tfName, tfAddr, tfEmail;
    JTextField tfTel1, tfTel2, tfTel3; //전화
    JComboBox cbJob; //직업
    JPasswordField pfPwd; //비밀번호   
    JComboBox cbYear, cbMonth, cbDate; //생년월일
    JRadioButton rbMan, rbWoman; //남, 여
    JTextArea taIntro;
    JButton btnInsert, btnCancel, btnUpdate,btnDelete; //가입, 취소, 수정 , 탈퇴 버튼
   
    GridBagLayout gb;
    GridBagConstraints gbc;
    Member_List mList ;
    JButton idCheck;
   boolean IdOK;
   
    public MemberProc(){ //가입용 생성자
       
        createUI(); // UI작성해주는 메소드
        btnUpdate.setEnabled(false);
        btnUpdate.setVisible(false);
        btnDelete.setEnabled(false);
        btnDelete.setVisible(false);
       
       
    }//생성자
   
    public MemberProc(Member_List mList){ //가입용 생성자
       
        createUI(); // UI작성해주는 메소드
        btnUpdate.setEnabled(false);
        btnUpdate.setVisible(false);
        btnDelete.setEnabled(false);
        btnDelete.setVisible(false);
        this.mList = mList;
       
    }
    
    public MemberProc(String id){ // 수정/삭제용 생성자
        createUI();
        btnInsert.setEnabled(false);
        btnInsert.setVisible(false);
        idCheck.setEnabled(false);
        idCheck.setVisible(false);
       
       
        System.out.println("id="+id);
       
        MemberDAO dao = new MemberDAO();
        MemberDTO vMem = dao.getMemberDTO(id);
        viewData(vMem);
       
       
    }
 
       
    //MemberDTO 의 회원 정보를 가지고 화면에 셋팅해주는 메소드
    private void viewData(MemberDTO vMem){
       
        String id = vMem.getId();
        String pwd = vMem.getPwd();
        String name = vMem.getName();
        String tel = vMem.getTel();
        String addr = vMem.getAddr();
        String birth = vMem.getBirth();
        String job = vMem.getJob();
        String gender = vMem.getGender();
        String email= vMem.getEmail();
        String intro = vMem.getIntro();    
       
        //화면에 세팅
        tfId.setText(id);
        tfId.setEditable(false); //편집 안되게
        pfPwd.setText(""); //비밀번호는 안보여준다.
        tfName.setText(name);
        String[] tels = tel.split("-");
        tfTel1.setText(tels[0]);
        tfTel2.setText(tels[1]);
        tfTel3.setText(tels[2]);
        tfAddr.setText(addr);
       
        cbYear.setSelectedItem(birth.substring(0, 4));
        cbMonth.setSelectedItem(birth.substring(4, 6));
        cbDate.setSelectedItem(birth.substring(6, 8));
       
        cbJob.setSelectedItem(job);
       
       
        if(gender.equals("M")){
            rbMan.setSelected(true);
        }else if(gender.equals("W")){
            rbWoman.setSelected(true);
        }
       
        tfEmail.setText(email);
        taIntro.setText(intro);
   
       
    }//viewData
   
   
   
    private void createUI(){
        this.setTitle("회원정보");
        gb = new GridBagLayout();
        setLayout(gb);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        
        //아이디
        JPanel idPan= new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        JLabel bId = new JLabel("아이디 : ");
        tfId = new JTextField(13); 
        idCheck=new JButton("중복체크");
        //그리드백에 붙이기
        gbAdd(bId, 0, 0, 1, 1);
       idPan.add(tfId);
       idPan.add(idCheck);
        gbAdd(idPan,1,0,3,1);
        idCheck.addActionListener(this);
       
        //비밀번호
        JLabel bPwd = new JLabel("비밀번호 : ");
        pfPwd = new JPasswordField(20);
        gbAdd(bPwd, 0, 1, 1, 1);
        gbAdd(pfPwd, 1, 1, 3, 1);
       
        //이름
        JLabel bName = new JLabel("이름 :");
        tfName = new JTextField(20);
        gbAdd(bName,0,2,1,1);
        gbAdd(tfName,1,2,3,1);
       
        //전화
        JLabel bTel = new JLabel("휴대전화 :");
        JPanel pTel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfTel1 = new JTextField(6);
        tfTel1.setText("010");
        tfTel1.setEnabled(false);
        tfTel1.setDisabledTextColor(Color.black);
        tfTel1.setBackground(Color.white);
        tfTel2 = new JTextField(6);    
        tfTel3 = new JTextField(6);
        pTel.add(tfTel1);
        pTel.add(new JLabel(" - "));
        pTel.add(tfTel2);
        pTel.add(new JLabel(" - "));
        pTel.add(tfTel3);
        gbAdd(bTel, 0, 3, 1,1);
        gbAdd(pTel, 1, 3, 3,1);
       
        //주소
        JLabel bAddr = new JLabel("주소: ");
        tfAddr = new JTextField(20);
        gbAdd(bAddr, 0,4,1,1);
        gbAdd(tfAddr, 1, 4, 3,1);
       
        //생일
        JLabel bBirth= new JLabel("생일: ");
        cbYear = new JComboBox();
        for(int i=2010; i>=1900; i--)
        {
           String a;
           a=Integer.toString(i);
           cbYear.addItem(a);
        }
        cbYear.setSelectedItem(null);
        cbMonth = new JComboBox();
        for(int i=01; i<=12; i++)
        {
           String b;
           b=Integer.toString(i);
           cbMonth.addItem(b);
        }
        cbMonth.setSelectedItem(null);
        cbDate = new JComboBox();
        for(int i=01; i<=31; i++)
        {
           String c;
           c=Integer.toString(i);
           cbDate.addItem(c);
        }
        cbDate.setSelectedItem(null);
        JPanel pBirth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBirth.add(cbYear);
        pBirth.add(new JLabel("/"));
        pBirth.add(cbMonth);
        pBirth.add(new JLabel("/"));
        pBirth.add(cbDate);
        gbAdd(bBirth, 0,5,1,1);
        gbAdd(pBirth, 1, 5, 3,1);
       
        //직업       
        JLabel bJob = new JLabel("직업 : ");
        String[] arrJob = {"---", "학생", "직장인", "주부"};
        cbJob = new JComboBox(arrJob);
        JPanel pJob = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pJob.add(cbJob);       
        gbAdd(bJob, 0,6,1,1);
        gbAdd(pJob,1,6,3,1);
       
        //성별
        JLabel bGender = new JLabel("성별 : ");
        JPanel pGender = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbMan = new JRadioButton("남",true);
        rbWoman = new JRadioButton("여",true);
        ButtonGroup group = new ButtonGroup();
        group.add(rbMan);
        group.add(rbWoman);
        pGender.add(rbMan);
        pGender.add(rbWoman);      
        gbAdd(bGender, 0,7,1,1);
        gbAdd(pGender,1,7,3,1);
       
        //이메일
        JLabel bEmail = new JLabel("이메일 : ");
        tfEmail = new JTextField(20);
        gbAdd(bEmail, 0,8,1,1);
        gbAdd(tfEmail,1,8,3,1);
       
        //자기소개
        JLabel bIntro = new JLabel("자기 소개: ");
        taIntro = new JTextArea(5, 20); //행 : 열
        JScrollPane pane = new JScrollPane(taIntro);
        gbAdd(bIntro,0,9,1,1);
        gbAdd(pane,1,9,3,1);
       
        //버튼
        JPanel pButton = new JPanel();
        btnInsert = new JButton("가입");
        btnUpdate = new JButton("수정"); 
        btnDelete = new JButton("탈퇴");
        btnCancel = new JButton("취소");     
        pButton.add(btnInsert);
        pButton.add(btnUpdate);
        pButton.add(btnDelete);
        pButton.add(btnCancel);
        
        gbAdd(pButton, 0, 10, 4, 1);
       
        //버튼에 감지기를 붙이자
        btnInsert.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnCancel.addActionListener(this);
        btnDelete.addActionListener(this);
       
        setSize(350,500);
        setVisible(true);
        
        
        
        //setDefaultCloseOperation(EXIT_ON_CLOSE); //System.exit(0) //프로그램종료
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //dispose(); //현재창만 닫는다.
       
       
    }//createUI
   
    //그리드백레이아웃에 붙이는 메소드
    private void gbAdd(JComponent c, int x, int y, int w, int h){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gb.setConstraints(c, gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        add(c, gbc);
    }//gbAdd
   
    public static void main(String[] args) {
       
        new MemberProc("rudtn0403");
    }
   
 
    @Override
    public void actionPerformed(ActionEvent ae) {
       
       if(ae.getSource() == idCheck) {
          MemberDAO dao = new MemberDAO();
          Connection conn = dao.getConn();
          PreparedStatement ptsm;
          String id = tfId.getText();
          IdOK = false;
          try {
            ptsm = conn.prepareStatement("select * from tb_member where id='" + tfId.getText() + "'");
            ResultSet rs = ptsm.executeQuery();
            if (rs.next() == false) { // id가 존재x
               if(id.length()<=8)
               {
                  JOptionPane.showMessageDialog(null, "8자 이상 입력하세요.");
                  IdOK = false;
               }
               else {
                      JOptionPane.showMessageDialog(null, "사용 가능한 ID입니다.");
                      tfId.setEditable(false);
                      IdOK = true;
               }
            }
            else{
               JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
               
               }
         }catch (SQLException e1) {
            e1.printStackTrace();
            }
          }
          


       
       if(ae.getSource() == btnInsert){
            insertMember();
            System.out.println("insertMember() 호출 종료");
            
        }else if(ae.getSource() == btnCancel){
            this.dispose();
        }else if(ae.getSource() == btnUpdate){
            UpdateMember();            
        }else if(ae.getSource() == btnDelete){
            int x = JOptionPane.showConfirmDialog(this,"정말 삭제하시겠습니까?","삭제",JOptionPane.YES_NO_OPTION);
           
            if (x == JOptionPane.OK_OPTION){
                deleteMember();
                System.exit(0);
            }
        }
       
    }

   
   
    private void deleteMember() {
        String id = tfId.getText();
        String pwd = pfPwd.getText();
        if(pwd.length()==0){
           
            JOptionPane.showMessageDialog(this, "비밀번호를 꼭 입력하세요!");
            return;
        }
        MemberDAO dao = new MemberDAO();
        boolean ok = dao.deleteMember(id, pwd);
       
        if(ok){
            JOptionPane.showMessageDialog(this, "삭제완료");
            dispose();         
           
        }else{
            JOptionPane.showMessageDialog(this, "삭제실패");
           
        }          
       
    }//deleteMember
   
    private void UpdateMember() {
       
        MemberDTO dto = getViewData(); 
        MemberDAO dao = new MemberDAO();
        boolean ok = dao.updateMember(dto);
       
        if(ok){
            JOptionPane.showMessageDialog(this, "수정되었습니다.");
            this.dispose();
        }else{
            JOptionPane.showMessageDialog(this, "수정실패: 값을 확인하세요");   
        }
    }
 
    private void insertMember(){
       
        //화면에서 사용자가 입력한 내용을 얻는다.
        MemberDTO dto = getViewData();
        MemberDAO dao = new MemberDAO();       
        boolean ok = dao.insertMember(dto);
       
        if(ok){
           
            JOptionPane.showMessageDialog(this, "가입이 완료되었습니다.");
            dispose();
           
        }else{
           
            JOptionPane.showMessageDialog(this, "가입이 정상적으로 처리되지 않았습니다.");
        }
       
       
       
    }//insertMember
   
    public MemberDTO getViewData(){
       
        //화면에서 사용자가 입력한 내용을 얻는다.
        MemberDTO dto = new MemberDTO();
        String id = tfId.getText();
        String pwd = pfPwd.getText();
        String name = tfName.getText();
        String tel1 = tfTel1.getText();
        String tel2 = tfTel2.getText();
        String tel3 = tfTel3.getText();
        String tel = tel1+"-"+tel2+"-"+tel3;
        String addr = tfAddr.getText();
        String birth1 = (String) cbYear.getSelectedItem();
        String birth2 = (String) cbMonth.getSelectedItem();
        String birth3 = (String) cbDate.getSelectedItem();
        String birth = birth1+"/"+birth2+"/"+birth3;
        String job = (String)cbJob.getSelectedItem();
        String gender = "";
        if(rbMan.isSelected()){
            gender = "M";
        }else if(rbWoman.isSelected()){
            gender = "W";
        }
       
        String email = tfEmail.getText();
        String intro = taIntro.getText();
       
        //dto에 담는다.
        dto.setId(id);
        dto.setPwd(pwd);
        dto.setName(name);
        dto.setTel(tel);
        dto.setAddr(addr);
        dto.setBirth(birth);
        dto.setJob(job);
        dto.setGender(gender);
        dto.setEmail(email);
        dto.setIntro(intro);
       
        return dto;
    }
}//end