<%@page import="action.LocOwnerDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	request.setCharacterEncoding("UTF-8");

	String current_owner = request.getParameter("current_owner");
	
	LocOwnerDB loc_owner = LocOwnerDB.getWriter();
	String res = loc_owner.write(current_owner);
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