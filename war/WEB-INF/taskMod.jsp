<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.appspot.tommy02.PageDefault" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="<%= PageDefault.css %>"/>
<title>タスク実績入力</title>
</head>
<body>
<br>
[ <%= request.getAttribute("taskName") %> :
<%= request.getAttribute("taskWorkload") %> / <%= request.getAttribute("taskTotal") %> ]<br>
<% if(request.getAttribute("result") != null){ %><br>
<font color="red"><%= request.getAttribute("result") %></font><br><% } %>
<% if(request.getAttribute("resultWorkDate") != null){ %><br>
<font color="red"><%= request.getAttribute("resultWorkDate") %></font><br><% } %>
<% if(request.getAttribute("resultWorkload") != null){ %><br>
<font color="red"><%= request.getAttribute("resultWorkload") %></font><br><% } %>
<% if(request.getAttribute("resultWorkTime") != null){ %><br>
<font color="red"><%= request.getAttribute("resultWorkTime") %></font><br><% } %>
<br>
<form name="form" action="/member/task/result/add" method="post">
実施日<br>
<select name="workYear">
<% Date date = new Date();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("M");
	SimpleDateFormat sdf3 = new SimpleDateFormat("d");
%>
<% for(int y = 2010; y <= 2100; y++){
	if(String.valueOf(y).equals(sdf1.format(date))){ %>
<option value="<%= y %>" selected><%= y %></option><% }else{ %>
<option value="<%= y %>"><%= y %></option><% } } %>
</select>年
<select name="workMonth">
<% for(int m = 1; m <= 12; m++){
	if(String.valueOf(m).equals(sdf2.format(date))){ %>
<option value="<%= m %>" selected><%= m %></option><% }else{ %>
<option value="<%= m %>"><%= m %></option><% } } %>
</select>月
<select name="workDay">
<% for(int d = 1; d <= 31; d++){
	if(String.valueOf(d).equals(sdf3.format(date))){ %>
<option value="<%= d %>" selected><%= d %></option><% }else{ %>
<option value="<%= d %>"><%= d %></option><% } } %>
</select>日
<br>
作業量<br>
<input type="text" name="workload" value="${workload}" size="5"/><br>
作業時間<br>
<input type="text" name="workHours" value="${workHours}" size="5"/>時間
<input type="text" name="workMinutes" value="${workMinutes}" size="5"/>分<br>
作業内容<br>
<textarea name="workMemo" rows="4" cols="50"></textarea>
<br>
<input type="hidden" name="menu" value="entry">
<input type="hidden" name="taskID" value="<%= request.getAttribute("taskID") %>">
<input type="submit" value="実績登録" />
</form>
<br>
<br>
<br>
</body>
</html>