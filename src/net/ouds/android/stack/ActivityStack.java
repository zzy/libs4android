package biz.ouds.android.stack;

import java.util.Stack;

import android.app.Activity;

public class ActivityStack {
	
	private static Stack<Activity> stack;
	private static ActivityStack instance;

	private ActivityStack() {
	}

	public static ActivityStack getActivityStack() {
		if (null == instance)
			instance = new ActivityStack();
		
		return instance;
	}

	// 退出栈顶Activity
	public void pullActivity(Activity activity) {
		if (null != activity) {
			activity.finish();
			stack.remove(activity);
			activity = null;
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = (Activity) stack.lastElement();
		
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (null == stack)
			stack = new Stack<Activity>();
		
		stack.add(activity);
	}

	// 退出栈中所有Activity
	public void pullAllActivityExceptOne(Class<?> cls) {
		while (true) {
			Activity activity = currentActivity();
			
			if (null == activity)
				break;
			
			if (activity.getClass().equals(cls))
				break;
			
			pullActivity(activity);
		}
	}
	
}
