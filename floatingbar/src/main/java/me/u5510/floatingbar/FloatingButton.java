package me.u5510.floatingbar;

import android.graphics.Bitmap;

/**
 * Created by u5510 on 2017/9/15.
 */

public class FloatingButton {

    private String tag;

    private int src;

    private Bitmap bitmap;

    private OnItemClickListener onItemClickListener;

    public FloatingButton(int src, String tag, OnItemClickListener onItemClickListener) {
        this.tag = tag;
        this.src = src;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) return true;
        // 如果传入的对象为空,则返回false
        if (obj == null) return false;
        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) return false;
        // 类型相同, 比较内容是否相同
        FloatingButton other = (FloatingButton) obj;
        return tag.equals(other.tag)
                && src == other.src;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTag() {
        return tag;
    }

    public int getSrc() {
        return src;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

}
