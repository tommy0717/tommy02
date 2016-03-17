package com.appspot.tommy02;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MypageFactory {

	public MypageFactory(){
	}

	public void forwardMypage(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		AccountBean ac = new AccountBean(userID);

		if(req.getParameter("menu") != null && req.getParameter("menu").equals("update")){
			ac.setAccountMemo(req.getParameter("accountMemo"));
			ac.update();
		}

		ac.get();
		req.setAttribute("accountMemo", ac.getAccountMemo());

		req.setAttribute("todaySchedule", todaySchedule(userID));
		req.setAttribute("todayTask", todayTask(userID));
		req.setAttribute("todayTaskResult", todayTaskResult(userID));
		req.setAttribute("yesterdayTaskResult", yesterdayTaskResult(userID));

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/mypage.jsp");
		rd.forward(req, resp);
	}

	private String todaySchedule(String userid){
		StringBuilder today = new StringBuilder();

		AccountBean ac = new AccountBean(userid);
		ac.get();
		if(ac.getRefreshToken() != null && ac.getRefreshToken() != ""){
			GoogleCalendar googleCalendar = new GoogleCalendar();
			List<String> todayTaskList = googleCalendar.todayTask(ac.getRefreshToken());

			if(todayTaskList.size() > 0){
				today.append("＜今日の予定＞");
				today.append("<br>");
				for(int i = 0; i < todayTaskList.size(); i++){
					today.append(todayTaskList.get(i));
					today.append("<br>");
				}
//				today.append("<br>");
			}
		}
		return today.toString();
	}

	private String todayTask(String userid){
		StringBuilder today = new StringBuilder();
		TaskBean tb = new TaskBean();

		List<String> todayTask = tb.todayTask(userid);
		if(todayTask.size() > 0){
			today.append("＜今日のタスク＞");
			today.append("<br>");

			for(int i = 0; i < todayTask.size(); i++){
				today.append(todayTask.get(i));
				today.append("<br>");
			}
//			today.append("<br>");
		}
		return today.toString();
	}

	private String todayTaskResult(String userid){
		StringBuilder today = new StringBuilder();
		TaskResultBean trb = new TaskResultBean();

		List<String> todayTaskResult = trb.todayTaskResult(userid, 0);
		if(todayTaskResult.size() > 0){
			today.append("＜今日の実績＞");
			today.append("<br>");

			for(int i = 0; i < todayTaskResult.size(); i++){
				today.append(todayTaskResult.get(i));
				today.append("<br>");
			}
//			today.append("<br>");
		}
		return today.toString();
	}

	private String yesterdayTaskResult(String userid){
		StringBuilder yesterday = new StringBuilder();
		TaskResultBean trb = new TaskResultBean();

		List<String> yesterdayTaskResult = trb.todayTaskResult(userid, -1);
		if(yesterdayTaskResult.size() > 0){
			yesterday.append("＜昨日の実績＞");
			yesterday.append("<br>");

			for(int i = 0; i < yesterdayTaskResult.size(); i++){
				yesterday.append(yesterdayTaskResult.get(i));
				yesterday.append("<br>");
			}
//			yesterday.append("<br>");
		}
		return yesterday.toString();
	}


}