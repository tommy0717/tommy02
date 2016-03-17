package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		loginForward(req, resp);

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

			TerminalBean login = new TerminalBean();
			String result = login.Login(userid,password,"second");

			switch(result){
			case "NG_userid":
				err_msg = "ユーザーIDに誤りがあります。";
				req.setAttribute("err_msg", err_msg);
				loginForward(req, resp);
				break;

			case "NG_password":
				err_msg = "パスワードに誤りがあります。";
				req.setAttribute("err_msg", err_msg);
				loginForward(req, resp);
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

				resp.sendRedirect("/member/mypage");
			}

		}else{
			loginForward(req, resp);

		}
	}

	private void loginForward(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
		rd.forward(req, resp);
	}

}