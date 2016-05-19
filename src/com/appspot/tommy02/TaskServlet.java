package com.appspot.tommy02;

import java.io.IOException;
import java.util.Collections;
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
	int displayNumber = 20;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String menu = req.getParameter("menu");
		if(menu == null){ indexForward(req, resp); }

		switch(menu){
		case "entry": //タスク登録
			taskAddForward("display", req, resp);
			break;

		case "delete": //タスク削除
			taskListForward("delete", req, resp);
			break;

		case "list": //タスクの一覧表示
			taskListForward("list", req, resp);
			break;

		case "result": //実績入力
			taskModForward("display", req, resp);
			break;

		case "resultDelete": //実績削除
			taskResultListForward("delete", req, resp);
			break;

		case "resultList": //実績一覧
			taskResultListForward("list", req, resp);
			break;

		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String menu = req.getParameter("menu");
		if(menu == null){ indexForward(req, resp); }

		switch(menu){
		case "entry":
			taskAddForward("entry", req, resp);
			break;

		case "update": //タスク更新
			taskListForward("update", req, resp);
			break;

		case "result": //タスク登録
			taskModForward("entry", req, resp);
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
//    query.addSort("taskStart");

    if(req.getParameter("filter1") != null && req.getParameter("filter1").equals("checked")){

    }else{
    	float max = 100;
    	query.setFilter(new Query.FilterPredicate("taskPercentage", FilterOperator.LESS_THAN, max));
    }

    // 作成したクエリからPrepareQueryクラスのオブジェクトを生成
    PreparedQuery pQuery = ds.prepare(query);

    FetchOptions fetch =FetchOptions.Builder.withOffset(start).limit(displayNumber);
    FetchOptions fetchAll =FetchOptions.Builder.withOffset(0);

    List list = pQuery.asList(fetch);
    Collections.sort(list, new TaskComparator());
    int allNumber = pQuery.countEntities(fetchAll);

    //必要なページ数の計算
    float wk1 = allNumber;
	float wk2 = displayNumber;
	int allPages = (int)Math.ceil( wk1 / wk2 );

    SearchResult sr = new SearchResult(list, allNumber, allPages);

	return sr;
}

private void indexForward(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{
	RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
	rd.forward(req, resp);
}

private void taskAddForward(String menu, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{

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

		String taskSubTypeDetail = ""; /* 後で見直し */

		TaskBean task = new TaskBean(taskName, taskContent, taskType,
				taskSubType,taskPriority, taskStartYear, taskStartMonth, taskStartDay,
				taskEndYear, taskEndMonth, taskEndDay, taskHours, taskMinutes,
				getUserID(req),taskTotal, taskSubTypeDetail);

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

	RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/taskAdd.jsp");
	rd.forward(req, resp);

}

private void taskModForward(String menu, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{

	Long taskID = null;

	if(menu.equals("display")){
		String strTaskID = strGetID(req.getParameter("key"));
		taskID = Long.parseLong(strTaskID);

	}else if(menu.equals("entry")){
		taskID = Long.parseLong(req.getParameter("taskID").toString());
		String workYear = req.getParameter("workYear").toString();
		String workMonth = req.getParameter("workMonth").toString();
		String workDay = req.getParameter("workDay").toString();
		String workload = req.getParameter("workload").toString();
		String workHours = req.getParameter("workHours").toString();
		String workMinutes = req.getParameter("workMinutes").toString();
		String workMemo = req.getParameter("workMemo").toString();
		String userID = getUserID(req);

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

	RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/taskMod.jsp");
	rd.forward(req, resp);

}

private void taskListForward(String menu, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{

	if(menu.equals("list")){
        req.setAttribute("filter1", req.getParameter("filter1"));

	}else if(menu.equals("delete")){
		TaskBean task = new TaskBean(lngGetID(req.getParameter("key")));
		String resultDelete = task.delete();

		if(resultDelete != "OK"){
			req.setAttribute("errMsg", resultDelete);
		}

	}else if(menu.equals("update")){
		TaskBean task = new TaskBean(lngGetID(req.getParameter("taskID")));
		task.setTaskName(req.getParameter("taskName"));
		task.setTaskContent(req.getParameter("taskContent"));
		task.setTaskTotal(req.getParameter("taskTotal"));

		task.setTaskStart(req.getParameter("taskStartYear"), req.getParameter("taskStartMonth"),
						  req.getParameter("taskStartDay"));

		task.setTaskEnd(req.getParameter("taskEndYear"), req.getParameter("taskEndMonth"),
						req.getParameter("taskEndDay"));

		if(task.update() == "OK"){
			req.setAttribute("filter1", req.getParameter("filter1"));
		}else{
			req.setAttribute("errMsg", "タスクが更新できませんでした。");
		}
	}

	SearchResult sr = taskSearch(req);
    req.setAttribute("task", sr.getList());
    req.setAttribute("pages", req.getParameter("pages"));
	req.setAttribute("allPages", sr.getAllPages());

	RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/taskList.jsp");
	rd.forward(req, resp);

}

private void taskResultListForward(String menu, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{

	String resultDelete = "";

	if(menu.equals("delete")){
		Long taskResultID = lngGetID(req.getParameter("taskResultID"));

		TaskResultBean trt = new TaskResultBean(taskResultID);
		resultDelete = trt.delete();

		if(!(resultDelete.equals("OK"))){
			req.setAttribute("errMsg", resultDelete);
		}
	}

	if(menu.equals("list") || resultDelete.equals("OK")){
		Long taskID = lngGetID(req.getParameter("key"));
        List list = taskSearch(taskID);

        Key taskKey = KeyFactory.createKey("TASK", taskID);
    	DatastoreService ds1 = DatastoreServiceFactory.getDatastoreService();
        try {
			Entity task = ds1.get(taskKey);
			req.setAttribute("taskName", task.getProperty("taskName").toString());

		} catch (EntityNotFoundException e) {
			req.setAttribute("taskName", "タスク名の取得に失敗しました。");
		}

        req.setAttribute("taskResult", list);
	}

	RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/taskResultList.jsp");
	rd.forward(req, resp);

}

}
