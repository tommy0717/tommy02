package com.appspot.tommy02;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public class TaskResultBean {

	private Long taskResultID;
	private Long taskID;
	private int workYear;
	private int workMonth;
	private int workDay;
	private String workload;
	private String workHours;
	private String workMinutes;

	private String workDate;
	private String workTime;

	private String resultWorkload;
	private String resultWorkTime;
	private String resultWorkDate;


	public TaskResultBean(Long taskResultID){
		//コンストラクタ（実績の修正、削除時）
		this.taskResultID = taskResultID;
	}

	public TaskResultBean(Long taskID, String workYear, String workMonth, String workDay,
						  String workload, String workHours, String workMinutes){
		//コンストラクタ（タスクの新規登録時）
		this.taskID = taskID;
		this.workYear = Integer.parseInt(workYear);
		this.workMonth = Integer.parseInt(workMonth);
		this.workDay = Integer.parseInt(workDay);
		this.workload = workload;
		this.workHours = workHours;
		this.workMinutes = workMinutes;

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
			ds.put(taskResult);

			//タスクマスタに加算（作業時間、作業量）
			TaskBean task = new TaskBean(taskID);
			if(task.get() == "OK"){

				int wl;
				int rt;
				try{
					wl = task.getTaskWorkload();
					wl = wl + Integer.parseInt(workload);
				}catch(NumberFormatException e){
					wl = Integer.parseInt(workload);
				}
				try{
					rt = task.getTaskWorkMinutes();
					rt = rt + Integer.parseInt(workTime);
				}catch(NumberFormatException e){
					rt = Integer.parseInt(workTime);
				}

				task.setTaskWorkload(Integer.toString(wl));
				task.setTaskWorkMinutes("0",Integer.toString(rt));
				if(task.update() == "OK"){
					result = "OK";
				}else{
					result = "タスクマスタへの登録に失敗しました。";
				}
			}else{
				result = "該当のタスクが見つかりません。";
			}
		}else{
			result = "セットされた情報に誤りがあります。";
		}

		return result;
	}

	public String delete(){
		String result;

		Key key = KeyFactory.createKey("taskResult", taskResultID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		try {
			Entity trt = ds.get(key);
			TaskBean task = new TaskBean(Long.parseLong(trt.getProperty("taskID").toString()));

			if(task.get() == "OK"){

				int wk1 = Integer.parseInt(trt.getProperty("workload").toString());
				int wk2 = Integer.parseInt(trt.getProperty("workTime").toString());

				int taskWorkload = task.getTaskWorkload();
				int taskWorkMinutes = task.getTaskWorkMinutes();

				taskWorkload = taskWorkload - wk1;
				taskWorkMinutes = taskWorkMinutes - wk2;

				task.setTaskWorkload(Integer.toString(taskWorkload));
				task.setTaskWorkMinutes("0", Integer.toString(taskWorkMinutes));

				if(task.update() == "OK"){
					result = "OK";
				}else{
					result = "NG";
				}
			}

			ds.delete(key);
			result = "OK";
		}catch(IllegalArgumentException e){
			result = "削除エラー" + e;
		}catch(DatastoreFailureException e){
			result = "削除エラー" + e;
		} catch (EntityNotFoundException e) {
			result = "削除エラー" + e;
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

}
