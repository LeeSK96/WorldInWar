<%@page import="action.InvenDB"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String user_idx_s = request.getParameter("user_idx");
	String unit_idx_s = request.getParameter("unit_idx");
	String count_s = request.getParameter("count");
	
	if(user_idx_s == null){
		out.println("inven");
	}
	
	else{
		int user_idx = Integer.parseInt(user_idx_s);
		int unit_idx = Integer.parseInt(unit_idx_s);
		int count = Integer.parseInt(count_s);
	
		InvenDB join = InvenDB.getWriter();
		String res = join.write(user_idx,unit_idx,count);
		out.print(res);
	}
	//안드로이드로 보내줌(JSON타입)



%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>inven</title>
</head>
<body>
</body>
</html>