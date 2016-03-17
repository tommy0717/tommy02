package com.appspot.tommy02;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Events.List;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendar {


	private static final String APPLICATION_NAME = "tommy02";

	private static HttpTransport HTTP_TRANSPORT;
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	// リダイレクトURL
	private static final String REDIRECT_URL = "http://1-dot-tommy02-1144.appspot.com/oauth2callback";

	public GoogleCalendar() {
	}

	/**
	 * google flow 認証のためのオブジェクト取得
	 */
	public GoogleAuthorizationCodeFlow getFlow() throws IOException,
			GeneralSecurityException {

		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		// スコープの設定
		Set<String> scopes = new HashSet<String>();
		scopes.add(CalendarScopes.CALENDAR);
		scopes.add(CalendarScopes.CALENDAR_READONLY);

		GoogleClientSecrets clientSecrets = getClientSecrets();

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
				.setAccessType("offline").setApprovalPrompt("force").build();

		return flow;
	}

	/**
	 * JSONファイル取得
	 */
	public GoogleClientSecrets getClientSecrets() throws IOException {
		Reader SECRET_FILE = new InputStreamReader(
				GoogleCalendar.class.getResourceAsStream("/client_secrets.json"));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, SECRET_FILE);

		return clientSecrets;
	}

	/**
	 * 認証URL取得
	 */
	public String getGoogleOAuthURL() throws IOException,
			GeneralSecurityException {
		GoogleAuthorizationCodeFlow flow = getFlow();
		return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URL).build();
	}

	public String getGoogleOAuthURL2(GoogleCredential credential){
		String URL = "https://accounts.google.com/o/oauth2/revoke?token=";
		return URL + credential.getAccessToken();
	}

	/**
	 * コールバック後、レスポンス取得
	 */
	public GoogleTokenResponse getGoogleResponse(String code)
			throws IOException, GeneralSecurityException {
		GoogleAuthorizationCodeFlow flow = getFlow();
		return flow.newTokenRequest(code).setRedirectUri(REDIRECT_URL)
				.execute();
	}

	/**
	 * 認証オブジェクト取得
	 */
	public GoogleCredential getGoogleCredential(String refresh_token)
			throws GeneralSecurityException, IOException {

		if (HTTP_TRANSPORT == null) {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		}

		GoogleClientSecrets secrets = getClientSecrets();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setClientSecrets(secrets.getDetails().getClientId(),
						secrets.getDetails().getClientSecret())
				.setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT)
				.build();

		credential.setRefreshToken(refresh_token);
		credential.refreshToken();

		return credential;
	}

	/**
	 * カレンダーにアクセスするためのオブジェクト取得
	 */
	public com.google.api.services.calendar.Calendar getCalendarClient(
			GoogleCredential credential) throws GeneralSecurityException,
			IOException {
		if (HTTP_TRANSPORT == null) {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		}

		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	/*
	 * カレンダーIDで予定を取得
	 */
	public com.google.api.services.calendar.model.Events getEvents(String calendarId,
				com.google.api.services.calendar.Calendar client, Date startDate,
				Date endDate) throws IOException {
			DateTime start = new DateTime(startDate,
					TimeZone.getTimeZone("Asia/Tokyo"));
			DateTime end = new DateTime(endDate, TimeZone.getTimeZone("Asia/Tokyo"));
			List calendar = client.events().list(calendarId);
			calendar.setTimeMin(start);
			calendar.setTimeMax(end);
			calendar.setTimeZone("Asia/Tokyo");
			calendar.setOrderBy("startTime");
			calendar.setSingleEvents(true);

			com.google.api.services.calendar.model.Events events = calendar.execute();
			return events;
	}

	public java.util.List<String> todayTask(String refreshToken){
		java.util.List<String> todayTaskList = new ArrayList<String>();
		java.util.List<com.appspot.tommy02.Event> List = new ArrayList<com.appspot.tommy02.Event>();
		StringBuilder todayTask = new StringBuilder();
		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

		//カレンダーにアクセスするためのオブジェクト取得
		GoogleCredential credential = null;
		com.google.api.services.calendar.Calendar client = null;
		try {
			credential = getGoogleCredential(refreshToken);
			client = getCalendarClient(credential);
		} catch (GeneralSecurityException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//カレンダーリスト取得
		CalendarList feed = null;
		try {
			feed = client.calendarList().list().execute();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		java.util.List<CalendarListEntry> list = feed.getItems();
		Calendar now = Calendar.getInstance(tz);
		now.set(Calendar.HOUR_OF_DAY,0);
		now.set(Calendar.MINUTE,0);

		Date startDate = now.getTime();
		now.set(Calendar.HOUR_OF_DAY,23);
		now.set(Calendar.MINUTE,59);
		Date endDate = now.getTime() ;

		format.setTimeZone(tz);

		for(int i = 0; i < list.size(); i++){
			Events event = null;
			try {
				event = getEvents(list.get(i).getId(), client, startDate, endDate);
				java.util.List<Event> eventList = event.getItems();

				if(eventList.size() > 0){

					int startHours;
					int startMinutes;
					int endHours;
					int endMinutes;
					String name;
					String displayName;

					for(int j = 0; j < eventList.size(); j++){
						todayTask = todayTask.delete(0, todayTask.length());
						if(event.getItems().get(j).getStart().getDateTime() != null){
							todayTask.append(event.getItems().get(j).getStart().
																getDateTime().toString().substring(11, 16));
							todayTask.append("～");
							todayTask.append(event.getItems().get(j).getEnd().
																getDateTime().toString().substring(11, 16));
							todayTask.append(" ");
							todayTask.append(event.getItems().get(j).getSummary());

							startHours = Integer.parseInt(event.getItems().get(j).getStart().
																getDateTime().toString().substring(11, 13));
							startMinutes = Integer.parseInt(event.getItems().get(j).getStart().
																getDateTime().toString().substring(14, 16));
							endHours = Integer.parseInt(event.getItems().get(j).getEnd().
																getDateTime().toString().substring(11, 13));
							endMinutes = Integer.parseInt(event.getItems().get(j).getEnd().
																getDateTime().toString().substring(14, 16));
							name = event.getItems().get(j).getSummary();
							displayName = todayTask.toString();

							List.add(new com.appspot.tommy02.Event(startHours, startMinutes, endHours, endMinutes,
																	name, displayName));

						}else{
							todayTask.append("【終日】");
							todayTask.append(" ");
							todayTask.append(event.getItems().get(j).getSummary());

							startHours = 0;
							startMinutes = 0;
							endHours = 0;
							endMinutes = 0;
							name = event.getItems().get(j).getSummary();
							displayName = todayTask.toString();

							List.add(new com.appspot.tommy02.Event(startHours, startMinutes, endHours, endMinutes,
									name, displayName));
						}
					}
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		Collections.sort(List, new EventComparator());

		for(int i = 0; i <  List.size(); i++){
			todayTaskList.add(List.get(i).displayName);
		}

		return todayTaskList;
	}



}