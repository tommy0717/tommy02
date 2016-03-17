<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.appspot.tommy02.BusinessDayCalculator" %>
<%@ page import="com.appspot.tommy02.DefaultJapaneseDayOffResolver" %>
<%@ page import="com.appspot.tommy02.DayOffResolver" %>
<%@ page import="com.appspot.tommy02.JapaneseHolidayUtils" %>
<%@ page import="com.appspot.tommy02.JapaneseNationalHoliday" %>
<%@ page import="com.appspot.tommy02.PageDefault" %>
<%@ page import="java.util.Calendar" %>
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
                <th>内容</th>
                <th>開始日</th>
                <th>終了日</th>
                <th>総作業量</th>
                <th>作業実績</th>
                <th>予定消化率（上段）／実績消化率（下段）</th>
                <th>実績<br>登録</th>
                <th>実績<br>一覧</th>
                <th>変更</th>
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
        			tableColor1 = "bgcolor=\"red\"";
        			tableColor2 = "style=\"background-color:red;\"";
        		}else if(Float.parseFloat(task.getProperty("taskWorkPercentage").toString()) > 80){
        			tableColor1 = "bgcolor=\"yellow\"";
        			tableColor2 = "style=\"background-color:yellow;\"";
        		}
        		%>
          	<form name="form" action="/member/task/list" method="post">
        	<input type="hidden" name="menu" value="update">
        	<input type="hidden" name="taskID" value="<%= task.getKey() %>">
                <tr <%= tableColor1 %>>
                    <td data-label="タスク名"><input type="text" name="taskName" value="<%= task.getProperty("taskName") %>"
                    <%= tableColor2 %>/></td>
                    <td data-label="内容">
                    <textarea name="taskContent" rows="4" cols="20"
                    <%= tableColor2 %>><%= task.getProperty("taskContent") %></textarea>
                    </td>
                    <td data-label="開始">
                    <select name="taskStartYear" <%= tableColor2 %>>
					<%
					String s[] = task.getProperty("taskStart").toString().split("/");
					int sYear = Integer.parseInt(s[0]);
					int sMonth = Integer.parseInt(s[1]);
					int sDay = Integer.parseInt(s[2]);
					%>
					<% for(int y = 2010; y <= 2100; y++){
						if(y == sYear){ %>
					<option value="<%= y %>" selected><%= y %></option><% }else{ %>
					<option value="<%= y %>"><%= y %></option><% } } %>
					</select>年<br>
					<select name="taskStartMonth" <%= tableColor2 %>>
					<% for(int m = 1; m <= 12; m++){
						if(m == sMonth){ %>
					<option value="<%= m %>" selected><%= m %></option><% }else{ %>
					<option value="<%= m %>"><%= m %></option><% } } %>
					</select>月
					<select name="taskStartDay" <%= tableColor2 %>>
					<% for(int d = 1; d <= 31; d++){
						if(d == sDay){ %>
					<option value="<%= d %>" selected><%= d %></option><% }else{ %>
					<option value="<%= d %>"><%= d %></option><% } } %>
					</select>日
                    </td>
                    <td data-label="終了">
                    <select name="taskEndYear" <%= tableColor2 %>>
					<%
					String e[] = task.getProperty("taskEnd").toString().split("/");
					int eYear = Integer.parseInt(e[0]);
					int eMonth = Integer.parseInt(e[1]);
					int eDay = Integer.parseInt(e[2]);
					%>
					<% for(int y = 2010; y <= 2100; y++){
						if(y == eYear){ %>
					<option value="<%= y %>" selected><%= y %></option><% }else{ %>
					<option value="<%= y %>"><%= y %></option><% } } %>
					</select>年<br>
					<select name="taskEndMonth" <%= tableColor2 %>>
					<% for(int m = 1; m <= 12; m++){
						if(m == eMonth){ %>
					<option value="<%= m %>" selected><%= m %></option><% }else{ %>
					<option value="<%= m %>"><%= m %></option><% } } %>
					</select>月
					<select name="taskEndDay" <%= tableColor2 %>>
					<% for(int d = 1; d <= 31; d++){
						if(d == eDay){ %>
					<option value="<%= d %>" selected><%= d %></option><% }else{ %>
					<option value="<%= d %>"><%= d %></option><% } } %>
					</select>日
                    </td>
                    <td data-label="総作業量"><input type="text" name="taskTotal" value="<%= task.getProperty("taskTotal") %>" size="5"
                    <%= tableColor2 %>/></td>
                    <td data-label="作業実績"><%= task.getProperty("taskWorkload") %></td>
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
                    <td data-label="実績登録"><a href='<%= url2 %>'>■</a></td>
                    <%	StringBuilder url3 = new StringBuilder();
                    	url3.append("/member/task/result/list?menu=list&key=");
                    	url3.append(task.getKey()); %>
                    <td data-label="実績一覧"><a href='<%= url3 %>'>■</a></td>
                    <td><input type="hidden" name="pages" value="<%= request.getAttribute("pages") %>"/>
                    	<% if(filter1 != ""){ %><input type="hidden" name="filter1" value="checked"/><% } %>
                    	<input type="submit" value="変更" /></td>
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
	for(int j=1; j <= allPages; j++){ %>
　<a href='/member/task/list?menu=list&pages=<%= j %>'><%= j %></a>　
<% } } %>
<br>
</body>
</html>