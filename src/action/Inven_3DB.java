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

public class Inven_3DB {
	private static Inven_3DB inven3 = new Inven_3DB();
	
	private Inven_3DB() {
		if(inven3 == null) {
			
		}
	}
	
	public static Inven_3DB getWriter() {
		return inven3;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	

	

	
	public JSONObject write(int uidx) {
		JSONObject obj = new JSONObject();
		try {
			ArrayList<UnitVO> ulist = new ArrayList<UnitVO>();
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "select * from unit where idx = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,uidx);
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
	
			    
			    JSONArray jArray = new JSONArray();//????????? ????????????
			    for (int i = 0; i < ulist.size(); i++)//??????
			    {
			    JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
			   
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
			    
			    obj.put("result", jArray);//????????? ??????
			   

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


	

		





