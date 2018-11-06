package com.ysl.myapplication.TouchListener;

import android.view.MotionEvent;
import android.view.View;

import android.os.Handler;

public class OnDoubleClickListener implements View.OnTouchListener{
	public int count = 0;
	private long firstClick = 0;
	private long secondClick = 0;
	private Handler handler;

	/**
	 * 两次点击的时间间隔，单位毫秒
	 */
	private static int totalTime = 500;

	/**
	 * 自定义回调接口
	 */
	private DoubleClickCallback mCallback;

	public interface DoubleClickCallback{
		void onSingleClick(View v);
		void onDoubleClick(View v);
	}

	public OnDoubleClickListener(DoubleClickCallback callback) {
		super();
		this.mCallback = callback;
		handler = new Handler();
	}

	/**
	 *触发事件处理
	 * @param v
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()){
			count++;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (count == 1){
						mCallback.onSingleClick(v);
					}else if (count == 2){
						mCallback.onDoubleClick(v);
					}
					handler.removeCallbacksAndMessages(null);
					//清空handler延时，并防内存泄漏
					count = 0;
				}
			},totalTime);
//			if (1 == count){
//				firstClick = System.currentTimeMillis();
//			}else if (2 == count){
//				secondClick = System.currentTimeMillis();
//				if (secondClick - firstClick < totalTime){
//					if (mCallback != null){
//						mCallback.onDoubleClick();
//					}
//					count = 0;
//					firstClick = 0;
//				}else {
//					firstClick = secondClick;
//					count = 1;
//				}
//				secondClick = 0;
//			}
		}
		return false;
	}
}
