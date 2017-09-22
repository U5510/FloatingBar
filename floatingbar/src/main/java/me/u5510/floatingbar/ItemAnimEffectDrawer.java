package me.u5510.floatingbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by u5510 on 2017/9/19.
 */

public class ItemAnimEffectDrawer extends EffectDrawer {

    /**
     * 期望值与实际值是否相等
     */
    private boolean isEquals = false;

    /**
     * 改变数据类型
     */
    private ChangedType type;


    /**
     * 被操作item的下标
     */
    private int index;

    /**
     * 实际值
     */
    private List<Rect> item = new ArrayList<>();

    /**
     * 期望值
     */
    private List<Rect> expectationItem = new ArrayList<>();

    ItemAnimEffectDrawer(FloatingBar bar) {
        super(bar);
    }

    @Override
    void dataChanged(ChangedType type, int index) {
        this.index = index;
        this.type = type;
        if (type==ChangedType.Unknown){
            List<Rect> a = new ArrayList<>();
            for (int i = 0; i < getBar().getItemSize(); i++) {
                a.add(getBar().getItemRect(i));
            }
            item.clear();
            item.addAll(a);
            getBar().invalidate();
        }else {
            updateExpectationItem();
            updateItem();
            checkSize();

            isEquals = false;
            getBar().getMyHandler().sendEmptyMessageDelayed(1, 1);
        }
    }

    /**
     * 更新实际的值
     */
    private void updateItem() {
        switch (type) {
            case Inserted:
                item.add(index, expectationItem.get(index));
                break;
            case Removed:
                item.remove(index);
                break;
        }
    }

    /**
     * 检查实际值与期望值是否又差异
     */
    private void checkSize() {
        if (item.size() != expectationItem.size()) {
            item.clear();
            item.addAll(expectationItem);
        }
    }

    /**
     * 更新期望的值
     */
    private void updateExpectationItem() {
        expectationItem.clear();
        for (int i = 0; i < getBar().getItemSize(); i++) {
            expectationItem.add(getBar().getItemRect(i));
        }
    }


    @Override
    boolean advance() {
        if (!isEquals) {
            for (int i = 0; i < getBar().getItemSize(); i++) {
                item.set(i, gradatimUpdate(item.get(i), expectationItem.get(i), 7));
            }
            getBar().postInvalidate();
            isEquals = item.equals(expectationItem);
        }
        return isEquals;
    }

    @Override
    public void draw(Canvas canvas) {
        int itemSize = getBar().getItemSize();
        for (int i = 0; i < itemSize; i++) {
            Paint p = getBar().resetPaint();
            p.setColor(getBar().getBodyColor());

            FloatingButton fb = loadBitmap(i);

            Rect itemRect = item.get(i);

            if (type == ChangedType.Removed || i != index || isEquals) {
                canvas.drawRect(itemRect, p);
                canvas.drawBitmap(fb.getBitmap(), itemRect.left, itemRect.top, p);
            }
        }
    }

    /**
     * 判断当前item是否为被选中的并且返回适当颜色
     */
    private int getItemBitmapColor(int index) {
        int iconSelected = getBar().getItemSelected();
        if (iconSelected != -1 && iconSelected == index) return getBar().getIconColorSelected();
        else return getBar().getIconColor();
    }

    /**
     * 加载图标
     */
    private FloatingButton loadBitmap(int index) {
        FloatingButton fb = getBar().getItem(index);
        if (fb.getBitmap() == null)
            fb.setBitmap(getBar().loadBitmap(getBar().getIconSize(), getItemBitmapColor(index), fb.getSrc()));
        return fb;
    }

}
