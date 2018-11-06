package com.ysl.myapplication.BubbleTips;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class BubblePopupWindow extends PopupWindow {

	private BubbleRelativeLayout bubbleView;
	private Context context;

	public BubblePopupWindow(Context context) {
		this.context = context;
		setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		//外部点击不可取消？？？
		setOutsideTouchable(false);
		setClippingEnabled(false);

		ColorDrawable dw = new ColorDrawable(0);
		setBackgroundDrawable(dw);
	}

	public void setBubbleView(View v){
		bubbleView = new BubbleRelativeLayout(context);
		bubbleView.setBackgroundColor(Color.TRANSPARENT);
		bubbleView.addView(v);
		setContentView(bubbleView);
	}

	public void setParam(int width, int height){
		setWidth(width);
		setHeight(height);
	}

	public void show(View parent){
		show(parent, Gravity.TOP, getMeasuredWidth() / 2);
	}

	public void show(View parent,int gravity){
		show(parent, gravity, getMeasuredWidth() / 2);
	}

	/**
	 * 显示弹窗
	 * @param parent
	 * @param gravity
	 * @param bubbleOffset 气泡尖角位置偏移量。默认位于中间
	 */
	public void show(View parent,int gravity,float bubbleOffset){
		BubbleRelativeLayout.BubbleLegOrientation orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
		if (!this.isShowing()){
			switch (gravity){
				case Gravity.BOTTOM:
					orientation = BubbleRelativeLayout.BubbleLegOrientation.TOP;
					break;
				case Gravity.TOP:
					orientation = BubbleRelativeLayout.BubbleLegOrientation.BOTTOM;
					break;
				case Gravity.RIGHT:
					orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
					break;
				case Gravity.LEFT:
					orientation = BubbleRelativeLayout.BubbleLegOrientation.RIGHT;
					break;
				default:
					break;
			}
			//设置气泡布局方向及尖角偏移
			bubbleView.setBubbleParams(orientation,bubbleOffset);

			int[] location = new int[2];
			parent.getLocationOnScreen(location);

			switch (gravity){
				case Gravity.BOTTOM:
					showAsDropDown(parent);
					break;
				case Gravity.TOP:
					showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - getMeasuredHeight());
					break;
				case Gravity.RIGHT:
					showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - (parent.getHeight() / 2));
					break;
				case Gravity.LEFT:
					showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - getMeasuredWidth(), location[1] - (parent.getHeight() / 2));
					break;
				default:
					break;
			}
		}else {
			this.dismiss();
		}
	}
	/**
	 * 测量高度
	 * @return
	 */
	private int getMeasuredWidth() {
		//MeasureSpc类封装了父View传递给子View的布局(layout)要求。每个MeasureSpc实例代表宽度或者高度。
		//UNSPECIFIED（未指定）：父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
		//EXACTLY（完全）：父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身的大小；
		//AT_MOST（最多）：子元素至最多达到指定大小的值。
		getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
		int popWidth = getContentView().getMeasuredWidth();
		return popWidth;
	}

	/**
	 * 测量宽度
	 * @return
	 */
	private int getMeasuredHeight(){
		getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
		int popHeight = getContentView().getMeasuredHeight();
		return popHeight;
	}
}
