package com.appspot.tommy02;

import java.util.ConcurrentModificationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class AccountBean {

	private String email;
	private String userName;
	private String password;
	private String emailSend;
	private String refreshToken;
	private int sendTime = 7;         /* デフォルト設定 */
	private String accountMemo = "";  /* デフォルト設定 */

	private String chkEmail;
	private String chkUserName;
	private String chkPassword;

	public AccountBean(){
	}

	public AccountBean(String email, String userName, String password, String emailSend){
		//新規登録時
		setEmail(email);
		setUserName(userName);
		setPassword(password);
		this.emailSend = emailSend;
	}

	public AccountBean(String email){
		setEmail(email);
	}

	public String get(){
		String result;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("Account", email);

		try {
			Entity ac = ds.get(key);
			this.userName = ac.getProperty("userName").toString();
			this.password = ac.getProperty("password").toString();
			this.emailSend = ac.getProperty("emailSend").toString();
			if(ac.getProperty("refreshToken") != null){
				this.refreshToken = ac.getProperty("refreshToken").toString();
			}else{
				this.refreshToken = "";
			}
			if(ac.getProperty("sendTime") != null){
				this.sendTime = Integer.parseInt(ac.getProperty("sendTime").toString());
			}
			if(ac.getProperty("accountMemo") != null){
				accountMemo = ac.getProperty("accountMemo").toString();
			}

			result = "OK";
		} catch (EntityNotFoundException e) {
			result = "エンティティ取得エラー";
		}

		return result;
	}

	public String insert(){
		String result;

		if(chkEmail == "OK" && chkUserName == "OK" && chkPassword =="OK"){

			/* 登録済チェック */
			Key key = KeyFactory.createKey("Account", email);
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			try{
				Entity entity = ds.get(key);
				result = "既に登録されているメールアドレスです。";
			}catch(EntityNotFoundException e){
				//処理なし

				Entity ac = new Entity("Account",email);
				ac.setProperty("email", email);
				ac.setProperty("userName", userName);
				ac.setProperty("password", password);
				ac.setProperty("emailSend", emailSend);
				ac.setProperty("sendTime", sendTime);
				ac.setProperty("accountMemo", accountMemo);
				try{
					ds.put(ac);
					result = "OK";
				}catch(IllegalArgumentException e1){
					result = "NG";
				}catch(ConcurrentModificationException e1){
					result = "NG";
				}catch(DatastoreFailureException e1){
					result = "NG";
				}
			}


		}else{
			result = "入力値エラー";
		}

		return result;

	}

	public String update(){

		String result;
		Key key = KeyFactory.createKey("Account", email);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity = ds.get(key);

			if(chkEmail == "OK"){ entity.setProperty("email", email); }
			if(chkUserName == "OK"){ entity.setProperty("userName", userName); }
			if(chkPassword == "OK"){ entity.setProperty("password", password); }
			if(emailSend != null && emailSend != ""){ entity.setProperty("emailSend", emailSend); }
			if(refreshToken != null && refreshToken != ""){ entity.setProperty("refreshToken", refreshToken); }
			entity.setProperty("sendTime", sendTime);        /* 過渡期対応中 */
			entity.setProperty("accountMemo", accountMemo);  /* 過渡期対応中 */

			ds.put(entity);
			result = "OK";

		}catch(EntityNotFoundException e){
			result = "NG";
		}

		return result;
	}

	/**
	 * emailを取得します。
	 * @return email
	 */
	public String getEmail() {
	    return email;
	}

	/**
	 * emailを設定します。
	 * @param email email
	 */
	public void setEmail(String email) {
		chkEmail = "OK";

		if(email == null || email == ""){
			chkEmail = "Eメールアドレスが入力されていません。";
		}else{
			/* メールアドレスの正当性チェック */
			String ptnStr = "[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+";
			Pattern ptn =Pattern.compile(ptnStr);
			Matcher mc = ptn.matcher(email);
			if(mc.matches()){
				/* 一致（処理なし） */
			}else{
				/* 一致しない */
				chkEmail = "無効なメールアドレスです。";
			}

			if(chkEmail == "OK"){
				this.email = email;
			}
		}
	}

	/**
	 * userNameを取得します。
	 * @return userName
	 */
	public String getUserName() {
	    return userName;
	}

	/**
	 * userNameを設定します。
	 * @param userName userName
	 */
	public void setUserName(String userName) {

		if(userName == null || userName.length() == 0){
			chkUserName = "名前が入力されていません。";
		}else{
			//名前の妥当性チェック（気が向いたら書く）
		    this.userName = userName;
			chkUserName = "OK";
		}
	}

	/**
	 * passwordを取得します。
	 * @return password
	 */
	public String getPassword() {
	    return password;
	}

	/**
	 * passwordを設定します。
	 * @param password password
	 */
	public void setPassword(String password) {

		if(password == null || password == ""){
			chkPassword = "パスワードが入力されていません。";
		}else if(password.length() < 8){
			chkPassword = "パスワードは8文字以上で登録してください。";
		}else{
			//名前の妥当性チェック（気が向いたら書く）
		    this.password = password;
		    chkPassword = "OK";
		}
	}

	/**
	 * chkEmailを取得します。
	 * @return chkEmail
	 */
	public String chkEmail() {
	    return chkEmail;
	}

	/**
	 * chkUserNameを取得します。
	 * @return chkUserName
	 */
	public String chkUserName() {
	    return chkUserName;
	}

	/**
	 * chkPasswordを取得します。
	 * @return chkPassword
	 */
	public String chkPassword() {
	    return chkPassword;
	}

	/**
	 * emailSendを取得します。
	 * @return emailSend
	 */
	public String getEmailSend() {
	    return emailSend;
	}

	/**
	 * emailSendを設定します。
	 * @param emailSend emailSend
	 */
	public void setEmailSend(String emailSend) {
	    this.emailSend = emailSend;
	}

	/**
	 * refreshTokenを取得します。
	 * @return refreshToken
	 */
	public String getRefreshToken() {
	    return refreshToken;
	}

	/**
	 * refreshTokenを設定します。
	 * @param refreshToken refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
	    this.refreshToken = refreshToken;
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

	/**
	 * sendTimeを取得します。
	 * @return sendTime
	 */
	public int getSendTime() {
	    return sendTime;
	}

	/**
	 * sendTimeを設定します。
	 * @param sendTime sendTime
	 */
	public void setSendTime(int sendTime) {
	    this.sendTime = sendTime;
	}

	/**
	 * accountMemoを取得します。
	 * @return accountMemo
	 */
	public String getAccountMemo() {
	    return accountMemo;
	}

	/**
	 * accountMemoを設定します。
	 * @param accountMemo accountMemo
	 */
	public void setAccountMemo(String accountMemo) {
	    this.accountMemo = accountMemo;
	}

}
