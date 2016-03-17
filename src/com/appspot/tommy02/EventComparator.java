package com.appspot.tommy02;

import java.util.Comparator;

public class EventComparator implements Comparator<Event>{

	@Override
	public int compare(Event e1, Event e2) {

		if(e1.startHours < e2.startHours){
			return -1;
		}else if(e1.startHours > e2.startHours){
			return 1;
		}else{
			if(e1.startMinutes < e2.startMinutes){
				return -1;
			}else if(e1.startMinutes > e2.startMinutes){
				return 1;
			}else{
				return 0;
			}
		}
	}
}