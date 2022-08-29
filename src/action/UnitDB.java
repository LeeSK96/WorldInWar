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

public class UnitDB {
	private static UnitDB unit = new UnitDB();
	
	private UnitDB() {
		if(unit == null) {
			
		}
	}
	
	public static UnitDB getWriter() {
		return unit;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	

	

	
	public JSONObject write() {
		JSONObject obj = new JSONObject();
		try {
			ArrayList<UnitVO> ulist = new ArrayList<UnitVO>();
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "select * from unit order by idx ASC";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
			int idx = Integer.parseInt(rs.getString("idx"));
			String unit_name = rs.getString("unit_name");
			int unit_attack = Integer.parseInt(rs.getString("unit_attack"));
			int unit_price = Integer.parseInt(rs.getString("unit_price"));
			int unit_defence = Integer.parseInt(rs.getString("unit_defence"));
			String unit_img = rs.getString("unit_img");
			int can_attack = Integer.parseInt(rs.getString("can_attack"));
			int can_defence = Integer.parseInt(rs.getString("can_defence"));
			


				UnitVO vo = new UnitVO();
				vo.setIdx(idx);
				vo.setUnit_name(unit_name);
				vo.setUnit_price(unit_price);
				vo.setUnit_attack(unit_attack);
				vo.setUnit_defence(unit_defence);
				vo.setUnit_img(unit_img);
				vo.setCan_attack(can_attack);
				vo.setCan_defence(can_defence);
				System.out.println(vo.getUnit_price());
				System.out.println(vo.getUnit_name());
				System.out.println(vo.getUnit_attack());
				System.out.println(vo.getUnit_defence());
				System.out.println(vo.getCan_attack());
				System.out.println(vo.getCan_defence());
				ulist.add(vo);
	
			    
			    JSONArray jArray = new JSONArray();//배열이 필요할때
			    for (int i = 0; i < ulist.size(); i++)//배열
			    {
			    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
			    sObject.put("unit_name", ulist.get(i).getUnit_name());
			    sObject.put("unit_price", ulist.get(i).getUnit_price());
			    sObject.put("unit_attack", ulist.get(i).getUnit_attack());
			    sObject.put("unit_defence", ulist.get(i).getUnit_defence());
			    sObject.put("unit_img",ulist.get(i).getUnit_img());
			    sObject.put("idx",ulist.get(i).getIdx());
			    sObject.put("can_attack", ulist.get(i).getCan_attack());
			    sObject.put("can_defence", ulist.get(i).getCan_defence());
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



	

		





