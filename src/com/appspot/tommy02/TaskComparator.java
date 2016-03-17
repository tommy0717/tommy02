package com.appspot.tommy02;

import java.util.Comparator;

import com.google.appengine.api.datastore.Entity;

public class TaskComparator implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		//タスクを終了日、タスク進捗率で並び替える。

		Entity task1 = (Entity)o1;
		Entity task2 = (Entity)o2;

		String[] e1 = task1.getProperty("taskEnd").toString().split("/");
		int endYear1 = Integer.parseInt(e1[0]);
		int endMonth1 = Integer.parseInt(e1[1]);
		int endDay1 = Integer.parseInt(e1[2]);

		String[] e2 = task2.getProperty("taskEnd").toString().split("/");
		int endYear2 = Integer.parseInt(e2[0]);
		int endMonth2 = Integer.parseInt(e2[1]);
		int endDay2 = Integer.parseInt(e2[2]);

		String[] s1 = task1.getProperty("taskStart").toString().split("/");
		int startYear1 = Integer.parseInt(s1[0]);
		int startMonth1 = Integer.parseInt(s1[1]);
		int startDay1 = Integer.parseInt(s1[2]);

		String[] s2 = task2.getProperty("taskStart").toString().split("/");
		int startYear2 = Integer.parseInt(s2[0]);
		int startMonth2 = Integer.parseInt(s2[1]);
		int startDay2 = Integer.parseInt(s2[2]);

		float taskPercentage1 = Float.parseFloat(task1.getProperty("taskPercentage").toString());
		float taskPercentage2 = Float.parseFloat(task2.getProperty("taskPercentage").toString());

		if(endYear1 < endYear2){
			return -1;

		}else if(endYear1 > endYear2){
			return 1;

		}else{

			if(endMonth1 < endMonth2){
				return -1;

			}else if(endMonth1 > endMonth2){
				return 1;

			}else{

				if(endDay1 < endDay2){
					return -1;

				}else if(endDay1 > endDay2){
					return 1;

				}else{


					if(startYear1 < startYear2){
						return -1;

					}else if(startYear1 > startYear2){
						return 1;

					}else{

						if(startMonth1 < startMonth2){
							return -1;

						}else if(startMonth1 > startMonth2){
							return 1;

						}else{

							if(startDay1 < startDay2){
								return -1;

							}else if(startDay1 > startDay2){
								return 1;

							}else{

								if(taskPercentage1 < taskPercentage2){
									return -1;

								}else if(taskPercentage1 > taskPercentage2){
									return 1;

								}else{
									return 0;

								}
							}
						}
					}
				}
			}
		}
	}
}
