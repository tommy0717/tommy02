package com.appspot.tommy02;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class CronTaskWorkDaysAdd  extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {


		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    // Queryクラスのインスタンスを生成
	    // 引き数にカインド名を指定
	    Query query = new Query("TASK");

	    // 作成したクエリからPrepareQueryクラスのオブジェクトを生成
	    PreparedQuery pQuery = ds.prepare(query);
		FetchOptions fetch =FetchOptions.Builder.withOffset(0).limit(500);

	    List taskList = pQuery.asList(fetch);

	    if(taskList.size() > 0){
	    	Entity task;
		    for(int i = 0; i < taskList.size(); i++){
		    	task = (Entity)taskList.get(i);

		    	TaskBean tb = new TaskBean();
		    	tb.taskWorkDaysAdd(task.getKey());

		    }
	    }




	}
}
