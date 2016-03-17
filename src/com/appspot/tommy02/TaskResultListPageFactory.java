package com.appspot.tommy02;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class TaskResultListPageFactory {

	int displayNumber = 20;

	public TaskResultListPageFactory(){

	}

	public void forwardTaskResultList(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String resultUpdate = "";
		String resultDelete = "";
		String menu = req.getParameter("menu");

		if(menu.equals("update")){
			Long taskResultID = lngGetID(req.getParameter("taskResultID"));

			TaskResultBean trt = new TaskResultBean(taskResultID);
			trt.setWorkload(req.getParameter("workload"));
			trt.setWorkMemo(req.getParameter("workMemo"));
			trt.setWorkTime(req.getParameter("workTime"));
			resultUpdate = trt.update();
		}

		if(menu.equals("delete")){
			Long taskResultID = lngGetID(req.getParameter("taskResultID"));

			TaskResultBean trt = new TaskResultBean(taskResultID);
			resultDelete = trt.delete();

			if(!(resultDelete.equals("OK"))){
				req.setAttribute("errMsg", resultDelete);
			}
		}

//		if(menu.equals("list") || resultDelete.equals("OK")){
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
//		}

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/taskResultList.jsp");
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

}
