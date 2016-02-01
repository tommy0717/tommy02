package com.appspot.tommy02;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


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
			TASK.setProperty("taskDays", bdc.countDays(from, to));
			TASK.setProperty("taskMinutes", taskMinutes);
			TASK.setProperty("taskWorkload", taskWorkload);
			TASK.setProperty("taskWorkMinutes", taskWorkMinutes);
			TASK.setProperty("taskTotal", taskTotal);
			ds.put(TASK);

			resultInsert = "OK";

		}else{
			resultInsert = "NG";
		}
		return resultInsert;
	}

	public String update(){
		String resultUpdate;
		int count = 0;
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
			if(chkTaskStart() == "OK"){ task.setProperty("taskStart", taskStart); count++; }
			if(chkTaskEnd() == "OK"){ task.setProperty("taskEnd", taskEnd); count++; }
			if(chkTaskMinutes() == "OK"){ task.setProperty("taskMinutes", taskMinutes); count++; }
			if(chkTaskTotal() == "OK"){ task.setProperty("taskTotal", taskTotal); count++; }
			if(chkTaskWorkMinutes() == "OK"){ task.setProperty("taskWorkMinutes", taskWorkMinutes); count++; }
			if(chkTaskWorkload() == "OK"){ task.setProperty("taskWorkload", taskWorkload); count++; }

			if(count > 0){
				ds.put(task);
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
			taskEnd = task.getProperty("taskEnd").toString();
			taskMinutes = Integer.parseInt(task.getProperty("taskMinutes").toString());
			taskTotal = Integer.parseInt(task.getProperty("taskTotal").toString());
			taskWorkMinutes = Integer.parseInt(task.getProperty("taskWorkMinutes").toString());
			taskWorkload = Integer.parseInt(task.getProperty("taskWorkload").toString());

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
			chkTaskContent = "タスク内容が入力されていません。";
		}else if(taskContent.length() > 50){
			chkTaskContent = "タスク内容は15文字以内で入力してください。";
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

}