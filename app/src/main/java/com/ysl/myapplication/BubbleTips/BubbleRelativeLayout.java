package com.ysl.myapplication.BubbleTips;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ysl.myapplication.R;

public class BubbleRelativeLayout extends RelativeLayout {

	/**
	 *
	 * 气泡的尖角方向
	 */

	public enum BubbleLegOrientation{
		TOP,LEFT,RIGHT,BOTTOM,NONE
	}

	//布局属性
	public static int PADDING = 30;
	public static int LEG_HALF_BASE = 30;
	public static float STROKE_WIDTH = 2.0f;
	public static float CORNER_RADIUS = 8.0f;
	public static int SHADOW_COLOR = Color.argb(100, 0, 0, 0);
	public static float MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;

	//路径与画笔
	private Paint mFillPaint = null;
	private final Path mPath  = new Path();
	private final Path mBubbleLegPrototype = new Path();
	//使位图进行有利的抖动的位掩码标志
	private final Paint mPaint = new Paint(Paint.DITHER_FLAG);

	//一些莫名其妙的属性
	private float mBubbleLegOffset = 0.75f;
	private BubbleLegOrientation mBubbleOrientation = BubbleLegOrientation.LEFT;

	public BubbleRelativeLayout(Context context) {
		this(context,null);
	}

	public BubbleRelativeLayout(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public BubbleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
		init(context,attrs);
	}

	/**
	 * 初始化
	 * @param context
	 * @param attrs
	 */
	private void init(final Context context, final AttributeSet attrs) {

		//长宽充满布局
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setLayoutParams(params);

		//从attrs.xml中获取属性
		if (attrs != null){
			TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.bubble);

			try {
				PADDING = a.getDimensionPixelSize(R.styleable.bubble_padding,PADDING);
				SHADOW_COLOR = a.getInt(R.styleable.bubble_shadowColor,SHADOW_COLOR);
				LEG_HALF_BASE = a.getDimensionPixelSize(R.styleable.bubble_halfBaseOfLeg,LEG_HALF_BASE);
				MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;
				STROKE_WIDTH = a.getFloat(R.styleable.bubble_strokeWidth,STROKE_WIDTH);
				CORNER_RADIUS = a.getFloat(R.styleable.bubble_cornerRadius,CORNER_RADIUS);
			}finally {
				if (a != null){
					a.recycle();
				}
			}
		}

		//设置画笔属性
		mPaint.setColor(SHADOW_COLOR);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeCap(Paint.Cap.BUTT);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(STROKE_WIDTH);
		mPaint.setStrokeJoin(Paint.Join.MITER);
		mPaint.setPathEffect(new CornerPathEffect(CORNER_RADIUS));

		//版本控制
		if (Build.VERSION.SDK_INT >= 11){
			//硬件加速
			setLayerType(LAYER_TYPE_SOFTWARE,mPaint);
		}

		//填充画笔，应该是内部
		mFillPaint = new Paint(mPaint);
		mFillPaint.setColor(Color.WHITE);
		mFillPaint.setShader(new LinearGradient(100f,0f,100f,200f,Color.WHITE,Color.WHITE, Shader.TileMode.CLAMP));

		if (Build.VERSION.SDK_INT >= 11){
			setLayerType(LAYER_TYPE_SOFTWARE,mFillPaint);
		}
		mFillPaint.setShadowLayer(2f,2f,5f,SHADOW_COLOR);

		//递交气泡脚的属性
		renderBubbleLegPrototype();

		//设置四个内边距
		setPadding(PADDING,PADDING,PADDING,PADDING);
	}

	//用于监测配置变更的，不知道什么用
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 尖角区域
	 */
	private void renderBubbleLegPrototype() {
		mBubbleLegPrototype.moveTo(0,0);
		mBubbleLegPrototype.lineTo(PADDING * 1.5f , -PADDING / 1.5f);
		mBubbleLegPrototype.lineTo(PADDING * 1.5f , PADDING / 1.5f);
		//形成闭合区域
		mBubbleLegPrototype.close();
	}

	public void setBubbleParams(final BubbleLegOrientation bubbleOrientation,final float bubbleOffset){
		mBubbleOrientation = bubbleOrientation;
		mBubbleLegOffset = bubbleOffset;
	}

	/**
	 * 根据显示的方向，获取尖角位置矩阵
	 * 就是一个矩阵，主要功能就是坐标映射，数值转换
	 */
	private Matrix renderBubbleLegMatrix(final float width , final float height){

		final float offset = Math.max(mBubbleLegOffset,MIN_LEG_DISTANCE);

		float dstX = 0;
		float dstY = Math.min(offset,height - MIN_LEG_DISTANCE);
		final Matrix matrix = new Matrix();

		//复用一个布局，不同方向只需要旋转即可
		switch (mBubbleOrientation) {

			case TOP:
				dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
				dstY = 0;
				matrix.postRotate(90);
				break;

			case RIGHT:
				dstX = width;
				dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
				matrix.postRotate(180);
				break;

			case BOTTOM:
				dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
				dstY = height;
				matrix.postRotate(270);
				break;

		}

		//提交变换
		matrix.postTranslate(dstX,dstY);
		return matrix;
	}

	//开始绘制
	@Override
	protected void onDraw(Canvas canvas) {
		final float width = canvas.getWidth();
		final float height = canvas.getHeight();

		//rewind会保留内部的数据结构，但不保留FillType
		mPath.rewind();

		mPath.addRoundRect(new RectF(PADDING,PADDING,width - PADDING,height - PADDING),
				CORNER_RADIUS,CORNER_RADIUS, Path.Direction.CW);
		mPath.addPath(mBubbleLegPrototype,renderBubbleLegMatrix(width,height));

		canvas.drawPath(mPath,mPaint);
		canvas.scale((width - STROKE_WIDTH)/width,(height - STROKE_WIDTH)/height, width/2f, height/2f);

		canvas.drawPath(mPath , mFillPaint);
	}
}
