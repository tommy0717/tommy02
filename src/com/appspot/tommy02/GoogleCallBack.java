package com.appspot.tommy02;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

@SuppressWarnings("serial")
public class GoogleCallBack extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		//callbackパラメータ
		String code = req.getParameter("code");

		if(code == null || code == ""){
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/googleLogin.jsp");
			rd.forward(req, resp);

		}else{
			String msg;

			GoogleCalendar googleCalendar = new GoogleCalendar();
			//レスポンス取得
			GoogleTokenResponse response = null;
			try {
				response = googleCalendar.getGoogleResponse(code);

				AccountBean ac = new AccountBean();
				ac.setEmail(ac.getUserID(req));
				ac.setRefreshToken(response.getRefreshToken());
				if(ac.update() == "OK"){
					msg = "認証が完了しました。";
				}else{
					msg = "認証に失敗しました。";
				}
			} catch (GeneralSecurityException e) {
				msg = "認証に失敗しました。" + e;
			}

			//リフレッシュトークンとアクセストークン
//			req.setAttribute("accessToken",response.getAccessToken());
//
//			//カレンダーにアクセスするためのオブジェクト取得
//			GoogleCredential credential;
//			Calendar client = null;
//			try {
//				credential = googleCalendar.getGoogleCredential(response.getRefreshToken());
//				client = googleCalendar.getCalendarClient(credential);
//			} catch (GeneralSecurityException e) {
//				e.printStackTrace();
//			}
//
//			//カレンダーリスト取得
//			com.google.api.services.calendar.model.CalendarList calendar = client.calendarList().list().execute();
////			CalendarList feed = client.calendarList().list().execute();
//
//			req.setAttribute("calendarList", calendar);

			req.setAttribute("msg", msg);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/googleFinish.jsp");
			rd.forward(req, resp);

		}







	}

}
