<%@page import="org.json.simple.JSONObject"%>
<%@page import="vo.UnitVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="action.UnitDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	UnitDB unit = UnitDB.getWriter();
	JSONObject json = unit.write();
	out.print(json);

	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>test</title>
</head>
<body>
<div>
토라이
</div>

</body>
</html>