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
				resp.sendRedirect("/entry");
				break;

			case "login":
				resp.sendRedirect("/member/mypage");
				break;

			case "contact":
				contactForward(req, resp);
				break;
			}

		}else{
			indexForward(req, resp);

		}
	}

	private void indexForward(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
		rd.forward(req, resp);
	}

	private void contactForward(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/contact.jsp");
		rd.forward(req, resp);
	}

}