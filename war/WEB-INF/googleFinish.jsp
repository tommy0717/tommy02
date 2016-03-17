<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.appspot.tommy02.GoogleCalendar" %>
<% GoogleCalendar gc = new GoogleCalendar();%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="tommy02.css?var=20160214"/>
<title>test</title>
</head>
<body>
<br>
<br><%= request.getAttribute("msg") %>
<br>
<br><a href='/member/mypage'>◆マイページ</a>
</body>
</html>
