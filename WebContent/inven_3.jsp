<%@page import="org.json.simple.JSONObject"%>
<%@page import="action.Inven_3DB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String idx_s = request.getParameter("idx");

	if(idx_s == null){
		out.println("inven_3");
	}
	
	else{
   		int idx = Integer.parseInt(idx_s);

		Inven_3DB inven3 = Inven_3DB.getWriter();
		JSONObject json = inven3.write(idx);
		System.out.println(json);
		out.print(json);		
	}
		
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>inven_3</title>
</head>
<body>

</body>
</html>