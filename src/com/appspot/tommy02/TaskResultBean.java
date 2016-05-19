package com.appspot.tommy02;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class TaskResultBean {

	private Long taskResultID;
	private Long taskID;
	private int workYear;
	private int workMonth;
	private int workDay;
	private String workload;
	private String workHours;
	private String workMinutes;
	private String workMemo;
	private String userID;

	private String workDate;
	private String workTime;

	private String resultWorkload;
	private String resultWorkTime;
	private String resultWorkDate;

	public TaskResultBean(){

	}

	public TaskResultBean(Long taskResultID){
		//コンストラクタ（実績の修正、削除時）
		this.taskResultID = taskResultID;
	}

	public TaskResultBean(Long taskID, String workYear, String workMonth, String workDay,
						  String workload, String workHours, String workMinutes,String workMemo,
						  String userID){
		//コンストラクタ（タスクの新規登録時）
		this.taskID = taskID;
		this.workYear = Integer.parseInt(workYear);
		this.workMonth = Integer.parseInt(workMonth);
		this.workDay = Integer.parseInt(workDay);
		this.workload = workload;
		this.workHours = workHours;
		this.workMinutes = workMinutes;
		this.workMemo = workMemo;
		this.userID = userID;

		StringBuilder buf1 = new StringBuilder();
		buf1.append(this.workYear);
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.workMonth));
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.workDay));
		this.workDate = buf1.toString();

		workloadCheck();
		workTimeCheck();
		workDateCheck();

	}

	public String get(){
		String result = "";

		Key key = KeyFactory.createKey("taskResult", taskResultID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity trt;
		try {
			trt = ds.get(key);
			taskID = Long.parseLong(trt.getProperty("taskID").toString());
			userID = trt.getProperty("userID").toString();
			workDate = trt.getProperty("workDate").toString();
			workload = trt.getProperty("workload").toString();
			workMemo = trt.getProperty("workMemo").toString();
			workTime = trt.getProperty("workTime").toString();
			result = "OK";

		} catch (EntityNotFoundException e) {
			// TODO 自動生成された catch ブロック
			result = "NG";
		}
		return result;
	}

	public String update(){
		String result = "";

		Key key = KeyFactory.createKey("taskResult", taskResultID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity trt;
		try {
			trt = ds.get(key);
			if(this.resultWorkload.equals("OK")){ trt.setProperty("workload", workload); }
			if(this.resultWorkTime.equals("OK")){ trt.setProperty("workTime", workTime); }
			trt.setProperty("workMemo", workMemo);
			ds.put(trt);
			result = "OK";

		} catch (EntityNotFoundException e) {
			// TODO 自動生成された catch ブロック
			result = "NG";
		}
		return result;
	}

	public String insert(){
		//タスク結果を登録する。
		String result = null;

		if(resultWorkload == "OK" &&
			resultWorkTime == "OK" &&
			resultWorkDate == "OK"){

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity taskResult = new Entity("taskResult");
			taskResult.setProperty("taskID", taskID);
			taskResult.setProperty("workload", workload);
			taskResult.setProperty("workDate", workDate);
			taskResult.setProperty("workTime", workTime);
			taskResult.setProperty("workMemo", workMemo);
			taskResult.setProperty("userID", userID);
			ds.put(taskResult);

			//タスクマスタに加算（作業時間、作業量）
			TaskBean task = new TaskBean();
			task.taskResultInsert(taskID, Integer.parseInt(workload), Integer.parseInt(workTime));

			result = "OK";

		}else{
			result = "セットされた情報に誤りがあります。";
		}

		return result;
	}

	public String delete(){
		String result;

		Key key = KeyFactory.createKey("taskResult", taskResultID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity trt;
		try {
			trt = ds.get(key);

			int wl = Integer.parseInt(trt.getProperty("workload").toString());
			int wt = Integer.parseInt(trt.getProperty("workTime").toString());

			TaskBean task = new TaskBean();
			task.taskResultDelete(Long.parseLong(trt.getProperty("taskID").toString()), wl, wt);

			ds.delete(key);

			result = "OK";

		} catch (EntityNotFoundException e) {
			// TODO 自動生成された catch ブロック
			result = "NG";
		}

		return result;

	}

	private void workloadCheck(){
		//空白、NULLチェック
		if(workload == null || workload ==""){
			resultWorkload = "作業量が入力されていません。";
		}else{
			//数値チェック
			try{
				Integer.parseInt(workload);
				resultWorkload = "OK";
			}catch(NumberFormatException e){
				resultWorkload = "数値以外が入力されています。" + workload;
			}
		}
	}

	private void workTimeCheck(){
		//空白、NULLチェック
		if((workHours == null || workHours =="") &&
			(workMinutes == null || workMinutes == "")){
			resultWorkTime = "作業時間が入力されていません。";
		}else{
			//数値チェック
			if(workHours ==null || workHours ==""){ workHours = "0"; }
			if(workMinutes ==null || workMinutes ==""){ workMinutes = "0"; }

			try {
		        workTime = Integer.toString((Integer.parseInt(workHours)*60)
		        					+ Integer.parseInt(workMinutes));
		        resultWorkTime = "OK";
			} catch (NumberFormatException e) {
		        resultWorkTime = "作業予定時間に数値以外が入力されています。 " + workHours + workMinutes;
		    }
		}
	}

	private void workDateCheck(){
		//日付の有効性チェック
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	    // 日付/時刻解析を厳密に行うかどうかを設定する。
	    format.setLenient(false);
	    try {
	        format.parse(workDate);
	        resultWorkDate = "OK";
	    } catch (ParseException e) {
	    	resultWorkDate = "開始日が不正です。 " + workDate;
	    }

	}

	/**
	 * workloadを設定します。
	 * @param workload workload
	 */
	public void setWorkload(String workload) {
	    this.workload = workload;
	    workloadCheck();
	}

	/**
	 * workMemoを設定します。
	 * @param workMemo workMemo
	 */
	public void setWorkMemo(String workMemo) {
	    this.workMemo = workMemo;
	}

	/**
	 * workTimeを設定します。
	 * @param workTime workTime
	 */
	public void setWorkTime(String workTime) {
	    this.workTime = workTime;
	    workTimeCheck();
	}

	/**
	 * resultWorkloadを取得します。
	 * @return resultWorkload
	 */
	public String getResultWorkload() {
	    return resultWorkload;
	}

	/**
	 * resultWorkTimeを取得します。
	 * @return resultWorkTime
	 */
	public String getResultWorkTime() {
	    return resultWorkTime;
	}

	/**
	 * resultWorkDateを取得します。
	 * @return resultWorkDate
	 */
	public String getResultWorkDate() {
	    return resultWorkDate;
	}

	/**
	 * 今日の実績を取得します。
	 * @return List<String>
	 */
	public List<String> todayTaskResult(String userID, int dayChange){
		List<String> todayTaskResult = new ArrayList<String>();
		StringBuilder str = new StringBuilder();

		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		format.setTimeZone(tz);

		Calendar cal = Calendar.getInstance(tz);
		if(dayChange != 0){
			cal.add(Calendar.DAY_OF_MONTH, dayChange);
		}

		Date now = cal.getTime();

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		FetchOptions fetch =FetchOptions.Builder.withOffset(0).limit(100);

		Query query = new Query("taskResult");

		// QueryクラスのaddFilterメソッドを用いて条件を指定
		CompositeFilter cf = new Query.CompositeFilter(CompositeFilterOperator.AND, Arrays.<Query.Filter>asList(
				new Query.FilterPredicate("userID", FilterOperator.EQUAL, userID),
				new Query.FilterPredicate("workDate", FilterOperator.EQUAL, format.format(now))));

		query.setFilter(cf);
//		query.addSort("taskStart",Query.SortDirection.ASCENDING);

		// 作成したクエリからPrepareQueryクラスのオブジェクトを生成
		PreparedQuery pQuery = ds.prepare(query);

		List list = pQuery.asList(fetch);

		if(list.size() > 0){
			Entity taskResult;
			TaskBean task;

			for(int i = 0; i < list.size(); i++){
				taskResult = (Entity)list.get(i);

				if(taskResult.getProperty("workMemo") != null){
					str.setLength(0);
					task = new TaskBean(Long.parseLong(taskResult.getProperty("taskID").toString()));

					if(task.get().equals("OK")){
						str.append("◆");
						str.append(task.getTaskName());
						str.append(" : ");
						str.append(taskResult.getProperty("workMemo").toString());
						todayTaskResult.add(str.toString());
					}
				}
			}
		}

		return todayTaskResult;
	}



}
