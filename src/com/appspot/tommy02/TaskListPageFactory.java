package com.appspot.tommy02;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class TaskListPageFactory {

	//タスク一覧に表示するタスク件数（１ページあたり）
	int displayNumber = 20;

	public TaskListPageFactory(){

	}

	public void forwardTaskListPage(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String menu = "";
		if(req.getParameter("menu") != null){ menu = req.getParameter("menu"); }


		if(menu.equals("delete")){
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

		}else{
			req.setAttribute("filter1", req.getParameter("filter1"));
		}


		SearchResult sr = taskSearch(userID, req);
	    req.setAttribute("task", sr.getList());
	    req.setAttribute("pages", req.getParameter("pages"));
		req.setAttribute("allPages", sr.getAllPages());

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/taskList.jsp");
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

	private SearchResult taskSearch(String userID, HttpServletRequest req){

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
//	    query.addSort("taskStart");

	    if(req.getParameter("filter1") != null && req.getParameter("filter1").equals("checked")){

	    }else{
	    	float max = 100;
			CompositeFilter cf = new Query.CompositeFilter(CompositeFilterOperator.AND,Arrays.<Query.Filter>asList(
					new Query.FilterPredicate("userID", FilterOperator.EQUAL, userID),
					new Query.FilterPredicate("taskPercentage", FilterOperator.LESS_THAN, max)));

	    	query.setFilter(cf);
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

}
