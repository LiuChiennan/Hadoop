package com.example.hadoop;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by 刘建南 on 2017/9/6.
 */

/**
* Json解析类
*/
public class JsonPraser {
    public final static String MAPPID="59aa2f17";

    public final static int REC_ERROR=0,
            CPU_FLAG=1,
            MEM_FLAG=2,
            LOAD_FLAG=3,
            NETWORK_FLAG=4,
            DISK_FLAG=5,
            MASTER_FLAG=6,
            SLAVE1_FLAG=7,
            SLAVE2_FLAG=8,
            FLUSH_FLAG=9,
            MAIN_FLAG=10,
            TOTAL_FLAG=11;


    /**
     * 0--没有任何关键词
     * 1--转到cpu
     * 2--转到mem
     * 3--转到load
     * 4--转到network
     * 5--转到disk
     * 6--MASTER结点
     * 7--SLAVE1结点
     * 8--SLAVE2结点
     * 9--刷新
     * 10--主界面
     * 11--整体图像
     */
    //解析语法分析之后的结果,判断是否含有特定关键词
   public static int manageResult(String result){
       if(result.contains("cpu") || result.contains("CPU") || result.contains("处理器")){
           return CPU_FLAG;
       }
       if(result.contains("memory") || result.contains("MEMORY") || result.contains("内存")){
           return MEM_FLAG;
       }
       if(result.contains("load") || result.contains("LOAD") || result.contains("负载")){
           return LOAD_FLAG;
       }
       if(result.contains("network") || result.contains("NETWORK") || result.contains("网络")){
           return NETWORK_FLAG;
       }
       if(result.contains("disk") || result.contains("DISK") || result.contains("磁盘")){
           return DISK_FLAG;
       }
       if(result.contains("flush") || result.contains("FLUSH")|| result.contains("刷新")){
           return FLUSH_FLAG;
       }
       if(result.contains("master") || result.contains("MASTER") || result.contains("NAMENODE")){
           return MASTER_FLAG;
       }
       if(result.contains("slave1") || result.contains("SLAVE1") || result.contains("DATANODE1")){
           return SLAVE1_FLAG;
       }
       if(result.contains("slave2") || result.contains("SLAVE2") || result.contains("DATANODE2")){
           return SLAVE2_FLAG;
       }
       if(result.contains("MAIN") || result.contains("MAINACTIVITY") || result.contains("主界面")){
           return MAIN_FLAG;
       }
       if(result.contains("TOTAL") || result.contains("所有") || result.contains("整体") ){
           return TOTAL_FLAG;
       }
       return REC_ERROR;
   }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append(obj.getString("w"));
//                    ret.append("【结果】" + obj.getString("w"));
//                    ret.append("【置信度】" + obj.getInt("sc"));
//                    ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

    public static String parseLocalGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("\n");
                }
            }
            ret.append("【置信度】" + joResult.optInt("sc"));

        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

    public static String parseTransResult(String json,String key) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            String errorCode = joResult.optString("ret");
            if(!errorCode.equals("0")) {
                return joResult.optString("errmsg");
            }
            JSONObject transResult = joResult.optJSONObject("trans_result");
            ret.append(transResult.optString(key));
			/*JSONArray words = joResult.getJSONArray("results");
			for (int i = 0; i < words.length(); i++) {
				JSONObject obj = words.getJSONObject(i);
				ret.append(obj.getString(key));
			}*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }


}
