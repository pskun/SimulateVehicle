package edu.bupt.sv.ui;

import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.entity.EntityConstants;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DirectionView extends View implements EntityConstants {
	
	private CoreApi api;

    private int circleWidth = 100; // 圆环直径
    private int circleColor = Color.argb(150, 255, 0, 0);
    private int innerCircleColor = Color.rgb(0, 150, 0);
    private int backgroundColor = Color.rgb(255, 255, 255);
    private Paint paint = new Paint();
    int center = 0;
    int innerRadius = 0;
    private float innerCircleRadius = 0;
    private float smallCircle = 10;
    public Dir dir = Dir.UNDEFINE;
    
    private final static String TAG = "directionview"; 
    
 

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionView(Context context) {
        super(context);

        // paint = new Paint();
    }

    public DirectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    
   
   public void init(CoreApi api){
	   this.api = api;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);

        center = getWidth() / 2;
        innerRadius = (center - circleWidth / 2 - 10);// 圆环
        innerCircleRadius = center / 3;
    }

    /**
     * 测量宽度
     * 
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 测量高度
     * 
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 开始绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initBackGround(canvas);
        drawDirTriangle(canvas, dir);
 
    }

    /**
     * 绘制方向小箭头
     * 
     * @param canvas
     */
    private void drawDirTriangle(Canvas canvas, Dir dir) {
        paint.setColor(innerCircleColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        System.out.println("log###"+dir);
        switch (dir) {
        case UP:
            drawUpTriangle(canvas);         
            break;
        case DOWN:
            //drawDownTriangle(canvas);
            break;
        case LEFT:
            drawLeftTriangle(canvas);
            break;
        case RIGHT:
            drawRightTriangle(canvas);
            break;
        case CENTER:
            //invalidate();
            break;
        default:
            break;
        }

        paint.setColor(backgroundColor);

        canvas.drawCircle(center, center, smallCircle, paint);
        // canvas.drawText(text, center, center+40, paint);

    }

    /**
     * 绘制向右的小箭头
     * 
     * @param canvas
     */
    private void drawRightTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);
        path.lineTo((float) (center + sqrt2), (float) (center - sqrt2));
        path.lineTo((float) (center + pow05), center);
        path.lineTo((float) (center + sqrt2), (float) (center + sqrt2));
        canvas.drawPath(path, paint);
        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center + innerCircleRadius, center, paint);

        drawOnclikColor(canvas, Dir.RIGHT);
    }

    /**
     * 绘制想左的小箭头
     * 
     * @param canvas
     */
    private void drawLeftTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);
        path.lineTo((float) (center - sqrt2), (float) (center - sqrt2));
        path.lineTo((float) (center - pow05), center);
        path.lineTo((float) (center - sqrt2), (float) (center + sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center - innerCircleRadius, center, paint);

        drawOnclikColor(canvas, Dir.LEFT);
        

    }

    /**
     * 绘制向下的小箭头
     * 
     * @param canvas
     */
    private void drawDownTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);
        path.lineTo((float) (center - sqrt2), (float) (center + sqrt2));
        path.lineTo(center, (float) (center + pow05));
        path.lineTo((float) (center + sqrt2), (float) (center + sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center, center + innerCircleRadius, paint);

        drawOnclikColor(canvas, Dir.DOWN);
    }

    /**
     * 点击的时候绘制黑色的扇形
     * 
     * @param canvas
     * @param dir
     */
    private void drawOnclikColor(Canvas canvas, Dir dir) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(100);
        switch (dir) {
        case UP:
            canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                    + innerRadius), 225, 90, false, paint);
            api.turnNewPath(TURN_STRAIGHT);
            dir = Dir.UNDEFINE;
            
            break;
        case DOWN:
            canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                    + innerRadius), 45, 90, false, paint);
            
            break;
        case LEFT:
            canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                    + innerRadius), 135, 90, false, paint);
            api.turnNewPath(TURN_LEFT);
            dir = Dir.UNDEFINE;
            break;
        case RIGHT:
            canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                    + innerRadius), -45, 90, false, paint);
            api.turnNewPath(TURN_RIGHT);
            dir = Dir.UNDEFINE;
            break;

        default:
            break;
        }

        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * 绘制像向上的箭头
     * 
     * @param canvas
     */
    private void drawUpTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);

        path.lineTo((float) (center - sqrt2), (float) (center - sqrt2));
        path.lineTo(center, (float) (center - pow05));
        path.lineTo((float) (center + sqrt2), (float) (center - sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center, center - innerCircleRadius, paint);

        drawOnclikColor(canvas, Dir.UP);
    }

    /**
     * 绘制基本的背景， 这包括了三个步骤：1.清空画布 2.绘制外圈的圆 3.绘制内圈的圆
     * 
     * @param canvas
     */
    private void initBackGround(Canvas canvas) {
        clearCanvas(canvas);
        drawBackCircle(canvas);
        drawInnerCircle(canvas);
    }

    /**
     * 绘制中心白色小圆
     * 
     * @param canvas
     */
    private void drawInnerCircle(Canvas canvas) {
        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawCircle(center, center, innerCircleRadius, paint);
    }

    /**
     * 绘制背景的圆圈和隔线
     * 
     * @param canvas
     */
    private void drawBackCircle(Canvas canvas) {
        paint.setColor(circleColor);
        paint.setStrokeWidth(circleWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(center, center, innerRadius, paint); // 绘制圆圈

        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        canvas.drawLine(center, center, 0, 0, paint);
        canvas.drawLine(center, center, center * 2, 0, paint);
        canvas.drawLine(center, center, 0, center * 2, paint);
        canvas.drawLine(center, center, center * 2, center * 2, paint);

    }

    /**
     * 清空画布
     * 
     * @param canvas
     */
    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(backgroundColor);
    }

    /**
     * 检测方向
     * 
     * @param x
     * @param y
     * @return
     */
    private Dir checkDir(float x, float y) {
        Dir dir = Dir.UNDEFINE;

        if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
            Log.v(TAG, "----中央");
        } else if (y < x && y + x < 2 * center) {
            dir = Dir.UP;
            Log.v(TAG, "----向上");
        } else if (y < x && y + x > 2 * center) {
            dir = Dir.RIGHT;
            Log.v(TAG, "----向右");
        } else if (y > x && y + x < 2 * center) {
            dir = Dir.LEFT;
            Log.v(TAG, "----向左");
        } else if (y > x && y + x > 2 * center) {
            dir = Dir.DOWN;
            Log.v(TAG, "----向下");
        }

        return dir;
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	Dir tmp = Dir.UNDEFINE;
        if ((tmp = checkDir(event.getX(), event.getY())) != Dir.UNDEFINE) {
            dir = tmp;
            invalidate();
        }
		return super.onTouchEvent(event);
	}

    /**
     * 关于方向的枚举
     * 
     * @author Administrator
     * 
     */
    public enum Dir {
    	CENTER, UP, DOWN, LEFT, RIGHT, UNDEFINE
    }

}