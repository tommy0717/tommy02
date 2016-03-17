package com.appspot.tommy02;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SettingsPageFactory {

	public SettingsPageFactory(){

	}

	public void forwardSettingsPage(String userID, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{

		AccountBean ac = new AccountBean();
		ac.setEmail(userID);
		ac.get();

		if(req.getParameter("change") != null && req.getParameter("change").equals("yes")){
			String userName = req.getParameter("userName");
			String password = req.getParameter("password");
			String emailSend = req.getParameter("emailSend");

			ac.setUserName(userName);
			ac.setPassword(password);

			if(emailSend != null && emailSend.equals("change") && ac.getEmailSend().equals("OK")){
				ac.setEmailSend("NG");
			}else if(emailSend != null && emailSend.equals("change") && ac.getEmailSend().equals("NG")){
				ac.setEmailSend("OK");
			}

			if(req.getParameter("sendTime") != null){
				int sendTime = Integer.parseInt(req.getParameter("sendTime"));
				ac.setSendTime(sendTime);
				ac.update();
			}

			if(ac.chkUserName().equals("OK")){
				ac.update();
			}else if(ac.chkPassword().equals("OK")){
				ac.update();
			}else if(emailSend != null && emailSend.equals("change")){
				ac.update();
			}

			ac.get();
		}

		req.setAttribute("userName", ac.getUserName());
		req.setAttribute("email", ac.getEmail());
		req.setAttribute("sendTime", ac.getSendTime());

		if(ac.getEmailSend().equals("OK")){
			req.setAttribute("emailSend", "送信する");
		}else{
			req.setAttribute("emailSend", "送信しない");
		}

		if(ac.getRefreshToken() != null && ac.getRefreshToken().length() != 0 ){
			req.setAttribute("google", "設定あり");
//			GoogleCalendar gc = new GoogleCalendar();
//			try {
//				String URL = gc.getGoogleOAuthURL2(gc.getGoogleCredential(ac.getRefreshToken()));
//				req.setAttribute("googleURL", URL);
//			} catch (GeneralSecurityException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}
		}else{
			req.setAttribute("google", "google認証を行うと、googleカレンダーの情報が"
			+ "表示されるようになります。");
		}

		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/setting.jsp");
		rd.forward(req, resp);

	}

}
