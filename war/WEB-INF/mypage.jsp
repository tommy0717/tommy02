<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <%@ page import="com.appspot.tommy02.PageDefault" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="<%= PageDefault.css %>"/>
<title>マイページ</title>
<style type="text/css">
div#main {
	background-color: #fffacd;
	padding: 20px 20px 20px 20px;
	border-width: 0px;
	box-shadow: 5px 5px rgba(0,0,0,0.4);
	margin: 10px 10px 10px 10px;
	word-wrap:break-all;s
}

textarea{
	scrollbar-face-color: #ffffff;
	scrollbar-track-color: #ffb6c1;
	scrollbar-arrow-color: #ffffff;
	scrollbar-highlight-color: #ffb6c1;
	scrollbar-shadow-color: #ffb6c1;
	scrollbar-3dlight-color: #ffffff;
	scrollbar-darkshadow-color: #ffffff;

	background-color: #ffb6c1;
	border-width: 0px;
}
</style>
</head>
<body>
<%= PageDefault.header %>
<br>
<% if(request.getAttribute("todaySchedule") != null &&
	!(request.getAttribute("todaySchedule").toString().equals(""))){ %>
<div id='main' style="width: 500px; float: left; background-color: #fffacd;">
<%= request.getAttribute("todaySchedule") %></div><% } %>
<% if(request.getAttribute("todayTask") != null &&
	!(request.getAttribute("todayTask").toString().equals(""))){ %>
<div id='main' style="width: 500px; float: left; background-color: #fffacd;">
<%= request.getAttribute("todayTask") %></div><% } %>
<% if(request.getAttribute("todayTaskResult") != null &&
	!(request.getAttribute("todayTaskResult").toString().equals(""))){ %>
<div id='main' style="width: 500px; float: left; background-color: #87cefa;">
<%= request.getAttribute("todayTaskResult") %></div><% } %>
<% if(request.getAttribute("yesterdayTaskResult") != null &&
	!(request.getAttribute("yesterdayTaskResult").toString().equals(""))){ %>
<div id='main' style="width: 500px; float: left; background-color: #87cefa;">
<%= request.getAttribute("yesterdayTaskResult") %></div><% } %>
<div id='main' style="width: 500px; float: left; background-color: #ffb6c1;">
<form name="form" action="/member/mypage" method="post">
<input type="hidden" name="menu" value="update"/>
＜備忘メモ＞　　<input type="submit" value="更新" /><br>
<textarea name="accountMemo" rows="5" cols="60"><%= request.getAttribute("accountMemo") %></textarea></form>
</div>
</body>
</html>