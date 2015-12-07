package com.appspot.tommy02;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class EntryServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

			/*/WEB-INF/index.jspを呼び出す */
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);
	}

	/* ブラウザからのPOSTリクエストを処理する */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

			/* エラー値の初期化 */
			String err_email = "0";
			String err_password = "0";
			String err_name = "0";
			String err_nickname = "0";

			/* パラメータを取得する */
			String status = req.getParameter("status");
			String email = req.getParameter("email");
			String password = req.getParameter("password");
			String name = req.getParameter("name");
			String nickname = req.getParameter("nickname");

			/* ステータスごとに処理を行う */
			switch(status){
			case "entry": // 入力内容確認処理を行う

				/* 必須入力チェックを行う */
				if(email == ""){ err_email = "1"; }
				if(password == ""){ err_password = "1"; }
				if(name == ""){ err_name = "1"; }
				if(nickname == ""){ err_nickname = "1"; }

				if(err_email == "0"){
					/* メールアドレスの正当性チェック */
					String ptnStr = "[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+";
					Pattern ptn =Pattern.compile(ptnStr);
					Matcher mc = ptn.matcher(email);
					if(mc.matches()){
						/* 一致（処理なし） */
					}else{
						/* 一致しない */
						err_email = "2";
					}
				}

				if(err_email == "0"){
					/* 登録済チェック */
					Key key = KeyFactory.createKey("User", email);
					DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//					String test;
					try{
						Entity entity = ds.get(key);
//						Object obj = entity.getProperty("email");
//						test = obj.toString();
						err_email = "3";
					}catch(EntityNotFoundException e){
						/* 処理なし */
					}

				}

				req.setAttribute("email", email);
				req.setAttribute("password", password);
				req.setAttribute("name", name);
				req.setAttribute("nickname", nickname);

				// エラーがある場合のみ、エラーコードを設定
				if(err_email != "0" || err_password != "0" || err_name != "0" || err_nickname != "0"){
					req.setAttribute("err_email", err_email);
					req.setAttribute("err_password", err_password);
					req.setAttribute("err_name", err_name);
					req.setAttribute("err_nickname", err_nickname);

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
					rd.forward(req, resp);

				}else{
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry_check.jsp");
					rd.forward(req, resp);
				}

				break;

			case "check": // 登録処理を行う

				/* データストアに情報を保存する 110ページ参照 */
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				try{
					Entity User = new Entity("User",email);
					User.setProperty("email", email);
					User.setProperty("password", password);
					User.setProperty("name", name);
					User.setProperty("nickname", nickname);
					ds.put(User);

				}catch(Exception e){
					/* エラー */
					email = "登録エラー";
				}

//				********************JDOを使用する場合（パフォーマンスが悪いのでボツ）
//				PersistenceManager pm = PMF.get().getPersistenceManager();
//				try{
//					User user = new User(email, password, name, nickname);
//					pm.makePersistent(user);
//				}finally{
//					pm.close();
//				}
//				********************ここまで

				/* 登録完了ページを出力する */
				req.setAttribute("email", email);
				req.setAttribute("password", password);
				req.setAttribute("name", name);
				req.setAttribute("nickname", nickname);

				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/entry_finish.jsp");
				rd1.forward(req, resp);
				break;

			case "modify": // 入力内容の修正を行う

				req.setAttribute("email", email);
				req.setAttribute("password", password);
				req.setAttribute("name", name);
				req.setAttribute("nickname", nickname);

				RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
				rd2.forward(req, resp);

				break;

			}

	}

}
