package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class LocOwnerDB {
		
	private static LocOwnerDB loc_owner = new LocOwnerDB();
	
	private LocOwnerDB() {
		if(loc_owner == null) {
			
		}
	}
	
	public static LocOwnerDB getWriter() {
		return loc_owner;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	public String write(String current_owner)
	{
		try
		{
			System.out.println(current_owner);
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "select nickname, color from user_info where idx = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, current_owner);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				String nickname = rs.getString("nickname");
				String color = rs.getString("color");
				returns = String.format("{res:[{'nickname':'%s'},{'color':'%s'}]}", nickname, color);
				System.out.println(returns);
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
