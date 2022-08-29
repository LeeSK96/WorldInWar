package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import vo.UnitVO;

public class Inven_2DB {
	private static Inven_2DB inven = new Inven_2DB();
	
	private Inven_2DB() {
		if(inven == null) {
			
		}
	}
	
	public static Inven_2DB getWriter() {
		return inven;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	public JSONObject write(int user_idx) {
		JSONObject obj = new JSONObject();
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
		Context init = new InitialContext();
		DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
		conn = ds.getConnection();
		
		String sql = "select distinct unit_idx from inventory where user_idx = ? order by unit_idx";
		System.out.println(user_idx);
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, user_idx);
		rs = pstmt.executeQuery();
		
		
		while(rs.next()) {
			int unit_idx = Integer.parseInt(rs.getString("unit_idx"));
			
			list.add(unit_idx);
		    
		    JSONArray jArray = new JSONArray();//배열이 필요할때
		    for (int i = 0; i < list.size(); i++)//배열
		    {
		    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
		    sObject.put("unit_idx", list.get(i));
		    jArray.add(sObject);
		    }
		    
		    obj.put("result", jArray);//배열을 넣음
		   

		}
		System.out.println(obj.toString());
			}catch(Exception e) {
			   
		   }finally {
				try {
					rs.close();
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
	
	}
	return obj;
		
	}

}
