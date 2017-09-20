package me.u5510.floatingbar;

import android.graphics.Rect;

/**
 * Created by u5510 on 2017/9/15.
 */

public class TouchPoint {

    private float x;

    private float y;

    private float lastX;

    private float lastY;

    public TouchPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void reset() {
        setX(0.0f);
        setY(0.0f);
    }

    public void setTouchPoint(TouchPoint p) {
        setX(p.getX());
        setY(p.getY());
        setLastX(p.getX());
        setLastY(p.getY());
    }

    public void setX(float x) {
        this.x = x;
        setLastX(x);
    }

    public void setY(float y) {
        this.y = y;
        setLastY(y);
    }

    final void setLastX(float lastX) {
        if (lastX > 0) this.lastX = lastX;
    }

    final void setLastY(float lastY) {
        if (lastY > 0) this.lastY = lastY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    /**
     * 计算坐标是否在矩形范围内
     *
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     * @return 是否在范围内
     */
    public boolean isInsideRect(int l, int t, int r, int b) {
        return l - getX() < 0 && r - getX() > 0 && t - getY() < 0 && b - getY() > 0;
    }

    public boolean isInsideRect(Rect rect) {
        return isInsideRect(rect.left, rect.top, rect.right, rect.bottom);
    }
}
