package me.u5510.floatingbar;

/**
 * view首次加载完成回调
 *
 * 如果在view加载完成之前调用了add()会引起绘制错乱
 *
 * Created by u5510 on 2017/9/22.
 */

public interface OnLoaded {

    void onLoaded();

}
