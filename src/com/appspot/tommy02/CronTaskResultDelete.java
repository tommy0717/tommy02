package com.appspot.tommy02;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
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

public class CronTaskResultDelete extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		// Queryクラスのインスタンスを生成
		// 引き数にカインド名を指定
		Query query = new Query("taskResult");

		// QueryクラスのaddFilterメソッドを用いて条件を指定
		//    query.setFilter(new Query.FilterPredicate("userID", FilterOperator.EQUAL, userID));
		query.addSort("workDate",Query.SortDirection.ASCENDING);

		// 作成したクエリからPrepareQueryクラスのオブジェクトを生成
		PreparedQuery pQuery = ds.prepare(query);

		FetchOptions fetch =FetchOptions.Builder.withOffset(0).limit(100);
		//    FetchOptions fetchAll =FetchOptions.Builder.withOffset(0);

		List list = pQuery.asList(fetch);
		 //   int allNumber = pQuery.countEntities(fetchAll);

		Entity task;

		for(int i = 0; i < list.size(); i++){
			task = (Entity)list.get(i);
			Key key = KeyFactory.createKey("TASK", (Long)task.getProperty("taskID"));

			try {
				ds.get(key);
			} catch (EntityNotFoundException e) {
				//該当タスクがない為、実績を削除する。
				ds.delete(task.getKey());
			}


		}

	}
}
