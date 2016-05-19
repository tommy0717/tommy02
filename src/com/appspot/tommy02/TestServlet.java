package com.appspot.tommy02;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String year = "12/20";
		Pattern p = Pattern.compile("[1-12]/[1-31]");
		Matcher m = p.matcher(year);

		req.setAttribute("test1", year);
		req.setAttribute("test2", p);
		req.setAttribute("test3", m.find());

		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/test.jsp");
		rd.forward(req, resp);

	}


}