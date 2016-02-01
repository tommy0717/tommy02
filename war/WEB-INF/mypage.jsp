	<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>マイページ</title>
</head>
<body>
<a href='/task?menu=entry'>新規タスク登録</a><br>
<br>
<a href='/task?menu=list&pages=1'>タスク編集／実績登録</a><br>
<br>
退会<br>
<br>
　<a href='/mypage?menu=logout'>ログアウト</a><br>
</body>
</html>