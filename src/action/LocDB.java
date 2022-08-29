package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class LocDB {
	
	private static LocDB loc = new LocDB();
	
	private LocDB() {
		if(loc == null) {
			
		}
	}
	
	public static LocDB getWriter() {
		return loc;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	public String write(String gps_x, String gps_y)
	{
		try
		{
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			
			String sql = "select * from node where gps_x=? and gps_y=?";
			
			pstmt = conn.prepareStatement(sql);
			System.out.println("넘어온 위도 경도 :" + gps_x + " / " + gps_y);
			pstmt.setString(1, gps_x);
			pstmt.setString(2, gps_y);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				String node_name = rs.getString("node_name");
				String current_owner = rs.getString("current_owner");
				returns = String.format("{res:[{'node_name':'%s'},{'current_owner':'%s'}]}", node_name, current_owner);
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
