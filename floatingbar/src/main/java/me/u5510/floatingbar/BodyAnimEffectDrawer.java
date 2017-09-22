package me.u5510.floatingbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 绘画主体以及增减动画的实现
 * Created by u5510 on 2017/9/19.
 */

class BodyAnimEffectDrawer extends EffectDrawer {

    /**
     * 期望值与实际值是否相等
     */
    private boolean isEquals = false;

    /**
     * 实际值
     */
    private Rect body;

    /**
     * 期望值
     */
    private Rect expectationBody;

    BodyAnimEffectDrawer(FloatingBar bar) {
        super(bar);
    }


    @Override
    public void dataChanged(ChangedType type, int index) {
        initBodyRect();
        expectationBody = getBar().getBodyRect();
        isEquals = false;
        getBar().getMyHandler().sendEmptyMessageDelayed(1, 1);
    }

    /**
     * 在不知道做了什么改变的情况下更新值
     */
    @Override
    public void dataChanged() {
        body = getBar().getBodyRect();
        getBar().invalidate();
    }

    @Override
    public boolean onHandler() {
        if (!isEquals) {
            body = gradatimUpdate(body, expectationBody, 7);
            getBar().postInvalidate();
            isEquals = body.equals(expectationBody);
        } else {
            body = getBar().getBodyRect();
        }
        return isEquals;
    }

    @Override
    public boolean onDraw(Canvas canvas) {
        initBodyRect();
        if (getBar().getItemSize() == 0) return false;

        Paint paint = getPaint();
        Paint zPaint = getBar().getZPaint();

        int l = body.left;
        int t = body.top;
        int r = body.right;
        int b = body.bottom;
        int _r = getBar().getRadius();
        int p = 1; //绘制投影的偏移量

        boolean isOrientation = getBar().isOrientation();

        if (getBar().isCustomBodyEnabled()) {
            throw new FloatingBarException("尚未实现");
        } else {
            switch (getBar().getBodyStyle()) {
                case Rect:
                    canvas.drawRect(l + p, t + p, r - p, b - p, zPaint);
                    canvas.drawRect(l, t, r, b, paint);
                    return true;
                case Normal:
                default:
                    if (isOrientation) {
                        //画阴影
                        canvas.drawArc(l + p, t + p, l + _r * 2 - p, b - p, 90, 180, false, zPaint);
                        canvas.drawArc(r - _r * 2 + p, t + p, r - p, b - p, 270, 180, false, zPaint);
                        canvas.drawRect(l + _r + p, t + p, r - _r - p, b - p, zPaint);
                        //画布局
                        canvas.drawArc(l, t, l + _r * 2, b, 90, 180, true, paint);
                        canvas.drawArc(r - _r * 2, t, r, b, 270, 180, true, paint);
                        canvas.drawRect(l + _r, t, r - _r, b, paint);
                    } else {
                        //画阴影
                        canvas.drawArc(l + p, t + p, r - p, t + _r * 2 - p, 180, 180, false, zPaint);
                        canvas.drawArc(l + p, b - _r * 2 + p, r - p, b - p, 0, 180, false, zPaint);
                        canvas.drawRect(l + p, t + _r + p, r - p, b - _r - p, zPaint);
                        //画布局
                        canvas.drawArc(l, t, r, t + _r * 2, 180, 180, true, paint);
                        canvas.drawArc(l, b - _r * 2, r, b, 0, 180, true, paint);
                        canvas.drawRect(l, t + _r, r, b - _r, paint);
                    }
                    return true;
            }
        }
    }

    /**
     * 获取paint
     */
    private Paint getPaint() {
        Paint p = getBar().resetPaint();
        p.setColor(getBar().getBodyColor());
        return p;
    }

    /**
     * 初始化实际值
     */
    private void initBodyRect() {
        if (body == null) body = getBar().getBodyRect();
    }

    public enum BodyStyle {

        /**
         * 两边绘制圆弧中间矩形
         */
        Normal,

        /**
         * 直角矩形
         */
        Rect


    }


}
