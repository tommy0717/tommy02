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
public class IndexServlet extends HttpServlet {
	/* ブラウザからのGETリクエストを処理する */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String menu = req.getParameter("menu");
		if(menu != null){

			switch(menu){
			case "entry":
				/*/WEB-INF/entry.jspを呼び出す */
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/entry.jsp");
				rd.forward(req, resp);
				break;

			case "login":
				//自動ログイン確認
				//必要情報を取得する（リクエスト＆セッション＆クッキー）
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

				//ログイン認証
				AccountBean login1 = new AccountBean((String)session.getAttribute("STATUS"),
												(String)session.getAttribute("TOKEN"),
												cok_token);
				String result = login1.LoginCheck();

				switch(result){
				case "OK_ses": //ログインOK（セッションあり）
					RequestDispatcher rd4 = getServletContext().getRequestDispatcher("/WEB-INF/mypage.jsp");
					rd4.forward(req, resp);
					break;
				case "NG_ses": //ログインNG（セッションあり）
					session.invalidate();                           //既存セッション破棄
					RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
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
					RequestDispatcher rd3 = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
					rd3.forward(req, resp);
					break;
				}

			case "contact":
				/*/WEB-INF/contact.jspを呼び出す */
				RequestDispatcher rd2 = getServletContext().getRequestDispatcher("/WEB-INF/contact.jsp");
				rd2.forward(req, resp);
				break;
			}

		}else{
			/*/WEB-INF/index.jspを呼び出す */
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(req, resp);

		}

	}

}
