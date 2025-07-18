# BaseOkHttp V3

<a href="https://github.com/kongzue/BaseOkHttp/">
<img src="https://img.shields.io/badge/BaseOkHttp-3.2.1-green.svg" alt="BaseOkHttp">
</a>
<a href="https://bintray.com/myzchh/maven/BaseOkHttp_v3/3.1.9/link">
<img src="https://img.shields.io/badge/Maven-3.2.1-blue.svg" alt="Maven">
</a>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

## 简介

- 无需操心线程问题，请求在异步线程执行，回调会自动回到主线程；
- 提供统一返回监听器 ResponseListener 处理一切返回数据，无论是错误还是成功，都可以一起处理，对于相同操作代码不再需要重复，避免代码反复臃肿。
- 提供基于 Map 对象和 List 对象的 Json 解析库及数据类型，直接与适配器配合，抛弃编写 JavaBean 的麻烦；
- 强大的全局方法和事件让您的请求得心应手。
- BaseOkHttp V3是基于BaseOkHttp V2( <https://github.com/kongzue/BaseOkHttp> )的升级版本，基于能够快速创建常用请求链接而封装的库。

> [!TIP]
> BaseOkHttpX 现已发布，不妨试试看全新重构更好用的 BaseOkHttpX？https://github.com/kongzue/BaseOkHttpX

## Maven仓库或Gradle的引用方式

### jitPack 方式

请先在 'build.gradle(project)' 中添加如下代码：

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在 'build.gradle(app)' 中引入：

```
dependencies {
    //BaseOkHttp V3 网络请求库
    implementation 'com.github.kongzue:BaseOkHttpV3:3.2.4.2'
    //BaseJson 解析库
    implementation 'com.github.kongzue:BaseJson:1.0.7.2'
}
```

额外的，如果您的项目未引入 okHttp，还需要添加 okHttp 最新版本依赖：

```
dependencies {
    implementation "com.squareup.okhttp3:okhttp:4.9.1" 
}
```

### jCenter 方式

因 jCenter 停止维护，不再推荐使用。

Maven仓库：

```
<dependency>
  <groupId>com.kongzue.baseokhttp_v3</groupId>
  <artifactId>baseokhttp_v3</artifactId>
  <version>3.2.2</version>
  <type>pom</type>
</dependency>
```

Gradle：

在dependencies{}中添加引用：

```
//BaseOkHttp V3 网络请求库
implementation 'com.kongzue.baseokhttp_v3:baseokhttp_v3:3.2.2'
//BaseJson 解析库
implementation 'com.kongzue.basejson:basejson:1.0.7'
```

新版本系统（API>=27）中，使用非 HTTPS 请求地址可能出现 java.net.UnknownServiceException 错误，解决方案请参考：<https://www.jianshu.com/p/528a3def1cf4>

![BaseOkHttpV3 Demo](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/baseokhttpv3demo.png)

试用版可以前往 <http://beta.kongzue.com/BaseOkHttp3> 下载

## 目录

· <a href="#一般请求">一般请求</a>

· <a href="#json请求">JSON请求</a>

· <a href="#文件上传">文件上传</a>

· <a href="#文件下载">文件下载</a>

· <a href="#putdelete">PUT&DELETE</a>

· <a href="#websocket">WebSocket</a>

· <a href="#json解析">JSON解析</a>

· <a href="#javabean解析">JavaBean解析</a>

· <a href="#额外功能">额外功能</a>

···· <a href="#全局日志">全局日志</a>

···· <a href="#全局请求地址">全局请求地址</a>

···· <a href="#全局-Header-请求头">全局 Header 请求头</a>

···· <a href="#全局请求返回拦截器">全局请求返回拦截器</a>

···· <a href="#HTTPS-支持">HTTPS 支持</a>

···· <a href="#全局参数拦截器">全局参数拦截器</a>

···· <a href="#请求超时">请求超时</a>

···· <a href="#停止请求">停止请求</a>

···· <a href="#容灾地址">容灾地址</a>

···· <a href="#Cookie">Cookie</a>

· <a href="#开源协议">开源协议</a>

· <a href="#更新日志">更新日志</a>

## 一般请求

BaseOkHttp V3 提供两种请求写法，范例如下：

#### 以参数形式创建请求：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
HttpRequest.POST(context, "http://你的接口地址", new Parameter().add("page", "1"), new ResponseListener() {
    @Override
    public void onResponse(String response, Exception error) {
        progressDialog.dismiss();
        if (error == null) {
            resultHttp.setText(response);
        } else {
            resultHttp.setText("请求失败");
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

Parameter 对象是一个 Map 的封装对象，可以通过 `.toParameterString()` 方法获得按 key 首字母排序、以“&”符号连接的文本串，或者使用 `.toParameterJson()` 方法将其转为 JSONObject。

一般请求中，使用 HttpRequest.POST(...) 方法可直接创建 POST 请求，相应的，`HttpRequest.GET(...)` 可创建 GET 请求，另外可选额外的方法增加 header 请求头：

```
HttpRequest.POST(Context context, String url, Parameter headers, Parameter parameter, ResponseListener listener);
HttpRequest.GET(Context context, String url, Parameter headers, Parameter parameter, ResponseListener listener);
```

#### 以流式代码创建请求：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
HttpRequest.build(context,"http://你的接口地址")
        .addHeaders("Charset", "UTF-8")
        .addParameter("page", "1")
        .addParameter("token", "A128")
        .setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(response);
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .doPost();
```

返回回调监听器只有一个，请在其中对 error 参数判空，若 error 不为空，则为请求失败，反之则为请求成功，请求成功后的数据存放在 response 参数中。

之所以将请求成功与失败放在一个回调中主要目的是方便无论请求成功或失败都需要执行的代码，例如上述代码中的 progressDialog 等待对话框都需要关闭（dismiss掉），这样的写法更为方便。

3.1.0 版本起提供直接解析返回值为 jsonMap 对象，可使用 `JsonResponseListener` 监听器返回：

```
HttpRequest.POST(context, "/femaleNameApi", new Parameter().add("page", "1"), new JsonResponseListener() {
    @Override
    public void onResponse(JsonMap main, Exception error) {
        if (error == null) {
            resultHttp.setText(main.getString("msg"));
        } else {
            resultHttp.setText("请求失败");
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

#### 关于返回线程的说明

BaseOkHttpV3 会智能的判断需要返回线程的场景，一般而言，您无需为此而操心，您可以直接在请求回调中进行 UI 的操作。

BaseOkHttpV3 的请求过程是异步进行的（多线程），而在返回数据后，是根据 context 进行判断的是要在异步线程执行还是在 UI 线程执行，当 context 传入的是 Activity 时，默认会在 UI 线程返回，无需额外处理线程问题，而当 context 为非 Activity，例如 ApplicationContext 或者 Service 时，则会在异步线程返回数据，若需要修改界面上的元素显示，您需要手动切换到主线程刷新 UI 组件。

这是在我们经过大量的使用场景调研后得出的最优设计方案，但如若您的请求返回数据流非常大，可能造成 UI 线程卡顿，建议在传入 context 时，使用 `context.getApplicationContext()` 方法来强制异步线程返回，待数据得到妥善处理后，需要返回 UI 线程刷新界面显示时，使用 `activity.runOnUiThread(...)`切换到主线程进行刷新。

特殊情况下需要同步请求可以使用全局参数`BaseOkHttp.async=(boolean)` 控制全局是否同步请求，也可以在单次请求局部设置`setAsync(boolean)`；

## JSON请求

有时候我们需要使用已经处理好的json文本作为请求参数，此时可以使用 `HttpRequest.JSONPOST(...)` 方法创建 json 请求。

json 请求中，参数为文本类型，创建请求方式如下：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
HttpRequest.JSONPOST(context, "http://你的接口地址", "{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}", new ResponseListener() {
    @Override
    public void onResponse(String response, Exception error) {
        progressDialog.dismiss();
        if (error == null) {
            resultHttp.setText(response);
        } else {
            resultHttp.setText("请求失败");
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

也可以使用 JsonMap 构建请求参数：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");

JsonMap jsonMap = new JsonMap();
jsonMap.set("key", "DFG1H56EH5JN3DFA");
jsonMap.set("token", "124ASFD53SDF65aSF47fgT211");

HttpRequest.JSONPOST(context, "http://你的接口地址", jsonMap, new ResponseListener() {
    @Override
    public void onResponse(String response, Exception error) {
        progressDialog.dismiss();
        if (error == null) {
            resultHttp.setText(response);
        } else {
            resultHttp.setText("请求失败");
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

另外参数也可以是 JSONObject 类型（来自 org.json 框架）。

Json请求中，可使用 `HttpRequest.JSONPOST(...)` 快速创建 Json 请求，另外可选额外的方法增加 header 请求头：

```
HttpRequest.JSONPOST(Context context, String url, Parameter headers, String jsonParameter, ResponseListener listener)
```

也可以使用流式代码创建请求：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
HttpRequest.build(context,"http://你的接口地址")
        .setJsonParameter("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}")
        .setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(response);
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .doPost();
```

因需要封装请求体，Json请求只能以非 GET 请求的方式进行。

默认情况下，Json 请求的 Mime 类型是自动设置的，要自定义，可以通过以下方法进行：

```java
.setCustomMimeInterceptor(new CustomMimeInterceptor() {
    @Override
    public String onRequestMimeInterceptor(RequestInfo requestInfo, Call call) {
        if (requestInfo.getUrl().endsWith("/mimeUrlTest")){
            return "application/json; charset=utf-8";	//根据网址返回自定义 MimeType
        }
        return super.onRequestMimeInterceptor(requestInfo, call);
    }
})
```

## 文件上传

要使用文件上传就需要将 File 类型的文件作为参数传入 Parameter，此时参数中亦可以传入其他文本类型的参数。

因需要封装请求体，文件上传只能以非 GET 请求的形式发送。

范例代码如下：

```
progressDialog = ProgressDialog.show(context, "请稍候", "请求中...");
HttpRequest.POST(context, "http://你的接口地址", new Parameter()
                         .add("key", "DFG1H56EH5JN3DFA")
                         .add("imageFile1", file1)
                         .add("imageFile2", file2)
        , new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(response);
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

也可以使用流式代码创建请求：

```
HttpRequest.build(context,"http://你的接口地址")
        .addHeaders("Charset", "UTF-8")
        .addParameter("page", "1")
        .addParameter("imageFile1", file1)
        .addParameter("imageFile2", file2)
        .setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(response);
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .doPost();
```

上传文件的 MIME 类型将自动根据上传文件的后缀名自动生成。

另外，对于单参数（key）多文件（value）的情况，可使用以下方法进行上传：

```
List<File> fileList = new ArrayList<File>();
fileList.add(file1);
fileList.add(file2);
HttpRequest.build(context,"http://你的接口地址")
        .addHeaders("Charset", "UTF-8")
        .addParameter("page", "1")
        .addParameter("file", fileList)
        .setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(response);
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .doPost();
```

要监控上传进度，可以使用方法 setUploadProgressListener(...) 进行：

```
HttpRequest.build(context,"http://你的接口地址")
        .addHeaders("Charset", "UTF-8")
        .addParameter("page", "1")
        .addParameter("file", fileList)
        .setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onUpload(float percentage, long current, long total, boolean done) {
                Log.e(">>>", "上传进度: 百分比（float）="+percentage +" "+
                        "已上传字节数="+current +" "+
                        "总字节数="+total +" "+
                        "是否已完成="+done +" "
                );
            }
        })
        .setResponseListener(...)
        .doPost();
```

默认情况下，MimeType会自动根据文件后缀设置，要自定义设置MimeType，可以使用以下方法：

```java
httpRequest.setCustomMimeInterceptor(new CustomMimeInterceptor() {
    @Override
    public String onUploadFileMimeInterceptor(File originFile) {
        if (originFile.getName().endsWith(".1pf")){
            return "file/*";	//根据文件名后缀返回自定义 MimeType
        }
        return super.onUploadFileMimeInterceptor(originFile);
    }
});
```

## 文件下载

首先请确保您的 APP 已经在 AndroidManifest.xml 声明读写权限：

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

并确保您以获得该权限许可。

您可以使用以下代码启动下载进程：

```
HttpRequest.DOWNLOAD(
        MainActivity.this,
        "http://cdn.to-future.net/apk/tofuture.apk",
        new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "BaseOkHttpV3"), "to-future.apk"),
        new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Toast.makeText(context, "文件已下载完成：" + file.getAbsolutePath(), Toast.LENGTH_LONG);
            }
            @Override
            public void onDownloading(int progress) {
                psgDownload.setProgress(progress);
            }
            @Override
            public void onDownloadFailed(Exception e) {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT);
            }
        }
);
```

也可以使用build创建：

```
httpRequest = HttpRequest.build(MainActivity.this, "http://cdn.to-future.net/apk/tofuture.apk");
httpRequest.doDownload(
        new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "BaseOkHttpV3"), "to-future.apk"),
        new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Toast.makeText(context, "文件已下载完成：" + file.getAbsolutePath(), Toast.LENGTH_LONG);
            }
            
            @Override
            public void onDownloading(int progress) {
                psgDownload.setProgress(progress);
            }
            
            @Override
            public void onDownloadFailed(Exception e) {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT);
            }
        }
);

//停止下载：
httpRequest.stop();
```

额外的，若存储文件的父文件夹不存在，会自动创建。

## PUT&DELETE

从 3.0.3 版本起新增了 PUT 和 DELETE 请求方式，使用方法和一般请求一致，可以通过以下两种方法创建：

```
//PUT 请求：
HttpRequest.PUT(context, "http://你的接口地址", new Parameter().add("page", "1"), new ResponseListener() {...});
//DELETE 请求：
HttpRequest.DELETE(context, "http://你的接口地址", new Parameter().add("page", "1"), new ResponseListener() {...});
```

也可适用流式代码创建：

```
HttpRequest.build(context,"http://你的接口地址")
        .addHeaders("Charset", "UTF-8")
        .addParameter("page", "1")
        .addParameter("token", "A128")
        .setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(String response, Exception error) {
                ...
            }
        })
        .doPut();
        //.doDelete();
```

## WebSocket

从 3.0.6 版本起新增了 WebSocket 封装工具类 BaseWebSocket，用于快速实现 WebSocket 请求连接。

请先前往 AndroidManifest.xml 中添加检查网络连接状态权限：

```
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

使用方法：

```
//通过 BUILD 方法获取 baseWebSocket 实例化对象，参数 context 为上下文索引，url 为 websocket服务器地址：
baseWebSocket = BaseWebSocket.BUILD(context, url)
        //实现监听方法
        .setWebSocketStatusListener(new WebSocketStatusListener() {
            @Override
            public void connected(Response response) {
                //连接上时触发
            }
            @Override
            public void onMessage(String message) {
                //处理收到的消息 message
            }
            @Override
            public void onMessage(ByteString message) {
            }
            @Override
            public void onReconnect() {
                //重新连接时触发
            }
            @Override
            public void onDisconnected(int breakStatus) {
                //断开连接时触发，breakStatus 值为 BREAK_NORMAL 时为正常断开，值为 BREAK_ABNORMAL  时为异常断开
            }
            @Override
            public void onConnectionFailed(Throwable t) {
                //连接错误处理
                t.printStackTrace();
            }
        })
        .startConnect();        //开始连接

//发送消息
baseWebSocket.send("Test!");

//断开连接
baseWebSocket.disConnect();

//重新连接
baseWebSocket.reConnect();
```

## JSON解析

从 3.1.7 版本起,Json 解析库独立为一个单独的仓库，详情请参阅 https://github.com/kongzue/BaseJson

从 3.0.7 版本起，新增 Json 解析功能，此功能基于 `org.json` 库二次实现，基本实现了无惧空指针异常的特性。

因原始 `org.json` 库提供的 JsonObject 和 JsonArray 框架使用起来相对麻烦，我们对其进行了二次封装和完善，且因 BaseOkHttpV3提供的 Json 解析框架底层使用的是 Map 和 List，与适配器具有更好的兼容性。

使用 BaseOkHttpV3提供的 Json 解析框架无需判断 Json 转换异常，可以直接将 Json 文本字符串传入解析。

另外，JsonMap 和 JsonList 的 `toString()` 方法可输出该对象原始 json 文本；

从 3.1.0 版本起提供直接解析返回值为 jsonMap 对象，详见 <a href="#一般请求">一般请求</a>

JsonMap 和 JsonList 用有构建方法 `parse(String json)` 用以直接根据 json 文本创建 JsonMap 和 JsonList 对象，可以通过该方法创建 JsonMap 或 JsonList 对象：

```
//通过 json 文本创建 JsonMap
JsonMap data = JsonMap.parse("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}");

//通过 json 文本创建 JsonList
JsonList list = JsonList.parse("[{\"answerId\":\"98\",\"questionDesc\":\"否\"},{\"answerId\":\"99\",\"questionDesc\":\"是\"}]");
```

### 请求后自动返回 JsonMap

使用 `JsonResponseListener` 作为返回监听器，即可在返回时直接处理 JsonMap 对象：

```
HttpRequest.POST(context, "/femaleNameApi", new Parameter().add("page", "1"), new JsonResponseListener() {
    @Override
    public void onResponse(JsonMap main, Exception error) {
        progressDialog.dismiss();
        if (error == null) {
            resultHttp.setText(main.toString());
        } else {
            resultHttp.setText("请求失败");
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
});
```

也可以这样写：

```
HttpRequest.build(context, "/femaleNameApi")
        .addParameter("page", "1")
        .setJsonResponseListener(new JsonResponseListener() {
            @Override
            public void onResponse(JsonMap main, Exception error) {
                progressDialog.dismiss();
                if (error == null) {
                    resultHttp.setText(main.toString());
                } else {
                    resultHttp.setText("请求失败");
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

请注意，为杜绝空指针异常，即使请求失败，error!=null 时，返回的 main 依然不会是空指针，而是一个空的 JsonMap 对象，可通过 .isEmpty() 方法判空。

具体请参考我们是如何杜绝空指针引发 APP 崩溃的：[《当 Json 存在问题》](https://github.com/kongzue/BaseJson#%E5%BD%93-json-%E5%AD%98%E5%9C%A8%E9%97%AE%E9%A2%98)

### 对于未知 Json 文本

```
Object obj = JsonUtil.deCodeJson(JsonStr);
```

deCodeJson 方法提供的是未知目标字符串是 JsonArray 还是 JsonObject 的情况下使用，返回对象为 Object，此时判断：

```
if(obj != null){
    if (obj instanceof JsonMap){
        //此对象为JsonObject，使用get(...)相关方法获取其中的值
    }
    if (obj instanceof JsonList){
        //此对象为JsonArray，使用get(index)相关方法获取其中的子项
    }
}
```

请注意对 obj 进行判空处理，若解析失败，则会返回 null。

### 对于已知 JsonObject 文本

```
JsonMap map = JsonUtil.deCodeJsonObject(JsonStr);        //直接解析为Map对象，JsonMap继承自LinkedHashMap，按照入栈顺序存储键值对集合
```

请注意对 map 进行判空处理，若解析失败，则会返回 null。

### 对于已知 JsonArray 文本

```
JsonList list = JsonUtil.deCodeJsonArray(JsonStr);        //直接解析为List对象，JsonList继承自ArrayList
```

请注意对 list 进行判空处理，若解析失败，则会返回 null。

### 额外说明

为方便解析使用，JsonMap 和 JsonList 都提供对应的如下方法来获取内部元素的值：

```
getString(...)
getInt(...)
getBoolean(...)
getLong(...)
getShort(...)
getDouble(...)
getFloat(...)
getList(...)
getJsonMap(...)
```

请注意，您亦可使用 Map、List 自带的 `get(...)` 方法获取元素的值，但 JsonMap 和 JsonList 提供的额外方法对于空指针元素，会返回一个默认值，例如对于实际是 null 的 String，会返回空字符串“”，对于实际是 null 的元素，获取其int值则为0。

若您需要空值判断，可以通过例如 `getInt(String key, int emptyValue)` 来进行，若为空值会返回您提供的 emptyValue。

这确实不够严谨，但更多的是为了提升开发效率，适应快速开发的生产要求。

## JavaBean解析

从 3.1.8 版本起，新增回调直接解析 json 为 JavaBean 的功能：

在设置回调时，请使用 BeanResponseListener，并在其泛型中传入要解析的 JavaBean，在回调中即可获取已经实例化并设置好数据的 JavaBean 对象：

```
HttpRequest.POST(MainActivity.this, "/getWangYiNews", new Parameter()
                .add("page", "1")
                .add("count", 5)
           , new BeanResponseListener<DataBean>() {
               @Override
               public void onResponse(DataBean main, Exception error) {
                   if (error == null) {
                       Log.e(">>>", "onResponse: " + main);
                   } else {
                       error.printStackTrace();
                   }
               }
           }
);
```

JavaBean 对象中必须包含每个属性的对应 get、set 方法，且必须使用驼峰命名规则。

支持 boolean 类型的数据获取方法以“is”开头的命名方式，例如“boolean isVIP()”或者“boolean getVIP()”都可以。

此外，JsonMap 与 JavaBean 之间的互相转换请查阅 [《BaseJson - JsonMap 与 JavaBean 的互相转换》](https://github.com/kongzue/BaseJson#jsonmap-%E4%B8%8E-javabean-%E7%9A%84%E4%BA%92%E7%9B%B8%E8%BD%AC%E6%8D%A2)

## 额外功能

### 全局日志

全局日志开关（默认是关闭态，需要手动开启）：

```
BaseOkHttp.DEBUGMODE = true;
```

BaseOkHttp V3支持增强型日志，使用输出日志内容是 json 字符串时，会自动格式化输出，方便查看。

![BaseOkHttp Logs](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/img_okhttp_logs.png)

在您使用 BaseOkHttp 时可以在 Logcat 的筛选中使用字符 “>>>” 对日志进行筛选（Logcat日志界面上方右侧的搜索输入框）。

您可以在 Android Studio 的 File -> Settings 的 Editor -> Color Scheme -> Android Logcat 中调整各类型的 log 颜色，我们推荐如下图方式设置颜色：

![Kongzue's log settings](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/baseframework_logsettings.png)

### 全局请求地址

设置全局请求地址后，所有接口都可以直接使用相对地址进行，例如设置全局请求地址：

```
BaseOkHttp.serviceUrl = "https://www.example.com";
```

发出一个请求：

```
HttpRequest.POST(context, "/femaleNameApi", new Parameter().add("page", "1"), new ResponseListener() {...});
```

那么实际请求地址即 <https://www.example.com/femaleNameApi> ，使用更加轻松方便。

注意，设置全局请求地址后，若 HttpRequest 的请求参数地址为“http”开头，则不会拼接全局请求地址。

### 全局 Header 请求头

使用如下代码预设置全局 Header 请求头：

```
BaseOkHttp.overallHeader = new Parameter()
        .add("Charset", "UTF-8")
        .add("Content-Type", "application/json")
        .add("Accept-Encoding", "gzip,deflate")
;
```

需要对请求 Header 进行实时处理，则可实现处理接口，例如下边的代码，对 Header 请求头进行了签名操作：

```
BaseOkHttp.headerInterceptListener = new HeaderInterceptListener() {
    @Override
    public Parameter onIntercept(Context context, String url, Parameter header) {
        String signStr = sign(header.toParameterString());
        header.add("sign", signStr);
        return header;
    }
};
```

### 全局请求返回拦截器

使用如下代码可以设置全局返回数据监听拦截器，return true 可返回请求继续处理，return false 即拦截掉不会继续返回原请求进行处理；

```
BaseOkHttp.responseInterceptListener = new ResponseInterceptListener() {
    @Override
    public boolean onResponse(Context context, String url, String response, Exception error) {
        if (error != null) {
            return true;
        } else {
            Log.i("!!!", "onResponse: " + response);
            return true;
        }
    }
};
```

请求返回拦截器另外还有 JsonResponseInterceptListener 实现和 BeanResponseInterceptListener 实现，以便于直接对特定返回数据形式进行处理，使用方法与 ResponseInterceptListener 一致。

### HTTPS 支持

1) 请将SSL证书文件放在assets目录中，例如“ssl.crt”；
2) 以附带SSL证书名的方式创建请求：

```
BaseOkHttp.SSLInAssetsFileName = "ssl.crt";
...
```

即可使用Https请求方式。

另外，可使用 `BaseOkHttp.httpsVerifyServiceUrl=(boolean)` 设置是否校验请求主机地址与设置的 HttpRequest.serviceUrl 一致；

### 全局参数拦截器

使用如下代码可以设置全局参数监听拦截器，此参数拦截器可以拦截并修改、新增所有请求携带的参数。

对于一个项目，拦截器 parameterInterceptListener 是 static 的，即唯一的，目前不支持单项目内多种类型（例如表单参数、Json参数、String参数）请求参数同时存在的拦截需求。

#### 对于一般表单形式参数的请求

此方法亦适用于需要对参数进行加密的场景：

```
BaseOkHttp.parameterInterceptListener = new ParameterInterceptListener<Parameter>() {
    @Override
    public Parameter onIntercept(Context context, String url, Parameter parameter) {
        parameter.add("key", "DFG1H56EH5JN3DFA");
        parameter.add("sign", makeSign(parameter.toParameterString()));
        return parameter;           //请注意将处理后的参数回传
    }
};

private String makeSign(String parameterString){
    //加密逻辑
    ...
}
```

onIntercept 返回值中，context 为当前请求的上下文索引，url 为当前请求地址，parameter 为请求参数，在处理完后，请注意需要 return 处理后的 parameter。

#### 对于Json形式的参数请求

使用泛型约束为 JsonMap 参数：

```
BaseOkHttp.parameterInterceptListener = new ParameterInterceptListener<JsonMap>() {
    @Override
    public Parameter onIntercept(Context context, String url, JsonMap parameter) {
        parameter.set("key", "DFG1H56EH5JN3DFA");
        return parameter;           //请注意将处理后的参数回传
    }
};
```

若为 JsonArray，则可约束为 JsonList 类型：

```
BaseOkHttp.parameterInterceptListener = new ParameterInterceptListener<JsonList>() {
    @Override
    public Parameter onIntercept(Context context, String url, JsonList parameter) {
        parameter.set("img1");
        parameter.set("img2");
        return parameter;           //请注意将处理后的参数回传
    }
};
```

#### 对于其他类型参数，包含String、XML等

统一约束泛型为 String 处理：

```
BaseOkHttp.parameterInterceptListener = new ParameterInterceptListener<String>() {
    @Override
    public Parameter onIntercept(Context context, String url, String parameter) {
        //对 parameter 进行处理
        return parameter;           //请注意将处理后的参数回传
    }
};
```

### 请求超时

使用以下代码设置请求超时时间（单位：秒）

```
BaseOkHttp.TIME_OUT_DURATION = 10;
```

一旦发生请求超时，将会停止本次请求，并在请求返回的 error 参数中回传 TimeOutException()。

### 停止请求

可使用以下方法停止请求过程：

```
httpRequest。stop();     //停止请求
```

### 容灾地址

使用 BaseOkHttp.reserveServiceUrls 设置容灾地址。

该属性类型为 StringArray，设置案例如下：

```
BaseOkHttp.reserveServiceUrls = new String[]{
        "https://www.testB.com",
        "https://www.testC.com",
        "https://api.testD.com"
};
```

当主服务器地址 BaseOkHttp.serviceUrl 请求不通后，会依次尝试 BaseOkHttp.reserveServiceUrls 配置的地址，若全部失败，则会执行回调函数，若其中一个能够请求成功，则主服务器地址 BaseOkHttp.serviceUrl 会被替换为该地址，完成接下来的请求。

### Cookie

设置 Cookie 请求头：

```
httpRequest.setCookie(String);
```

开启自动存储和携带服务端返回的 Cookie：

```
BaseOkHttp.autoSaveCookies = true;
```

获取已保存 Cookie 队列：

```
httpRequest.getCookies();
```

### 重复请求拦截

开启拦截重复请求：

```
BaseOkHttp.disallowSameRequest = true;
```

开启后，同一请求（url、参数都相同）一次只能发出一个，在该请求未完成（成功、失败、超时）前无法发出相同的请求。

若有需要同时发出相同地址和参数的请求，可以选择增加一个时间戳来区分请求。

若有清除重复请求判断队列的需要，可以使用以下代码清除：

```
BaseOkHttp.cleanSameRequestList();
```

### 自定义 OkHttpClient 或 OkHttpClient.Builder

BaseOkHttpV3 的底层是 OkHttp，我们提供了自动化的默认 OkHttpClient 实现，但您也许会有需要客制化这些组件以实现更多功能。

要定制 OkHttpClient 或 OkHttpClient.Builder，请先使用 `HttpRequest.build(context)`构建请求，然后使用 `setCustomOkHttpClient(CustomOkHttpClient)`和`setCustomOkHttpClientBuilder(CustomOkHttpClientBuilder)`来设置定制接口，在请求发出前对 OkHttpClient.Builder 和 OkHttpClient 进行修改，需要将修改后的组件 return 到接口返回参数中才可生效。

要全局拦截 OkHttpClient 或 OkHttpClient.Builder，您可以设置`BaseOkHttp.globalCustomOkHttpClient`和`BaseOkHttp.globalCustomOkHttpClientBuilder`进行拦截修改，需要将修改后的组件 return 到接口返回参数中才可生效。

## 开源协议

```
Copyright Kongzue BaseOkHttp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

本项目中使用的网络请求底层框架为square.okHttp3(<https://github.com/square/okhttp> )，感谢其为开源做出的贡献。

相关协议如下：

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

另外感谢

## 更新日志

v3.2.2:

- 升级 okHttp 底层到最新版本 4.9.0；

v3.2.1:

- 新增 CustomOkHttpClient 和 CustomOkHttpClientBuilder 支持自定义 OkHttpClient 和 Builder;
- BaseOkHttp 新增全局的 GlobalCustomOkHttpClientBuilder 和 GlobalCustomOkHttpClient 接口;
- BaseOkHttp 新增 requestCache 选项，默认开启，关闭后将不启用缓存实现；
- HttpRequest 新增 `getParameter()`、`getUrl()`、`getJsonParameter()`和`getStringParameter()`等方法；

v3.2.0:

- 新增禁止同时重复请求功能，同一地址、同一参数在同时只能发起一个请求，相同的请求会被拦截处理；
- 修复参数加入其他类型数据时可能被强制转换 String 的问题；

v3.1.9:

- 整合回调拦截器，并提供新的回调拦截器 BeanResponseInterceptListener 和 JsonResponseInterceptListener；
- 回调接口 ResponseListener、JsonResponseListener 和 BeanResponseListener 不再返回空指针数据，即当 error 非空时，直接get主数据依然不会是空指针，但 BeanResponseListener 构造 Bean 失败的情况除外；
- 提供新的 BaseOkHttp.headerInterceptListener 用于实时对请求头进行拦截和处理操作。
- HttpRequest 新增 setUploadProgressListener(...) 可实时监听上传进度回调，具体参数含义请参考接口注释。
- 上传所使用的方法 setMediaType(...) 被废弃，现在会根据文件后缀自动判断并设置 MIME 类型；
- HttpRequest 新增 JsonMap 直接作为参数类型的请求方法；
- 修复请求回调拦截器对 TimeOutException 请求超时错误无法拦截的 bug； 

v3.1.8:

- 新增 BeanResponseListener 回调，可直接解析 json 返回数据为 JavaBean；
- 日志输出统一化，打印更加清晰明了；
- 新增外部接口 setProxy(...) 可设置代理，及全局代理设置 BaseOkHttp.proxy；
- 修复了一些 bug；

v3.1.7:

- 新增 BaseOkHttp.DETAILSLOGS 设置用以判断是否显示详细的下载部分日志；
- Json 解析库独立（ https://github.com/kongzue/BaseJson ），当前版本使用 BaseJson 库 1.0.3 版本；
- 修复可能由 context 引发的空指针问题；

v3.1.6:

- BaseOkHttp 新增容灾地址设置 reserveServiceUrls，具体请参考文档 <a href="#容灾地址">容灾地址</a>；
- 修复了 GET 请求存在的可能出现 url 中已经存在“?”的情况下加入参数，最终请求 url 出现多个“?” 的bug；

v3.1.5:

- 修复返回数据 ResponseListener 和 JsonResponseListener 可能存在崩溃的问题；
- 全局参数拦截器 ParameterInterceptListener 现在支持多种参数的拦截处理，详见 <a href="#全局参数拦截器">全局参数拦截器</a>；

v3.1.4:

- JsonMap 和 JsonList 新增构建方法 parse(String json) 用以直接根据 json 文本创建 JsonMap 和 JsonList 对象；
- 新增临时方法 set/getTimeoutDuration(int second) 可独立设置当前接口超时时长；
- 参数拦截器 ParameterInterceptListener 新增参数 context 和 url；
- 修改请求创建逻辑顺序，现在创建请求时，全局 serviceUrl 会优先和当前请求的子接口 url 合并后判断请求地址是否为空，以解决无法向服务器地址请求的问题；
- 修改请求创建逻辑顺序，现在创建请求时，全局参数会在拦截器前优先添加至参数列表，以便于可以在拦截器中处理全局参数；
- 新增 setJsonParameter(JsonList) 方法；
- 修复了请求超时的日志未遵循 DEBUGMODE 的 bug；
- 修复了 JsonUtil 当字符串存在中括号文本时存在的解析异常问题；

v3.1.3:

- 修改 Context 为 WeakReference 弱引用，以便解决内存泄漏问题，并提供了 onDetach() 方法用于清空 Context 引用。
- 修改 BaseResponseListener 结构，以便于后续更多回调扩展；

v3.1.2:

- 修复了 url 传入 null 可能造成异常的问题；
- 代码格式化规范；
- 新增 Cookie 管理方法，详情参见 <a href="#Cookie">Cookie</a>；

v3.1.1：

- 新增 setJsonParameter(JsonMap) 方法用以直接设置 json 请求参数；
- JsonMap 和 JsonList 新增 toString() 可根据内容变化输出 json 文本；

v3.1.0：

- 新增 setJsonResponseListener 返回监听器，可直接返回已解析的 jsonMap，新增解析 Json 异常：DecodeJsonException；
- 新增文件下载功能，以及下载进度监听器 OnDownloadListener；
- 修复参数拦截器 parameter 为空的问题；
- 新增 stop() 方法可以停止请求进程（但请注意已发出的请求无法撤回）；
- JsonMap 和 JsonList 新增 toString() 可输出该对象原始 json 文本；
- 修改部分日志文案；

v3.0.9.1：

- 修正 JsonUtil 解析过程中误将所有空格剔除的 bug；

v3.0.9：

- JsonList 新增方法 set(Object) 可使用流式代码添加内容；
- JsonMap 新增方法 set(String, Object) 可使用流式代码添加内容；

v3.0.8：

- 修改 JsonUtil 的子方法为静态方法，可直接使用，提升使用便利性；

v3.0.7：

- 新增 JSON 解析框架，包含 JsonUtil、JsonMap和JsonList 三个工具类。

v3.0.6：

- 新增 BaseWebSocket 封装类，可快速实现 WebSocket 请求与连接。
- （此版本为小更新）新增 StringPOST 请求方式，可以丢任意文本封装为请求体发送给服务端，MediaType 默认为“text/plain”；
- 升级 OkHttp 底层框架至 3.9.1 版本；

v3.0.5：

- 新增了 skipSSLCheck() 方法用于临时忽略使用 HTTPS 证书；
- 删除了自定义异常 NetworkErrorException 的使用；

v3.0.4：

- 默认禁止了网络环境差的重复请求；
- 修复其他请求无法正常执行的 bug；

v3.0.3：

- 新增 put、delete 请求方法；
- 完善了请求的创建逻辑；

v3.0.2：

- 日志新增打印请求头；
- 日志请求参数打印增强；
  ![BaseOkHttp Logs2.0](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/baseokhttp_log2.0.png)
- 修改完善了 OkHttplient 创建方式以及默认未设置证书时对 HTTPS 的验证忽略；
- 修复了文件上传的相关 bug；

v3.0.1：

- 修复了一些bug；
