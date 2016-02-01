package com.appspot.tommy02;

import java.io.IOException;
import java.util.Calendar;

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


		String start = "2016/1/13";
		String end = "2016/1/19";

		String[] s = start.split("/");
		String[] e = end.split("/");

		int s1 = Integer.parseInt(s[0]);
		int s2 = Integer.parseInt(s[1]);
		int s3 = Integer.parseInt(s[2]);

		int e1 = Integer.parseInt(e[0]);
		int e2 = Integer.parseInt(e[1]);
		int e3 = Integer.parseInt(e[2]);


		//営業日計算
		BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		Calendar calToday = Calendar.getInstance();

		calStart.set(s1, s2 - 1, s3);
		calEnd.set(e1, e2 - 1, e3);

		req.setAttribute("test1", "土曜日 : " + Calendar.SATURDAY);
		req.setAttribute("test2", "日曜日 : " + Calendar.SUNDAY);
		req.setAttribute("test3", "2016/1/13 : " + calStart.get(Calendar.DAY_OF_WEEK));
		req.setAttribute("test4", "2016/1/17 : " + calEnd.get(Calendar.DAY_OF_WEEK));

		req.setAttribute("start", start);
		req.setAttribute("end", end);
		req.setAttribute("countDays1", bdc.countDays(calStart, calEnd));
		req.setAttribute("countDays2", bdc.countDays(calToday, calEnd));

		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/test.jsp");
		rd.forward(req, resp);



	}

}
