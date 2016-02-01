package com.appspot.tommy02;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class TaskServlet extends HttpServlet {

	//タスク一覧に表示するタスク件数（１ページあたり）
	int displayNumber = 10;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String strTaskID;
		Long lngTaskID;
		int index1;
		int index2;


		String menu = req.getParameter("menu");
		if(menu == null){
			//例外処理
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}

		switch(menu){
		case "entry": //タスク登録

			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
			rd.forward(req, resp);
			break;

		case "delete": //タスク削除

			TaskBean task = new TaskBean(lngGetID(req.getParameter("key")));
			String resultDelete = task.delete();

			if(resultDelete == "OK"){
				//既存タスクを取得
				SearchResult sr = taskSearch(req);
				req.setAttribute("allPages", sr.getAllPages());
		        req.setAttribute("task", sr.getList());
		        req.setAttribute("pages", req.getParameter("pages"));

				//画面遷移
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/task_list.jsp");
				rd1.forward(req, resp);

			}else{
				req.setAttribute("errMsg", resultDelete);
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/task_list.jsp");
				rd1.forward(req, resp);
			}

		case "list": //タスクの一覧表示

			//既存タスクを取得
				SearchResult sr = taskSearch(req);
				req.setAttribute("allPages", sr.getAllPages());
		        req.setAttribute("task", sr.getList());
		        req.setAttribute("pages", req.getParameter("pages"));

				//画面遷移
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/task_list.jsp");
				rd1.forward(req, resp);

			break;

		case "result": //実績入力

			strTaskID = strGetID(req.getParameter("key"));

			req.setAttribute("taskID", strTaskID);
			RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/task_mod.jsp");
			rd2.forward(req, resp);

			break;

		case "resultDelete": //実績削除

			Long taskResultID = lngGetID(req.getParameter("taskResultID"));

			TaskResultBean trt = new TaskResultBean(taskResultID);
			String resultDelete2 = trt.delete();

			if(resultDelete2 == "OK"){
				menu = "resultList";
			}else{
				req.setAttribute("errMsg", resultDelete2);
				RequestDispatcher rd3 = getServletContext().getRequestDispatcher("/WEB-INF/taskResultList.jsp");
				rd3.forward(req, resp);
			}

		case "resultList": //実績一覧

			lngTaskID = lngGetID(req.getParameter("key"));
	        List list1 = taskSearch(lngTaskID);

	        Key taskKey = KeyFactory.createKey("TASK", lngTaskID);
	    	DatastoreService ds1 = DatastoreServiceFactory.getDatastoreService();
	        try {
				Entity task1 = ds1.get(taskKey);
				req.setAttribute("taskName", task1.getProperty("taskName").toString());

			} catch (EntityNotFoundException e) {
				req.setAttribute("taskName", "タスク名の取得に失敗しました。");
			}

	        req.setAttribute("taskResult", list1);

			//画面遷移
			RequestDispatcher rd4 = getServletContext().getRequestDispatcher("/WEB-INF/taskResultList.jsp");
			rd4.forward(req, resp);

			break;

		}
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		Long taskID;
		String taskName;
		String taskContent;
		String taskTotal;
		TaskBean task;

		String menu = req.getParameter("menu");
		if(menu == null){
			//例外処理
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}

		switch(menu){
		case "entry":
			//タスクを登録する。
			taskName = req.getParameter("taskName");
			taskContent = req.getParameter("taskContent");
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

			taskTotal = req.getParameter("taskTotal");


			task = new TaskBean(taskName, taskContent, taskType,
					taskSubType,taskPriority, taskStartYear, taskStartMonth, taskStartDay,
					taskEndYear, taskEndMonth, taskEndDay, taskHours, taskMinutes,
					getUserID(req),taskTotal);

			//タスクを登録する。
			if(task.insert() == "OK"){
				req.setAttribute("result", "登録完了");
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
				rd.forward(req, resp);

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

				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
				rd.forward(req, resp);
			}

			break;

		case "result": //タスク登録

			taskID = Long.parseLong(req.getParameter("taskID").toString());
			String workYear = req.getParameter("workYear").toString();
			String workMonth = req.getParameter("workMonth").toString();
			String workDay = req.getParameter("workDay").toString();
			String workload = req.getParameter("workload").toString();
			String workHours = req.getParameter("workHours").toString();
			String workMinutes = req.getParameter("workMinutes").toString();

			TaskResultBean trb = new TaskResultBean(taskID, workYear, workMonth, workDay,
					  								workload, workHours, workMinutes);

			String insertResult = trb.insert();

			if(insertResult == "OK"){
				req.setAttribute("result", "登録完了");
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_mod.jsp");
				rd.forward(req, resp);
			}else{
				req.setAttribute("result", insertResult);
				if(trb.getResultWorkDate() != "OK"){ req.setAttribute("resultWorkDate", trb.getResultWorkDate()); }
				if(trb.getResultWorkload() != "OK"){ req.setAttribute("resultWorkload", trb.getResultWorkload()); }
				if(trb.getResultWorkTime() != "OK"){ req.setAttribute("resultWorkTime", trb.getResultWorkTime()); }

				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_mod.jsp");
				rd.forward(req, resp);

			}

			break;

		case "update": //タスク更新

			taskID = lngGetID(req.getParameter("taskID"));

			taskName = req.getParameter("taskName");
			taskContent = req.getParameter("taskContent");
			taskTotal = req.getParameter("taskTotal");

			task = new TaskBean(taskID);
			task.setTaskName(taskName);
			task.setTaskContent(taskContent);
			task.setTaskTotal(taskTotal);

			if(task.update() == "OK"){
				SearchResult sr = taskSearch(req);
				req.setAttribute("task", sr.getList());
				req.setAttribute("allPages", sr.getAllPages());
				req.setAttribute("pages", req.getParameter("pages"));
			}else{
				req.setAttribute("errMsg", "タスクが更新できませんでした。");
			}

			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_list.jsp");
			rd.forward(req, resp);

			break;

		}
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

/**
 * セッション情報からuserIDを取得します。
 * @return String userID
 */
private String getUserID(HttpServletRequest req){
	String userID;
	HttpSession ss = req.getSession();
	String token = ss.getAttribute("TOKEN").toString();
	Key loginKey = KeyFactory.createKey("LOGIN", token);
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		try {
			Entity login = ds.get(loginKey);
			userID = (String)login.getProperty("USER_ID");
		} catch (EntityNotFoundException e) {
			userID = "NG";
		}

	return userID;
}

private List taskSearch(Long taskID){

	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    // Queryクラスのインスタンスを生成
    // 引き数にカインド名を指定
    Query query = new Query("taskResult");

    // QueryクラスのaddFilterメソッドを用いて条件を指定
    query.setFilter(new Query.FilterPredicate("taskID", FilterOperator.EQUAL, taskID));
    query.addSort("workDate");

    // 作成したクエリからPrepareQueryクラスのオブジェクトを生成
    PreparedQuery pQuery = ds.prepare(query);

    FetchOptions fetch =FetchOptions.Builder.withOffset(0);
    List list = pQuery.asList(fetch);

	return list;
}

private SearchResult taskSearch(HttpServletRequest req){

	String userID = getUserID(req);
	int pages;

	try{ pages = Integer.parseInt(req.getParameter("pages"));
	}catch(NumberFormatException e){ pages = 1; }

	int start = ( pages - 1 ) * displayNumber;

	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    // Queryクラスのインスタンスを生成
    // 引き数にカインド名を指定
    Query query = new Query("TASK");

    // QueryクラスのaddFilterメソッドを用いて条件を指定
    query.setFilter(new Query.FilterPredicate("userID", FilterOperator.EQUAL, userID));
    query.addSort("taskStart");

    // 作成したクエリからPrepareQueryクラスのオブジェクトを生成
    PreparedQuery pQuery = ds.prepare(query);

    FetchOptions fetch =FetchOptions.Builder.withOffset(start).limit(displayNumber);
    FetchOptions fetchAll =FetchOptions.Builder.withOffset(0);

    List list = pQuery.asList(fetch);
    int allNumber = pQuery.countEntities(fetchAll);

    //必要なページ数の計算
    float wk1 = allNumber;
	float wk2 = displayNumber;
	int allPages = (int)Math.ceil( wk1 / wk2 );

    SearchResult sr = new SearchResult(list, allNumber, allPages);

	return sr;
}

}
