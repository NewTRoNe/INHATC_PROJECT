package junggoNara;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class TwitDAO {

private static final String DRIVER
    = "oracle.jdbc.driver.OracleDriver";
private static final String URL
    = "jdbc:oracle:thin:@localhost:1521:XE";

private static final String USER = "scott"; //DB ID
private static final String PASS = "tiger"; //DB 패스워드
//Member_List mList;
   



public Connection getConn(){
    Connection con = null;
   
    try {
        Class.forName(DRIVER); //1. 드라이버 로딩
        con = DriverManager.getConnection(URL,USER,PASS); //2. 드라이버 연결
       
    } catch (Exception e) {
        e.printStackTrace();
    }
   
    return con;
}

public static void main(String[] args) {
   Connection con = new PostDAO().getConn();
}

//테이블 불러오기
public Vector getTableList()
{
   
   Vector data = new Vector();
   
      
    
    Connection con = null;      
    PreparedStatement ps = null; 
    ResultSet rs = null;        
   
    try{
        
        con = getConn();
        String sql = "select * from  Twit order by twit_time desc";
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
       
        while(rs.next()){
            String id = rs.getString("id");
            String twitContent = rs.getString("twit_content");
            String twitDate = rs.getDate("twit_date").toString();
            
           
            Vector row = new Vector();
            row.add(id);
            row.add(twitContent);
            row.add(twitDate);
            
           
            data.add(row);             
        }//while
    }catch(Exception e){
        e.printStackTrace();
    }finally
    {
       try {
         con.close();
         ps.close();
             rs.close();
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
        
    }
    return data;
}


public boolean insertTwit(TwitDTO dto){
      
    boolean ok = false;
   
    Connection con = null;       //연결
    PreparedStatement ps = null; //명령
   
    try{
       
        con = getConn();
        String sql = null;
        sql = "select post_id from twit";
        ps = con.prepareStatement(sql);
        
        
        sql = "insert into twit(" +
                 "post_id,id,twit_content) "+
                 "values(tmp_seq.NEXTVAL,?,?)";
       
        ps = con.prepareStatement(sql);
        ps.setInt(1, dto.getPost_id());
        ps.setString(2, dto.getId());
        ps.setString(3, dto.getTwit_content());
       
        int r = ps.executeUpdate();

           
       
    }catch(Exception e){
        e.printStackTrace();
    }finally
    {
       try {
         con.close();
         ps.close();
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
        
    }
   
    return ok;
}

public int setTwitId() {
    Connection con = null; 
       PreparedStatement ps = null;
   
   return 0;
   
}




}
