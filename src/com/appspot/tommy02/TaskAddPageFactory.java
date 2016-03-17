package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TaskAddPageFactory {

	public TaskAddPageFactory(){

	}

	public void forwardTaskAddPage(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String menu = "";
		if(req.getParameter("menu") != null){ menu = req.getParameter("menu"); }

		if(menu.equals("entry")){
			//タスクを登録する。
			String taskName = req.getParameter("taskName");
			String taskContent = req.getParameter("taskContent");
			String taskType = req.getParameter("taskType");
			String taskSubType = req.getParameter("taskSubType");
			String taskPriority = req.getParameter("taskPriority");

			String taskStartYear = req.getParameter("taskStartYear");
			String taskStartMonth = req.getParameter("taskStartMonth");
			String taskStartDay = req.getParameter("taskStartDay");

			String taskEndYear = req.getParameter("taskEndYear");
			String taskEndMonth = req.getParameter("taskEndMonth");
			String taskEndDay = req.getParameter("taskEndDay");

			String taskHours = req.getParameter("taskHours");
			String taskMinutes = req.getParameter("taskMinutes");

			String taskTotal = req.getParameter("taskTotal");

			TaskBean task = new TaskBean(taskName, taskContent, taskType,
					taskSubType,taskPriority, taskStartYear, taskStartMonth, taskStartDay,
					taskEndYear, taskEndMonth, taskEndDay, taskHours, taskMinutes,
					userID,taskTotal);

			//タスクを登録する。
			if(task.insert() == "OK"){
				req.setAttribute("result", "登録完了");

			}else{
				//エラーあり タスク入力画面に遷移
				if(task.chkTaskName() != "OK"){
					req.setAttribute("resultTaskName", task.chkTaskName()); }
				if(task.chkTaskContent() != "OK"){
					req.setAttribute("resultTaskContent", task.chkTaskContent()); }
				if(task.chkTaskType() != "OK"){
					req.setAttribute("resultTaskType", task.chkTaskType()); }
				if(task.chkTaskPriority() != "OK"){
					req.setAttribute("resultTaskPriority", task.chkTaskPriority()); }
				if(task.chkTaskStart() != "OK"){
					req.setAttribute("resultTaskStart", task.chkTaskStart()); }
				if(task.chkTaskEnd() != "OK"){
					req.setAttribute("resultTaskEnd", task.chkTaskEnd()); }
				if(task.chkTaskMinutes() != "OK"){
					req.setAttribute("resultTaskHours", task.chkTaskMinutes()); }
				if(task.chkTaskTotal() != "OK"){
					req.setAttribute("resultTotal", task.chkTaskTotal()); }

				req.setAttribute("taskName", taskName);
				req.setAttribute("taskContent", taskContent);
				req.setAttribute("taskHours", taskHours);
				req.setAttribute("taskMinutes", taskMinutes);
				req.setAttribute("taskTotal", taskTotal);
			}
		}

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/taskAdd.jsp");
		rd.forward(req, resp);

	}


}
