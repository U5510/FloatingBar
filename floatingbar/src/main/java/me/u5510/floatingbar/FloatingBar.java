package me.u5510.floatingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * FloatingBar
 * Created by u5510 on 2017/9/15.
 *
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_orientation
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_gravity
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemColor
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemColorSelected
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingLeft
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingTop
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingRight
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingBottom
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingLR
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPaddingTB
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemPadding
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_itemSize
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_customBodyEnabled
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_bodyColor
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_elevation
 * @attr ref me.u5510.floatingbar.R.styleable#FloatingBar_changeDataMode
 */

public class FloatingBar extends View {

    // TODO: 2017/9/19  effect drawer

    /**
     * 绘画body以及增减动画
     */
    private BodyAnimEffectDrawer bodyAnimEffectDrawer;

    /**
     * 绘画item以及增减动画
     */
    private ItemAnimEffectDrawer itemAnimEffectDrawer;

    // TODO: 2017/9/17 变量

    /**
     * 绘制body的样式
     */
    private BodyAnimEffectDrawer.BodyStyle bodyStyle;

    /**
     * 数据操作模式
     */
    private DataMode dataMode;

    /**
     * 海拔高度
     */
    private int elevation;

    /**
     * body的颜色
     */
    private int bodyColor;

    /**
     * icon的颜色
     */
    private int iconColor;

    /**
     * icon被按下的颜色
     */
    private int iconColorSelected;

    /**
     * 选中的item
     */
    private int itemSelected = -1;

    /**
     * 方向
     */
    private boolean orientation;

    /**
     * 位置
     */
    private Gravity gravity;

    /**
     * item间距(建议)
     * 在gravity为fill时无效
     */
    private int itemPaddingLeft;
    private int itemPaddingTop;
    private int itemPaddingRight;
    private int itemPaddingBottom;

    /**
     * icon的大小
     */
    private int iconSize;

    /**
     * 是否采用自定义.9图片做为body背景
     */
    private boolean customBodyEnabled;


    /**
     * 子项的列表
     */
    private List<FloatingButton> itemList = new ArrayList<>();

    /**
     * 画笔
     */
    private Paint paint = new Paint();

    private Paint zPaint = new Paint();

    /**
     * 屏幕触摸的点
     */
    private TouchPoint touchPoint = new TouchPoint(0.0f, 0.0f);

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!(bodyAnimEffectDrawer.onHandler()
                    & itemAnimEffectDrawer.onHandler())) {
                postInvalidate();
                handler.sendEmptyMessageDelayed(1, 1);
            }
        }
    };


    // TODO: 2017/9/17 构造方法 

    public FloatingBar(Context context) {
        this(context, null);
    }

    public FloatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FloatingBar, defStyleAttr, defStyleRes);
        initAttrs(a);
        a.recycle();
        initData();
        initEffectDrawer();

    }

    // TODO: 2017/9/17 初始化

    /**
     * 初始化属性
     */
    private void initAttrs(TypedArray a) {
        /*
          item颜色，
         */
        iconColor = a.getColor(R.styleable.FloatingBar_itemColor, Color.parseColor("#26A69A"));
        /*
         * item选中颜色，
         */
        iconColorSelected = a.getColor(R.styleable.FloatingBar_itemColorSelected, Color.parseColor("#00796B"));
        /*
         * 方向 默认水平
         */
        orientation = 1 != a.getInteger(R.styleable.FloatingBar_orientation, 0);
        /*
         * item大小，默认60
         */
        iconSize = a.getDimensionPixelSize(R.styleable.FloatingBar_itemSize, 60);
        /*
         * 是否启用自定义背景，默认否
         */
        customBodyEnabled = a.getBoolean(R.styleable.FloatingBar_customBodyEnabled, false);
        /*
         * body颜色，默认白色
         */
        bodyColor = a.getColor(R.styleable.FloatingBar_bodyColor, Color.WHITE);
        /*
         * 阴影值
         */
        elevation = a.getDimensionPixelSize(R.styleable.FloatingBar_elevation, 8);
        /*
         * item的填充,默认都为20
         */
        initItemPadding(a);
        /*
         * body的位置，默认fill
         */
        initGravity(a);
        /*
         * item数据模式
         */
        initDataMode(a);

        /*
         * 绘制body的样式
         */
        initBodyStyle(a);
    }

    private void initItemPadding(TypedArray a) {
        if (a.hasValue(R.styleable.FloatingBar_itemPadding)) {
            int n = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPadding, 0);
            itemPaddingLeft = n;
            itemPaddingTop = n;
            itemPaddingRight = n;
            itemPaddingBottom = n;
        } else {
            if (a.hasValue(R.styleable.FloatingBar_itemPaddingLR)) {
                int n = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingLR, 0);
                itemPaddingLeft = n;
                itemPaddingRight = n;
            } else {
                itemPaddingLeft = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingLeft, 40);
                itemPaddingRight = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingRight, 40);
            }
            if (a.hasValue(R.styleable.FloatingBar_itemPaddingTB)) {
                int n = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingTB, 0);
                itemPaddingTop = n;
                itemPaddingBottom = n;
            } else {
                itemPaddingTop = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingTop, 40);
                itemPaddingBottom = a.getDimensionPixelSize(R.styleable.FloatingBar_itemPaddingBottom, 40);
            }
        }
    }

    private void initDataMode(TypedArray a) {
        switch (a.getInteger(R.styleable.FloatingBar_changeDataMode, 0)) {
            case 1:
                setDataMode(DataMode.INTERNAL);
                break;
            case 2:
                setDataMode(DataMode.EXTERNAL);
                break;
            default:
                setDataMode(DataMode.UNKNOWN);
        }
    }

    private void initBodyStyle(TypedArray a) {
        switch (a.getInteger(R.styleable.FloatingBar_bodyStyle, 0)) {
            case 1:
                bodyStyle = BodyAnimEffectDrawer.BodyStyle.Rect;
                break;
            default:
                bodyStyle = BodyAnimEffectDrawer.BodyStyle.Normal;
        }
    }

    private void initGravity(TypedArray a) {
        switch (a.getInteger(R.styleable.FloatingBar_gravity, 0)) {
            case 0://fill
                gravity = Gravity.FILL;
                break;
            case 1: //left
                gravity = Gravity.LEFT;
                break;
            case 2: //top
                gravity = Gravity.TOP;
                break;
            case 3: //right
                gravity = Gravity.RIGHT;
                break;
            case 4: //bootom
                gravity = Gravity.BOTTOM;
                break;
            case 5: //center_horizontal
                gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case 6: //center_vertical
                gravity = Gravity.CENTER_VERTICAL;
                break;
        }
    }

    /**
     * 初始化内部数据
     */
    private void initData() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //硬件加速关闭
        paint.setAntiAlias(true);
        zPaint.setAntiAlias(true);
        zPaint.setShadowLayer(elevation / 2, elevation / 3, elevation / 3, Color.argb(70, 0, 0, 0));
    }

    // TODO: 2017/9/19 init effect drawer
    private void initEffectDrawer() {
        bodyAnimEffectDrawer = new BodyAnimEffectDrawer(this);
        itemAnimEffectDrawer = new ItemAnimEffectDrawer(this);
    }

    // TODO: 2017/9/17 Get


    public DataMode getDataMode() {
        return dataMode;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setDataMode(DataMode dataMode) {
        if (this.dataMode == null || (this.dataMode == DataMode.UNKNOWN && dataMode != DataMode.UNKNOWN)) {
            this.dataMode = dataMode;
        } else {
            String s = "";
            if (this.dataMode != DataMode.UNKNOWN) s += " 当前已设置数据操作模式为: " + this.dataMode;
            if (dataMode == DataMode.UNKNOWN) s += "传入的枚举为: " + dataMode;
            throw new FloatingBarException("设置数据操作模式失败: " + s);
        }
    }

    public int getBodyColor() {
        return bodyColor;
    }

    public int getItemSelected() {
        return itemSelected;
    }

    /**
     * 获取icon的颜色
     */
    public int getIconColor() {
        return iconColor;
    }

    /**
     * 获取icon的尺寸
     */
    public int getIconSize() {
        return iconSize;
    }

    /**
     * 获取item被选中的颜色
     */
    public int getIconColorSelected() {
        return iconColorSelected;
    }

    /**
     * 获取paint
     */
    protected Paint getPaint() {
        return paint;
    }


    /**
     * 获取触摸的坐标
     */
    protected TouchPoint getTouchPoint() {
        return touchPoint;
    }


    /**
     * 获取布局的宽度
     */
    public int getWidth_() {
        return getWidth() - elevation;
    }

    /**
     * 获取布局的高度
     */
    public int getHeight_() {
        return getHeight() - elevation;
    }

    /**
     * 获取item的数量
     */
    public int getItemSize() {
        return itemList.size();
    }

    /**
     * 获取item
     *
     * @param index 下标
     */
    public FloatingButton getItem(int index) {
        return itemList.get(index);
    }

    /**
     * 获取item的左填充
     */
    protected int getItemPaddingLeft() {
        return itemPaddingLeft;
    }

    /**
     * 获取item的右填充
     */
    protected int getItemPaddingRight() {
        return itemPaddingRight;
    }

    /**
     * 获取item的顶部填充
     */
    protected int getItemPaddingTop() {
        return itemPaddingTop;
    }

    /**
     * 获取item的底部填充
     */
    protected int getItemPaddingBottom() {
        return itemPaddingBottom;
    }

    /**
     * 获取body的方向
     *
     * @return 水平/垂直
     */
    protected boolean isOrientation() {
        return orientation;
    }

    /**
     * 获取body的位置
     */
    protected Gravity getGravity() {
        return gravity;
    }

    /**
     * 获取draw中圆的半径
     */
    protected int getRadius() {
        if (orientation) {
            return getHeight_() / 2;
        } else {
            return getWidth_() / 2;
        }
    }


    /**
     * 获取item的高度
     */
    protected int getItemHeight() {
        return getIconSize();
    }

    /**
     * 获取item的高度 带填充
     */
    protected int getItemHeight(boolean padding) {
        if (padding) return getItemHeight() + itemPaddingTop + itemPaddingBottom;
        else return getItemHeight();
    }

    /**
     * 获取item的宽度
     */
    protected int getItemWidth() {
        return getIconSize();
    }

    /**
     * 获取item的宽度 带填充
     */
    protected int getItemWidth(boolean padding) {
        if (padding) return getItemWidth() + itemPaddingLeft + itemPaddingRight;
        else return getItemWidth();
    }

    /**
     * 获取body的高度
     */
    protected int getBodyHeight() {
        return calculateBodyHeight(getItemSize());
    }

    /**
     * 获取body的宽
     */
    protected int getBodyWidth() {
        return calculateBodyWidth(getItemSize());
    }

    /**
     * 获取body所在的矩形
     */
    protected Rect getBodyRect() {
        return calculateBodyRect(getItemSize());
    }

    /**
     * 获取item所在的矩形范围
     *
     * @param index 下标
     */
    protected Rect getItemRect(int index) {
        return calculateItemRect(getItemSize(), index);
    }

    /**
     * 获取绘制阴影的paint
     */
    protected Paint getZPaint() {
        return zPaint;
    }

    /**
     * 获取绘制body的样式
     */
    protected BodyAnimEffectDrawer.BodyStyle getBodyStyle() {
        return bodyStyle;
    }

    /**
     * 是否为自定义body背景
     */
    public boolean isCustomBodyEnabled() {
        return customBodyEnabled;
    }

    // TODO: 2017/9/17  计算(calculate)

    /**
     * 计算body的高度
     *
     * @param size 子项的数量
     */
    protected int calculateBodyHeight(int size) {
        if (isOrientation())
            return getItemHeight(true) + getPaddingTop() + getPaddingBottom();
        else
            return getItemHeight(true) * size + getPaddingTop() + getPaddingBottom();
    }

    /**
     * 计算body的宽
     *
     * @param size 这个值一般为子项的数量 list.size()
     *             意味着可以获取想要的值
     */
    protected int calculateBodyWidth(int size) {
        if (isOrientation())
            return getItemWidth(true) * size + getPaddingLeft() + getPaddingRight();
        else
            return getItemWidth(true) + getPaddingLeft() + getPaddingRight();

    }

    /**
     * 计算body所在的矩形
     *
     * @param size 这个值一般为子项的数量 list.size()
     *             意味着可以获取想要的值
     */
    protected Rect calculateBodyRect(int size) {
        int l, t, r, b;
        int diameter = getRadius() * 2;
        if (isOrientation()) {
            t = 0;
            b = getHeight_();
            switch (getGravity()) {
                case LEFT: //left
                    l = 0;
                    r = calculateBodyWidth(size);
                    r += calculateDifferential(l, r, diameter);
                    break;
                case RIGHT: //right
                    l = getWidth_() - calculateBodyWidth(size);
                    r = getWidth_();
                    l -= calculateDifferential(l, r, diameter);
                    break;
                case FILL: //fill
                    l = 0;
                    r = getWidth_();
                    break;
                case TOP: //top
                case BOTTOM: //bottom
                case CENTER_HORIZONTAL:
                case CENTER_VERTICAL:
                default: //center
                    l = (getWidth_() - calculateBodyWidth(size)) / 2;
                    r = l + calculateBodyWidth(size);
                    l -= calculateDifferential(l, r, diameter) / 2;
                    r += calculateDifferential(l, r, diameter) / 2;
                    break;
            }
        } else {
            l = 0;
            r = getWidth_();
            switch (getGravity()) {
                case TOP:
                    t = 0;
                    b = calculateBodyHeight(size);
                    break;
                case BOTTOM:
                    t = getHeight_() - calculateBodyHeight(size);
                    b = getHeight_();
                    break;
                case FILL:
                    t = 0;
                    b = getHeight_();
                    break;
                case LEFT:
                case RIGHT:
                case CENTER_HORIZONTAL:
                case CENTER_VERTICAL:
                default:
                    t = (getHeight_() - calculateBodyHeight(size)) / 2;
                    b = t + calculateBodyHeight(size);
                    break;
            }
        }
        return new Rect(l, t, r, b);
    }

    /**
     * 计算两个数的差是否小于第三个数，如果差比第三个数小，返回差，不然返回0;
     * <p>
     * 通常用于检测item只有一个时，body的宽度小于高度/高度小于宽度（左右圆相错，矩形为负），避免出现绘制错乱。
     *
     * @param a 第一个数
     * @param b 第二个数
     * @param c 被比较的数(第三个数)
     */
    private int calculateDifferential(int a, int b, int c) {
        int differential;

        if (a > b) differential = a - b;
        else differential = b - a;

        if (differential < c) {
            return c - differential;
        } else return 0;
    }


    /**
     * 计算item所在的矩形范围
     *
     * @param size  这个值一般为子项的数量 list.size()
     *              意味着可以获取想要的值
     * @param index 需要得到item的下标
     */
    protected Rect calculateItemRect(int size, int index) {
        Rect item;
        if (Gravity.FILL != getGravity()) {
            if (isOrientation()) {
                item = calculateItemRect_n(getItemWidth(true), index);
            } else {
                item = calculateItemRect_n(getItemHeight(true), index);
            }
        } else {
            if (isOrientation()) {
                item = calculateItemRect_f(size, index);
            } else {
                item = calculateItemRect_f(size, index);
            }
        }
        return item;
    }

    /**
     * 内部方法
     * 计算Gravity不为FILL时的item矩形范围
     *
     * @param size  item尺寸
     * @param index item下标
     */
    private Rect calculateItemRect_n(int size, int index) {
        Rect body = getBodyRect();
        body.left += getPaddingLeft();
        body.top += getPaddingTop();
        body.right -= getPaddingRight();
        body.bottom -= getPaddingBottom();
        int l, t, r, b;
        if (isOrientation()) {
            l = body.left + getItemPaddingLeft() + size * index;
            t = body.top + getItemPaddingTop();
            r = l + size - getItemPaddingRight() - getItemPaddingLeft();
            b = body.bottom - getItemPaddingBottom();
        } else {
            l = body.left + getItemPaddingLeft();
            t = body.top + getItemPaddingTop() + size * index;
            r = body.right - getItemPaddingRight();
            b = t + size - getItemPaddingBottom() - getItemPaddingTop();
        }
        return new Rect(l, t, r, b);
    }

    /**
     * 内部方法
     * 计算Gravity是FILL时的item矩形范围
     *
     * @param size  这个值一般为子项的数量 list.size()
     *              意味着可以获取想要的值
     * @param index item的下标
     */
    private Rect calculateItemRect_f(int size, int index) {
        Rect body = getBodyRect();
        body.left += getPaddingLeft();
        body.top += getPaddingTop();
        body.right -= getPaddingRight();
        body.bottom -= getPaddingBottom();
        int l, t, r, b, p;
        int w = getItemWidth();
        int h = getItemWidth();
        if (isOrientation()) {
            p = ((body.right - body.left) / size - w) / 2;
            l = body.left + p + (w + 2 * p) * index;
            t = body.top + getItemPaddingTop();
            r = l + w;
            b = body.bottom - getItemPaddingBottom();
        } else {
            p = ((body.bottom - body.top) / size - h) / 2;
            l = body.left + getItemPaddingLeft();
            t = body.top + p + (h + 2 * p) * index;
            r = body.right - getItemPaddingRight();
            b = t + h;
        }
        return new Rect(l, t, r, b);
    }

    /**
     * 内部方法
     * 添加item四周填充
     */
    protected Rect itemAddPadding(Rect item) {
        int l = item.left - getItemPaddingLeft();
        int t = item.top - getItemPaddingTop();
        int r = item.right + getItemPaddingRight();
        int b = item.bottom + getItemPaddingBottom();
        return new Rect(l, t, r, b);
    }

    // TODO: 2017/9/17

    /**
     * 设置Gravity
     */
    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    /**
     * 设置选中的item
     */
    public void setItemSelected(int itemSelected) {
        this.itemSelected = itemSelected;
        invalidate();
    }

    /**
     * 清空选中的item
     */
    public void resetItemSelected() {
        setItemSelected(-1);
    }


    /**
     * 限制一个数
     *
     * @param orig 这个数
     * @param min  最小值
     * @param max  最大值
     */
    protected int limitInteger(int orig, int min, int max) {
        if (max <= min) throw new FloatingBarException();
        if (orig < min) orig = min;
        else if (orig > max) orig = max;
        return orig;
    }


    /**
     * 重置 paint
     */
    protected Paint resetPaint() {
        getPaint().reset();
        getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return getPaint();
    }

    /**
     * 加载图标
     *
     * @param size  尺寸
     * @param color 颜色
     * @param src   资源位置
     */
    protected Bitmap loadBitmap(int size, int color, int src) {
        return BitmapUtil.changeBitmapColor(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(), src), size, size), color);
    }

    // TODO: 2017/9/17 enum

    public enum Gravity {

        /**
         * 填充，在此效果下，子项的间距值无效。
         */
        FILL,

        /**
         * 居左
         */
        LEFT,

        /**
         * 居右
         */
        RIGHT,

        /**
         * 水平居中
         */
        CENTER_HORIZONTAL,

        /**
         * 居上
         */
        TOP,

        /**
         * 居下
         */
        BOTTOM,

        /**
         * 垂直居中vertical
         */
        CENTER_VERTICAL
    }

    public enum DataMode {

        /**
         * 内部操作数据列表
         */
        INTERNAL,

        /**
         * 外部操作数据列表
         */
        EXTERNAL,

        /**
         * 未知
         */
        UNKNOWN
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w, h;
        if (isOrientation()) {
            w = MeasureSpec.getSize(widthMeasureSpec);
            h = getBodyHeight();
        } else {
            w = getBodyWidth();
            h = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(w + elevation, h + elevation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        TouchPoint tp = new TouchPoint(event.getX(), event.getY());

        boolean insideBody = tp.isInsideRect(getBodyRect());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (insideBody) {
                    touchPoint.setTouchPoint(tp);

                    for (int i = 0; i < getItemSize(); i++) {
                        if (getTouchPoint().isInsideRect(itemAddPadding(getItemRect(i)))) {
                            OnItemClickListener o = getItem(i).getOnItemClickListener();
                            if (null != o) o.onClickDown();
                        }
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (insideBody) {

                    for (int i = 0; i < getItemSize(); i++) {
                        if (getTouchPoint().isInsideRect(itemAddPadding(getItemRect(i)))) {
                            setItemSelected(i);
                            OnItemClickListener o = getItem(i).getOnItemClickListener();
                            if (null != o) o.onClickDown();
                        }
                    }

                    getTouchPoint().reset();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bodyAnimEffectDrawer.onDraw(canvas);
        itemAnimEffectDrawer.onDraw(canvas);
    }

    // TODO: 2017/9/20 notify

    public void notifyItemInserted(int position) {
        bodyAnimEffectDrawer.onDataChanged(EffectDrawer.ChangedType.Inserted, position);
        itemAnimEffectDrawer.onDataChanged(EffectDrawer.ChangedType.Inserted, position);
    }

    public void notifyItemRemoved(int position) {
        bodyAnimEffectDrawer.onDataChanged(EffectDrawer.ChangedType.Removed, position);
        itemAnimEffectDrawer.onDataChanged(EffectDrawer.ChangedType.Removed, position);
    }

    public void notifyDataChanged() {
        bodyAnimEffectDrawer.dataChanged();
        itemAnimEffectDrawer.dataChanged();
    }


    // TODO: 2017/9/17


    public void moveItem(int fromPosition, int toPosition) {
        if (dataMode == DataMode.INTERNAL) {
            FloatingButton fb = itemList.remove(fromPosition);
            notifyItemRemoved(fromPosition);
            itemList.add(toPosition, fb);
            notifyItemInserted(toPosition);
        } else e(DataMode.INTERNAL);
    }

    public void addFloatingButton(FloatingButton floatingButton) {
        if (dataMode == DataMode.INTERNAL) {
            itemList.add(floatingButton);
            notifyItemInserted(getItemSize() - 1);
        } else e(DataMode.INTERNAL);

    }

    public void addFloatingButton(int index, FloatingButton floatingButton) {
        if (dataMode == DataMode.INTERNAL) {
            itemList.add(index, floatingButton);
            notifyItemInserted(index);
        } else e(DataMode.INTERNAL);

    }

    public void addFloatingButtonAll(List<FloatingButton> fbs) {
        for (FloatingButton fb : fbs) {
            addFloatingButton(fb);
        }
    }

    public void removeFloatingButton(int index) {
        if (dataMode == DataMode.INTERNAL) {
            itemList.remove(index);
            notifyItemRemoved(index);
        } else e(DataMode.INTERNAL);
    }

    public void removeFloatingButtonAll(List<FloatingButton> fbs) {
        for (FloatingButton fb : fbs) {
            removeFloatingButton(fb);
        }
    }

    /**
     * 删除列表中的所有相同的元素
     */
    public void removeFloatingButton(FloatingButton fb) {
        if (dataMode == DataMode.INTERNAL) {
            List<Integer> same = new ArrayList<>();
            for (int i = 0; i < getItemSize(); i++) {
                if (getItem(i).equals(fb)) {
                    same.add(i);
                }
            }
            if (same.size() != 0) {
                for (Integer i : same) {
                    removeFloatingButton(i);
                    notifyItemRemoved(i);
                }
            }
        } else e(DataMode.INTERNAL);
    }

    /**
     * 清空
     */
    public void clear() {
        if (dataMode == DataMode.INTERNAL) {
            itemList.clear();
            notifyDataChanged();
        } else e(DataMode.INTERNAL);

    }

    public void setItemList(List<FloatingButton> fbs) {
        if (dataMode == DataMode.EXTERNAL) {
            itemList.clear();
            itemList = fbs;
        } else e(DataMode.EXTERNAL);

    }

    private void e(DataMode a) {
        throw new FloatingBarException("操作错误: 当前数据操作模式为:" + this.dataMode + ",只有当模式为" + a + "时，此方法才有效");
    }


}
