package com.appspot.tommy02;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class TerminalBean {

	private String sesStatus;
	private String sesToken;
	private String cokToken;
	private String result;

	public TerminalBean(){
		//コンストラクタ
	}

	public TerminalBean(String ses_status,String ses_token,String cok_token){
		this.sesStatus = ses_status;
		this.sesToken = ses_token;
		this.cokToken = cok_token;
	}

	public String Login(String userid,String password, String first){

		AccountBean ac = new AccountBean(userid);
		ac.get();
		if(password.equals(ac.getPassword()) || first.equals("first")){

			/*ログイントークンを生成する */
			int TOKEN_LENGTH = 16;//16*2=32バイト
			byte token[] = new byte[TOKEN_LENGTH];
			StringBuffer buf = new StringBuffer();
			SecureRandom random = null;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
				random.nextBytes(token);
				for (int i = 0; i < token.length; i++) {
					buf.append(String.format("%02x", token[i]));
				}
			}catch(NoSuchAlgorithmException e){
				/* トークン生成エラー */
			}

			/* 作成したログイントークンをログインテーブルへ登録する */
			Calendar nowCalendar = Calendar.getInstance();

		    // 上記、現在日時のフォーマットを指定
		    SimpleDateFormat formatA = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		    String formatDate = formatA.format(nowCalendar.getTime());

			try{
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Entity Login = new Entity("LOGIN",buf.toString());
				Login.setProperty("TOKEN", buf.toString());
				Login.setProperty("USER_ID", userid);
				Login.setProperty("REGISTRATED_TIME", formatDate);
				ds.put(Login);

				result = buf.toString();

			}catch(Exception e){
				/* エラー */
			}

		}else{
			//ログインNG（パスワード違い）
			result = "NG_password";
		}

		return result;
	}

	public String Logout(){

		//ログインテーブルから削除
		Key key = KeyFactory.createKey("LOGIN", sesToken);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity = ds.get(key);
			ds.delete(entity.getKey()); //ログインテーブルの削除

		}catch(EntityNotFoundException e){
			//ログインNG（対象のログインテーブルが存在しない）
			result = "NG";
		}

		//クッキーから削除（コール元で削除）
		//セッションから削除（コール元で削除）

		return result;
	}

	public String LoginCheck(){
		/* ログイン状態を確認する */
		if(sesStatus == ("login")){
			//セッション有
			//前回ログイン時間チェック（2週間経過でログアウト）
			if(loginDayCheck(sesToken) == "OK"){
				result = "OK_ses";
			}else{
				result = "NG_ses";
			}

		}else{
			//セッション無
			//cookie状態の確認とログイン時間チェック
			if(loginDayCheck(cokToken) == "OK"){
				result = "OK_cok";
			}else{
				result = "NG_cok";
			}
		}
		return result;
	}

	private String loginDayCheck(String token){
		String dayCheck = "NG";


		try{
			Key key = KeyFactory.createKey("LOGIN", token);
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity entity = ds.get(key);

			//ストリングからCalendarに変換
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			Date date = null;

			try {
				date = new Date( dateformat.parse((String) entity.getProperty("REGISTRATED_TIME")).getTime());
			} catch (java.text.ParseException e) {
				//エラー処理（後で書くこと）
			}

			Calendar cal_loginDay = Calendar.getInstance();
			cal_loginDay.setTime(date);

			//2週間前の日付を取得
			Calendar cal_before2week = Calendar.getInstance();
			cal_before2week.add(Calendar.DAY_OF_MONTH,-14);

			//ログインテーブルの日付チェック
			if(cal_loginDay.compareTo(cal_before2week)>=0){
				dayCheck = "OK";
			}else{
				dayCheck = "NG";
				ds.delete(entity.getKey()); //ログインテーブルの削除
			}

		}catch(EntityNotFoundException e){
			//ログインNG（対象のログインテーブルが存在しない）
			dayCheck = "NG";
		}catch(IllegalArgumentException e){
			//ログインNG（キーがNULLもしくはブランク）
			dayCheck = "NG";
		}

		return dayCheck;

	}

	public String getUserID(HttpServletRequest req){
		String userID;
		HttpSession ss = req.getSession();
		String token = ss.getAttribute("TOKEN").toString();
		Key loginKey = KeyFactory.createKey("LOGIN", token);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			try {
				Entity login = ds.get(loginKey);
				userID = (String)login.getProperty("USER_ID");
			} catch (EntityNotFoundException e) {
				userID = "NG";
			}
		return userID;
	}

	public String getUserID(String token){
		String userID;
		Key loginKey = KeyFactory.createKey("LOGIN", token);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			try {
				Entity login = ds.get(loginKey);
				userID = (String)login.getProperty("USER_ID");
			} catch (EntityNotFoundException e) {
				userID = "NG";
			}
		return userID;
	}


	public String getSesToken() {return sesToken;}
	public void setSesToken(String ses_token) {this.sesToken = ses_token;}
	public String getCokToken() {return cokToken;}
	public void setCokToken(String cok_token) {this.cokToken = cok_token;}
//	public String getStatus() {return status;}
	public void setStatus(String status) {this.sesStatus = status;}
	public String getResult() {return result;}
//	public void setResult(String result) {this.result = result;}

}
