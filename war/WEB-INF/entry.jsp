<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
<% if(request.getAttribute("result_nickname") != null){ %>
<font color="red"><%= request.getAttribute("result_nickname") %></font><br><% } %>
<br>
<form action="/entry" method="post">
Eメールアドレス<br>
<input type="text" name="email" value="${email}"/><br>
パスワード<br>
<input type="password" name="password" value="${password}" /><br>
氏名<br>
<input type="text" name="name" value="${name}"/><br>
ニックネーム<br>
<input type="text" name="nickname" value="${nickname}" /><br>
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
