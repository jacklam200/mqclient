package cn.mqclient.service;


import cn.mqclient.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import cn.mqclient.http.upload.FileChunk;
import cn.mqclient.model.UploadRequest;
import cn.mqclient.utils.Md5;
import cn.mqclient.utils.MqConstants;
import cn.mqclient.utils.SharePref;

/**
 * Created by LinZaixiong on 2016/9/25.
 */

public class UploadService {
    private static final String TAG = "UploadService";

    private String url = "http://consumer.wissage.com/api/v1/fileupload/uploadfile?token=";
    private FileChunk fileChunk;
    private String fileName;
    private String name;
    private String type;
    private String md5;
    private long size;
    private long chunks;
    private String id;

    public UploadService(FileChunk file, String id){
        fileChunk = file;
        this.id = id;
        fileName = fileChunk.getFile().getAbsolutePath()/*+fileChunk.getFile().getName()*/;
        Log.d(TAG, "fileName:" + fileName);
        name = fileChunk.getFile().getName();
        Log.d(TAG, "name:" + name);
        type =  new MimetypesFileTypeMap().getContentType(fileChunk.getFile());
        Log.d(TAG, "type:" + type);
        try{
            md5 = Md5.getMd5File(fileChunk.getFile());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "md5:" + md5);
        size = fileChunk.getFileSize();
        Log.d(TAG, "size:" + size);
        chunks = fileChunk.getChunkSize();
        Log.d(TAG, "totalChunk:" + chunks);
    }

    private static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
//            file.createNewFile();
                Log.e("获取文件大小", "文件不存在!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return size;
    }

    public String uploadImage(long chunk) throws IOException{
        Map<String, String> textMap = new HashMap<String, String>();
        long start = System.currentTimeMillis();
        Log.d(TAG, "size :" + size);
        Log.d(TAG,"md5 :" + md5);
        //可以设置多个input的name，value
        textMap.put("name", name);
        textMap.put("md5", md5);
        textMap.put("size", "" + size);
        textMap.put("type", type);
        textMap.put("id", id);
        textMap.put("chunks", "" + chunks);
        textMap.put("chunk", "" + chunk);
        String ret = formUpload(url+SharePref.getInstance().getString(MqConstants.TOKEN, ""), textMap, chunk);
        Log.d(TAG, "use :" + (System.currentTimeMillis() - start));
        Log.d(TAG, "POST响应:" + ret);
        return ret;
    }

    public String formUpload(String urlStr, Map<String, String> textMap, long chunk) {
        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "e033308c-d063-4e40-842e-8c904ffd0d24";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
//            String cookies = "JSESSIONID=y3fntdknft7cbl87htkgjqsg; visited=yes";//SharePref.getInstance().getString("Set-Cookie", "");
//            conn.setRequestProperty("Cookie", cookies);
            conn.setRequestProperty("Accept",
                    "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");


            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            if (type == null || "".equals(type)) {
                type = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file"
                    + "\"; filename=\"" + fileName
                    + "\"\r\n");
            strBuf.append("Content-Type:" + type + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());



            byte[] bufferOut = null;

            long chunkSize = fileChunk.getChunkSizeByIndex(chunk);
            long offset = fileChunk.getChunkOffsetByIndex(chunk);
            long chunkLastOffset = offset + chunkSize;
            long iter = offset;
            long size = 1024;
            Log.d(TAG,"ChunkOffset :" + offset);
            Log.d(TAG,"chunkLastOffset :" + chunkLastOffset);
            //  2048 4000
            // 2048 + 1024 = 3072 < 4000
            // 3072 + 1024 = 4096 > 4000
            //
            while(iter <  chunkLastOffset){
                System.out.println("iter :" + iter);
                if(iter < chunkLastOffset &&  iter + size > chunkLastOffset){
                    size = chunkLastOffset - iter;
                }
                Log.d(TAG,"size :" + size);
                bufferOut = fileChunk.getRawData(iter, size);
                out.write(bufferOut, 0, (int)size);

                iter += size;
            }


//            DataInputStream in = new DataInputStream(
//                    new FileInputStream(fileChunk.getFile()));
//            int bytes = 0;
//
//            while ((bytes = in.read(bufferOut)) != -1) {
//                out.write(bufferOut, 0, bytes);
//            }
//            in.close();

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据

            StringBuffer strBufResponse = new StringBuffer();
            InputStream inputStream;
            try {
                inputStream = conn.getInputStream();
            } catch (IOException ioe) {
                inputStream = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBufResponse.append(line).append("\n");
            }
            res = strBufResponse.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return "UploadService{" +
                "chunks=" + chunks +
                ", url='" + url + '\'' +
                ", fileChunk=" + fileChunk +
                ", fileName='" + fileName + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", md5='" + md5 + '\'' +
                ", size=" + size +
                '}';
    }
}
