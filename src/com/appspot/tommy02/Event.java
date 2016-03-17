package com.appspot.tommy02;

public class Event {

	public int startHours;
	public int startMinutes;
	public int endHours;
	public int endMinutes;
	public String name;
	public String displayName;

	public Event(int startHours, int startMinutes, int endHours, int endMinutes,
					String name, String displayName){

		this.startHours = startHours;
		this.startMinutes = startMinutes;
		this.endHours = endHours;
		this.endMinutes = endMinutes;
		this.name = name;
		this.displayName = displayName;
	}


}
