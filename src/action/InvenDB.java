package action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class InvenDB {
	private static InvenDB inven = new InvenDB();
	
	private InvenDB() {
		if(inven == null) {
			
		}
	}
	
	public static InvenDB getWriter() {
		return inven;
	}
	
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs = null;
	String returns = "";
	
	//DB에 데이터추가
	public String write(int user_idx, int unit_idx , int count) {
		
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle_test");
			conn = ds.getConnection();
			
			String sql = "insert into inventory values(seq_inven_idx.NEXTVAL, ?, ?, ? ,? ,?)";
			
			//쿼리문 수행
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_idx);
			pstmt.setInt(2, unit_idx);
			pstmt.setInt(3, count);
			pstmt.setInt(4, 0);
			pstmt.setString(5, "0");
			pstmt.executeUpdate();
			
			returns = String.format("{'res' : [{'result' : '%s'}]}", "success");
			
		} catch (Exception e) {//서버에서 오류가 났을때 들어옴
			returns = String.format("{'res' : [{'result' : '%s'}]}", "fail");
		}finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returns;//JSON타입으로 반환
	}//write
	

}
