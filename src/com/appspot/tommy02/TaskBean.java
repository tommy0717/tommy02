package com.appspot.tommy02;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class TaskBean {

	private Long taskID;
	private String userID;
	private String taskName;
	private String taskContent;
	private String taskType;
	private String taskSubType;
	private String taskPriority;
	private String taskStart;
	private String taskEnd;
	private int taskMinutes;
	private int taskTotal;
	private int taskWorkMinutes;
	private int taskWorkload;
	private int taskWorkDays;
	private float taskWorkPercentage;
	private float taskPercentage;

	private int taskStartYear;
	private int taskStartMonth;
	private int taskStartDay;
	private int taskEndYear;
	private int taskEndMonth;
	private int taskEndDay;

	private String chkTaskID;
	private String chkUserID;
	private String chkTaskName;
	private String chkTaskContent;
	private String chkTaskType;
//	private String chkTaskSubType;
	private String chkTaskPriority;
	private String chkTaskStart;
	private String chkTaskEnd;
	private String chkTaskMinutes;
	private String chkTaskTotal;
	private String chkTaskWorkMinutes;
	private String chkTaskWorkload;

	public TaskBean(){
		//コンストラクタ
	}

	public TaskBean(Long TaskID){
		//コンストラクタ（タスク変更、削除時）
		this.taskID = TaskID;
	}

	public TaskBean(String taskName, String taskContent, String taskType,
				String taskSubType,String taskPriority,
				String taskStartYear, String taskStartMonth, String taskStartDay,
				String taskEndYear, String taskEndMonth, String taskEndDay,
				String taskHours, String taskMinutes, String userID,
				String taskTotal){
		//コンストラクタ(新規登録時）
		setUserID(userID);
		setTaskName(taskName);
		setTaskContent(taskContent);
		setTaskType(taskType,taskSubType);
		setTaskPriority(taskPriority);
		setTaskStart(taskStartYear, taskStartMonth, taskStartDay);
		setTaskEnd(taskEndYear, taskEndMonth, taskEndDay);
		setTaskMinutes(taskHours, taskMinutes);
		setTaskTotal(taskTotal);
		setTaskWorkMinutes("0","0");
		setTaskWorkload("0");
		setTaskWorkDays(0);
		setTaskWorkPercentage(0);
		setTaskPercentage(0);
	}

	public String insert(){
		String resultInsert;

		if(chkUserID() == "OK" && chkTaskName() == "OK" && chkTaskContent() == "OK" &&
			chkTaskType() == "OK" && chkTaskPriority() == "OK" && chkTaskStart() == "OK" &&
			chkTaskEnd() == "OK" && chkTaskMinutes() == "OK" && chkTaskTotal() == "OK" &&
			chkTaskWorkMinutes() == "OK" && chkTaskWorkload() == "OK"){

			//営業日計算
			BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();

			//MONTHの引数は0～11！！
			from.set(taskStartYear, taskStartMonth - 1, taskStartDay);
			to.set(taskEndYear, taskEndMonth - 1, taskEndDay);

			//エラーなし タスクテーブルの登録
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity TASK = new Entity("TASK");
			TASK.setProperty("userID", userID);
			TASK.setProperty("taskName", taskName);
			TASK.setProperty("taskContent", taskContent);
			TASK.setProperty("taskType", taskType);
			TASK.setProperty("taskSubType", taskSubType);
			TASK.setProperty("taskPriority", taskPriority);
			TASK.setProperty("taskStart", taskStart);
			TASK.setProperty("taskEnd", taskEnd);
			TASK.setProperty("taskDays", bdc.countDays(from, to) + 1);
			TASK.setProperty("taskMinutes", taskMinutes);
			TASK.setProperty("taskWorkload", taskWorkload);
			TASK.setProperty("taskWorkMinutes", taskWorkMinutes);
			TASK.setProperty("taskWorkDays", taskWorkDays);
			TASK.setProperty("taskTotal", taskTotal);
			TASK.setProperty("taskPercentage", taskPercentage);
			ds.put(TASK);

			taskWorkDaysAdd(TASK.getKey());

			resultInsert = "OK";

		}else{
			resultInsert = "NG";
		}
		return resultInsert;
	}

	public String update(){
		String resultUpdate;
		int count = 0;
		int dayChange =0;
		Key taskKey = KeyFactory.createKey("TASK", taskID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity task = ds.get(taskKey);

			if(chkTaskName() == "OK"){ task.setProperty("taskName", taskName); count++; }
			if(chkUserID() == "OK"){ task.setProperty("userID", userID); count++; }
			if(chkTaskContent() == "OK"){ task.setProperty("taskContent", taskContent); count++; }
			if(chkTaskType() == "OK"){ task.setProperty("taskType", taskType);
										task.setProperty("taskSubType", taskSubType); count++; }
			if(chkTaskPriority() == "OK"){ task.setProperty("taskPriority", taskPriority); count++; }
			if(chkTaskStart() == "OK"){ task.setProperty("taskStart", taskStart); count++; dayChange++; }
			if(chkTaskEnd() == "OK"){ task.setProperty("taskEnd", taskEnd); count++; dayChange++; }
			if(chkTaskMinutes() == "OK"){ task.setProperty("taskMinutes", taskMinutes); count++; }
			if(chkTaskTotal() == "OK"){ task.setProperty("taskTotal", taskTotal); count++; }
			if(chkTaskWorkMinutes() == "OK"){ task.setProperty("taskWorkMinutes", taskWorkMinutes); count++; }
			if(chkTaskWorkload() == "OK"){ task.setProperty("taskWorkload", taskWorkload); count++; }

			if(dayChange==2){
				//営業日計算
				BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();

				//MONTHの引数は0～11！！
				from.set(taskStartYear, taskStartMonth - 1, taskStartDay);
				to.set(taskEndYear, taskEndMonth - 1, taskEndDay);

				task.setProperty("taskDays", bdc.countDays(from, to) + 1);

//			}else if(dayChange==1 && chkTaskStart().equals("OK")){
//
//			}else if(dayChange ==1 && chkTaskEnd().equals("OK")){

			}

			if(count > 0){
				ds.put(task);

				if(dayChange > 0){ taskWorkDaysAdd(taskKey); }

				resultUpdate = "OK";
			}else{
				resultUpdate = "更新対象なし";
			}

		} catch (EntityNotFoundException e) {
			resultUpdate = "NG";
		}

		return resultUpdate;
	}

	public String delete(){
		String resultDelete;

		Key key = KeyFactory.createKey("TASK", taskID);
		DatastoreService ds1 = DatastoreServiceFactory.getDatastoreService();

		try{
		ds1.delete(key);
		resultDelete = "OK";
		}catch(IllegalArgumentException e){
			resultDelete = "削除エラー" + e;
		}catch(DatastoreFailureException e1){
			resultDelete = "削除エラー" + e1;
		}

		return resultDelete;
	}

	public String get(){
		String resultGet;

		Key taskKey = KeyFactory.createKey("TASK", taskID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity task = ds.get(taskKey);

			userID = task.getProperty("userID").toString();
			taskName = task.getProperty("taskName").toString();
			taskContent = task.getProperty("taskContent").toString();
			taskType = task.getProperty("taskType").toString();
			taskSubType = task.getProperty("taskSubType").toString();
			taskPriority = task.getProperty("taskPriority").toString();
			taskStart = task.getProperty("taskStart").toString();
			
			String[] start = taskStart.split("/");
			taskStartYear = Integer.parseInt(start[0]);
			taskStartMonth = Integer.parseInt(start[1]);
			taskStartDay = Integer.parseInt(start[2]);
			
			taskEnd = task.getProperty("taskEnd").toString();
			
			String[] end = taskEnd.split("/");
			taskEndYear = Integer.parseInt(end[0]);
			taskEndMonth = Integer.parseInt(end[1]);
			taskEndDay = Integer.parseInt(end[2]);
			
			taskMinutes = Integer.parseInt(task.getProperty("taskMinutes").toString());
			taskTotal = Integer.parseInt(task.getProperty("taskTotal").toString());
			taskWorkMinutes = Integer.parseInt(task.getProperty("taskWorkMinutes").toString());
			taskWorkload = Integer.parseInt(task.getProperty("taskWorkload").toString());
			if(task.getProperty("taskWorkDays") != null){
				taskWorkDays = Integer.parseInt(task.getProperty("taskWorkDays").toString());
			}else{
				taskWorkDays = 0;
			}
			if(task.getProperty("taskWorkPercentage") != null){
				taskWorkPercentage = Float.parseFloat(task.getProperty("taskWorkPercentage").toString());
			}else{
				taskWorkPercentage = 0;
			}
			if(task.getProperty("taskPercentage") != null){
				taskPercentage = Float.parseFloat(task.getProperty("taskPercentage").toString());
			}else{
				taskPercentage = 0;
			}

			resultGet = "OK";

		} catch (EntityNotFoundException e) {
			resultGet = "NG";
		}
		return resultGet;
	}

	/**
	 * taskIDを設定します。
	 * @param taskID taskID
	 */
	public void setTaskID(String taskID) {

		try{
			this.taskID = Long.parseLong(taskID);
			chkTaskID = "OK";
		}catch(NumberFormatException e){
			chkTaskID = "タスクIDに数値以外が入力されています。";
		}
	}

	/**
	 * taskIDを取得します。
	 * @return taskID
	 */
	public Long getTaskID() {
	    return taskID;
	}

	/**
	 * userIDを設定します。
	 * @param userID userID
	 */
	public void setUserID(String userID) {
		//UserIDは現時点ではシステム入力の為、チェック不要。
		//気が向いたら実装する。
	    this.userID = userID;
	    chkUserID = "OK";
	}

	/**
	 * userIDを取得します。
	 * @return userID
	 */
	public String getUserID() {
	    return userID;
	}

	/**
	 * taskNameを設定します。
	 * @param taskName taskName
	 */
	public void setTaskName(String taskName) {
		if(taskName == null || taskName == ""){
			chkTaskName = "タスク名が入力されていません。";
		}else if(taskName.length() > 15){
			chkTaskName = "タスク名は15文字以内で入力してください。";
		}else{
			this.taskName = taskName;
			chkTaskName = "OK";
		}
	}

	/**
	 * taskNameを取得します。
	 * @return taskName
	 */
	public String getTaskName() {
	    return taskName;
	}

	/**
	 * taskContentを設定します。
	 * @param taskContent taskContent
	 */
	public void setTaskContent(String taskContent) {
		if(taskContent == null || taskContent == ""){
//			chkTaskContent = "タスク内容が入力されていません。";
			if(taskName != null){
				this.taskContent = taskName;
			}else{
				this.taskContent = "";
			}
			chkTaskContent = "OK";
		}else if(taskContent.length() > 100){
			chkTaskContent = "タスク内容は100文字以内で入力してください。";
		}else{
			this.taskContent = taskContent;
			chkTaskContent = "OK";
		}
	}

	/**
	 * taskTypeを設定します。
	 * @param taskType taskType
	 */
	public void setTaskType(String taskType, String taskSubType) {
		if(taskType == null || taskType == ""){
			chkTaskType = "種別が入力されていません。";
		}else if(taskType.equals("routine")){
			if(taskSubType.equals("year") ||
				taskSubType.equals("month") ||
				taskSubType.equals("day")){
				this.taskType = taskType;
				this.taskSubType = taskSubType;
				chkTaskType = "OK";
			}else{
				chkTaskType = "周期に誤りがあります。　" + taskSubType;
			}
		}else{
			this.taskType = taskType;
			this.taskSubType = taskSubType;
			chkTaskType = "OK";
		}
	}

	/**
	 * taskContentを取得します。
	 * @return taskContent
	 */
	public String getTaskContent() {
	    return taskContent;
	}

	/**
	 * taskTypeを取得します。
	 * @return taskType
	 */
	public String getTaskType() {
	    return taskType;
	}

	/**
	 * taskSubTypeを取得します。
	 * @return taskSubType
	 */
	public String getTaskSubType() {
	    return taskSubType;
	}

	/**
	 * taskPriorityを設定します。
	 * @param taskPriority taskPriority
	 */
	public void setTaskPriority(String taskPriority) {
		if(taskPriority == null || taskPriority == ""){
			chkTaskPriority = "優先度が入力されていません。";
		}else{
			this.taskPriority = taskPriority;
			chkTaskPriority = "OK";
		}
	}

	/**
	 * taskStartを設定します。
	 * @param taskStart taskStart
	 */
	public void setTaskStart(String taskStartYear, String taskStartMonth,
							String taskStartDay) {

		this.taskStartYear = Integer.parseInt(taskStartYear);
		this.taskStartMonth = Integer.parseInt(taskStartMonth);
		this.taskStartDay = Integer.parseInt(taskStartDay);

		StringBuilder buf1 = new StringBuilder();
		buf1.append(taskStartYear);
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.taskStartMonth));
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.taskStartDay));
		String taskStart = buf1.toString();

		//日付の有効性チェック
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	    // 日付/時刻解析を厳密に行うかどうかを設定する。
	    format.setLenient(false);
	    try {
	        format.parse(taskStart);
	        this.taskStart = taskStart;
	        chkTaskStart = "OK";
	    } catch (Exception e) {
	    	chkTaskStart = "開始日が不正です。 " + taskStart;
	    }
	}

	/**
	 * taskEndを設定します。（taskStartを先に設定すること！）
	 * @param taskEnd taskEnd
	 */
	public void setTaskEnd(String taskEndYear, String taskEndMonth,
							String taskEndDay) {

		this.taskEndYear = Integer.parseInt(taskEndYear);
		this.taskEndMonth = Integer.parseInt(taskEndMonth);
		this.taskEndDay = Integer.parseInt(taskEndDay);

		StringBuilder buf1 = new StringBuilder();
		buf1.append(taskEndYear);
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.taskEndMonth));
		buf1.append("/");
		buf1.append(String.format("%1$02d", this.taskEndDay));
		String taskEnd = buf1.toString();

		//日付の有効性チェック
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	    // 日付/時刻解析を厳密に行うかどうかを設定する。
	    format.setLenient(false);
	    try {
	        format.parse(taskEnd);
	        this.taskEnd = taskEnd;
	        chkTaskEnd = "OK";
	    } catch (Exception e) {
	    	chkTaskEnd = "終了日が不正です。 " + taskEnd;
	    }

	    //開始日との相関チェック
	    SimpleDateFormat DF = new SimpleDateFormat("yyyy/MM/dd");
	    try {
			if(DF.parse(taskEnd).before(DF.parse(taskStart))){
				chkTaskEnd = "終了日が開始日より前です。 " + taskEnd ;
			}
		} catch (ParseException e) {
				chkTaskEnd = "エラー" + taskEnd ;
		}
	}

	/**
	 * taskMinutesを設定します。
	 * @param taskMinutes taskMinutes
	 */
	public void setTaskMinutes(String taskHours, String taskMinutes) {
		if((taskHours == null || taskHours == "") &&
			(taskMinutes == null || taskMinutes == "")){
				chkTaskMinutes = "作業予定時間が入力されていません。";
			}else{
				//数値チェック
				if(taskHours ==null || taskHours ==""){ taskHours = "0"; }
				if(taskMinutes ==null || taskMinutes ==""){ taskMinutes = "0"; }
				try {
			        this.taskMinutes = (Integer.parseInt(taskHours)*60)
			        					+ Integer.parseInt(taskMinutes);
			        chkTaskMinutes = "OK";
				} catch (NumberFormatException e) {
			        chkTaskMinutes = "作業予定時間に数値以外が入力されています。 "
			        					+ taskHours + taskMinutes;
			    }
			}
	}

	/**
	 * taskTotalを設定します。
	 * @param taskTotal taskTotal
	 */
	public void setTaskTotal(String taskTotal) {
		if(taskTotal == null || taskTotal == ""){
			chkTaskTotal = "作業量が入力されていません。";
		}else{
			try{
				this.taskTotal = Integer.parseInt(taskTotal);
				chkTaskTotal = "OK";
			}catch(NumberFormatException e){
				chkTaskTotal = "作業量に数値以外が入力されています。　" + taskTotal;
			}
		}
	}

	/**
	 * taskWorkMinutesを設定します。
	 * @param taskWorkMinutes taskWorkMinutes
	 */
	public void setTaskWorkMinutes(String taskWorkHours, String taskWorkMinutes) {

		if((taskWorkHours == null || taskWorkHours == "") &&
				(taskWorkMinutes == null || taskWorkMinutes == "")){
			chkTaskWorkMinutes = "作業予定時間が入力されていません。";
		}else{
			//数値チェック
			if(taskWorkHours ==null || taskWorkHours ==""){ taskWorkHours = "0"; }
			if(taskWorkMinutes ==null || taskWorkMinutes ==""){ taskWorkMinutes = "0"; }
			try {
		        this.taskWorkMinutes = (Integer.parseInt(taskWorkHours)*60)
		        					+ Integer.parseInt(taskWorkMinutes);
		        chkTaskWorkMinutes = "OK";
			} catch (NumberFormatException e) {
		        chkTaskWorkMinutes = "作業予定時間に数値以外が入力されています。 "
		        					+ taskWorkHours + taskWorkMinutes;
		    }
		}
	}

	/**
	 * taskWorkloadを設定します。
	 * @param taskWorkload taskWorkload
	 */
	public void setTaskWorkload(String taskWorkload) {

		if(taskWorkload == null || taskWorkload == ""){
			chkTaskWorkload = "作業量が入力されていません。";
		}else{
			try{
				this.taskWorkload = Integer.parseInt(taskWorkload);
				chkTaskWorkload = "OK";
			}catch(NumberFormatException e){
				chkTaskWorkload = "作業量に数値以外が入力されています。　" + taskWorkload;
			}
		}
	}

	public String chkTaskID(){ return chkTaskID; }
	public String chkUserID(){ return chkUserID; }
	public String chkTaskName(){ return chkTaskName; }
	public String chkTaskContent(){ return chkTaskContent; }
	public String chkTaskType(){ return chkTaskType; }
	public String chkTaskPriority(){ return chkTaskPriority; }
	public String chkTaskStart(){ return chkTaskStart; }
	public String chkTaskEnd(){ return chkTaskEnd; }
	public String chkTaskMinutes(){ return chkTaskMinutes; }
	public String chkTaskTotal(){ return chkTaskTotal; }
	public String chkTaskWorkMinutes(){ return chkTaskWorkMinutes; }
	public String chkTaskWorkload(){ return chkTaskWorkload; }

	/**
	 * taskPriorityを取得します。
	 * @return taskPriority
	 */
	public String getTaskPriority() {
	    return taskPriority;
	}

	/**
	 * taskStartを取得します。
	 * @return taskStart
	 */
	public String getTaskStart() {
	    return taskStart;
	}

	/**
	 * taskEndを取得します。
	 * @return taskEnd
	 */
	public String getTaskEnd() {
	    return taskEnd;
	}

	/**
	 * taskMinutesを取得します。
	 * @return taskMinutes
	 */
	public int getTaskMinutes() {
	    return taskMinutes;
	}

	/**
	 * taskTotalを取得します。
	 * @return taskTotal
	 */
	public int getTaskTotal() {
	    return taskTotal;
	}

	/**
	 * taskWorkMinutesを取得します。
	 * @return taskWorkMinutes
	 */
	public int getTaskWorkMinutes() {
	    return taskWorkMinutes;
	}

	/**
	 * taskWorkloadを取得します。
	 * @return taskWorkload
	 */
	public int getTaskWorkload() {
	    return taskWorkload;
	}

	/**
	 * 今日のタスクを取得します。完了分は取得しません。
	 * @return List<String>
	 */
	public List<String> todayTask(String email){
		List<String> todayTaskList = new ArrayList<String>();
		List<String> todayTaskList1 = new ArrayList<String>();
		List<String> todayTaskList2 = new ArrayList<String>();
		List<String> todayTaskList3 = new ArrayList<String>();
		StringBuilder str = new StringBuilder();


		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		FetchOptions fetch =FetchOptions.Builder.withOffset(0).limit(100);

		Query query = new Query("TASK");

		// QueryクラスのaddFilterメソッドを用いて条件を指定
		query.setFilter(new Query.FilterPredicate("userID", FilterOperator.EQUAL, email));
		query.addSort("taskStart",Query.SortDirection.ASCENDING);

		// 作成したクエリからPrepareQueryクラスのオブジェクトを生成
		PreparedQuery pQuery = ds.prepare(query);

		List list1 = pQuery.asList(fetch);

		if(list1.size() > 0){

			TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

			Entity task;
			Date now1 = new Date();
			format.setTimeZone(tz);

			for(int i = 0; i < list1.size(); i++){
				str.setLength(0);
				task = (Entity)list1.get(i);

				try {
					Date today = format.parse(format.format(now1));
					Date taskStart = format.parse(task.getProperty("taskStart").toString());
					Date taskEnd = format.parse(task.getProperty("taskEnd").toString());
					int taskTotal = Integer.parseInt(task.getProperty("taskTotal").toString());
					int taskWorkload = Integer.parseInt(task.getProperty("taskWorkload").toString());

	        		String strEnd = task.getProperty("taskEnd").toString();
	        		String[] e = strEnd.split("/");
	        		int e1 = Integer.parseInt(e[0]);
	        		int e2 = Integer.parseInt(e[1]);
	        		int e3 = Integer.parseInt(e[2]);

	        		BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
	        		Calendar calEnd = Calendar.getInstance();
	        		Calendar calToday = Calendar.getInstance();
	        		calEnd.set(e1, e2 - 1, e3);

					float days;
	        		if(calEnd.compareTo(calToday) >= 0){
	            		float all = Integer.parseInt(task.getProperty("taskDays").toString());
	            		float left = bdc.countDays(calToday, calEnd);
	            		days = ( all - left ) / all * 100;
	        		}else{ days = 100; }

					//開始日以降かつ作業が完了していないもののみ出力
//					if(taskStart.compareTo(today) <= 0 && taskEnd.compareTo(today) >= 0 &&
//						taskTotal > taskWorkload){
					if(taskStart.compareTo(today) <= 0 && taskTotal > taskWorkload){

						if(days == 100){
							str.append("★");
							str.append(task.getProperty("taskName"));
							todayTaskList1.add(str.toString());

						}else if(days > 80){
							str.append("●");
							str.append(task.getProperty("taskName"));
							todayTaskList2.add(str.toString());

						}else{
							str.append("○");
							str.append(task.getProperty("taskName"));
							todayTaskList3.add(str.toString());

						}
					}

				} catch (ParseException e) {
					// TODO 自動生成された catch ブロック
					str.append("日付判定エラー：");
					str.append(task.getProperty("taskName").toString());
				}
			}
		}

		todayTaskList.addAll(todayTaskList1);
		todayTaskList.addAll(todayTaskList2);
		todayTaskList.addAll(todayTaskList3);

		return todayTaskList;
	}

	public void taskResultInsert(Long taskID, int workload, int workTime){

		Key taskKey = KeyFactory.createKey("TASK", taskID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity task;
		try {
			task = ds.get(taskKey);

			int wl;
			int rt;
			try{
				wl = Integer.parseInt(task.getProperty("taskWorkload").toString());
				wl = wl + workload;
			}catch(NumberFormatException e){
				wl = workload;
			}
			try{
				rt = Integer.parseInt(task.getProperty("taskWorkMinutes").toString());
				rt = rt + workTime;
			}catch(NumberFormatException e){
				rt = workTime;
			}

			float floatTotal = Integer.parseInt(task.getProperty("taskTotal").toString());
			float floatWorkload = wl;
			float floatTaskPercentage = floatWorkload / floatTotal * 100;
			if(floatTaskPercentage > 100){ floatTaskPercentage = 100; }

			task.setProperty("taskWorkload", wl);
			task.setProperty("taskWorkMinutes", rt);
			task.setProperty("taskPercentage", Math.floor(floatTaskPercentage) );

			ds.put(task);

		} catch (EntityNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}

	public void taskResultDelete(Long taskID, int workload, int workTime){

		Key taskKey = KeyFactory.createKey("TASK", taskID);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity task;
		try {
			task = ds.get(taskKey);

			int taskWorkload = Integer.parseInt(task.getProperty("taskWorkload").toString());
			int taskWorkMinutes = Integer.parseInt(task.getProperty("taskWorkMinutes").toString());

			taskWorkload = taskWorkload - workload;
			taskWorkMinutes = taskWorkMinutes - workTime;

			float floatTotal = Integer.parseInt(task.getProperty("taskTotal").toString());
			float floatWorkload = taskWorkload;
			float floatTaskPercentage = floatWorkload / floatTotal * 100;
			if(floatTaskPercentage > 100){ floatTaskPercentage = 100; }

			task.setProperty("taskWorkload", taskWorkload);
			task.setProperty("taskWorkMinutes", taskWorkMinutes);
			task.setProperty("taskPercentage", Math.floor(floatTaskPercentage) );

			ds.put(task);

		} catch (EntityNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}


	public void taskWorkDaysAdd(Key taskKey){

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity task =ds.get(taskKey);

//			if(task.getProperty("taskDays") != null && task.getProperty("taskWorkDays") != null
//				&& task.getProperty("taskDays").equals(task.getProperty("taskWorkDays"))){
//				//処理しない
//			}else{

	    		String strEnd = task.getProperty("taskEnd").toString();
	    		String[] e = strEnd.split("/");
	    		int e1 = Integer.parseInt(e[0]);
	    		int e2 = Integer.parseInt(e[1]);
	    		int e3 = Integer.parseInt(e[2]);

				BusinessDayCalculator bdc = new BusinessDayCalculator(new DefaultJapaneseDayOffResolver());
				TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
				Calendar calEnd = Calendar.getInstance(tz);
				Calendar calToday = Calendar.getInstance(tz);

	    		calEnd.set(e1, e2 - 1, e3);

        		int taskDays = Integer.parseInt(task.getProperty("taskDays").toString());
        		int countDays = bdc.countDays(calToday, calEnd);

        		if(countDays >= 0 && countDays <= taskDays){
        			int Days = taskDays - countDays;
        			float wk1 = taskDays;
        			float wk2 = Days;
        			float wk3 = wk2 / wk1 * 100;

        			task.setProperty("taskWorkDays", Days);
        			task.setProperty("taskWorkPercentage", Math.floor(wk3));
        			ds.put(task);
        		}else if(countDays > taskDays){
         			task.setProperty("taskWorkDays", 0);
        			task.setProperty("taskWorkPercentage", 0);
         			ds.put(task);
        		}else if(countDays < 0){
        			task.setProperty("taskWorkDays", taskDays);
        			task.setProperty("taskWorkPercentage", 100);
        			ds.put(task);
        		}

//			}

		} catch (EntityNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}


	/**
	 * taskWorkDaysを取得します。
	 * @return taskWorkDays
	 */
	public int getTaskWorkDays() {
	    return taskWorkDays;
	}

	/**
	 * taskWorkDaysを設定します。
	 * @param taskWorkDays taskWorkDays
	 */
	public void setTaskWorkDays(int taskWorkDays) {
	    this.taskWorkDays = taskWorkDays;
	}

	/**
	 * taskWorkPercentageを取得します。
	 * @return taskWorkPercentage
	 */
	public float getTaskWorkPercentage() {
	    return taskWorkPercentage;
	}

	/**
	 * taskWorkPercentageを設定します。
	 * @param taskWorkPercentage taskWorkPercentage
	 */
	public void setTaskWorkPercentage(int taskWorkPercentage) {
	    this.taskWorkPercentage = taskWorkPercentage;
	}

	/**
	 * taskPercentageを取得します。
	 * @return taskPercentage
	 */
	public float getTaskPercentage() {
	    return taskPercentage;
	}

	/**
	 * taskPercentageを設定します。
	 * @param taskPercentage taskPercentage
	 */
	public void setTaskPercentage(int taskPercentage) {
	    this.taskPercentage = taskPercentage;
	}

	/**
	 * taskStartYearを取得します。
	 * @return taskStartYear
	 */
	public int getTaskStartYear() {
	    return taskStartYear;
	}

	/**
	 * taskStartMonthを取得します。
	 * @return taskStartMonth
	 */
	public int getTaskStartMonth() {
	    return taskStartMonth;
	}

	/**
	 * taskStartDayを取得します。
	 * @return taskStartDay
	 */
	public int getTaskStartDay() {
	    return taskStartDay;
	}

	/**
	 * taskEndYearを取得します。
	 * @return taskEndYear
	 */
	public int getTaskEndYear() {
	    return taskEndYear;
	}

	/**
	 * taskEndMonthを取得します。
	 * @return taskEndMonth
	 */
	public int getTaskEndMonth() {
	    return taskEndMonth;
	}

	/**
	 * taskEndDayを取得します。
	 * @return taskEndDay
	 */
	public int getTaskEndDay() {
	    return taskEndDay;
	}

}