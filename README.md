# FloatingBar
一个浮动底栏/侧栏



添加至你的项目:

```java
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
  
  
dependencies {
    ...
    compile 'com.github.U5510:FloatingBar:1.0.1'
}
```

一个例子:
----
xml
```xml
<me.u5510.floatingbar.FloatingBar
    android:id="@+id/fb"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:gravity="center_horizontal"
    app:orientation="horizontal"
    app:changeDataMode="internal" />
```

java
```java
@Override
protected void onCreate(Bundle savedInstanceState) {

    ...

    FloatingBar fb = (FloatingBar)findViewById(R.id.fb);

    FloatingButton btn = new FloatingButton(int src, String tag, OnItemClickListener listener);

    fb.setOnLoaded(new OnLoaded() {
        @Override
        public void onLoaded() {
            fb.add(btn);
        }
    });
}
```

```java
public class MainActivity extends MapActivity implements OnBodyClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        FloatingBar fb = (FloatingBar)findViewById(R.id.fb);
	FloatingButton btn = new FloatingButton(R.drawable.btn, "btn");
	fb.setOnBodyClickListener(this);
    }

    @Override
    public void onClick(String tag) {
         if (tag.equals("btn")){
	 }
    }
}
```
  
属性:
====

基础
----
* orientation  horizontal/vertical
* elevation
* gravity  fill/left/top/right/bottom/center_horizontal/center_vertical
* changeDataMode
    internal
    external

个性化
----
* bodyColor
* itemColor
* itemColorSelected
* itemSize

item填充
----
* itemPaddingLeft
* itemPaddingTop
* itemPaddingRight
* itemPaddingBottom
* itemPaddingLR
    同时设置左右填充
* itemPaddingTB
    同时设置上下填充
* itemPadding

其他
----
* customBodyEnabled
    使用.9图片作为body的背景(待定)
