<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%  List list = (List)request.getAttribute("taskResult");
	Entity task; %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="<%= PageDefault.css %>"/>
<title>タスク実績一覧</title>
</head>
<body>
<%@ page import="com.appspot.tommy02.PageDefault" %>
<%= PageDefault.header %>
[ <%= request.getAttribute("taskName") %> ]<br>
<table>
        <thead>
            <tr>
                <th>実施日</th>
                <th>作業量</th>
                <th>作業時間</th>
                <th>作業内容</th>
                <th>変更</th>
                <th>削除</th>
            </tr>
        </thead>
        <tbody>
         	<% for(int i = 0; i < list.size(); i++){
        		task = (Entity)list.get(i); %>
        		<form name="form" action="/member/task/result/list" method="post">
        		<input type="hidden" name="menu" value="update">
        		<input type="hidden" name="taskResultID" value="<%= task.getKey() %>">
        		<input type="hidden" name="key" value="<%= task.getProperty("taskID") %>">
                <tr>
                    <td><%= task.getProperty("workDate") %></td>
                    <td><input type="text" name="workload" value="<%= task.getProperty("workload") %>" size="5"></td>
                    <td><input type="text" name="workTime" value="<%= task.getProperty("workTime") %>" size="5"></td>
                    <td>
                    <textarea name="workMemo" rows="4" cols="50"
                    ><% if(task.getProperty("workMemo") == null || task.getProperty("workMemo") == ""){
                    %>記載なし<% }else{ %><%= task.getProperty("workMemo") %><% } %></textarea></td>
                    <td><input type="submit" value="変更" /></td>
                    <%	StringBuilder urlDelete = new StringBuilder();
                    	urlDelete.append("/member/task/result/list?menu=delete&taskResultID=");
                    	urlDelete.append(task.getKey());
                    	urlDelete.append("&key=");
                    	urlDelete.append(task.getProperty("taskID")); %>
                    <td><a href='<%= urlDelete %>'>削除</a></td>
                </tr>
                </form>
            <% } %>
        </tbody>
    </table>
<br>
</body>
</html>