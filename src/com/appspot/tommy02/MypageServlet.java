package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MypageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		//必要情報を取得する（リクエスト＆セッション＆クッキー）
		String menu = req.getParameter("menu");
		if (menu == null || menu.length() == 0){
		      menu = "default";
		}

		HttpSession session = req.getSession();
		Cookie ck[] = req.getCookies();
		String cok_token = "nothing";

		if (ck != null){
		    for (int i = 0 ; i < ck.length ; i++){
		      if (ck[i].getName().equals("TOKEN")){
		        cok_token = ck[i].getValue();
		      }
		    }
		  }

		switch(menu){
		case "logout":
			//ログアウト処理
			AccountBean login = new AccountBean();
			login.setSes_token((String)session.getAttribute("TOKEN"));
			login.Logout();         //ログインテーブルの削除
			session.invalidate();   //既存セッション破棄
			//クッキーは削除する？

			/* index.jspを呼び出す */
			RequestDispatcher rd4 = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd4.forward(req, resp);

		default:
			//ログイン認証
			AccountBean login1 = new AccountBean((String)session.getAttribute("STATUS"),
											(String)session.getAttribute("TOKEN"),
											cok_token);
			String result = login1.LoginCheck();

			switch(result){
			case "OK_ses": //ログインOK（セッションあり）
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/mypage.jsp");
				rd.forward(req, resp);
				break;
			case "NG_ses": //ログインNG（セッションあり）
				session.invalidate();                           //既存セッション破棄
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
				rd1.forward(req, resp);
				break;
			case "OK_cok": //ログインOK（セッションなし）
				session.invalidate();                           //既存セッション破棄
				HttpSession newSession = req.getSession(true);  //新規セッション開始
				newSession.setAttribute("TOKEN", cok_token);    //セッションに値を格納
				newSession.setAttribute("STATUS", "login");     //セッションに値を格納

				RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/mypage.jsp");
				rd2.forward(req, resp);
				break;
			case "NG_cok": //ログインNG（セッションなし）
				RequestDispatcher rd3 = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
				rd3.forward(req, resp);
				break;

			}

		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String err_msg;

		//必要情報を取得する（リクエスト＆セッション＆クッキー）
		String menu = req.getParameter("menu");
		String userid = req.getParameter("email");
		String password = req.getParameter("password");
		if (menu == null || menu.length() == 0){ menu = "default"; }
		if (userid == null || userid.length() == 0){ userid = "default"; }
		if (password == null || password.length() == 0){ password = "default"; }

		if(menu.equals("login")){

			AccountBean login = new AccountBean();
			String result = login.Login(userid,password);

			switch(result){
			case "NG_userid":
				err_msg = "ユーザーIDに誤りがあります。";
				req.setAttribute("err_msg", err_msg);
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
				rd1.forward(req, resp);
				break;
			case "NG_password":
				err_msg = "パスワードに誤りがあります。";
				req.setAttribute("err_msg", err_msg);
				RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
				rd2.forward(req, resp);
				break;
			default:
				/* ログイントークンをCookieに格納する */
				Cookie cookie = new Cookie("TOKEN", result);
				cookie.setMaxAge(60*60*24*14); //２週間
				resp.addCookie(cookie);

				/* ログイントークンをセッションに格納する */
				HttpSession session = req.getSession();         //既存セッション取得
				session.invalidate();                           //既存セッション破棄
				HttpSession newSession = req.getSession(true);  //新規セッションを開始

				//セッションに値を格納
				newSession.setAttribute("TOKEN", result);
				newSession.setAttribute("STATUS", "login");

				/* 登録完了ページを出力する */
				RequestDispatcher rd3 = getServletContext().getRequestDispatcher("/WEB-INF/mypage.jsp");
				rd3.forward(req, resp);
			}

		}else{
			RequestDispatcher rd4 = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd4.forward(req, resp);
		}

	}

}
