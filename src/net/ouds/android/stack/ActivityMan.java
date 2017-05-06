package net.ouds.android.stack;


import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ActivityMan extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityMan instance;
	
	public static ActivityMan getInstance() {
		if (null == instance)
			instance = new ActivityMan();
		
		return instance;
	}
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void destroyActivities() {
		for (Activity activity : activityList) {
			if(!activity.isFinishing())
				activity.finish();
		}
		
		activityList.clear();
	}
}
