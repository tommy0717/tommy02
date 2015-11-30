package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
				/*/WEB-INF/login.jspを呼び出す */
				RequestDispatcher rd1 = getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
				rd1.forward(req, resp);
				break;

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
