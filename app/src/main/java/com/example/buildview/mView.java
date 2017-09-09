package com.example.buildview;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by 刘建南 on 2017/9/8.
 */

public class mView extends View {

    public mView(Context context){
        super(context);
    }
    //测量尺寸,widthMeasureSpec表示宽度属性，表示测量模式和尺寸大小，heightMeasureSpec同理
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        super.setMeasuredDimension(100,100);
    }

    //重绘
    @Override
    public void onDraw(Canvas canvas){

    }
}
