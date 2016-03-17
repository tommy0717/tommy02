package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class LoginCheckFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		HttpSession session = request.getSession();
		Cookie ck[] = request.getCookies();
		String status = "";
		String ses_token = "";
		String cok_token = "";

		if(session.getAttribute("STATUS") != null){
			status = session.getAttribute("STATUS").toString();
		}

		if(session.getAttribute("TOKEN") != null){
			ses_token = (String)session.getAttribute("TOKEN");
		}

		if (ck != null){
		    for (int i = 0 ; i < ck.length ; i++){
		      if (ck[i].getName().equals("TOKEN")){
		        cok_token = ck[i].getValue();
		      }
		    }
		  }

		//ログイン認証
		TerminalBean login = new TerminalBean(status, ses_token, cok_token);
		String loginResult = login.LoginCheck();

		switch(loginResult){
		case "OK_ses": //ログインOK（セッションあり）
			chain.doFilter(request, response);
			break;

		case "NG_ses": //ログインNG（セッションあり）
			session.invalidate();                               //既存セッション破棄
			response.sendRedirect("/login");
			break;

		case "OK_cok": //ログインOK（セッションなし）
			session.invalidate();                               //既存セッション破棄
			HttpSession newSession = request.getSession(true);  //新規セッション開始
			newSession.setAttribute("TOKEN", cok_token);        //セッションに値を格納
			newSession.setAttribute("STATUS", "login");         //セッションに値を格納
			chain.doFilter(request, response);
			break;

		case "NG_cok": //ログインNG（セッションなし）
			response.sendRedirect("/login");
			break;
		}
	}

	@Override
	public void destroy() {
		// TODO 自動生成されたメソッド・スタブ
	}

}
