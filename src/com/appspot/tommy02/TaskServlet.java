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
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String menu = req.getParameter("menu");
		if(menu == null){
			//例外処理
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}

		switch(menu){
		case "entry":
			//新規入力画面へ遷移
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
			rd.forward(req, resp);
			break;

		case "update":
			//既存タスクを取得

			HttpSession ss = req.getSession();
			Key loginKey = KeyFactory.createKey("LOGIN", (String)ss.getAttribute("TOKEN"));
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			try {
				Entity login = ds.get(loginKey);
				String userId = (String)login.getProperty("USER_ID");

			// 1.DatastoreServiceクラスのインスタンスを生成
//	        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	        // 2.Queryクラスのインスタンスを生成
	        // 引き数にカインド名を指定
	        Query query = new Query("TASK");

	        // 3.QueryクラスのaddFilterメソッドを用いて条件を指定
	        query.setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, userId));

	        // 4.作成したクエリからPrepareQueryクラスのオブジェクトを生成
	        PreparedQuery pQuery = ds.prepare(query);

	        FetchOptions fetch =FetchOptions.Builder.withOffset(0);
	        List list = pQuery.asList(fetch);

	        req.setAttribute("task", list);

			//画面遷移
			RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/task_mod.jsp");
			rd1.forward(req, resp);

			break;
			}catch(EntityNotFoundException e){

			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String menu = req.getParameter("menu");
		if(menu == null){
			//例外処理
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}

		switch(menu){
		case "entry":
			//タスクを登録する。
			String task_name = req.getParameter("task_name");
			String task_content = req.getParameter("task_content");
			String task_type = req.getParameter("task_type");
			String task_type1 = req.getParameter("task_type1");
			String task_priority = req.getParameter("task_priority");

			StringBuilder buf1 = new StringBuilder();
			buf1.append(req.getParameter("task_start_year"));
			buf1.append("/");
			buf1.append(req.getParameter("task_start_month"));
			buf1.append("/");
			buf1.append(req.getParameter("task_start_day"));
			String task_start = buf1.toString();

			StringBuilder buf2 = new StringBuilder();
			buf2.append(req.getParameter("task_end_year"));
			buf2.append("/");
			buf2.append(req.getParameter("task_end_month"));
			buf2.append("/");
			buf2.append(req.getParameter("task_end_day"));
			String task_end = buf2.toString();

			String task_hours = req.getParameter("task_hours");
			String task_minutes = req.getParameter("task_minutes");

			TaskBean task = new TaskBean(task_name, task_content, task_type,
					task_type1,task_priority, task_start, task_end, task_hours, task_minutes);

			HttpSession ss = req.getSession();
			Key loginKey = KeyFactory.createKey("LOGIN", (String)ss.getAttribute("TOKEN"));
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			try {
				Entity login = ds.get(loginKey);

				//タスクを登録する。
				if(task.insert((String)login.getProperty("USER_ID")) == "OK"){
					req.setAttribute("result", "登録完了");
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
					rd.forward(req, resp);

				}else{
					//エラーあり タスク入力画面に遷移
					if(task.getResult_task_name() != "OK"){
						req.setAttribute("result_task_name", task.getResult_task_name()); }
					if(task.getResult_task_content() != "OK"){
						req.setAttribute("result_task_content", task.getResult_task_content()); }
					if(task.getResult_task_type() != "OK"){
						req.setAttribute("result_task_type", task.getResult_task_type()); }
					if(task.getResult_task_priority() != "OK"){
						req.setAttribute("result_task_priority", task.getResult_task_priority()); }
					if(task.getResult_task_start() != "OK"){
						req.setAttribute("result_task_start", task.getResult_task_start()); }
					if(task.getResult_task_end() != "OK"){
						req.setAttribute("result_task_end", task.getResult_task_end()); }
					if(task.getResult_task_hours() != "OK"){
						req.setAttribute("result_task_hours", task.getResult_task_hours()); }

					req.setAttribute("task_name", task_name);
					req.setAttribute("task_content", task_content);
					req.setAttribute("task_hours", task_hours);
					req.setAttribute("task_minutes", task_minutes);

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
					rd.forward(req, resp);
				}

			} catch (EntityNotFoundException e) {
				//ログインテーブルの取得エラー
				req.setAttribute("result", e);
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/task_add.jsp");
				rd.forward(req, resp);

			}

		}

	}

}
