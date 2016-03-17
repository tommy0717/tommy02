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
<title>新規会員登録　入力内容確認</title>
</head>
<body>
<br>
<br>
新規会員登録　入力内容確認<br>
<br>
以下の内容で登録してよろしいでしょうか。<br>
<br>

◆Eメールアドレス<br>
　<c:out value="${email}" /><br>
◆パスワード<br>
　********
<%-- <c:out value="${password}" /> --%><br>
◆名前<br>
　<c:out value="${name}" /><br>
◆<% if(request.getAttribute("emailSend") != null && request.getAttribute("emailSend").toString() == "OK"){ %>
メールでタスク状況を受け取る
<% }else{ %>メールでタスク状況を受け取らない <% } %><br>
<br>

<form action="/entry" method="post">
<input type="hidden" name="email" value="${email}">
<input type="hidden" name="password" value="${password}">
<input type="hidden" name="name" value="${name}">
<input type="hidden" name="emailSend" value="${emailSend}">
<input type="hidden" name="status" value="check">
<input type="submit" value="登録" />
</form>　

<form action="/entry" method="post">
<input type="hidden" name="email" value="${email}">
<input type="hidden" name="password" value="${password}">
<input type="hidden" name="name" value="${name}">
<input type="hidden" name="emailSend" value="${emailSend}">
<input type="hidden" name="status" value="modify">
<input type="submit" value="修正" />
</form>

<br>
<br>
<br>
　

</body>
</html>