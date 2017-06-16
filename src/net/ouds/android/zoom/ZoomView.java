package biz.ouds.android.zoom;

import android.view.MotionEvent;
import android.view.View;

public abstract class ZoomView<V extends View> {

	protected V view;
	
	private boolean isMultiTouch = false;
	private double startingDistance;
	protected float zoomScale = 0.1f;

	public ZoomView(V view, float scale) {
		this.view = view;
		this.zoomScale = scale;
		
		setTouchListener();
	}
	
	private static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	private void setTouchListener() {
		
		view.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getPointerCount() == 2) {
					
					if (isMultiTouch == false) {
						startingDistance = getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						isMultiTouch = true;
					}
					else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						double distance = getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						
						if (distance > startingDistance)
							zoomIn();
						else if (distance < startingDistance)
							zoomOut();
						
						view.invalidate();
					}
					
				}
				else
					isMultiTouch = false;
				
				return isMultiTouch;
			}
		});
		
	}

	protected abstract void zoomIn();

	protected abstract void zoomOut();
	
}


