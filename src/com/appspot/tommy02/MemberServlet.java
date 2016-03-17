package com.appspot.tommy02;

import java.io.IOException;

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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class MemberServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		main(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		main(req, resp);

	}

	private void main(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		String urlParts[] = req.getRequestURI().split("/");

		if(!(urlParts[1].equals("member"))){
			//エラー
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}

		String userID = getUserID(req);
		switch(urlParts[2]){
		case "mypage":
			MypageFactory mf = new MypageFactory();
			mf.forwardMypage(userID, req, resp);
			break;

		case "task":
			switch(urlParts[3]){
			case "add":
				TaskAddPageFactory ta = new TaskAddPageFactory();
				ta.forwardTaskAddPage(userID, req, resp);
				break;

			case "list":
				TaskListPageFactory tl = new TaskListPageFactory();
				tl.forwardTaskListPage(userID, req, resp);
				break;

			case "detail":
				TaskDetailPageFactory td = new TaskDetailPageFactory();
				td.forwardTaskDetailPage(userID, req, resp);
				break;

			case "result":
				switch(urlParts[4]){
				case "add":
					TaskResultAddPageFactory tra = new TaskResultAddPageFactory();
					tra.forwardTaskResultAdd(userID, req, resp);
					break;

				case "list":
					TaskResultListPageFactory trl = new TaskResultListPageFactory();
					trl.forwardTaskResultList(userID, req, resp);;
					break;
				}
			}

		case "google":

		case "settings":
			SettingsPageFactory sp = new SettingsPageFactory();
			sp.forwardSettingsPage(userID, req, resp);
			break;

		case "logout":
			//ログアウト処理
			HttpSession session = req.getSession();
			TerminalBean terminal = new TerminalBean();
			terminal.setSesToken((String)session.getAttribute("TOKEN"));
			terminal.Logout();         //端末テーブルの削除
			session.invalidate();      //既存セッション破棄
			//クッキーは削除する？

			/* index.jspを呼び出す */
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
		}
	}

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

}
