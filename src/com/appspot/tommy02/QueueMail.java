package com.appspot.tommy02;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueueMail extends HttpServlet {

	private static final Logger log = Logger.getLogger(QueueMail.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {

		String email = req.getParameter("email");
		StringBuilder messageBody = new StringBuilder();
		Boolean send = false;

		AccountBean ac = new AccountBean(email);
		ac.get();

		messageBody.append("Hello！ ");
		messageBody.append(ac.getUserName());
		messageBody.append(" ！");
		messageBody.append("\r\n");
		messageBody.append("\r\n");

		TaskBean tb = new TaskBean();
		List<String> todayTask = tb.todayTask(email);

		//アカウントテーブルにリフレッシュトークンが設定されていればカレンダー情報を取得する。
		if(ac.getRefreshToken() != null && ac.getRefreshToken() != ""){
			GoogleCalendar googleCalendar = new GoogleCalendar();
			List<String> todayCalendar = googleCalendar.todayTask(ac.getRefreshToken());

			if(todayCalendar.size() > 0){
				send = true;
				messageBody.append("＜今日の予定＞");
				messageBody.append("\r\n");
				for(int i = 0; i < todayCalendar.size(); i++){
					messageBody.append(todayCalendar.get(i));
					messageBody.append("\r\n");
				}
				messageBody.append("\r\n");
			}
		}

		if(todayTask.size() > 0){
			send = true;
			messageBody.append("＜今日のタスク＞");
			messageBody.append("\r\n");

			for(int i = 0; i < todayTask.size(); i++){
				messageBody.append(todayTask.get(i));
				messageBody.append("\r\n");
			}
			messageBody.append("\r\n");
		}

		if(send){
			messageBody.append("\r\n");
			messageBody.append("http://1-dot-tommy02-1144.appspot.com/member/mypage");

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
			format.setTimeZone(tz);
			Calendar now = Calendar.getInstance(tz);
			Date now1 = now.getTime();

			try{
				Message message = new MimeMessage(session);
//				message.setFrom(new InternetAddress("tommy02-1144@appspot.gserviceaccount.com", "tommy02"));
				message.setFrom(new InternetAddress("tommy.service.app@gmail.com", "tommy02"));
				message.setRecipient(Message.RecipientType.TO,
										new InternetAddress(email));
				message.setSubject("Today's task ( " + format.format(now1) + " )");
				message.setText(messageBody.toString());
				Transport.send(message);
			} catch(AddressException e){
				log.warning(e.toString());
			} catch(MessagingException e){
				log.warning(e.toString());
			}
		}else{
			log.warning("NoSend");
		}
		log.info("SendOK");
	}
}