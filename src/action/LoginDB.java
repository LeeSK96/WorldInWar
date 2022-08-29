package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class LoginDB {
	
	private static LoginDB loginDB = new LoginDB();
	
	private LoginDB()
	{};
	
	public static LoginDB getWriter()
	{
		return loginDB;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	String idx = "";
	
	public String write(String id, String pw)
	{
		try
		{
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "SELECT * FROM user_info WHERE id = ? AND pw = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			
			while(rs.next())
			{
				idx = rs.getString("idx");
				rs.getString("id");
				rs.getString("pw");
				cnt++;
			}
			
			if(cnt == 1)
			{
				returns = String.format("{res:[{'result':'%s'}, {idx:'%s'}]}", "success", idx);
			}
			else
			{
				returns = String.format("{res:[{'result':'%s'}], idx:'%s'}", "failed", "0");
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return returns;
	}//write

}

