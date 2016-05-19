package com.appspot.tommy02;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ConcurrentModificationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeUtility;
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

	private final String passKey = "tommy02";

	private String email;
	private String userName;
	private String password;
	private String emailSend;
	private String refreshToken;
	private int sendTime = 7;         /* デフォルト設定 */
	private String accountMemo = "";  /* デフォルト設定 */
	private String calSummary1 = "-";
	private String calSummary2 = "-";
	private String calSummary3 = "-";

	private String chkEmail = "";
	private String chkUserName = "";
	private String chkPassword = "";
	private String chkSendTime = "";
	private String chkAccountMemo = "";
	private String chkCalSummary1 = "";
	private String chkCalSummary2 = "";
	private String chkCalSummary3 = "";

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
//			this.password = ac.getProperty("password").toString();

			byte[] by = decodeBase64(ac.getProperty("password").toString());
			this.password = decrypt(by);

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
			if(ac.getProperty("calSummary1") != null){
				calSummary1 = ac.getProperty("calSummary1").toString();
			}
			if(ac.getProperty("calSummary2") != null){
				calSummary2 = ac.getProperty("calSummary2").toString();
			}
			if(ac.getProperty("calSummary3") != null){
				calSummary3 = ac.getProperty("calSummary3").toString();
			}

			result = "OK";
		} catch (Exception e) {
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

				try {
					byte[] by = encrypt(password);
					ac.setProperty("password", encodeBase64(by));

				} catch (Exception e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				}

//				ac.setProperty("password", password);
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
			if(chkPassword == "OK"){
				try {
					byte[] by = encrypt(password);
					entity.setProperty("password", encodeBase64(by));
				} catch (Exception e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				}
			}
			if(emailSend != null && emailSend != ""){ entity.setProperty("emailSend", emailSend); }
			if(refreshToken != null && refreshToken != ""){ entity.setProperty("refreshToken", refreshToken); }
			if(this.chkSendTime.equals("OK")){ entity.setProperty("sendTime", sendTime); }
			if(this.chkAccountMemo.equals("OK")){entity.setProperty("accountMemo", accountMemo); }
			if(this.chkCalSummary1.equals("OK")){entity.setProperty("calSummary1", calSummary1); }
			if(this.chkCalSummary2.equals("OK")){entity.setProperty("calSummary2", calSummary2); }
			if(this.chkCalSummary3.equals("OK")){entity.setProperty("calSummary3", calSummary3); }

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
	    this.chkSendTime = "OK";
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
	    this.chkAccountMemo = "OK";
	}

	/**
	 * calSummary1を取得します。
	 * @return calSummary1
	 */
	public String getCalSummary1() {
	    return calSummary1;
	}

	/**
	 * calSummary1を設定します。
	 * @param calSummary1 calSummary1
	 */
	public void setCalSummary1(String calSummary1) {
	    this.calSummary1 = calSummary1;
	    this.chkCalSummary1 = "OK";
	}

	/**
	 * calSummary2を取得します。
	 * @return calSummary2
	 */
	public String getCalSummary2() {
	    return calSummary2;
	}

	/**
	 * calSummary2を設定します。
	 * @param calSummary2 calSummary2
	 */
	public void setCalSummary2(String calSummary2) {
	    this.calSummary2 = calSummary2;
	    this.chkCalSummary2 = "OK";
	}

	/**
	 * calSummary3を取得します。
	 * @return calSummary3
	 */
	public String getCalSummary3() {
	    return calSummary3;
	}

	/**
	 * calSummary3を設定します。
	 * @param calSummary3 calSummary3
	 */
	public void setCalSummary3(String calSummary3) {
	    this.calSummary3 = calSummary3;
	    this.chkCalSummary3 = "OK";
	}

	public byte[] encrypt(String text)
		    throws Exception {

		    SecretKeySpec sksSpec =
		        new SecretKeySpec(passKey.getBytes(), "Blowfish");

		    Cipher cipher = Cipher.getInstance("Blowfish");
		    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, sksSpec);

		    return cipher.doFinal(text.getBytes());
		}

	public String decrypt(byte[] encrypted)
		    throws Exception {

		    SecretKeySpec sksSpec =
		        new SecretKeySpec(passKey.getBytes(), "Blowfish");

		    Cipher cipher = Cipher.getInstance("Blowfish");
		    cipher.init(Cipher.DECRYPT_MODE, sksSpec);

		    return new String(cipher.doFinal(encrypted));
		}

	public String encodeBase64(byte[] data) throws Exception {

	    ByteArrayOutputStream forEncode = new ByteArrayOutputStream();

	    OutputStream toBase64 = MimeUtility.encode(forEncode, "base64");
	    toBase64.write(data);
	    toBase64.close();

	    return forEncode.toString("iso-8859-1");
	}

	public byte[] decodeBase64(String base64) throws Exception {

	    InputStream fromBase64 = MimeUtility.decode(
	        new ByteArrayInputStream(base64.getBytes()), "base64");

	    byte[] buf = new byte[1024];
	    ByteArrayOutputStream toByteArray = new ByteArrayOutputStream();

	    for (int len = -1;(len = fromBase64.read(buf)) != -1;)
	        toByteArray.write(buf, 0, len);

	    return toByteArray.toByteArray();
	}


}
