<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ログイン</title>
</head>
<body>
<br>
<br>
ログイン情報入力<br>
<br>
<% if(request.getAttribute("err_msg") != null){ %>
<%= request.getAttribute("err_msg") %><br><%} %>
<br>
<form action="/mypage" method="post">
Eメールアドレス<br>
<input type="text" name="email" value="${email}"/><br>
パスワード<br>
<input type="password" name="password" value="${password}" /><br>
<br>
<input type="hidden" name="menu" value="login"/>
<input type="submit" value="ログイン" />
</form>
<br>
　<a href='/index'>トップページに戻る</a><br>
<br>
</body>
</html>
