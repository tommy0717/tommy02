package com.appspot.tommy02;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class CronMessageSender extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Query qac = new Query("Account");
		PreparedQuery pQueryAc = ds.prepare(qac);
		FetchOptions fetch =FetchOptions.Builder.withOffset(0).limit(100);

		List acList = pQueryAc.asList(fetch);

		if(acList.size() > 0){

			Entity ac;
		    Queue queue = QueueFactory.getDefaultQueue();

			for(int j = 0; j < acList.size(); j++){
				ac = (Entity)acList.get(j);

//				送信時間指定
				TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
				Calendar cal = Calendar.getInstance(tz);
				int now = cal.get(Calendar.HOUR_OF_DAY);

				int sendTime = 7;
				if(ac.getProperty("sendTime") != null){
					try{
						sendTime = Integer.parseInt(ac.getProperty("sendTime").toString());
					}catch(NumberFormatException e){
						//デフォルト設定のまま
					}
				}

				if(ac.getProperty("emailSend").toString().equals("OK") && sendTime == now){
			        //詳細を指定してPost
			        TaskOptions to = TaskOptions.Builder.withUrl("/queueMail")
			        					.param("email", ac.getProperty("email").toString());
			        queue.add(to.method(Method.POST));

				}

			}

			}
		}
	}