<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
 <%-- JSTLの利用宣言 --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>タスク登録</title>
</head>
<body>
<br>
【タスク登録】<br>
<br>
<% if(request.getAttribute("result") != null){ %>
<font color="red"><%= request.getAttribute("result") %></font><br><% } %>
<% if(request.getAttribute("resultTaskName") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskName") %></font><br><% } %>
<% if(request.getAttribute("resultTaskContent") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskContent") %></font><br><% } %>
<% if(request.getAttribute("resultTaskType") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskType") %></font><br><% } %>
<% if(request.getAttribute("resultTaskPriority") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskPriority") %></font><br><% } %>
<% if(request.getAttribute("resultTaskStart") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskStart") %></font><br><% } %>
<% if(request.getAttribute("resultTaskEnd") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskEnd") %></font><br><% } %>
<% if(request.getAttribute("resultTaskHours") != null){ %>
<font color="red"><%= request.getAttribute("resultTaskHours") %></font><br><% } %>
<% if(request.getAttribute("resultTotal") != null){ %>
<font color="red"><%= request.getAttribute("resultTotal") %></font><br><% } %>
<br>
<form name="form" action="/task" method="post">
タスク名<br>
<input type="text" name="taskName" value="${taskName}"/><br>
タスク内容<br>
<input type="text" name="taskContent" size="50" value="${taskContent}"/><br>
タスク種別（定例／通常）<br>
<!-- set name and onChange -->
<select name="taskType" onChange="selectBunruiA(this)">
	<option value="">（種別）</option>
	<option value="normal">通常</option>
	<option value="routine">定期</option>
</select>
<select name="taskSubType" width="100px">
	<option value="">(周期)</option>
</select><br>
優先度<br>
<select name="taskPriority">
	<option value="high">高</option>
	<option value="middle" selected>中</option>
	<option value="low">低</option>
</select><br>
開始日<br>
<select name="taskStartYear">
<% Date date = new Date();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("M");
	SimpleDateFormat sdf3 = new SimpleDateFormat("d");
%>
<% for(int y = 2010; y <= 2100; y++){
	if(String.valueOf(y).equals(sdf1.format(date))){ %>
<option value="<%= y %>" selected><%= y %></option><% }else{ %>
<option value="<%= y %>"><%= y %></option><% } } %>
</select>年
<select name="taskStartMonth">
<% for(int m = 1; m <= 12; m++){
	if(String.valueOf(m).equals(sdf2.format(date))){ %>
<option value="<%= m %>" selected><%= m %></option><% }else{ %>
<option value="<%= m %>"><%= m %></option><% } } %>
</select>月
<select name="taskStartDay">
<% for(int d = 1; d <= 31; d++){
	if(String.valueOf(d).equals(sdf3.format(date))){ %>
<option value="<%= d %>" selected><%= d %></option><% }else{ %>
<option value="<%= d %>"><%= d %></option><% } } %>
</select>日
<br>
終了日<br>
<select name="taskEndYear">
<% for(int y = 2010; y <= 2100; y++){
	if(String.valueOf(y).equals(sdf1.format(date))){ %>
<option value="<%= y %>" selected><%= y %></option><% }else{ %>
<option value="<%= y %>"><%= y %></option><% } } %>
</select>年
<select name="taskEndMonth">
<% for(int m = 1; m <= 12; m++){
	if(String.valueOf(m).equals(sdf2.format(date))){ %>
<option value="<%= m %>" selected><%= m %></option><% }else{ %>
<option value="<%= m %>"><%= m %></option><% } } %>
</select>月
<select name="taskEndDay">
<% for(int d = 1; d <= 31; d++){
	if(String.valueOf(d).equals(sdf3.format(date))){ %>
<option value="<%= d %>" selected><%= d %></option><% }else{ %>
<option value="<%= d %>"><%= d %></option><% } } %>
</select>日
<br>
予定作業時間<br>
<input type="text" name="taskHours" value="${taskHours}" size="5"/>時間
<input type="text" name="taskMinutes" value="${taskMinutes}" size="5"/>分<br>
作業量（作成する成果物の総数など）<br>
<input type="text" name="taskTotal" value="${taskTotal}" size="5"/><br>
<br>
<input type="hidden" name="menu" value="entry">
<input type="submit" value="タスク登録" />
</form>
<br>
<a href='/mypage'>マイページに戻る</a><br>
<br>
<br>

<script type="text/javascript" language="JavaScript">
<!--

// 次の分類(分類Aごとの分類Bリスト)を定義
    var bunruiB = new Array();
    bunruiB["normal"]= new Array("選択不要");
    bunruiB["normal_value"]= new Array("");
    bunruiB["routine"]= new Array("年次","月次","日次");
    bunruiB["routine_value"]= new Array("year","month","day");

    // 分類Aの選択リストを作成
    createSelection( form.elements['taskType'], "(種別)", bunruiA, bunruiA);

    ////////////////////////////////////////////////////
    //
    // 選択ボックスに選択肢を追加する関数
    //	引数: ( selectオブジェクト, value値, text値)
    function addSelOption( selObj, myValue, myText )
    {
        selObj.length++;
        selObj.options[ selObj.length - 1].value = myValue ;
        selObj.options[ selObj.length - 1].text  = myText;

    }
    /////////////////////////////////////////////////////
    //
    //	選択リストを作る関数
    //	引数: ( selectオブジェクト, 見出し, value値配列 , text値配列 )
    //
    function createSelection( selObj, midashi, aryValue, aryText )
    {
        selObj.length = 0;
        addSelOption( selObj, '', midashi);
        // 初期化
        for( var i=0; i < aryValue.length; i++)
        {
            addSelOption ( selObj , aryValue[i], aryText[i]);
        }
    }
    ///////////////////////////////////////////////////
    //
    // 	分類Aが選択されたときに呼び出される関数
    //
    function selectBunruiA(obj)
    {
        // 選択肢を動的に生成
        createSelection(form.elements['taskSubType'], "(周期)",
                bunruiB[obj.value+'_value'], bunruiB[obj.value]);

    }

//-->
</script>

</body>
</html>
