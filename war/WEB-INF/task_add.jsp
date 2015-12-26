<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>task_add</title>
</head>
<body>
<br>
【タスク登録】<br>
<br>
<form action="/task" method="post">
タスク名<br>
<input type="text" name="task_name" value="${task_name}"/><br>
タスク内容<br>
<input type="text" name="task_content" value="${task_content}"/><br>
タスク種別（定例・通常）<br>
<input type="text" name="task_type" value="${task_type}"/><br>
優先度<br>
<input type="text" name="task_priority" value="${task_priority}"/><br>
開始日<br>
<input type="text" name="task_start" value="${task_start}"/><br>
終了日<br>
<input type="text" name="task_end" value="${task_end}"/><br>
予定作業時間<br>
<input type="text" name="task_hours" value="${task_hours}"/><br>
<br>
<input type="hidden" name="menu" value="entry">
<input type="submit" value="タスク登録" />
</form>
<br>
【実績】<br>
作業日<br>
作業時間<br>
進捗率（％）<br>
備考<br>
<br>
</body>
</html>
