package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UserDB {
	
	private static UserDB user = new UserDB();
	
	private UserDB() {
		if(user == null) {
			
		}
	}
	
	public static UserDB getWriter() {
		return user;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	String nickname = "";
	String color = "";
	String level = "";
	
	public String write(String idx)
	{
		try
		{
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			
			String sql = "select * from user_info where idx=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, idx);
			
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			
			while(rs.next())
			{
				nickname = rs.getString("nickname");
				color = rs.getString("color");
				level = rs.getString("user_level");
				cnt++;
			}
			
			if(cnt == 1)
			{
				returns = String.format("{res:[{'nickname':'%s'}, {'color':'%s'}, {'level':'%s'}]}", nickname, color, level);
			}
			else
			{
				//returns = String.format("{res:[{'result':'%s'}], idx:'%s'}", "failed", "0");
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
