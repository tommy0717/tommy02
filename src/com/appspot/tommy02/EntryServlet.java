package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

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

			/* パラメータを取得する */
			String status = req.getParameter("status");
			String email = req.getParameter("email");
			String password = req.getParameter("password");
			String name = req.getParameter("name");
			String nickname = req.getParameter("nickname");

			/* ステータスごとに処理を行う */
			switch(status){
			case "entry": // 入力内容確認処理を行う

				ValidationBean VB = new ValidationBean();
				String result_email = VB.emailCheck(email);
				String result_password = VB.passwordCheck(password);
				String result_name = VB.nameCheck(name);
				String result_nickname = VB.nicknameCheck(nickname);

				req.setAttribute("email", email);
				req.setAttribute("password", password);
				req.setAttribute("name", name);
				req.setAttribute("nickname", nickname);

				if(result_email == "OK"&&
					result_password == "OK"&&
					result_name == "OK" &&
					result_nickname =="OK"){

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry_check.jsp");
					rd.forward(req, resp);

				}else{
					// エラーがある場合のみ、エラーコードを設定
					if(result_email != "OK"){
						req.setAttribute("result_email", result_email);}
					if(result_password != "OK"){
						req.setAttribute("result_password", result_password);}
					if(result_name != "OK"){
						req.setAttribute("result_name", result_name);}
					if(result_nickname != "OK"){
						req.setAttribute("result_nickname", result_nickname);}

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
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

				LoginBean login = new LoginBean();
				String result = login.Login(email,password);

				switch(result){
				case "NG_userid":
					//Userid違い（ありえない）
					break;
				case "NG_password":
					//password違い（ありえない）
					break;
				default:
					/* ログイントークンをCookieに格納する */
					Cookie cookie = new Cookie("TOKEN", result);
					cookie.setMaxAge(60*60*24*14); //２週間
					resp.addCookie(cookie);

					/* ログイントークンをセッションに格納する */
					//セッション取得
					HttpSession session = req.getSession(true);

					//既存セッション破棄
					session.invalidate();

					//新規セッションを開始
					HttpSession newSession = req.getSession(true);

					//セッションに値を格納
					newSession.setAttribute("TOKEN", result);
					newSession.setAttribute("STATUS", "login");

					/* 登録完了ページを出力する */
					req.setAttribute("email", email);
					req.setAttribute("password", password);
					req.setAttribute("name", name);
					req.setAttribute("nickname", nickname);

					RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/entry_finish.jsp");
					rd1.forward(req, resp);
				}

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
