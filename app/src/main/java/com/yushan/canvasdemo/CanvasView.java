package com.yushan.canvasdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义折线图view
 *
 * @author yushan
 */
public class CanvasView extends View {

    protected int oldX;                       // 上传Touch的X轴坐标
    protected int mTouchSlop;
    protected boolean mIsBeingDragged = false;// 滑动是否开始
    protected Paint mPaint;                   // 画笔
    private Paint mInnerCirclePaint;          // 绘制内圆画笔
    private Paint mOuterCirclePaint;          // 绘制外圆画笔
    private Paint mTitlePaint;                // 绘制标题画笔

    private int mActivePointerId;

    protected List<Integer>[] mPoints;        // 数据点集合
    protected List<Integer>[] mData;          // 其他数据集合

    private List<String[]> mXAxisValues;      // x轴坐标集合

    protected int mDistance = 100;            // 绘制点之间的距离
    private int mMaxHeight = 180;             // y轴最大值
    private int mCenterPosition = -1;         // 中心点
    private int mYTitleWidth = 40;            // Y轴文字对应的宽度

    private Rect mYTitleRect;                 // Y轴对应的Rect dataOne
    private Rect nYTitleRect;                 // Y轴对应的Rect dataTwo
    private Rect pYTitleRect;                 // Y轴对应的Rect dataThree

    private int mDrawCount = 7;               // 绘制多少个点 ，默认绘制5个

    protected IActionEndListener mAEndListener;
    protected IPassCenterListener mPCenterListener;

    private float mTrendLineSize = 2;         // 趋势图背景颜色
    protected int mTowards;                   // 滑动方向
    protected int[] mPointColors;             // 趋势图中折线颜色

    protected float mTextColorSize = 10;      // 字体颜色
    protected float mTextColorSmall = 9;      // 字体颜色

    protected int mYCenterSize = 2;           // y轴中心线的大小
    private int focusTextSize = 13;           // 焦点对应的时间值大小

    protected int mInnerCircleColor = 0xFFFFFEFF; //内圆颜色
    protected int mInnerCircleSize = 10;      // 内圆大小
    protected int mOuterCircleSize = 16;      // 外圆大小
    private int mCenterColor = 0xFFFFD583;
    private int mOuterCircleRadius = 6;
    private int mInnerCircleRadius = 4;

    // dataOne界限区域范围
    public static float RatioUp = (float) 0.20;
    public static float RatioDown = (float) 0.68;
    private Paint mRangeTrendBackgroundPaint;
    private int[] mRangeTrendColors;          // dataOne的参考范围背景颜色

    // dataTwo界限区域范围
    public static float colorUp = (float) 0.32;
    public static float colorDown = (float) 0.57;
    private Paint nRangeTrendBackgroundPaint;
    private int[] nRangeTrendColors;          // dataTwo的参考范围背景颜色

    // dataThree界限区域范围
    public static float mPulseUp = (float) 0.68;
    public static float mPulseDown = (float) 0.18;
    private Paint mPulsePaint;
    private int[] mPulseColors;               // dataThree的参考范围背景颜色

    // dataThree曲线的颜色
    private int mPulseColor = 0XFFEF6411;

    // 每格的刻度值
    private double mScaleValue = 35.0;
    private double scaleValue = 35.0;

    private int mCenterRecorded;              // 用于每次滑动中心点center所在集合中的位置
    private int currentCenter;

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context) {
        this(context, null);
    }

    /**
     * @param pointValues    点数据集合
     * @param tempValues     其他数据集合
     * @param colors         趋势图中折线颜色
     * @param xValues        x轴坐标集
     * @param center         focus中心 暂时没有用到
     * @param centerRecorded 记录某个类型的划动之前的position位置
     */
    public void setPoints(List<Integer>[] pointValues, List<Integer>[] tempValues, int[] colors, List<String[]> xValues, int center, int centerRecorded) {
        mCenterPosition = -1;
        mPoints = pointValues;
        mCenterRecorded = centerRecorded;
        mData = tempValues;
        mPointColors = colors;
        mXAxisValues = xValues;

        setCenterPosition(mCenterRecorded);
        setSelectedInCenter(mCenterRecorded);
        invalidate();
    }

    public void setXAxisValues(List<String[]> xAxisValues) {
        this.mXAxisValues = xAxisValues;
    }

    public void setOnEndListener(IActionEndListener aEndListener) {
        this.mAEndListener = aEndListener;
    }

    public void setOnCenterListener(IPassCenterListener pCenterListener) {
        this.mPCenterListener = pCenterListener;
    }

    private void init(Context context) {
        mTextColorSize = sp2px(context, mTextColorSize);
        mTextColorSmall = sp2px(context, mTextColorSmall);
        mTrendLineSize = dp2px(context, mTrendLineSize);
        mInnerCircleSize = (int) dp2px(context, mInnerCircleSize);
        mOuterCircleSize = (int) dp2px(context, mOuterCircleSize);
        mOuterCircleRadius = (int) dp2px(context, mOuterCircleRadius);
        mInnerCircleRadius = (int) dp2px(context, mInnerCircleRadius);
        mYCenterSize = (int) dp2px(context, mYCenterSize);
        focusTextSize = (int) dp2px(context, focusTextSize);

        mPaint = new Paint();
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setStyle(Style.STROKE);
        mPaint.setAntiAlias(true);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setTextAlign(Align.CENTER);
        mInnerCirclePaint.setColor(mInnerCircleColor);
        mInnerCirclePaint.setTextSize(mInnerCircleSize);
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setTextSize(mInnerCircleSize);

        mOuterCirclePaint = new Paint();
        mOuterCirclePaint.setTextAlign(Align.CENTER);
        mOuterCirclePaint.setTextSize(mOuterCircleSize);
        mOuterCirclePaint.setAntiAlias(true);
        mOuterCirclePaint.setTextSize(mOuterCircleSize);

        mTitlePaint = new Paint();
        mTitlePaint.setTextAlign(Align.CENTER);
        mTitlePaint.setTextSize(sp2px(context, 20));
        mTitlePaint.setTextAlign(Align.CENTER);

        mRangeTrendBackgroundPaint = new Paint();

        nRangeTrendBackgroundPaint = new Paint();
        mPulsePaint = new Paint();


        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();

        mYTitleRect = new Rect();
        nYTitleRect = new Rect();
        pYTitleRect = new Rect();
        mPointColors = new int[]{0xFF349800, 0xFF0082b4};    // 画笔的颜色
        mYTitleWidth = (int) dp2px(context, mYTitleWidth);
        mRangeTrendColors = new int[]{0XFFDBF9CC, 0XFFDBF9CC, 0XFFDBF9CC};
        nRangeTrendColors = new int[]{0XFFE0F6FF, 0XFFE0F6FF, 0XFFE0F6FF, 0XFFE0F6FF};
        mPulseColors = new int[]{0XFFFFFBE4, 0XFFFFFBE4, 0XFFFFFBE4};
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mDistance = (getWidth() - mYTitleWidth) / mDrawCount;
        currentCenter = (getWidth() - mDistance);
        if (mCenterPosition == -1) {
            int positionLocal = mCenterRecorded * mDistance;
            scrollTo(positionLocal - currentCenter, 0); // 根据可显示的区域 动态计算中点
            mCenterPosition = 0;
        }

        // 设置字体、笔画宽度
//        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
//        mTitlePaint.setStrokeWidth(4);

        // draw trend background
        // 设置dataOne界限的区域
        drawBackground(canvas, mRangeTrendBackgroundPaint, mRangeTrendColors, RatioUp, RatioDown);
        // 设置dataTwo界限的区域
        drawBackground(canvas, nRangeTrendBackgroundPaint, nRangeTrendColors, colorUp, colorDown);
        // 设置dataThree界限的区域
        drawBackground(canvas, mPulsePaint, mPulseColors, mPulseUp, mPulseDown);

        // draw form
        drawForm(canvas, mTitlePaint);
        // split form
        setSplitForm(canvas, mTitlePaint, Color.WHITE, 2);
        // draw sign
        drawCenterSign(canvas, mTitlePaint, 6);


        if (mPoints != null) {
            // draw canvas line
            drawCanvasLine(canvas, mPaint, mPoints[0], mPointColors[0]);
            drawCanvasLine(canvas, mPaint, mPoints[1], mPointColors[1]);

            // draw circles
            ArrayList<Integer> conditionsOne = new ArrayList<>();
            conditionsOne.add(90);
            conditionsOne.add(140);
            ArrayList<Integer> conditionsTwo = new ArrayList<>();
            conditionsTwo.add(60);
            conditionsTwo.add(90);

            drawCircles(canvas, mPaint, mPoints[0], mPointColors[0], conditionsOne);
            drawCircles(canvas, mPaint, mPoints[1], mPointColors[1], conditionsTwo);

        }

        if (mData != null) {

            // draw canvas line
            drawCanvasLine(canvas, mPaint, mData[0], mPulseColor);

            // draw circles
            ArrayList<Integer> conditionsThree = new ArrayList<>();
            conditionsThree.add(-70);
            conditionsThree.add(-10);

            drawCircles(canvas, mPaint, mData[0], mPulseColor, conditionsThree);

        }

        //and x-axis values
        if (mXAxisValues != null && mXAxisValues.size() > 0) {
            drawXTitle(canvas, mTitlePaint, mXAxisValues);
        }

        // draw y r xis rect
        drawYTitle(canvas, mTitlePaint, Color.WHITE, 0xff888888);

    }

    private int centerPosition;

    private void drawCenterTextColor(int position) {
        if (position == centerPosition) {
            mTitlePaint.setColor(0xff0ea3f3);
            mTitlePaint.setTextSize(focusTextSize);
        } else {
            mTitlePaint.setColor(0xff888888);
            mTitlePaint.setTextSize(mTextColorSize);
        }
    }

    /**
     * View 的参数
     *
     * @return
     */
    private Map getViewParams() {

        float scrollX = getScrollX();

        int xAxisHeight_up = getFontHeight(mTitlePaint, mTextColorSize);
        int xAxisHeight_blow = getFontHeight(mPaint, mTextColorSmall) - 80;

        int xAxisTitleHeight = xAxisHeight_up + xAxisHeight_blow + 50;// 多留20出来
        int xAxisHeadHeight = 20;// 图例title （可以调整表格绘制的起始点位置X）

        int trendHeight = getHeight() - xAxisTitleHeight - xAxisHeadHeight; // 图例趋势图区域高
        int firstPosition = (getScrollX() + mYTitleWidth) / mDistance;
        int offsetCount = 5; // 偏移量, 绘制多少个超出屏幕的点

        Map<String, Object> params = new HashMap<>();
        params.put("scrollX", scrollX);
        params.put("xAxisHeight_up", xAxisHeight_up);
        params.put("xAxisHeight_blow", xAxisHeight_blow);
        params.put("xAxisTitleHeight", xAxisTitleHeight);
        params.put("xAxisHeadHeight", xAxisHeadHeight);
        params.put("trendHeight", trendHeight);
        params.put("firstPosition", firstPosition);
        params.put("offsetCount", offsetCount);

        return params;
    }

    /**
     * 绘制Y轴Title文字
     *
     * @param canvas
     * @param paint
     * @param backgroundColor
     * @param textColor
     */
    private void drawYTitle(Canvas canvas, Paint paint, int backgroundColor, int textColor) {

        Map<String, Object> params = getViewParams();
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        // 由于折线图是左右贯通的，Y轴Title在画布上会造成显示混乱，所以添加底部遮挡
        paint.setColor(backgroundColor);
        paint.setStyle(Style.FILL);
        mYTitleRect.set((int) ((Float) params.get("scrollX") + 0), 0, (int) ((Float) params.get("scrollX") + mYTitleWidth) - 30, getHeight() - 80);
        canvas.drawRect(mYTitleRect, paint);

        // and y-axis values
        paint.setColor(textColor);
        paint.setTextSize(mTextColorSize);
        //绘制Y轴值
        for (int i = 0; i <= 8; i++) {
            String showTitle;
            if (i >= 6 && i <= 8) {
                showTitle = 40 * (6 - i) + 120 + "";
            } else {
                showTitle = 40 + (5 - i) * 35 + "";
            }

            float textBaseY = ((Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (i - 1)) * 2 - (((Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (i - 1)) * 2 - fontHeight) / 2 - fontMetrics.bottom;
            canvas.drawText(showTitle, ((Float) params.get("scrollX") + mYTitleWidth / 2) - 20, textBaseY, paint);
        }
    }

    /**
     * 绘制x轴Title文字
     *
     * @param canvas
     * @param paint
     * @param data
     */
    private void drawXTitle(Canvas canvas, Paint paint, List<String[]> data) {

        paint.setColor(0xff888888);
        paint.setTextSize(mTextColorSize);
        paint.setStyle(Style.FILL);

        List<Integer> maxItem = getMaxItem();
        Map<String, Object> params = getViewParams();
        int startPosition = ((Integer) params.get("firstPosition") - (Integer) params.get("offsetCount")) >= 0 ? ((Integer) params.get("firstPosition") - (Integer) params.get("offsetCount")) : 0;
        int endPosition = maxItem.size();

        if (endPosition > startPosition && endPosition > 0) {
            float textBaseY_x_up = (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1) + 40;
            for (int i = startPosition; i < endPosition; i++) {

                drawCenterTextColor(i);
                // draw x axis up
                mYTitleRect.set(mDistance * (i - 1), (getHeight() - (Integer) params.get("xAxisHeight_up") - (Integer) params.get("xAxisHeight_blow") - 10), mDistance * (i + 1), getHeight() - (Integer) params.get("xAxisHeight_blow") - 10);
                canvas.drawText(data.get(i)[0].toString(), mYTitleRect.centerX(), textBaseY_x_up, paint);
            }
        }
    }

    /**
     * 绘制圆
     *
     * @param canvas
     * @param paint
     * @param canvasLine
     * @param color
     * @param condition
     */
    private void drawCircles(Canvas canvas, Paint paint, List<Integer> canvasLine, int color, ArrayList<Integer> condition) {

        ArrayList<Integer> LinePosition = dealCanvasData(canvasLine);
        int startPosition = LinePosition.get(0);
        int endPosition = LinePosition.get(1);

        Map<String, Object> params = getViewParams();

        mOuterCirclePaint.setStrokeWidth(mTrendLineSize);
        mInnerCirclePaint.setStrokeWidth(mTrendLineSize);
        mOuterCirclePaint.setColor(color);
        if (endPosition > startPosition && endPosition > 0) {
            for (int i = startPosition; i < endPosition; i++) {
                int currentY = (int) ((Integer) params.get("xAxisHeadHeight") + (mMaxHeight - canvasLine.get(i)) / mScaleValue * ((Integer) params.get("trendHeight") / 8));
                // 处理为负的数据，不需要可以屏蔽
                if (canvasLine.get(i) < 0) {
                    double Y = canvasLine.get(i) * 32.0 / 40.0;
                    currentY = (int) ((Integer) params.get("xAxisHeadHeight") + (mMaxHeight - Y) / scaleValue * ((Integer) params.get("trendHeight") / 8));
                }

                if (canvasLine.get(i) > condition.get(1) || canvasLine.get(i) < condition.get(0)) {
                    drawCircle(canvas, i * mDistance, currentY);  // 实心
                } else {
                    // 下面需要对 90~140的数据处理
                    drawCirque(canvas, i * mDistance, currentY);  // 空心
                }
            }
        }
    }

    /**
     * 绘制实心圆
     *
     * @param canvas
     * @param positionX
     * @param positionY
     */
    private void drawCircle(Canvas canvas, int positionX, int positionY) {
        canvas.drawCircle(positionX, positionY, mOuterCircleRadius, mOuterCirclePaint);
    }

    /**
     * 绘制空心圆
     *
     * @param canvas
     * @param positionX
     * @param positionY
     */
    private void drawCirque(Canvas canvas, int positionX, int positionY) {
        canvas.drawCircle(positionX, positionY, mOuterCircleRadius, mOuterCirclePaint);
        canvas.drawCircle(positionX, positionY, mInnerCircleRadius, mInnerCirclePaint);
    }

    /**
     * 绘制转折线
     *
     * @param canvas
     * @param paint
     * @param canvasLine
     * @param color
     */
    private void drawCanvasLine(Canvas canvas, Paint paint, List<Integer> canvasLine, int color) {

        Path mPath = new Path();  // 绘制趋势图对于的Path对象
        ArrayList<Integer> LinePosition = dealCanvasData(canvasLine);
        Map<String, Object> params = getViewParams();

        int startPosition = LinePosition.get(0);
        int endPosition = LinePosition.get(1);
        paint.setColor(color);
        // draw trend
        if (endPosition > startPosition && endPosition > 0) {

            for (int i = startPosition; i < endPosition; i++) {
                int currentY = (int) ((Integer) params.get("xAxisHeadHeight") + (mMaxHeight - canvasLine.get(i)) / mScaleValue * ((Integer) params.get("trendHeight") / 8));
                // 处理为负的数据，不需要可以屏蔽
                if (canvasLine.get(i) < 0) {
                    double Y = canvasLine.get(i) * 32.0 / 40.0;
                    currentY = (int) ((Integer) params.get("xAxisHeadHeight") + (mMaxHeight - Y) / scaleValue * ((Integer) params.get("trendHeight") / 8));
                }

                if (i == startPosition) {
                    mPath.moveTo(i * mDistance, currentY);
                } else {
                    mPath.lineTo(i * mDistance, currentY);
                }
            }
        }
        canvas.drawPath(mPath, paint);
        canvas.save();
        mPath.reset();
    }

    /**
     * 判断出开始位置与结束位置
     *
     * @param canvasLine
     * @return
     */
    private ArrayList<Integer> dealCanvasData(List<Integer> canvasLine) {
        Map<String, Object> params = getViewParams();

        int startPosition = ((Integer) params.get("firstPosition") - (Integer) params.get("offsetCount")) >= 0 ? ((Integer) params.get("firstPosition") - (Integer) params.get("offsetCount")) : 0;
        int endPosition = (mDrawCount + (Integer) params.get("offsetCount") + (Integer) params.get("firstPosition")) >= canvasLine.size() ? canvasLine.size() : (mDrawCount + (Integer) params.get("offsetCount") + (Integer) params.get("firstPosition"));

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(startPosition);
        arrayList.add(endPosition);

        return arrayList;
    }

    /**
     * 设置界限的区域的颜色值
     *
     * @param canvas
     * @param paint
     * @param rangeTrendColors
     * @param up
     * @param down
     */
    private void drawBackground(Canvas canvas, Paint paint, int[] rangeTrendColors, float up, float down) {

        Map<String, Object> params = getViewParams();

        Rect rect = new Rect();
        rect.set((int) ((Float) params.get("scrollX") + 0), (int) ((Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") * up), (int) ((Float) params.get("scrollX") + getWidth()), (int) (getHeight() - (Integer) params.get("xAxisTitleHeight") - (Integer) params.get("trendHeight") * down));
        LinearGradient gradient = new LinearGradient((Float) params.get("scrollX") + getWidth(), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") * up, (Float) params.get("scrollX") + getWidth(), getHeight() - (Integer) params.get("xAxisTitleHeight") - (Integer) params.get("trendHeight") * down, rangeTrendColors, null, Shader.TileMode.CLAMP);

        paint.setShader(gradient);
        canvas.drawRect(rect, paint);
        canvas.save();
    }

    /**
     * 绘制表格
     *
     * @param canvas
     * @param paint
     */
    private void drawForm(Canvas canvas, Paint paint) {

        for (int i = 0; i < mDrawCount; i++) {
            drawColumnLine(canvas, paint, 0xffd5edff, (int) mTextColorSize, i);
        }

        for (int i = 1; i <= mDrawCount + 1; i++) {
            if (i == 1 || i == 5 || i == 6 || i == 8) {
                drawRowLine(canvas, paint, 0xff7ecef9, 1, i);
            } else {
                drawRowLine(canvas, paint, 0xffe5e5e5, 1, i);
            }
        }
    }

    /**
     * 绘制表格竖线
     *
     * @param canvas
     * @param paint
     * @param lineColor
     * @param lineWith
     * @param position
     */
    private void drawColumnLine(Canvas canvas, Paint paint, int lineColor, int lineWith, int position) {

        Map<String, Object> params = getViewParams();

        paint.setColor(lineColor);
        paint.setTextSize(lineWith);
        canvas.drawLine((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position), (Integer) params.get("xAxisHeadHeight"), (Float) params.get("scrollX") + mYTitleWidth + mDistance * (position), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1), paint);
    }

    /**
     * 绘制表格横线
     *
     * @param canvas
     * @param paint
     * @param lineColor
     * @param lineWith
     * @param position
     */
    private void drawRowLine(Canvas canvas, Paint paint, int lineColor, int lineWith, int position) {

        Map<String, Object> params = getViewParams();

        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWith);
        canvas.drawLine((Float) params.get("scrollX") + mYTitleWidth - 20, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (position - 1), (Float) params.get("scrollX") + getWidth(), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (position - 1), paint);
    }

    /**
     * 绘制中心标志
     *
     * @param canvas
     * @param paint
     * @param position
     */
    private void drawCenterSign(Canvas canvas, Paint paint, int position) {
        drawCenterLine(canvas, paint, position);
        drawTriangle(canvas, paint, position);
    }

    /**
     * 绘制中心线
     *
     * @param canvas
     * @param paint
     * @param position
     */
    private void drawCenterLine(Canvas canvas, Paint paint, int position) {

        Map<String, Object> params = getViewParams();

        paint.setColor(mCenterColor);  // 修改中心竖线颜色
        paint.setStrokeWidth(mYCenterSize);
        canvas.drawLine((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position), (Integer) params.get("xAxisHeadHeight"), (Float) params.get("scrollX") + mYTitleWidth + mDistance * (position), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1) - 20, paint);
    }

    /**
     * 画三角形
     *
     * @param canvas
     * @param paint
     * @param position
     */
    public void drawTriangle(Canvas canvas, Paint paint, int position) {

        Map<String, Object> params = getViewParams();

        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(0xff7ecef9);
        Path path = new Path();
        path.reset();
        path.moveTo((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1) - 20);// 开始坐标 也就是三角形的顶点
        path.lineTo((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position) - 20, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1));
        path.lineTo((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position) + 20, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1));
        path.close();
        canvas.drawPath(path, paint);
        // 去掉底边
        mTitlePaint.setColor(Color.WHITE);
        mTitlePaint.setStrokeWidth(3);
        canvas.drawLine((Float) params.get("scrollX") + mYTitleWidth + mDistance * (position) - 19, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1), (Float) params.get("scrollX") + mYTitleWidth + mDistance * (position) + 19, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (8 - 1), mTitlePaint);

    }

    /**
     * 分割网格
     *
     * @param canvas
     */
    private void setSplitForm(Canvas canvas, Paint paint, int color, int width) {

        Map<String, Object> params = getViewParams();

        paint.setColor(color);
        paint.setStrokeWidth(width);
        canvas.drawRect((Float) params.get("scrollX") + 0, (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (5 - 1) + 1, (Float) params.get("scrollX") + getWidth(), (Integer) params.get("xAxisHeadHeight") + (Integer) params.get("trendHeight") / 8 * (6 - 1), paint);// 长方形
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                oldX = (int) event.getX();
                if ((mIsBeingDragged)) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                invalidate();
                mActivePointerId = event.getPointerId(0);
                return true;
            case MotionEvent.ACTION_MOVE:

                final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }

                final int x = (int) event.getX(activePointerIndex);
                int deltaX = oldX - x;
                if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaX > 0) {
                        deltaX -= mTouchSlop;
                    } else {
                        deltaX += mTouchSlop;
                    }
                }

                // HorizontalScrollView
                if (mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
                    oldX = x;
                    mTowards = deltaX;
                    scrollBy(deltaX, 0);
                    invalidate();
                    if (mPCenterListener != null) {
                        int nextCenter = getToNextCenter();
                        mPCenterListener.passCenter(nextCenter);
                    }
                }
                invalidate();
                return true;
            default:

                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    int nextCenter = getToNextCenter();

                    mTowards = 0;
                    int halfWidth = currentCenter;
                    int positionLocal = nextCenter * mDistance;
                    scrollTo(positionLocal - halfWidth, 0);
                    if (mAEndListener != null) {
                        mAEndListener.actionEnd(nextCenter);
                        invalidate();
                        centerPosition = nextCenter;
                    }
                } else {

                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


    public void setSelectedInCenter(int position) {

        if (position > mPoints[0].size() - 1 || position < 0) {
            return;
        }
        if (mAEndListener != null) {
            mAEndListener.actionEnd(position);
        }
        int halfWidth = (getWidth() + mYTitleWidth) / 2;
        int positionLocal = position * mDistance;
        scrollTo(positionLocal - halfWidth, 0);
        requestLayout();
    }

    /**
     * Touch事件结束监听
     *
     * @author zhoumin
     */
    public interface IActionEndListener {
        void actionEnd(int position);
    }

    /**
     * 通过中心时监听
     *
     * @author zhoumin
     */
    public interface IPassCenterListener {
        void passCenter(int positions);
    }

    public int getFontHeight(Paint paint, float fontSize) {
        paint.setTextSize(fontSize);
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    public int getXTitleHeight(Paint paint, float fontSize) {
        return getFontHeight(paint, fontSize) + 20;
    }

    /**
     * 获取 趋势图的高
     *
     * @return
     */
    public int getTrendHeight() {
        int xAxisHeight_up = getFontHeight(mTitlePaint, 18);
        int xAxisHeight_blow = getFontHeight(mTitlePaint, 14);
        int xAxisTitleHeight = xAxisHeight_up + xAxisHeight_blow + 20;// 多留20出来
        int xAxisHeadHeight = getFontHeight(mTitlePaint, 18) + 20;// 图例title
        // Height
        return getHeight() - xAxisTitleHeight - xAxisHeadHeight;
    }

    /**
     * 移动到下一个中点
     *
     * @return
     */
    private int getToNextCenter() {

        int centerX = getScrollX() + mYTitleWidth + currentCenter;  // x轴中点坐标
        float curNearCenter = ((float) centerX) / mDistance;
        if (curNearCenter <= 0 || mPoints == null) {
            return 0;
        }
        List<Integer> maxPoint = getMaxItem();

        if (curNearCenter > maxPoint.size() - 1) {
            return maxPoint.size() - 1;
        }

        return (int) curNearCenter;
    }

    public List<Integer> getMaxItem() {
        List<Integer> maxPoint = null;
        if (mPoints.length <= 0) {
            return null;
        }
        for (int i = 0; i < mPoints.length; i++) {
            if (i == 0) {
                maxPoint = mPoints[0];
            } else {
                if (mPoints[i].size() > maxPoint.size()) {
                    maxPoint = mPoints[i];
                }
            }
        }
        return maxPoint;
    }

    public void clearDate(boolean isBack) {
        mPoints = null;
        mXAxisValues = null;
        mData = null;
        if (isBack) {
            if (mAEndListener != null) {
                mAEndListener.actionEnd(0);
            }
        }
        invalidate();
    }

    public int[] getPointColors() {
        return mPointColors;
    }

    public float sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public void setCenterPosition(int pCenter) {
        this.centerPosition = pCenter;
    }

    /**
     * 事件分发机制
     * 对事件进行分发,将事件分发给当前的onInterceptTouchEvent()，
     * 然后分发给 当前的View 的onTouchEvent。由此处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }
}
