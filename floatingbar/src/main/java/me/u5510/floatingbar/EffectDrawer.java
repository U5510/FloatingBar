package me.u5510.floatingbar;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

/**
 * 效果实现抽象类
 * 周期:
 * |
 * |         dataChanged       onTouchEvent()/onDataChanged()
 * |             |                           |
 * |             |                           |
 * |             |                           |
 * |             |                        onDraw()     <--------\
 * |             |                           |                  |
 * |             |                           |                  |
 * |             |                           |                false
 * |          onDraw()                       |                  |
 * |             |                           |                  |
 * |             |                       onHandler()    --------/
 * |             |                           |
 * |             |                           |
 * |             |                          true
 * |             |                           |
 * |             |                           |
 * |            END                         END
 * |
 * |
 * Created by u5510 on 2017/9/19.
 */

abstract class EffectDrawer {

    private FloatingBar bar;

    EffectDrawer(FloatingBar bar) {
        this.bar = bar;
    }

    /**
     * 触发条件
     * 触摸事件
     */
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * 触发条件
     * 监听数据改变
     *
     * @param type  原数据改变的类型
     * @param index 更改的下标
     */
    public void onDataChanged(ChangedType type, int index) {
        dataChanged(type, index);
    }

    /**
     * 触发条件
     * 监听数据改变
     * 直接更改实际值，无动画
     */
    public void onDataChanged() {
        dataChanged(ChangedType.Unknown, 0);
    }


    /**
     * 绘画
     *
     * @param canvas 画板
     */
    public void onDraw(Canvas canvas) {
        if (getBar().getItemSize() > 0) {
            draw(canvas);
        }
    }

    /**
     * 循环过程及结束条件
     *
     * @return 值是否相等
     * true：结束循环
     * false: 继续循环
     */
    public boolean onAdvance() {
        return advance();
    }

    void dataChanged(ChangedType type, int index) {
    }

    void draw(Canvas canvas) {
    }

    boolean advance() {
        return true;
    }


    /**
     * 逐步地更新矩形
     *
     * @param orig        原数据
     * @param expectation 期望的数据
     * @param s           增量/减量
     * @return 每步
     */
    Rect gradatimUpdate(Rect orig, Rect expectation, int s) {
        Rect rect = new Rect();
        rect.left = gradatimUpdate(orig.left, expectation.left, s);
        rect.top = gradatimUpdate(orig.top, expectation.top, s);
        rect.right = gradatimUpdate(orig.right, expectation.right, s);
        rect.bottom = gradatimUpdate(orig.bottom, expectation.bottom, s);
        return rect;
    }

    /**
     * 逐步地更新值
     *
     * @param orig        原值
     * @param expectation 期望的值
     * @param s           增量/减量
     * @return 每步
     */
    int gradatimUpdate(int orig, int expectation, int s) {
        //当2个值的差小于每次增减的值(s)的时候,将该值(s)设为1
        int a = Math.abs(orig - expectation);
        if (0 != a && s > a) s = 1;

        int i;
        if (orig > expectation) i = orig - s;
        else if (orig < expectation) i = orig + s;
        else i = orig;
        return i;
    }


    FloatingBar getBar() {
        return bar;
    }

    public enum ChangedType {

        /**
         * 插入
         */
        Inserted,

        /**
         * 移除
         */
        Removed,

        Unknown,

    }
}
