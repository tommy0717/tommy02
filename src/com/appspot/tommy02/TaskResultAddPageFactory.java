package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TaskResultAddPageFactory {

	public TaskResultAddPageFactory(){

	}

	public void forwardTaskResultAdd(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		Long taskID = null;

		if(req.getParameter("menu").equals("display")){
			String strTaskID = strGetID(req.getParameter("key"));
			taskID = Long.parseLong(strTaskID);

		}else if(req.getParameter("menu").equals("entry")){
			taskID = Long.parseLong(req.getParameter("taskID").toString());
			String workYear = req.getParameter("workYear").toString();
			String workMonth = req.getParameter("workMonth").toString();
			String workDay = req.getParameter("workDay").toString();
			String workload = req.getParameter("workload").toString();
			String workHours = req.getParameter("workHours").toString();
			String workMinutes = req.getParameter("workMinutes").toString();
			String workMemo = req.getParameter("workMemo").toString();

			TaskResultBean trb = new TaskResultBean(taskID, workYear, workMonth, workDay,
					  								workload, workHours, workMinutes, workMemo, userID);

			String insertResult = trb.insert();

			if(insertResult == "OK"){
				req.setAttribute("result", "登録完了");

			}else{
				req.setAttribute("result", insertResult);
				if(trb.getResultWorkDate() != "OK"){ req.setAttribute("resultWorkDate", trb.getResultWorkDate()); }
				if(trb.getResultWorkload() != "OK"){ req.setAttribute("resultWorkload", trb.getResultWorkload()); }
				if(trb.getResultWorkTime() != "OK"){ req.setAttribute("resultWorkTime", trb.getResultWorkTime()); }

				req.setAttribute("workload", workload);
				req.setAttribute("workHours", workHours);
				req.setAttribute("workMinutes", workMinutes);

			}
		}

		TaskBean tb = new TaskBean(taskID);
		tb.get();

		req.setAttribute("taskName", tb.getTaskName());
		req.setAttribute("taskTotal", tb.getTaskTotal());
		req.setAttribute("taskWorkload", tb.getTaskWorkload());
		req.setAttribute("taskMinutes", tb.getTaskMinutes());
		req.setAttribute("taskWorkMinutes", tb.getTaskWorkMinutes());
		req.setAttribute("taskID", taskID);

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/taskMod.jsp");
		rd.forward(req, resp);

	}

	private String strGetID(String strID){

		String ID;
		int index1 = strID.indexOf("(");
		int index2 = strID.indexOf(")");
		if(index1 > 0 && index2 >0){
			ID = strID.substring(index1+1, index2);
		}else{
			ID = strID;
		}

		return ID;
	}

}
