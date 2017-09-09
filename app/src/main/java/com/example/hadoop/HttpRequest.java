package com.example.hadoop;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class HttpRequest{
    public final static String url = "http://202.121.178.223/ganglia/data.php";

/*
    //请求需要的数据，运用httpClient，采用GET方法
    public static String DataGet(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            //请求发送成功
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                return EntityUtils.toString(response.getEntity());
            } else {
                return null;
            }
        } finally {
            response.close();
        }
    }
*/

    //httpURLConnection方法
    public static String POST(String myURL, String para) throws IOException,RuntimeException{
        //返回的结果
        String response = new String();
        //数据的缓存读取
        BufferedReader bufferedReader = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(myURL);
            URLConnection urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
            //设置请求方式
            httpURLConnection.setRequestMethod("POST");
            //设置向httpURLConnection输出，也就是用于传递参数，GET方法就不用
            httpURLConnection.setDoOutput(true);
            //设置不使用缓存，POST请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            //设置请求格式
            httpURLConnection.setRequestProperty("Content-Type", " application/json");
            //设置长连接
            httpURLConnection.setRequestProperty("Connection","keep-alive");
            //设置编码语言
            httpURLConnection.setRequestProperty("Accept_charset","utf-8");
            //设置连接时长和打开时长
            httpURLConnection.setConnectTimeout(80000);
            httpURLConnection.setReadTimeout(80000);

            //进行连接
            httpURLConnection.connect();
//            System.out.println(para);
            //打开输出流，传递参数
            OutputStream outputStream = httpURLConnection.getOutputStream();

            //写入参数
            outputStream.write(para.getBytes());
            outputStream.flush();
            outputStream.close();

            //打开输入流，并得到结果
            InputStream inputStream = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            if (httpURLConnection.getResponseCode() == 200) {
                String temp;

                while ((temp = bufferedReader.readLine()) != null) {
                    response += temp;
                    response +="\r\n";
                }
            }
            bufferedReader.close();

        } catch (IOException e) {
            throw new IOException();
        } catch(RuntimeException e){
            throw new RuntimeException();
        }
        finally {
            //当连接不成功时，这里会报错
            if (bufferedReader == null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }
//        System.out.println("end");
        return response.toString();
    }


    public static String GET(String myURL) throws IOException,RuntimeException{
        BufferedReader bufferedReader=null;
        HttpURLConnection httpURLConnection=null;
        String response=new String();
        try{
            URL url = new URL(myURL);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream=httpURLConnection.getInputStream();
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            if(200 == httpURLConnection.getResponseCode()){
                String temp;
                while((temp=bufferedReader.readLine()) != null){
                    response +=temp;
                }
            }
        } catch (RuntimeException e){
            throw new RuntimeException();
        } catch (IOException e){
            throw new IOException();
        }finally {
            //当连接不成功时，这里会报错
            if(bufferedReader == null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    throw new IOException();
                }
            }
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return response.toString();

    }

    //将post参数改成get参数，加速网络获取
    public static String paraTransform(String url,JSONObject para){
        StringBuffer resbuf=new StringBuffer();
        resbuf.append(url+"?");
        String key,res=null;
        Iterator mIterator=para.keys();
        try{
            while(mIterator.hasNext()){
                key=(String)mIterator.next();
                resbuf.append(key+"="+para.getString(key)+"&");
            }
            //去掉最后一个&
            res = resbuf.substring(0,resbuf.length()-1);
        }catch(Exception e){
            System.out.println(e);
        }
//        System.out.println(res);
        return res;
    }

    public static void main(String []args){

        //测试httpURLConnection
        try{
            //测试POST方法
            String url="http://localhost:8088/data.php";
            System.out.println(1);

            JSONObject jsonObject=new JSONObject();
            System.out.println("test");
            jsonObject.put("r","hour");
            jsonObject.put("c","hadoop_cluster");
//            jsonObject.put("m","load_one");
            jsonObject.put("g","mem_report");
            jsonObject.put("json",1);
            String para=jsonObject.toString();
            String res=HttpRequest.POST(url,para);
            System.out.println(res);

            //测试GET方法
//            System.out.println(HttpRequest.GET("http://202.121.178.223/ganglia/data.php?r=hour&c=hadoop_cluster&mc=2&g=cpu_report&json=1&m=load_five"));

        }catch (Exception e){
            System.out.print(e.toString());
        }
    }
}


class NewThreadByPOST implements Runnable{
    private String curr_url,curr_para;
    public String result;
    Thread t;
    NewThreadByPOST(String myURL,String myPARA){
        curr_url=myURL;
        curr_para=myPARA;
        t=new Thread(this,"HttpPost");
        t.start();
    }
    @Override
    public void run(){
        try{
            result=HttpRequest.POST(curr_url,curr_para);
        }catch(IOException e){
        }
        catch(RuntimeException e){
            throw new RuntimeException();
        }
    }

    public String getMessage(){
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){}
        return result;
    }

}

class NewThreadByGET implements Runnable{
    private String url;
    public String result;
    Thread t;
    NewThreadByGET(String myURL){
        url=myURL;
        t=new Thread(this,"HttpGet");
        t.start();
    }
    @Override
    public void run(){
        try{
            result=HttpRequest.GET(url);
        }catch (IOException e){}
        catch (RuntimeException e){
            throw new RuntimeException();
        }
    }
    public String getMessage(){
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){}
        return result;
    }
}

