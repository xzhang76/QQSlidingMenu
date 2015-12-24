package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.lenovo.zhangxt4.imoocqqslidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by zhangxt4 on 2015/11/7.
 */
public class SlidingMenu extends HorizontalScrollView {
    private LinearLayout mWapper;
    private ViewGroup mMenu; //左边菜单栏的ViewGroup
    private ViewGroup mContent; //右边内容的ViewGroup
    private int mScreenWidth; //屏幕宽度
    private int mMenuWidth; //左侧菜单View的宽度
    private int mMenuRightPadding = 200; //dp
    private boolean once;
    private boolean isOpen; //判断左侧菜单是否已经打开

    /**
     * 未使用自定义属性时，调用该构造方法
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 当使用了自定义的属性时，会调用此方法
    */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for(int i = 0; i < n; i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.SlidingMenu_rightPadding:
                    //默认值
                    int defautRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());
                    mMenuRightPadding = a.getDimensionPixelSize(attr, defautRightPadding);
                    break;
            }
        }
        a.recycle();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        //把50dp转化成像素值
        //mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }

    public void toggle(){
        if(isOpen) {
            closeMenu();
            isOpen = false;
        }else {
            openMenu();
            isOpen = true;
        }
    }

    private void closeMenu() {
        if(!isOpen)return;
        this.smoothScrollTo(mMenuWidth, 0);
    }

    private void openMenu(){
        if(isOpen)return;
        this.smoothScrollTo(0, 0);
    }

    /*
        决定自己和自己内部的View的宽和高
         */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /*
    决定子view放置的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            this.scrollTo(mMenuWidth, 0); //scrollTo(x, y) x为正值表示滚动条向右移动，则内容向左移动
        }
    }

    /*
    监听点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                //获取HorizontalScrollView最左边离屏幕左边的宽度，即左侧隐藏View的宽度
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth/2){
                    //隐藏宽度比较大，继续隐藏
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                }else{
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 屏幕出现滚动时（不管是自动滚，还是手动滚），该方法被调用
     * @param l, 这个参数就是通过getScrollX()获取的菜单最左边距屏幕左边的宽度
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        //调用属性动画，设置TranslationX
        //l是菜单最左边距屏幕左边的宽度，范围：mMenuWidth -> 0
        //scale范围：1 -> 0
        float scale = l * 1.0f / mMenuWidth;
        /*
        content视图有1.0~0.7缩放效果:
        菜单视图有0.7~1.0缩放效果
        菜单视图还有0.6~1.0的透明度变化效果
         */
        //1.设置对应的梯度值
        float rightScale = 0.7f+0.3f*scale;
        float leftScale = 1.0f-scale*0.3f;
        float leftAlpha = 1.0f-0.4f*scale;
        //2.设置菜单视图的偏移动画
        //setTranslationX(View, TranlationX), TranslationX表示View偏移的宽度，大于0右偏；小于0左偏
        ViewHelper.setTranslationX(mMenu, scale * mMenuWidth * 0.8f);
        //3.设置menu的缩放和透明度变化
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, leftAlpha);
        //4.设置content缩放中心点和缩放效果
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);
    }
}
