<%@page import="org.json.simple.JSONObject"%>
<%@page import="action.Inven_2DB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	
	String user_idx_s = request.getParameter("user_idx");

	if(user_idx_s == null){
		out.println("inven_2");
	}
	
	else{
		int user_idx = Integer.parseInt(user_idx_s); 
		
		Inven_2DB inven = Inven_2DB.getWriter();
		JSONObject json = inven.write(user_idx);
		System.out.println(json);
		out.print(json);		
	}

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>inven_2</title>
</head>
<body>

</body>
</html>