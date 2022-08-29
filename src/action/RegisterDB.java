package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class RegisterDB {
	
	public static RegisterDB registerDB = new RegisterDB();
	
	private RegisterDB() {
	
	}
	
	public static RegisterDB getWriter()
	{
		return registerDB;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	public String write(String id, String pw, String email, String name, String color) {
		
		try {
			
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "insert into user_info values( seq_user_idx.nextVal, ?, ?, ?, ?, ?, 0, 1)";
			
			//쿼리문 수행
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.setString(3, email);
			pstmt.setString(4, name);
			pstmt.setString(5, color);
			pstmt.executeUpdate();
			
			returns = String.format("{res:[{'result':'%s'}]}", "success");
			
		} catch (Exception e) {
			returns = String.format("{res:[{'result':'%s'}]}", "failed");
			e.printStackTrace();
		}finally {
			
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		
		return returns;
		
	}//write()
	

}
