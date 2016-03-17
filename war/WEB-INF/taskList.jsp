<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.appspot.tommy02.PageDefault" %>
<%
List list = (List)request.getAttribute("task");
int allPages = Integer.parseInt(request.getAttribute("allPages").toString());
Entity task;
String filter1 = "";
if(request.getAttribute("filter1") != null && request.getAttribute("filter1").equals("checked")){
	filter1 = "checked=\"checked\"";
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="<%= PageDefault.css %>"/>
<title>タスク一覧</title>
</head>
<body>
<%= PageDefault.header %>
<div style="text-align: right; 	word-spacing: 1em;">
<form name="form" action="/member/task/list" method="get">
<input type="checkbox" name="filter1" value="checked" <%= filter1 %>/>完了分を表示する。
<input type="submit" value="更新" />
</form>
</div>
<table>
        <thead>
            <tr>
                <th>タスク名</th>
                <th>開始日</th>
                <th>終了日</th>
                <th>予定消化率（上段）／実績消化率（下段）</th>
                <th>実績<br>登録</th>
                <th>実績<br>一覧</th>
                <th>詳細</th>
                <th>削除</th>
            </tr>
        </thead>
        <tbody>
         	<% for(int i = 0; i < list.size(); i++){
        		task = (Entity)list.get(i);

        		String tableColor1 = "";
        		String tableColor2 = "";
        		String graphColor = "";
        		if(Float.parseFloat(task.getProperty("taskPercentage").toString()) == 100){
        			tableColor1 = "bgcolor=\"gray\"";
        			tableColor2 = "style=\"background-color:gray;\"";
        			graphColor = "&chf=bg,s,808080|c,s,808080";
        		}else if(Float.parseFloat(task.getProperty("taskWorkPercentage").toString()) == 100){
        			tableColor1 = "bgcolor=\"#ffb6c1\"";
        			tableColor2 = "style=\"background-color:#ffb6c1;\"";
        		}else if(Float.parseFloat(task.getProperty("taskWorkPercentage").toString()) > 80){
        			tableColor1 = "bgcolor=\"#fffacd\"";
        			tableColor2 = "style=\"background-color:#fffacd;\"";
        		}
        		%>
          	<form name="form" action="/member/task/list" method="post">
        	<input type="hidden" name="menu" value="update">
        	<input type="hidden" name="taskID" value="<%= task.getKey() %>">
                <tr <%= tableColor1 %>>
                    <td data-label="タスク名"><%= task.getProperty("taskName") %></td>
                    <td data-label="開始"><%= task.getProperty("taskStart") %></td>
                    <td data-label="終了"><%= task.getProperty("taskEnd") %></td>
                    <td data-label="進捗"><img src="http://chart.apis.google.com/chart?
									chs=300x60
									&chd=t:<%= task.getProperty("taskWorkPercentage") %>|<%= task.getProperty("taskPercentage") %>
									&cht=bhg
									&chg=10,100,3,3
									&chxt=x
									&chbh=10,5
									&chco=ff0000,0000ff
									<%= graphColor %>"></td>
					<%	StringBuilder url2 = new StringBuilder();
                    	url2.append("/member/task/result/add?menu=display&key=");
                    	url2.append(task.getKey()); %>
                    <td data-label="実績登録"><a href='#'
                     onClick="window.open('<%= url2 %>', 'child', 'width=500,height=400');">■</a></td>
                    <%	StringBuilder url3 = new StringBuilder();
                    	url3.append("/member/task/result/list?menu=list&key=");
                    	url3.append(task.getKey()); %>
                    <td data-label="実績一覧"><a href='<%= url3 %>'>■</a></td>
                    <%	StringBuilder url4 = new StringBuilder();
                    	url4.append("/member/task/detail?taskID=");
                    	url4.append(task.getKey()); %>
                    <td data-label="変更"><a href='#'
                     onClick="window.open('<%= url4 %>', 'child', 'width=500,height=400');">■</a></td>
        			<%	StringBuilder url = new StringBuilder();
                    	url.append("/member/task/list?menu=delete&key=");
                    	url.append(task.getKey());
                    	url.append("&pages=");
                    	url.append(request.getAttribute("pages")); %>
                    <td data-label="削除"><a href='<%= url %>'>■</a></td>
                </tr>
            </form>
            <% } %>
        </tbody>
    </table>
<br>
<% if(allPages > 1){
	for(int j=1; j <= allPages; j++){
		if(request.getAttribute("filter1") != null && request.getAttribute("filter1").equals("checked")){
	%>
　<a href='/member/task/list?filter1=checked&menu=list&pages=<%= j %>'><%= j %></a>　
	<% }else{ %>
　<a href='/member/task/list?menu=list&pages=<%= j %>'><%= j %></a>　
	<% } %>
<% } } %>
<br>
</body>
</html>