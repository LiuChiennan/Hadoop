package com.example.hadoop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by 刘建南 on 2017/8/16.
 *
 */

public class LOADShow extends AppCompatActivity implements myPara,View.OnClickListener{

    private FloatingActionButton fab;
    //    private Button butt;
//    private Spinner spinner;
    //当前是哪一个结点,null表示无结点
    private String node=null;
    private LinearLayout layout;
    private Toast mToast;
    private Handler mHandler;

    private final int ADD_VIEW=1;
    //带动画的语音识别对象
    private RecognizerDialog mRecoDialog=null;
    //语音识别传回的结果
    String result="";


    ArrayList<JSONObject> mjsonObjects=new ArrayList<>();
    //存储各个图表的数据
    ArrayList<graphData> mGraphDatas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_load);
        initLayout();
        initSpeech();
        initViews(null);
    }
    private void initLayout(){
        //初始化语音服务
        SpeechUtility.createUtility(LOADShow.this,SpeechConstant.APPID+"="+JsonPraser.MAPPID);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        //设置到最顶层
        fab.bringToFront();
        fab.setOnClickListener(this);
        mToast=Toast.makeText(LOADShow.this,"",Toast.LENGTH_SHORT);
        layout=(LinearLayout)findViewById(R.id.load);

        //用于主线程更新UI
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case ADD_VIEW:
                        super.handleMessage(msg);
                        GraphicalView gv;
                        for(graphData g : mGraphDatas){
                            //获取chartview
                            gv=ChartFactory.getTimeChartView(LOADShow.this,g.getDataSet(),g.getRenderer(),g.getFormat());
                            //更新UI
                            layout.addView(gv,LinearLayout.LayoutParams.MATCH_PARENT,
                                    1200);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    private void initSpeech(){
        //有动画的监听
        mRecoDialog=new RecognizerDialog(this,initListener);
        mRecoDialog.setParameter(SpeechConstant.LANGUAGE,"zh-cn");
        mRecoDialog.setParameter(SpeechConstant.ACCENT,"mandarin");
        mRecoDialog.setListener(mRecoDiaListener);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){

            case R.id.fab:
                result="";
                if(null == mRecoDialog){
                    initSpeech();
                    mRecoDialog.show();
                }
                else{
                    mRecoDialog.show();
                }
                break;
            default:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.topo_graph:
                //need to draw
                Intent intent=new Intent();
                intent.setData(Uri.parse("http://202.121.178.223/myweb/html/draw_topo.html"));
                startActivity(intent);
                break;
            case R.id.add_node:
                //need to do
                break;
            case R.id.dec_node:
                //need to do
                break;
            case R.id.menu_cpu:
                startActivity(new Intent(LOADShow.this,CPUShow.class));
                break;
            case R.id.menu_mem:
                startActivity(new Intent(LOADShow.this,MEMShow.class));
                break;
            case R.id.menu_network:
                startActivity(new Intent(LOADShow.this,NETWORKShow.class));
                break;
            case R.id.menu_load:
                startActivity(new Intent(LOADShow.this,LOADShow.class));
                break;
            case R.id.refresh:
                mGraphDatas.clear();
                layout.removeAllViews();
                initViews(node);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void initViews(String node){

        //采用POST方法获取数据，先添加参数
        JSONObject jsonObject_hour=new JSONObject();
        JSONObject jsonObject_2hr=new JSONObject();
        JSONObject jsonObject_4hr=new JSONObject();
        JSONObject jsonObject_day=new JSONObject();
        JSONObject jsonObject_week=new JSONObject();
        JSONObject jsonObject_month=new JSONObject();
        JSONObject jsonObject_year=new JSONObject();

        mjsonObjects.add(jsonObject_hour);
        mjsonObjects.add(jsonObject_2hr);
        mjsonObjects.add(jsonObject_4hr);
        mjsonObjects.add(jsonObject_day);
        mjsonObjects.add(jsonObject_week);
        mjsonObjects.add(jsonObject_month);
        mjsonObjects.add(jsonObject_year);

        try{
            for (JSONObject temp:mjsonObjects
                    ) {
                temp.put("g","load_report");
                temp.put("json",1);
                temp.put("c","hadoop_cluster");
                if(null != node){
                    temp.put("h",node);
                }
            }
            jsonObject_hour.put("r","hour");
            jsonObject_2hr.put("r","2hr");
            jsonObject_4hr.put("r","4hr");
            jsonObject_day.put("r","day");
            jsonObject_week.put("r","week");
            jsonObject_month.put("r","month");
            jsonObject_year.put("r","year");

//            System.out.println(jsonObject_2hr);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    graphData gD;
                    //获取数据
                    for (JSONObject temp:mjsonObjects
                            ) {
                        try{
                            String result=HttpRequest.GET(HttpRequest.paraTransform(HttpRequest.url,temp));
                            String title=temp.getString("r");
                            if(result != null){
                                gD=getGraphData(new JSONArray(result),temp.getString("r"), title);
                                mGraphDatas.add(gD);
                            }

                        }catch (Exception e){
                            System.out.println(e+"thread");
                        }
                    }
                    //通知主线程更新UI
                    Message msg=new Message();
                    msg.what=ADD_VIEW;
                    Looper.prepare();
                    mHandler.sendMessage(msg);
                }
            }).start();

//            System.out.println("finish");

        }catch(Exception e){
            Log.d("test",e.toString());
            e.printStackTrace();
        }

    }



    //获取画折线图的数据，第一个参数是数据，第二个参数是时间范围
    private graphData getGraphData(JSONArray jsonArray,String para, String title){
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataSet;
//        System.out.println("begin");
        //得到数据集
        dataSet=chartService.getXYMultipleSeriesDataset(jsonArray,para);
//        System.out.println(4);
//        System.out.println("test"+para);
        int len=jsonArray.length();

        renderer.setApplyBackgroundColor(true);//true:允许自定义背景颜色，false:不允许自定义背景颜色
        renderer.setBackgroundColor(Color.BLACK);
        //防止与ScrollView冲突
        renderer.setInScroll(true);
        // 设置XY轴名称
        renderer.setXTitle("时间");
        renderer.setYTitle("Loads/Procs");
        // 设置标题
        renderer.setChartTitle("load in the last "+title);
        // 设置Y轴最大值,也是各个metric不同的地方
        renderer.setYAxisMax(chartService.manageYlabel(8.5));
//        renderer.setXAxisMax(10);
//        renderer.setXAxisMin(0);
        renderer.setYAxisMin(0);
        // 设置XY轴颜色
        renderer.setAxesColor(Color.WHITE);
        renderer.setLabelsColor(Color.WHITE);
        // 设置XY轴显示
        renderer.setYLabels(10);
        renderer.setXLabels(10);
        // 设置 是否显示图例
        renderer.setShowLegend(true);
        // 设置不显示放大缩小图标
        renderer.setZoomEnabled(true);
        // 设置是否支持图表缩放
        renderer.setPanEnabled(false, false);
        // 设置是否可点击
        renderer.setClickEnabled(false);
        // 是否显示网格
        renderer.setShowGrid(true);
        // 设置空白区的颜色
        renderer.setMarginsColor(Color.BLACK);
//        renderer.setBackgroundColor(getResources().);
        // 设置坐标轴文字颜色
        renderer.setXLabelsColor(Color.WHITE);
        renderer.setYLabelsColor(0, Color.WHITE);
        // 刻度线与刻度标注之间的相对位置关系
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        // 刻度线与刻度标注之间的相对位置关系
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
        renderer.setPanEnabled(true);
        renderer.setMargins(new int[] { 150, 130, 120, 20 });// 设置图表的外边框(上/左/下/右)
        renderer.setAxisTitleTextSize(45);// 设置轴标题文字的大小
        renderer.setChartTitleTextSize(60);// 设置整个图表标题文字的大小
        renderer.setLabelsTextSize(35);// 设置轴刻度文字的大小
        renderer.setLegendTextSize(50);// 设置图例文字大小
        renderer.setPointSize(5);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
        for(int i=0;i<len;i++){
            XYSeriesRenderer curr_renderer=new XYSeriesRenderer();
            curr_renderer.setColor(lineColor[i]);
//            curr_renderer.setPointStyle(PointStyle.CIRCLE);
            curr_renderer.setFillPoints(true);
            curr_renderer.setLineWidth(5f);
            renderer.addSeriesRenderer(curr_renderer);
        }

        graphData graphData=new graphData(dataSet,renderer,chartService.getDateFormat(para));
        //移除掉所有的view，便于刷新
//        layout.removeAllViews();
//        System.out.println("here");
//        layout.setBackgroundColor(Color.BLACK);
//        System.out.println("end");
        return graphData;

    }

    /**
     * 各个监听器
     */
    //初始化监听器
    private InitListener initListener=new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            }
        }
    };

    //有动画的监听器
    private RecognizerDialogListener mRecoDiaListener=new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            if(!b){
                if(null != recognizerResult){
                    String text;
                    text= JsonPraser.parseIatResult(recognizerResult.getResultString());
                    result += text;
                }
                else{
                    Log.d(TAG, "recognizer result : null");
                }
            }
            else{
                //成功之后跳转到另一个activity
                int rat=JsonPraser.manageResult(result);
                Intent intent;
                switch (rat){
                    //主界面
                    case JsonPraser.MAIN_FLAG:
                        intent=new Intent(LOADShow.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    //cpu
                    case JsonPraser.CPU_FLAG:
                        intent=new Intent(LOADShow.this,CPUShow.class);
                        startActivity(intent);
                        break;
                    //mem
                    case JsonPraser.MEM_FLAG:
                        intent=new Intent(LOADShow.this,MEMShow.class);
                        startActivity(intent);
                        break;
                    //load
                    case JsonPraser.LOAD_FLAG:
                        //DO NOTHING
                        break;
                    //network
                    case JsonPraser.NETWORK_FLAG:
                        intent=new Intent(LOADShow.this,NETWORKShow.class);
                        startActivity(intent);
                        break;
                    //disk
//                    case JsonPraser.DISK_FLAG:
//                        break;
                    //error
                    case JsonPraser.REC_ERROR:
                        Toast.makeText(LOADShow.this,"识别错误",Toast.LENGTH_SHORT).show();
                        break;
                    //刷新
                    case JsonPraser.FLUSH_FLAG:
                        mGraphDatas.clear();
                        layout.removeAllViews();
                        initViews(node);
                        break;
                    case JsonPraser.MASTER_FLAG:
                        if(node != "master"){
                            mGraphDatas.clear();
                            node="master";
                            initViews(node);
                        }
                    case JsonPraser.SLAVE1_FLAG:
                        if(node != "slave1"){
                            mGraphDatas.clear();
                            node="slave1";
                            initViews(node);
                        }
                    case JsonPraser.SLAVE2_FLAG:
                        if(node != "slave2"){
                            mGraphDatas.clear();
                            node="slave2";
                            initViews(node);
                        }
                    default:
                        break;
                }
                System.out.println(result);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            System.out.println("error");
            //获取错误码描述
            showTip("onError Code："	+ speechError.getErrorCode());
        }
    };


    //提示语
    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

}
