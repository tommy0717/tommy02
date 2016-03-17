package com.appspot.tommy02;

import java.io.IOException;

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

		StringBuilder test = new StringBuilder();

		String strYear = "2016";
		String strMonth = "10";
		String strDay = "11";

		if(Integer.parseInt(strMonth) < 10){ strMonth = "0" + strMonth; }
		if(Integer.parseInt(strDay) < 10){ strDay = "0" + strDay; }

		String strDate = strYear + strMonth + strDay;
		int intDate = Integer.parseInt(strDate);

		strDate = Integer.toString(intDate);

		strYear = strDate.substring(0, 4);
		strMonth = strDate.substring(4, 6);
		strDay= strDate.substring(6, 8);

		test.append(strDate);
		test.append("<br>");
		test.append(strYear);
		test.append("<br>");
		test.append(strMonth);
		test.append("<br>");
		test.append(strDay);

		req.setAttribute("test", test.toString());
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/test.jsp");
		rd.forward(req, resp);

	}


}