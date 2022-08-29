<%@page import="action.UnitDB"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="vo.UnitVO"%>
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
<title>unit_market</title>
</head>
<body>
ㅇㅇ
</body>
</html>