<%@page import="action.LocDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	request.setCharacterEncoding("UTF-8");

	String gps_x = request.getParameter("gps_x");
	String gps_y = request.getParameter("gps_y");
	
	LocDB loc = LocDB.getWriter();
	String res = loc.write(gps_x, gps_y);
	out.println(res);
%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>