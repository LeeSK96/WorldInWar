<%@page import="action.RegisterDB"%>
<%@page import="action.LoginDB"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
    		<%
			request.setCharacterEncoding("UTF-8");
		
			String type = request.getParameter("type");
			String id = request.getParameter("id");
			String pw = request.getParameter("pw");
			
			if( type == null ){
				out.println("login");
			}
			
			else if( type.equals("type_login") ){
				
				LoginDB login = LoginDB.getWriter();
				String res = login.write(id, pw);
				out.println(res);
				
			}
			
			else if (type.equals("type_regi"))
			{
				String email = request.getParameter("email");
				String name = request.getParameter("name");
				String color = request.getParameter("color");
				
				RegisterDB regi = RegisterDB.getWriter();
				String res = regi.write(id, pw, email, name, color);
				out.println(res);
			}
		%>
		
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>login</title>
	</head>
	<body>
	</body>
</html>