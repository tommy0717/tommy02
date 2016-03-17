package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TaskDetailPageFactory {

	TaskDetailPageFactory(){

	}

	public void forwardTaskDetailPage(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String menu = req.getParameter("menu");
		Long taskID = lngGetID(req.getParameter("taskID"));

		TaskBean task = new TaskBean(taskID);

		if(menu != null && menu.equals("change")){

			task.get();
			task.setTaskName(req.getParameter("taskName"));
			task.setTaskContent(req.getParameter("taskContent"));
			task.setTaskStart(req.getParameter("taskStartYear"), req.getParameter("taskStartMonth"),
								req.getParameter("taskStartDay"));
			task.setTaskEnd(req.getParameter("taskEndYear"), req.getParameter("taskEndMonth"),
					req.getParameter("taskEndDay"));
			task.setTaskMinutes(req.getParameter("taskHours"), req.getParameter("taskMinutes"));
			task.setTaskTotal(req.getParameter("taskTotal"));

			task.update();

		}

		task.get();
		req.setAttribute("taskName", task.getTaskName());
		req.setAttribute("taskContent", task.getTaskContent());
		req.setAttribute("taskStartYear", task.getTaskStartYear());
		req.setAttribute("taskStartMonth", task.getTaskStartMonth());
		req.setAttribute("taskStartDay", task.getTaskStartDay());
		req.setAttribute("taskEndYear", task.getTaskEndYear());
		req.setAttribute("taskEndMonth", task.getTaskEndMonth());
		req.setAttribute("taskEndDay", task.getTaskEndDay());

		int hours = task.getTaskMinutes() / 60;
		int Minutes = task.getTaskMinutes() % 60;

		req.setAttribute("taskHours", hours);
		req.setAttribute("taskMinutes", Minutes);
		req.setAttribute("taskTotal", task.getTaskTotal());
		req.setAttribute("taskID", req.getParameter("taskID"));

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/taskDetail.jsp");
		rd.forward(req, resp);

	}

	private long lngGetID(String strID){

		int index1 = strID.indexOf("(");
		int index2 = strID.indexOf(")");
		if(index1 > 0 && index2 >0){
			strID = strID.substring(index1+1, index2);
		}
		Long ID = Long.parseLong(strID);
		return ID;
	}

}
