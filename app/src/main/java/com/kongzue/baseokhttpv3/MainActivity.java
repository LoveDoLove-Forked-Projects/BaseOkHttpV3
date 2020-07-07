package com.kongzue.baseokhttpv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.baseframework.BaseAdapter;
import com.kongzue.baseframework.interfaces.SimpleAdapterSettings;
import com.kongzue.baseokhttp.BaseWebSocket;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.listener.OnDownloadListener;
import com.kongzue.baseokhttp.listener.ParameterInterceptListener;
import com.kongzue.baseokhttp.listener.ResponseInterceptListener;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.kongzue.baseokhttp.listener.WebSocketStatusListener;
import com.kongzue.baseokhttp.util.BaseOkHttp;
import com.kongzue.baseokhttp.util.JsonFormat;
import com.kongzue.baseokhttp.util.JsonList;
import com.kongzue.baseokhttp.util.JsonMap;
import com.kongzue.baseokhttp.util.JsonUtil;
import com.kongzue.baseokhttp.util.Parameter;

import java.io.File;

import baseokhttp3.Response;
import baseokio.ByteString;

public class MainActivity extends AppCompatActivity {
    
    private ProgressDialog progressDialog;
    
    private Button btnHttp;
    private ListView listView;
    private TextView resultHttp;
    private Button btnConnect;
    private Button btnDisconnect;
    private TextView resultWebsocket;
    private EditText editSend;
    private Button btnSend;
    private Button btnDownload;
    private Button btnDownloadCancel;
    private ProgressBar psgDownload;
    
    
    private BaseWebSocket baseWebSocket;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnHttp = findViewById(R.id.btn_http);
        listView = findViewById(R.id.listView);
        resultHttp = findViewById(R.id.result_http);
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        resultWebsocket = findViewById(R.id.result_websocket);
        editSend = findViewById(R.id.edit_send);
        btnSend = findViewById(R.id.btn_send);
        btnDownload = findViewById(R.id.btn_download);
        btnDownloadCancel = findViewById(R.id.btn_download_cancel);
        psgDownload = findViewById(R.id.psg_download);
        
        BaseOkHttp.DEBUGMODE = true;
        BaseOkHttp.serviceUrl = "https://api.apiopen.top";
//        BaseOkHttp.reserveServiceUrls = new String[]{
//                "https://www.google.com",
//                "https://www.twitter.com",
//                "https://api.apiopen.top"
//        };
        BaseOkHttp.overallHeader = new Parameter()
                .add("Charset", "UTF-8")
                .add("Content-Type", "application/json")
                .add("Accept-Encoding", "gzip,deflate")
        ;
        BaseOkHttp.parameterInterceptListener = new ParameterInterceptListener<JsonMap>() {
            @Override
            public JsonMap onIntercept(Context context, String url, JsonMap o) {
                return null;
            }
        };
        BaseOkHttp.responseInterceptListener = new ResponseInterceptListener() {
            @Override
            public boolean onResponse(Context context, String url, String response, Exception error) {
                if (error != null) {
                    return true;
                } else {
                    //Log.i("!!!", "onResponse: " + response);
                    return true;
                }
            }
        };
        
        btnHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(MainActivity.this, "请稍候", "请求中...");
                HttpRequest.POST(MainActivity.this, "/getWangYiNews", new Parameter()
                                .add("page", "1")
                                .add("count", 5)
                        , new JsonResponseListener() {
                            @Override
                            public void onResponse(JsonMap main, Exception error) {
                                progressDialog.dismiss();
                                if (error == null) {
                                    resultHttp.setText(main.toString());
                                    JsonList dataList = main.getList("result");
                                    
                                    BaseAdapter<ViewHolder, JsonMap> baseAdapter = new BaseAdapter<ViewHolder, JsonMap>(
                                            MainActivity.this, dataList, R.layout.item_list, new SimpleAdapterSettings<ViewHolder, JsonMap>() {
                                        @Override
                                        public ViewHolder setViewHolder(View convertView) {
                                            ViewHolder viewHolder = new ViewHolder();
                                            viewHolder.txtList = convertView.findViewById(R.id.txt_list);
                                            
                                            return viewHolder;
                                        }
                                        
                                        @Override
                                        public void setData(ViewHolder viewHolder, JsonMap data, int index) {
                                            viewHolder.txtList.setText(data.getString("title"));
                                        }
                                    });
                                    
                                    listView.setAdapter(baseAdapter);
                                    
                                } else {
                                    resultHttp.setText("请求失败");
                                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                HttpRequest.build(context,"/femaleNameApi")
//                        .addHeaders("Charset", "UTF-8")
//                        .addParameter("page", "1")
//                        .setResponseListener(new ResponseListener() {
//                            @Override
//                            public void onResponse(String response, Exception error) {
//                                progressDialog.dismiss();
//                                if (error == null) {
//                                    resultHttp.setText(response);
//                                } else {
//                                    resultHttp.setText("请求失败");
//                                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .doPost();


//                progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
//                HttpRequest.JSONPOST(context, "/femaleNameApi", "{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}", new ResponseListener() {
//                    @Override
//                    public void onResponse(String response, Exception error) {
//                        progressDialog.dismiss();
//                        if (error == null) {
//                            resultHttp.setText(response);
//                        } else {
//                            resultHttp.setText("请求失败");
//                            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

//                HttpRequest.build(context,"/femaleNameApi")
//                        .setJsonParameter("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}")
//                        .setResponseListener(new ResponseListener() {
//                            @Override
//                            public void onResponse(String response, Exception error) {
//                                progressDialog.dismiss();
//                                if (error == null) {
//                                    resultHttp.setText(response);
//                                } else {
//                                    resultHttp.setText("请求失败");
//                                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .doPost();

//                File file1 = new File("");
//                File file2 = new File("");

//                progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
//                HttpRequest.POST(context, "/femaleNameApi", new Parameter()
//                                         .add("key", "DFG1H56EH5JN3DFA")
//                                         .add("imageFile1", file1)
//                                         .add("imageFile2", file2)
//                        , new ResponseListener() {
//                            @Override
//                            public void onResponse(String response, Exception error) {
//                                progressDialog.dismiss();
//                                if (error == null) {
//                                    resultHttp.setText(response);
//                                } else {
//                                    resultHttp.setText("请求失败");
//                                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

//                HttpRequest.build(context,"/femaleNameApi")
//                        .addHeaders("Charset", "UTF-8")
//                        .addParameter("page", "1")
//                        .addParameter("imageFile1", file1)
//                        .addParameter("imageFile2", file2)
//                        .setResponseListener(new ResponseListener() {
//                            @Override
//                            public void onResponse(String response, Exception error) {
//                                progressDialog.dismiss();
//                                if (error == null) {
//                                    resultHttp.setText(response);
//                                } else {
//                                    resultHttp.setText("请求失败");
//                                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .doPost();
            
            }
        });
        
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultWebsocket.setText("开始连接...");
                btnConnect.setEnabled(false);
                baseWebSocket = BaseWebSocket.BUILD(MainActivity.this, "wss://echo.websocket.org")
                        .setWebSocketStatusListener(new WebSocketStatusListener() {
                            @Override
                            public void connected(Response response) {
                                resultWebsocket.setText("已连接");
                                btnDisconnect.setEnabled(true);
                                btnConnect.setEnabled(false);
                                editSend.setEnabled(true);
                                btnSend.setEnabled(true);
                            }
                            
                            @Override
                            public void onMessage(String message) {
                                resultWebsocket.setText("收到返回消息：" + message);
                            }
                            
                            @Override
                            public void onMessage(ByteString message) {
                            
                            }
                            
                            @Override
                            public void onReconnect() {
                                resultWebsocket.setText("正在重连");
                                btnDisconnect.setEnabled(true);
                                btnConnect.setEnabled(false);
                                editSend.setEnabled(false);
                                btnSend.setEnabled(false);
                            }
                            
                            @Override
                            public void onDisconnected(int breakStatus) {
                                resultWebsocket.setText("已断开连接");
                                btnDisconnect.setEnabled(false);
                                btnConnect.setEnabled(true);
                                editSend.setEnabled(false);
                                btnSend.setEnabled(false);
                            }
                            
                            @Override
                            public void onConnectionFailed(Throwable t) {
                                resultWebsocket.setText("连接失败");
                                btnDisconnect.setEnabled(false);
                                btnConnect.setEnabled(true);
                                editSend.setEnabled(false);
                                btnSend.setEnabled(false);
                                t.printStackTrace();
                            }
                        })
                        .startConnect();
            }
        });
        
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseWebSocket != null) baseWebSocket.disConnect();
            }
        });
        
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editSend.getText().toString().trim();
                if (!s.isEmpty()) {
                    if (baseWebSocket != null) baseWebSocket.send(s);
                }
            }
        });
        
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDownload.setEnabled(false);
                btnDownloadCancel.setEnabled(true);
                httpRequest = HttpRequest.build(MainActivity.this, "http://cdn.to-future.net/apk/tofuture.apk");
                httpRequest.doDownload(
                        new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "to-future.apk"),
                        new OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                Toast.makeText(MainActivity.this, "文件已下载完成：" + file.getAbsolutePath(), Toast.LENGTH_LONG);
                            }
                            
                            @Override
                            public void onDownloading(int progress) {
                                psgDownload.setProgress(progress);
                            }
                            
                            @Override
                            public void onDownloadFailed(Exception e) {
                                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT);
                            }
                        }
                );

//                HttpRequest.DOWNLOAD(
//                        MainActivity.this,
//                        "http://cdn.to-future.net/apk/tofuture.apk",
//                        new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "BaseOkHttpV3"), "to-future.apk"),
//                        new OnDownloadListener() {
//                            @Override
//                            public void onDownloadSuccess(File file) {
//                                Toast.makeText(MainActivity.this, "文件已下载完成：" + file.getAbsolutePath(), Toast.LENGTH_LONG);
//                            }
//
//                            @Override
//                            public void onDownloading(int progress) {
//                                psgDownload.setProgress(progress);
//                            }
//
//                            @Override
//                            public void onDownloadFailed(Exception e) {
//                                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT);
//                            }
//                        }
//                );
            }
        });
        
        btnDownloadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDownload.setEnabled(true);
                btnDownloadCancel.setEnabled(false);
                httpRequest.stop();
            }
        });
    }
    
    private HttpRequest httpRequest;
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (baseWebSocket != null) baseWebSocket.disConnect();
    }
    
    class ViewHolder {
        TextView txtList;
    }
}
