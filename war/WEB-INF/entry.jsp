<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="tommy02.css?var=20160214"/>
<title>新規会員登録　内容入力</title>
</head>
<body>
<br>
<br>
新規会員登録　内容入力<br>
<br>
<% if(request.getAttribute("result_email") != null){ %>
<font color="red"><%= request.getAttribute("result_email") %></font><br><% } %>
<% if(request.getAttribute("result_password") != null){ %>
<font color="red"><%= request.getAttribute("result_password") %></font><br><% } %>
<% if(request.getAttribute("result_name") != null){ %>
<font color="red"><%= request.getAttribute("result_name") %></font><br><% } %>
<br>
<form action="/entry" method="post">
◆Eメールアドレス<br>
<input type="text" name="email" value="${email}"/><br>
◆パスワード<br>
<input type="password" name="password" value="${password}" /><br>
◆名前<br>
<input type="text" name="name" value="${name}"/><br>
<input type="checkbox" name="emailSend" value="OK"
<% if(request.getAttribute("emailSend") != null && request.getAttribute("emailSend").toString() == "OK"){ %>
checked="checked"<% } %>>
メールでタスク状況を受け取る<br>

<br>
<input type="hidden" name="status" value="entry">
<input type="submit" value="会員登録" />
</form>
<br>
<br>
<a href='/index'>トップページに戻る</a><br>
<br>
</body>
</html>
