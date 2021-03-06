package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class EntryServlet extends HttpServlet {

	String status;
	String email;
	String password;
	String name;
	String emailSend;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
			rd.forward(req, resp);
	}

	/* ブラウザからのPOSTリクエストを処理する */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

			AccountBean ac;

			/* パラメータを取得する */
			getRequest(req);

			/* ステータスごとに処理を行う */
			switch(status){
			case "entry": // 入力内容確認処理を行う

				ac = new AccountBean(email, name, password, emailSend);
				req = setRequest(req);

				if(ac.chkEmail() == "OK"&&
					ac.chkPassword() == "OK"&&
					ac.chkUserName() == "OK"){

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry_check.jsp");
					rd.forward(req, resp);

				}else{
					// エラーがある場合のみ、エラーコードを設定
					if(ac.chkEmail() != "OK"){
						req.setAttribute("result_email", ac.chkEmail());}
					if(ac.chkPassword() != "OK"){
						req.setAttribute("result_password", ac.chkPassword());}
					if(ac.chkUserName() != "OK"){
						req.setAttribute("result_name", ac.chkUserName());}

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
					rd.forward(req, resp);

				}

				break;

			case "check": // 登録処理を行う

				ac = new AccountBean(email, name, password, emailSend);
				String insertResult = ac.insert();

				if(insertResult != "OK"){

					req = setRequest(req);

					req.setAttribute("result_email", insertResult);
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
					rd.forward(req, resp);

				}

				TerminalBean login = new TerminalBean();
				String result = login.Login(email,password,"first");

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
					req = setRequest(req);

					RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/entry_finish.jsp");
					rd1.forward(req, resp);
				}

				break;

			case "modify": // 入力内容の修正を行う
				req = setRequest(req);

				RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
				rd2.forward(req, resp);

				break;

			}
	}

	private void getRequest(HttpServletRequest req){

		status = req.getParameter("status");
		email = req.getParameter("email");
		password = req.getParameter("password");
		name = req.getParameter("name");
		if(req.getParameter("emailSend") == null || req.getParameter("emailSend") == "" ||
				req.getParameter("emailSend").equals("NG")){
				emailSend = "NG";
			}else{
				emailSend ="OK";
			}
	}

	private HttpServletRequest setRequest(HttpServletRequest req){

		req.setAttribute("email", email);
		req.setAttribute("password", password);
		req.setAttribute("name", name);
		req.setAttribute("emailSend", emailSend);

		return req;
	}

}
