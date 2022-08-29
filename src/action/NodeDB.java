package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import vo.NodeVO;

public class NodeDB {
	private static NodeDB node = new NodeDB();
	
	private NodeDB() {
		if(node == null) {
			
		}
	}
	
	public static NodeDB getWriter() {
		return node;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	//DB에 데이터추가
	public List<String> write() {
		
		List<String> node_list = new ArrayList<String>();
		
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "select * from node";
			
			//쿼리문 수행
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();// sql 실행
			
	
			while(rs.next()) {
	            
	            int idx = rs.getInt("idx");
	            double gps_x = rs.getDouble("gps_x");
	            double gps_y = rs.getDouble("gps_y");
	            String node_name = rs.getString("node_name");
	            int current_owner = rs.getInt( "current_owner");
	            
	            returns = String.format("{res:[{'idx':'%d','gps_x':'%f','gps_y':'%f','node_name':'%s','current_owner':'%d'}]}", idx, gps_x, gps_y, node_name, current_owner);
	            node_list.add(returns);
	         }
			
			System.out.println(node_list);
			
		} catch (Exception e) {//서버에서 오류가 났을때 들어옴
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return node_list;//JSON타입으로 반환
	}//write
	

}
