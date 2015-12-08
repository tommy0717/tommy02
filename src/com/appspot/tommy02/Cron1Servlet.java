package com.appspot.tommy02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Cron1Servlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		try {
			URL url = new URL("http://tommy02-1144.appspot.com/admin/sessioncleanup");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				resp.getWriter().println(line);
			}
			reader.close();

		} catch (MalformedURLException e) {
			resp.getWriter().println("URL faild : " + e);
		} catch (IOException e) {
			resp.getWriter().println("URL faild : " + e);
		}

	}
}