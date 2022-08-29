<%@page import="action.UserDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	request.setCharacterEncoding("UTF-8");

	String idx = request.getParameter("idx");
	
	UserDB user = UserDB.getWriter();
	String res = user.write(idx);
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