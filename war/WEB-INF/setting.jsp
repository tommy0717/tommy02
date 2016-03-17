<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.appspot.tommy02.GoogleCalendar" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="<%= PageDefault.css %>"/>
<title>個人設定</title>
</head>
<body>
<%@ page import="com.appspot.tommy02.PageDefault" %>
<%= PageDefault.header %>
<br>
<table>
	<thead>
		<tr bgcolor="#66cdaa">
			<th>項目</th>
			<th>内容</th>
			<th>変更</th>
		</tr>
	</thead>
	<tbody>
		<tr>
		<form name="form" action="/member/settings" method="post">
		<input type="hidden" name="change" value="yes"/>
			<td>名前</td>
			<td><input type="text" name="userName" value="<%= request.getAttribute("userName") %>"/></td>
			<td><input type="submit" value="変更" /></td>
		</form>
		</tr>
		<tr>
		<form name="form" action="/member/settings" method="post">
		<input type="hidden" name="change" value="yes"/>
			<td>パスワード</td>
			<td>※変更がある場合のみ入力してください。<br><input type="password" name="password" value="" /></td>
			<td><input type="submit" value="変更" /></td>
		</form>
		</tr>
		<tr>
			<td>メールアドレス</td>
			<td><%= request.getAttribute("email") %></td>
			<td>変更不可</td>
		</tr>
		<tr>
		<form name="form" action="/member/settings" method="post">
		<input type="hidden" name="change" value="yes"/>
		<input type="hidden" name="emailSend" value="change"/>
			<td>メール送信可否</td>
			<td><%= request.getAttribute("emailSend") %></td>
			<td><input type="submit" value="変更" /></td>
		</form>
		</tr>
		<tr>
		<form name="form" action="/member/settings" method="post">
		<input type="hidden" name="change" value="yes"/>
			<td>メール送信時間</td>
			<td><select name="sendTime">
				<% for(int d = 0; d <= 23; d++){
				if(d == Integer.parseInt(request.getAttribute("sendTime").toString())){ %>
				<option value="<%= d %>" selected><%= d %></option><% }else{ %>
				<option value="<%= d %>"><%= d %></option><% } } %>
				</select>時
			</td>
			<td><input type="submit" value="変更" /></td>
		</form>
		</tr>
		<tr>
			<td>google連携有無</td>
			<td><%= request.getAttribute("google") %></td>
			<td><% if(request.getAttribute("google").equals("設定なし")){
					GoogleCalendar gc = new GoogleCalendar();%>
				<a href='<%= gc.getGoogleOAuthURL() %>'>認証</a>
				<% }else{ %>解除<% } %>
			</td>
		</tr>
	</tbody>
</table>
<br>
</body>
</html>
