<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="tommy02.css?var=20160214"/>
<title>test</title>
<style type="text/css">

table {
width: 400px;
margin-bottom: 20px;
border: 1px white solid;
border-collapse: collapse;
background-color: #90ee90;
}
td, th {
border: 1px white solid;
}

</style>
</head>
<body>
<%= request.getAttribute("test") %><br>
<br>
<table>
<tr>
<td>データセル1-1</td>
<td>データセル1-2</td>
<td>データセル1-3</td>
</tr>
<tr>
<td>データセル2-1</td>
<td>データセル2-2</td>
<td>データセル2-3</td>
</tr>
<tr>
<td>データセル3-1</td>
<td>データセル3-2</td>
<td>データセル3-3</td>
</tr>
</table>
<br>
</body>
</html>
