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
<title>タスク実績一覧</title>
</head>
<body>
＜タスク実績一覧　：　<%= request.getAttribute("taskName") %>＞　　　　　<a href='/mypage'>＞＞マイページに戻る</a><br>
<table border="1">
        <thead>
            <tr>
                <th>実施日</th>
                <th>作業量</th>
                <th>作業時間</th>
                <th>変更</th>
                <th>削除</th>
            </tr>
        </thead>
        <tbody>
         	<% for(int i = 0; i < list.size(); i++){
        		task = (Entity)list.get(i); %>
                <tr>
                    <td><%= task.getProperty("workDate") %></td>
                    <td><%= task.getProperty("workload") %></td>
                    <td><%= task.getProperty("workTime") %></td>
                    <td>変更</td>
                    <%	StringBuilder urlDelete = new StringBuilder();
                    	urlDelete.append("/task?menu=resultDelete&taskResultID=");
                    	urlDelete.append(task.getKey());
                    	urlDelete.append("&key=");
                    	urlDelete.append(task.getProperty("taskID")); %>
                    <td><a href='<%= urlDelete %>'>削除</a></td>
                </tr>
            <% } %>
        </tbody>
    </table>
<br>
</body>
</html>