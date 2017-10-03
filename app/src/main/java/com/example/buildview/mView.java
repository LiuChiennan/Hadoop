/*
package com.example.buildview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.hadoop.R;

*/
/**
 * Created by 刘建南 on 2017/9/8.
 *//*


public class mView extends View {

    public mView(Context context){
        super(context);
    }

    public mView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    //测量尺寸,widthMeasureSpec表示宽度属性，表示测量模式和尺寸大小，heightMeasureSpec同理
    //是根据viewgroup传入的测量值来决定本view的宽高
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int final_width;
        int final_height;

        if(widthMode == MeasureSpec.EXACTLY){
            final_width=widthSize;
        }else{
            final_width=100;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            final_height=heightSize;
        }
        else{
            final_height=100;
        }
        super.setMeasuredDimension(100,100);
    }

    //重绘
    @Override
    public void onDraw(Canvas canvas){
        float height=this.getHeight();
        float width=this.getWidth();
        //设置画笔基本属性  
        Paint paint=new Paint();
        paint.setAntiAlias(true);//抗锯齿功能  
        paint.setColor(Color.RED);//设置画笔颜色      
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE  
        paint.setStrokeWidth(5);//设置画笔宽度  
        paint.setShadowLayer(10,15,15,Color.GREEN);//设置阴影  
        // 设置画布背景颜色       
        canvas.drawRGB(255,255,255);
        //画圆  
        canvas.drawCircle(width/2,height/2,height/2,paint);
    }
}


class mTopograph extends RelativeLayout{

    public mTopograph(Context context){
        super(context);
        setWillNotDraw(false);
    }

    public mTopograph(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        try{
            //根据父布局计算自己的宽高和布局模式，这里就指定高度了
            int widthMode=MeasureSpec.getMode(widthMeasureSpec);
            int widthSize=MeasureSpec.getSize(widthMeasureSpec);

            int final_width;
            measureChildren(widthMeasureSpec,heightMeasureSpec);

            //计算子view的宽高,AT_MOST就是wrap_content
            if(widthMode == MeasureSpec.AT_MOST){
                final_width=300;
            }else{
                final_width=widthSize;
            }

            //宽为match_parent，指定高度
            setMeasuredDimension(final_width,900);
        }catch (Exception e){
            System.out.println(e+"viewGroup计算错误");
        }
    }

    @Override
    protected void onLayout(boolean changed,int l,int t, int r, int b){
        try{
            */
/**
             * 此处对子布局如下：中间最上方防止master结点
             * 下面一层一次放置slave结点
             * 子view为一个linearLayout，包含一个imageview和一个textview
             *//*

            View childView;
            //子view的个数
            int cCount=this.getChildCount();
            //此viewgroup的宽和高
            int pWidth=this.getMeasuredWidth();
            int pHeight=this.getMeasuredHeight();
            //slave之间的间隔距离
            int div=(pWidth-findViewById(R.id.master).getMeasuredWidth()*2)/cCount;

            for(int i=0;i<cCount;i++){
                childView=getChildAt(i);
                //子view的宽和高
                int cWidth=childView.getMeasuredWidth();
                int cHeight=childView.getMeasuredHeight();
                MarginLayoutParams params=(MarginLayoutParams)childView.getLayoutParams();
                int ml=0,mt=0,mr=0,mb=0;

                switch (i){
                    //master结点
                    case 0:
                        ml=pWidth/2-cWidth/2;
                        mr=ml+cWidth;
                        mt=params.topMargin;
                        mb=mt+cHeight;
                        break;
                    //slave1结点
                    case 1:
                        ml=div+params.leftMargin;
                        mr=ml+cWidth;
                        mb=pHeight-params.bottomMargin;
                        mt=mb-cHeight;
                        break;
                    //slave2结点
                    case 2:
                        mr=pWidth-div;
                        ml=mr-cWidth;

                        mb=pHeight-params.bottomMargin;
                        mt=mb-cHeight;
                        break;
                }
                childView.layout(ml,mt,mr,mb);
            }
        }catch (Exception e){
            System.out.println(e+" viewgroup布局错误");
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        System.out.println("begin draw");
        super.onDraw(canvas);
        int cCount=getChildCount();
//        for(int i=0;i<cCount;i++){
//            getChildAt(i).draw(canvas);
//        }
        Paint paint=new Paint();
        //设置画笔基本属性  
        paint.setAntiAlias(true);//抗锯齿功能  
        paint.setColor(Color.BLUE);//设置画笔颜色      
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE  
        paint.setStrokeWidth(5);//设置画笔宽度  
        paint.setShadowLayer(10,15,15,Color.GREEN);//设置阴影  
        // 设置画布背景颜色       
        canvas.drawRGB(255,255,255);
        LinearLayout master,slave1,slave2;
        master=findViewById(R.id.master);
        slave1=findViewById(R.id.slave1);
        slave2=findViewById(R.id.slave2);
        float startX=master.getX()+master.getMeasuredWidth()/2;
        float startY=master.getY()+master.getMeasuredHeight();
        float endX1=slave1.getX()+slave1.getMeasuredWidth()/2;
        float endY1=slave1.getY();
        float endX2=slave2.getX()+slave2.getMeasuredWidth()/2;
        float endY2=slave2.getY();
        canvas.drawLine(startX,startY,endX1,endY1,paint);
        canvas.drawLine(startX,startY,endX2,endY2,paint);
        System.out.println("end draw");
    }
}
*/
