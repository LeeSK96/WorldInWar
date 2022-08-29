<%@page import="vo.NodeVO"%>
<%@page import="java.util.List"%>
<%@page import="action.NodeDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	NodeDB node = NodeDB.getWriter();
	List<String> node_list = node.write();
	out.print(node_list);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>node</title>
</head>
<body>

</body>
</html>