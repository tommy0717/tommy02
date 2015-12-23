package com.appspot.tommy02;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ValidationBean {

	private String result_email = "OK";
	private String result_password = "OK";
	private String result_name = "OK";
	private String result_nickname ="OK";

	public ValidationBean(){
		//コンストラクタ
	}

	public String emailCheck(String email){
		if(email == null || email == ""){
			result_email = "Eメールアドレスが入力されていません。";
		}else{
			/* メールアドレスの正当性チェック */
			String ptnStr = "[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+";
			Pattern ptn =Pattern.compile(ptnStr);
			Matcher mc = ptn.matcher(email);
			if(mc.matches()){
				/* 一致（処理なし） */
			}else{
				/* 一致しない */
				result_email = "無効なメールアドレスです。";
			}

			if(result_email == "OK"){
				/* 登録済チェック */
				Key key = KeyFactory.createKey("User", email);
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		//				String test;
				try{
					Entity entity = ds.get(key);
		//					Object obj = entity.getProperty("email");
		//					test = obj.toString();
					result_email = "既に登録されているメールアドレスです。";
				}catch(EntityNotFoundException e){
					/* 処理なし */
				}
			}
		}
		return result_email;
	}

	public String passwordCheck(String password){
		if(password == null || password == ""){
			result_password = "パスワードが入力されていません。";
		}else{
			//パスワードの妥当性チェック（気が向いたら書く）
		}
		return result_password;
	}

	public String nameCheck(String name){
		if(name == null || name == ""){
			result_name = "名前が入力されていません。";
		}else{
			//名前の妥当性チェック（気が向いたら書く）
		}
		return result_name;
	}

	public String nicknameCheck(String nickname){
		if(nickname == null || nickname == ""){
			result_nickname = "名前が入力されていません。";
		}else{
			//ニックネームの妥当性チェック（気が向いたら書く）
		}
		return result_nickname;
	}



}
