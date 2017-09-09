package com.example.hadoop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
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

public class MainActivity extends AppCompatActivity implements myPara,View.OnClickListener{
   private FloatingActionButton fab;
    //跳转的四个按钮
    private Button button_cpu,button_mem,button_network,button_load,audioRec;
    //进度条
    private ProgressBar progressBar;
    //加载图表的那个布局
    private LinearLayout layout;
    //用于异步获取数据
    private Handler myHandler;
    private GraphicalView view;
    //所有图表所生成的view
    private ArrayList<GraphicalView> graphs=new ArrayList<>();
    //生成TimeChart时需要的数据
    private ArrayList<graphData> graphDatas=new ArrayList<>();

    private final static int ADD_VIEW=1;

    private final static String TAG ="test" ;

    private Toast mToast;

    private String result="";
    //语法文件
    private String mGrammarContent="#ABNF 1.0 UTF-8;\n" +
            "language zh-CN; \n" +
            "mode voice;\n" +
            "\n" +
            "root $main;\n" +
            "$main = $place1 到 $place2;\n" +
            "$place1 = 北京|武汉|南京|天津|东京;\n" +
            "$place2 = 上海|合肥;";
    //语法ID
    private String mGrammarID;
    //语音识别对象
    private SpeechRecognizer mSpeechRecognizer=null;
    //带动画的语音识别对象
    private RecognizerDialog mRecoDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Log.d("test","-------------------------------");
//        HttpRequest httpRequest=new HttpRequest();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initLayout();
        initSpeech();

        myHandler=new Handler(){

            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case ADD_VIEW:
                        super.handleMessage(msg);
                        getViews();
                        for(GraphicalView g:graphs){
                            //更新UI
                            layout.addView(g,LinearLayout.LayoutParams.MATCH_PARENT,
                                    1200);
                        }
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        };

        //新建一个线程来获取数据生成GraphView
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    getData();
                    Message msg=new Message();
                    msg.what=ADD_VIEW;
                    Looper.prepare();
                    myHandler.sendMessage(msg);
//                    myHandler.handleMessage(msg);
                }catch (Exception e){
                    System.out.println("thread error"+e);
                }
//                System.out.println("here6");
            }
        }).start();
    }

    private void initLayout(){
        //初始化语音服务
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"="+JsonPraser.MAPPID);
        layout=(LinearLayout)findViewById(R.id.home);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        //悬浮窗口最上层
        fab.bringToFront();
        fab.setOnClickListener(this);
        Toast.makeText(MainActivity.this,"hhh",Toast.LENGTH_SHORT).show();;
        /*button_cpu=(Button)findViewById(R.id.cpu);
        button_cpu.setOnClickListener(this);
        button_mem=(Button)findViewById(R.id.mem);
        button_mem.setOnClickListener(this);
        button_network=(Button)findViewById(R.id.network);
        button_network.setOnClickListener(this);
        button_load=(Button)findViewById(R.id.load);
        button_load.setOnClickListener(this);
        audioRec=(Button)findViewById(R.id.audioRec) ;
        audioRec.setOnClickListener(this);*/
        mToast=Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void initSpeech(){
//        System.out.println("here1");
        /*//无动画监听
        mSpeechRecognizer= SpeechRecognizer.createRecognizer(this,initListener);
        mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE,"zh-cn");
        mSpeechRecognizer.setParameter(SpeechConstant.DOMAIN,"iat");
        mSpeechRecognizer.setParameter(SpeechConstant.ACCENT,"mandarin");
        mSpeechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
        System.out.println("here2");
        //构建语法
        mSpeechRecognizer.buildGrammar("abnf",mGrammarContent,mGramListener);
        //设置引擎模式
        mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
        //设置语法id
        mSpeechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR,mGrammarID);*/
//        System.out.println("here3");
          //有动画的监听
        mRecoDialog=new RecognizerDialog(this,initListener);
        mRecoDialog.setParameter(SpeechConstant.LANGUAGE,"zh-cn");
        mRecoDialog.setParameter(SpeechConstant.ACCENT,"mandarin");
        mRecoDialog.setListener(mRecoDiaListener);
    }
    @Override
    public  void onClick(View v){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent;
        switch (v.getId()){
            //语音悬浮按钮
            case R.id.fab:
                progressBar.setVisibility(View.GONE);
//                Toast.makeText(MainActivity.this,"this is a float button",Toast.LENGTH_SHORT).show();
                //有动画的处理
                if(mRecoDialog != null){
                    mRecoDialog.show();
                }
                else{
                    initSpeech();
                    mRecoDialog.show();
                }

                /*if(mSpeechRecognizer != null){
                    mSpeechRecognizer.startListening(mRecListener);
                }
                else{
                    initSpeech();
                    mSpeechRecognizer.startListening(mRecListener);
                }*/
                break;
            default:
                break;
        }
    }
   /*@Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        progressBar.setVisibility(View.GONE);
    }*/

    //初始化，并获得数据，生成画图所需的graphData
    private void getData(){

        JSONObject json_cpu=new JSONObject();
        JSONObject json_mem=new JSONObject();
        JSONObject json_network=new JSONObject();
        JSONObject json_load=new JSONObject();
//        System.out.println("5");
        try{
            json_cpu.put("c","hadoop_cluster");
            json_mem.put("c","hadoop_cluster");
            json_network.put("c","hadoop_cluster");
            json_load.put("c","hadoop_cluster");

//            System.out.println("here");
            json_cpu.put("r","hour");
            json_mem.put("r","hour");
            json_network.put("r","hour");
            json_load.put("r","hour");

            json_cpu.put("g","cpu_report");
            json_mem.put("g","mem_report");
            json_network.put("g","network_report");
            json_load.put("g","load_report");

            json_cpu.put("json",1);
            json_mem.put("json",1);
            json_network.put("json",1);
            json_load.put("json",1);
            String res_cpu,res_mem,res_network,res_load;
//            System.out.println("6");
            try{
                res_cpu=HttpRequest.GET(HttpRequest.paraTransform(HttpRequest.url,json_cpu));
                res_mem=HttpRequest.GET(HttpRequest.paraTransform(HttpRequest.url,json_mem));
                res_network=HttpRequest.GET(HttpRequest.paraTransform(HttpRequest.url,json_network));
                res_load=HttpRequest.GET(HttpRequest.paraTransform(HttpRequest.url,json_load));
//                res_cpu=new NewThreadByPOST(HttpRequest.url,json_cpu.toString()).getMessage();
//                res_mem=new NewThreadByPOST(HttpRequest.url,json_mem.toString()).getMessage();
//                res_network=new NewThreadByPOST(HttpRequest.url,json_network.toString()).getMessage();
//                res_load=new NewThreadByPOST(HttpRequest.url,json_load.toString()).getMessage();

            }catch (Exception e){
                System.out.println("error");
                throw e;
            }
//            System.out.println("7");
            graphData temp;

            temp=getGraphData(new JSONArray(res_cpu),"hour",layout,"cpu",100,"Percent");
            graphDatas.add(temp);

//            System.out.println("8");
//            System.out.println("finish");

            temp=getGraphData(new JSONArray(res_mem),"hour",layout,"mem",2000000000,"Bytes");
            graphDatas.add(temp);
//            System.out.println("9");
            temp=getGraphData(new JSONArray(res_network),"hour",layout,"network",30000,"Bytes/secs");
            graphDatas.add(temp);
//            System.out.println("10");
            temp=getGraphData(new JSONArray(res_load),"hour",layout,"load",8.5,"Loads/Procs");
            graphDatas.add(temp);
//            System.out.println("11");
        }catch(Exception e){
            System.out.println(e);
        }

    }


    //直接生成graphData
    private graphData getGraphData(JSONArray jsonArray, String timeRange,
                                LinearLayout layout, String chartTitle,
                                double yMax, String yTitle){
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataSet;
//        System.out.println("begin");
        //得到数据集
        try{
            dataSet=chartService.getXYMultipleSeriesDataset(jsonArray,timeRange);
        }catch (Exception e) {throw e;}
//        System.out.println("8");
//        System.out.println(4);
//        System.out.println("test");
        int len=jsonArray.length();

        renderer.setApplyBackgroundColor(true);//true:允许自定义背景颜色，false:不允许自定义背景颜色
//        renderer.setBackgroundColor(Color.GRAY);
        //防止与ScrollView冲突
        renderer.setInScroll(true);
        // 设置XY轴名称
        renderer.setXTitle("时间");
        renderer.setYTitle(yTitle);
        // 设置标题
        renderer.setChartTitle(chartTitle +" Show in the last hour");
        // 设置Y轴最大值
        renderer.setYAxisMax(yMax);
//        renderer.setXAxisMax(10);
//        renderer.setXAxisMin(0);
        renderer.setYAxisMin(0);
        // 设置XY轴颜色
        renderer.setAxesColor(Color.BLACK);
        renderer.setLabelsColor(Color.BLACK);
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
        renderer.setMarginsColor(Color.WHITE);
//        renderer.setBackgroundColor(getResources().);
        // 设置坐标轴文字颜色
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        // 刻度线与刻度标注之间的相对位置关系
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        // 刻度线与刻度标注之间的相对位置关系
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
        renderer.setPanEnabled(true);
        renderer.setMargins(new int[] { 50, 50, 20, 20 });// 设置图表的外边框(上/左/下/右)
        renderer.setAxisTitleTextSize(20);// 设置轴标题文字的大小
        renderer.setChartTitleTextSize(30);// 设置整个图表标题文字的大小
        renderer.setLabelsTextSize(15);// 设置轴刻度文字的大小
        renderer.setLegendTextSize(15);// 设置图例文字大小
        renderer.setPointSize(5);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
        for(int i=0;i<len;i++){
            XYSeriesRenderer curr_renderer=new XYSeriesRenderer();
            curr_renderer.setColor(lineColor[i]);
//            curr_renderer.setPointStyle(PointStyle.CIRCLE);
            curr_renderer.setFillPoints(true);
            renderer.addSeriesRenderer(curr_renderer);
        }
//        System.out.println("here+");

        return new graphData(dataSet,renderer,"M/d HH:mm");
        //这个函数会用到Handler
//        GraphicalView view = ChartFactory.getTimeChartView(this, dataSet,
//                renderer,"M/d HH:mm");
////        layout.setBackgroundColor(Color.BLACK);

    }

    //生成各个view
    private void getViews(){
        for(graphData gd:graphDatas){
            view= ChartFactory.getTimeChartView(this,gd.getDataSet(),gd.getRenderer(),gd.getFormat());
            graphs.add(view);
        }
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
                    //cpu
                    case JsonPraser.CPU_FLAG:
                        intent=new Intent(MainActivity.this,CPUShow.class);
                        startActivity(intent);
                        break;
                    //mem
                    case JsonPraser.MEM_FLAG:
                        intent=new Intent(MainActivity.this,MEMShow.class);
                        startActivity(intent);
                        break;
                    //load
                    case JsonPraser.LOAD_FLAG:
                        intent=new Intent(MainActivity.this,LOADShow.class);
                        startActivity(intent);
                        break;
                    //network
                    case JsonPraser.NETWORK_FLAG:
                        intent=new Intent(MainActivity.this,NETWORKShow.class);
                        startActivity(intent);
                        break;
                    //disk
//                    case JsonPraser.DISK_FLAG:
//                        break;
                    //error
                    case JsonPraser.REC_ERROR:
                        Toast.makeText(MainActivity.this,"识别错误",Toast.LENGTH_SHORT).show();
                        break;
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

    //无动画的监听器
    private RecognizerListener mRecListener=new RecognizerListener() {
        //处理返回的数据，recognizerResult是返回的结果,b表示是否是最后一次传回结果
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
                    //cpu
                    case 0:
                        intent=new Intent(MainActivity.this,CPUShow.class);
                        startActivity(intent);
                        break;
                    //mem
                    case 1:
                        intent=new Intent(MainActivity.this,MEMShow.class);
                        startActivity(intent);
                        break;
                    //load
                    case 2:
                        intent=new Intent(MainActivity.this,LOADShow.class);
                        startActivity(intent);
                        break;
                    //network
                    case 3:
                        intent=new Intent(MainActivity.this,NETWORKShow.class);
                        startActivity(intent);
                        break;
                    //disk
//                    case 4:
//                        break;
                    //error
                    case 5:
                        Toast.makeText(MainActivity.this,"识别错误",Toast.LENGTH_SHORT).show();
                        break;
                }
                System.out.println(result);
            }
        }


        //音量值0~30
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }
        //开始录音
        @Override
        public void onBeginOfSpeech() {
            System.out.println("begin record!");
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            System.out.println("end begin");
        }

        //结束录音
        @Override
        public void onEndOfSpeech() {
            System.out.println("end");
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }


        @Override
        public void onError(SpeechError speechError) {
            System.out.println("error");
            //获取错误码描述
            showTip("onError Code："	+ speechError.getErrorCode());
        }

        //扩展用接口 
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                Log.d(TAG, "session id =" + sid);
            }
        }
    };

    //语法监听器
    private GrammarListener mGramListener=new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarID, SpeechError speechError) {
            if(speechError == null){
                showTip("语法创建成功");
                System.out.println("创建成功");
                mGrammarID=grammarID;
            }
            else{
                showTip("语法创建失败");
                System.out.println("创建失败");
            }
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

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}


