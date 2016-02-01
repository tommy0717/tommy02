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
<%@ page import="java.util.Calendar" %>

<%  List list = (List)request.getAttribute("task");
	int allPages = Integer.parseInt(request.getAttribute("allPages").toString());
	Entity task; %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>タスク一覧</title>
</head>
<body>
＜タスク一覧＞　　　　　<a href='/mypage'>＞＞マイページに戻る</a><br>
<table border="1">
        <thead>
            <tr>
                <th>タスク名</th>
                <th>内容</th>
                <th>開始日</th>
                <th>終了日</th>
                <th>総作業量</th>
                <th>作業実績</th>
                <th>予定消化率（上段）<bh>／実績消化率（下段）</th>
                <th>実績<br>登録</th>
                <th>実績<br>一覧</th>
                <th>変更</th>
                <th>削除</th>
            </tr>
        </thead>
        <tbody>
         	<% for(int i = 0; i < list.size(); i++){
        		task = (Entity)list.get(i);

        		String strEnd = task.getProperty("taskEnd").toString();
        		String[] e = strEnd.split("/");
        		int e1 = Integer.parseInt(e[0]);
        		int e2 = Integer.parseInt(e[1]);
        		int e3 = Integer.parseInt(e[2]);

        		BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
        		Calendar calEnd = Calendar.getInstance();
        		Calendar calToday = Calendar.getInstance();

        		calEnd.set(e1, e2 - 1, e3);

        		float days;
        		if(calEnd.compareTo(calToday) > 0){
            		float all = Integer.parseInt(task.getProperty("taskDays").toString());
            		float left = bdc.countDays(calToday, calEnd);
            		days = ( all - left ) / all * 100;
        		}else{ days = 100; }

        		float tt = Integer.parseInt(task.getProperty("taskTotal").toString());
        		float wl = Integer.parseInt(task.getProperty("taskWorkload").toString());

        		float now = wl / tt * 100; %>
          	<form name="form" action="/task" method="post">
        	<input type="hidden" name="menu" value="update">
        	<input type="hidden" name="taskID" value="<%= task.getKey() %>">
                <tr>
                    <td><input type="text" name="taskName" value="<%= task.getProperty("taskName") %>"/></td>
                    <td><input type="text" name="taskContent" value="<%= task.getProperty("taskContent") %>"/></td>
                    <td><%= task.getProperty("taskStart") %></td>
                    <td><%= strEnd %></td>
                    <td><input type="text" name="taskTotal" value="<%= task.getProperty("taskTotal") %>" size="5"/></td>
                    <td><%= task.getProperty("taskWorkload") %></td>
                    <td><img src="http://chart.apis.google.com/chart?
									chs=300x60
									&chd=t:<%= Math.floor(days) %>|<%= Math.floor(now) %>
									&cht=bhg
									&chg=10,100,3,3
									&chxt=x
									&chbh=10,5
									&chco=ff0000,0000ff"></td>
					<%	StringBuilder url2 = new StringBuilder();
                    	url2.append("/task?menu=result&key=");
                    	url2.append(task.getKey()); %>
                    <td><a href='<%= url2 %>'>実績<br>登録</a></td>
                    <%	StringBuilder url3 = new StringBuilder();
                    	url3.append("/task?menu=resultList&key=");
                    	url3.append(task.getKey()); %>
                    <td><a href='<%= url3 %>'>実績<br>一覧</a></td>
                    <td><input type="hidden" name="pages" value="<%= request.getAttribute("pages") %>"/>
                    	<input type="submit" value="変更" /></td>
        			<%	StringBuilder url = new StringBuilder();
                    	url.append("/task?menu=delete&key=");
                    	url.append(task.getKey());
                    	url.append("&pages=");
                    	url.append(request.getAttribute("pages")); %>
                    <td><a href='<%= url %>'>削除</a></td>

                </tr>
            </form>
            <% } %>
        </tbody>
    </table>
<br>
<% if(allPages > 1){
	for(int j=1; j <= allPages; j++){ %>
　<a href='/task?menu=list&pages=<%= j %>'><%= j %></a>　
<% } } %>
<br>
</body>
</html>