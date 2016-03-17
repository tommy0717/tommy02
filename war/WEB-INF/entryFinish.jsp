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
<title>新規会員登録　登録完了</title>
</head>
<body>
<br>
<br>
新規会員登録　登録完了<br>
<br>
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
<br>
<br>
<br>
<%= session.getAttribute("STATUS") %><br>
<%= session.getAttribute("TOKEN") %><br>
<FORM NAME="form" METHOD="GET" ACTION="/member/mypage">
<A HREF="javascript:document.form.submit()">マイページへ</A>
</FORM>
</body>
</html>