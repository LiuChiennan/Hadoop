package com.example.hadoop;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by 刘建南 on 2017/8/24.
 */

public class chartService {

    private Context context;

    //构造函数
    public chartService(Context context){
        this.context=context;
    }


    //得到默认的渲染器，各种属性设置
    public static XYMultipleSeriesRenderer getDefaultRenderer(){
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

//        renderer.setAntialiasing(true);//用在折线图会报错。。。true:消除锯齿；false:不消除锯齿；
//
        renderer.setApplyBackgroundColor(true);//true:允许自定义背景颜色，false:不允许自定义背景颜色
        renderer.setAxesColor(0x6666FFFF);
//
        renderer.setAxisTitleTextSize(20.0f);//xy轴标题字体的大小
//
        renderer.setBackgroundColor(0x6666FFFF);//设置xy轴围成的矩形的颜色
//        renderer.setBarSpacing(22.0);
        renderer.setChartTitle("cpu");//图标的标题
        renderer.setChartTitleTextSize(22.0f);//图表标题字体的大小
        renderer.setClickEnabled(true);//是否可移动折线，true:折线是固定不能移动的；false：折线可以移动；
        renderer.setDisplayValues(false);//是否显示图标上的数据
        renderer.setExternalZoomEnabled(true);//?TODO
        renderer.setFitLegend(true);//是否适应屏幕，true:适应屏幕，沾满屏幕；false:不适应；
        renderer.setGridColor(0xFF6666FF);//设置网格的颜色
        renderer.setInitialRange(new double[]{1.0,1.2,4.0});//?TODO
        renderer.setInitialRange(new double[]{1.0,1.2,4.0}, 10);//?TODO
        renderer.setInScroll(false);//?TODO
        renderer.setLabelsColor(0x3399FFFF);//xy轴和图表标题的颜色
        renderer.setLabelsTextSize(30.0f);//设置xy轴上数值的大小
        renderer.setLegendHeight(1);//? TODO
        renderer.setLegendTextSize(10f);//?TODO
        renderer.setMargins(new int[]{30,30,30,30});//设置外边距
        renderer.setMarginsColor(0x666666FF);//设置外边距的颜色
        renderer.setOrientation(null);//设置方向，比如设置柱状图的方向，水平或垂直
        renderer.setPanEnabled(true);//是否移动
        renderer.setPanEnabled(false, true);//某个数轴上可移动
        renderer.setPanLimits(new double[]{-x,x,-y,y});//设置平移的范围
        renderer.setPointSize(1.0f);//设置点的大小
        renderer.setRange(new double[]{1.2,1.4});//？TODO
        renderer.setScale(0.1f);//?TODO
        renderer.setSelectableBuffer(11);
        renderer.setShowLegend(false);
        renderer.setShowGrid(true);//是否显示网格，true：显示；false:显示。
        renderer.setShowAxes(true);//?TODO
        renderer.setShowCustomTextGrid(false);//?TODO
        renderer.setShowGrid(true);//是否显示网格线
        renderer.setShowGridX(true);//是否显示X方向的网格线
        renderer.setShowGridY(true);//是否显示Y方向的网格线
        renderer.setShowLabels(false);//是否显示XY轴的数值和标题
        renderer.setShowLegend(true);//是否显示图例，就是图表下对图中一些折线或者标识的一些解释
        renderer.setStartAngle(80.0f);//?TODO 应该是应用在饼状图中，
//        renderer.setTextTypeface(typefaceName, style);//设置字体名和类型 ，怎么用？？TODO
////        renderer.setXAxisMax(15.0f);//设置X最大值
////        renderer.setXAxisMax(15.0f, 0);//scale? TODO
////        renderer.setXAxisMin(1.1);//设置X最小值
////        renderer.setXLabels(9);//设置x轴上的标签数量，最大值根据所给坐标而定
////        renderer.setXLabelsAlign(Align.LEFT);//设置X轴标签的对齐方式，就是便签相对于左边点的位置
////        renderer.setXLabelsAngle(angle)//?TODO
////        renderer.setXLabelsColor(0x660000FF);//x轴数值的字体颜色
//        renderer.setXTitle("X Title");
////        renderer.setYAxisAlign(Align.CENTER, 0);//设置Y轴的位置。
////        renderer.setYAxisMax(max);//设置Y最大值
////        renderer.setYAxisMax(max, scale);//scale?TODO
////        renderer.setYAxisMin(min);设置Y最小值
////        renderer.setYLabels(yLabels);//设置Y轴标签的数量
////        renderer.setYLabelsAlign(Align.RIGHT);//设置y轴标签相对于y坐标轴的位置
////        renderer.setYLabelsAlign(Align.RIGHT, scale);//scale?TODO
////        renderer.setYLabelsColor(scale, color);//scale?TODO
//        renderer.setYTitle("YTitle");
////        renderer.setYTitle(title, scale);//scalse?TODO
//        renderer.setZoomButtonsVisible(true);//折线缩放按钮是否可见
////        renderer.setZoomEnabled(false);//是否可缩放
////        renderer.setZoomEnabled(true, false);//确定可以缩放的轴
////        renderer.setZoomLimits(double[]);//设置缩放的范围
////        renderer.setZoomRate(2.0f);

        return renderer;
    }

    //处理数据，生成制图需要的数据集,第二个参数是时间范围
    public static XYMultipleSeriesDataset getXYMultipleSeriesDataset(JSONArray myData,String para){

//        System.out.println(myData);
        //曲线的条数
        int chartLength=myData.length();
//        System.out.println(chartLength);
        //单条曲线数据点个数
        int data_len=0;
        //时间轴的跨度
        int time_para=myPara.timePara.get(para);
        //时间轴结束时间
        long end_date=new Date().getTime();
        //时间轴起始时间
        long start_date=end_date-1000*time_para;
        try{
            data_len=myData.getJSONObject(0).getJSONArray("datapoints").length();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("error");
        }
        //每个点的时间差
        long div=time_para*1000/data_len;
//        System.out.println(end_date);
        Date date[]=new Date[data_len] ;
        for(int i=0;i<data_len;i++){
            date[i]=new Date();
            date[i].setTime(start_date+i*div);
        }

//        System.out.println(date[100]);
        XYMultipleSeriesDataset dataSet=new XYMultipleSeriesDataset();
        try{
            for(int i=0;i<chartLength;i++){
                //得到每一组数据
                JSONObject curr_obj=myData.getJSONObject(i);
                //实际的数据信息
                JSONArray curr_arr=curr_obj.getJSONArray("datapoints");
//                Log.d("test",curr_arr.toString());

                //单条曲线数据集
                TimeSeries series=new TimeSeries(curr_obj.getString("metric_name"));
//                Log.d("test",curr_obj.getString("metric_name"));
                for(int j=0;j<data_len;j++){
                    //y轴数据
                    double d= curr_arr.getJSONArray(j).getDouble(0);
                    series.add(date[j],d);
                }
                dataSet.addSeries(series);
            }
        }catch(Exception e){
            Log.d("test","exception");
        }

        return dataSet;
    }


    //得到当前月份的天数
    public static int getCurrentMonthDays() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE,1);//把日期设置为当月第一天  
        a.roll(Calendar.DATE,-1);//日期回滚一天，也就是最后一天  
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    //得到当前年份的天数
    public static int getCurrentYearDays(){
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        if((year % 100) ==0){
            if((year % 400) ==0){
                return 366;
            }
            else return 365;
        }
        else{
            if((year % 4) ==0){
                return 366;
            }
            else return 365;
        }
    }

}




//画图时需要的一些参数
interface myPara{
    //时间参数
    Map<String,Integer> timePara=new HashMap<String,Integer>(){{
        put("hour",60*60);
        put("2hr",2*60*60);
        put("4hr",4*60*60);
        put("day",24*60*60);
        put("week",7*24*60*60);
        put("month", 31*24*60*60);
        put("year",chartService.getCurrentYearDays()*24*60*60);
    }};


    int[] lineColor={Color.BLUE,Color.RED,Color.GREEN,Color.rgb(0xff,0x33,0xee),Color.CYAN,Color.green(0x333333)};
}

class graphData{
    private XYMultipleSeriesDataset dataSet;
    private XYMultipleSeriesRenderer renderer;
    private String format;
    graphData(XYMultipleSeriesDataset dataSet,
              XYMultipleSeriesRenderer renderer,
              String format){
        this.dataSet =dataSet;
        this.renderer=renderer;
        this.format=format;
    }
    public XYMultipleSeriesDataset getDataSet(){return this.dataSet;}
    public XYMultipleSeriesRenderer getRenderer(){return this.renderer;}
    public String getFormat(){return this.format;}
}
