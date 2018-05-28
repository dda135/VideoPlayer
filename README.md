# VideoPlayer
一个播放器，内部实现基于MediaPlayer和ijkPlayer
## 基本想法
主要的解码等操作都是由MediaPlayer或者ijkplyer来完成，所以说需要做的其实是合理地进行封装，需要达到的目的也很简单<br>
1.外部只能通过统一的接口来访问，从而屏蔽内部具体实现，这样才能方便MediaPlayer、ijkplayer等的切换
2.列表中的视频播放需要有一个统一的管理类，比方说同一时刻列表中只能有一个播放中的视频
3.视频具体展示视图由外部布局决定，这里是考虑直接放进到一个FrameLayout里面，但是全屏和小窗口模式采用内聚的模式，降低外部的使用成本
## 例子
初始化播放器和数据源
```
    private void initMedia(){
        //可以指定使用MediaPlayer或者ijkplayer
        videoPlayerClient = new MediaPlayerClient(this,MediaPlayerClient.TYPE_MEDIAPLAYER);
        //关联进FrameLayout，从而进行内容展示
        videoPlayerClient.attachView(frameLayout);
        Uri uri = Uri.parse("http://10.1.93.196:8080/IM/Starry_Night.mp4");
        try {
            //指定播放源
            videoPlayerClient.setDataResource(uri);
        } catch (DataResourceException e) {
            e.printStackTrace();
        }
    }
```
关联Activity的生命周期，主要需要做这样的几件事情，比方说pause的时候要暂停视频的播放、resume恢复播放、destroy的时候释放资源等等
```
    @Override
    protected void onPause() {
        super.onPause();
        if(null != videoPlayerClient){
            videoPlayerClient.setViewVisible(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != videoPlayerClient){
            videoPlayerClient.setViewVisible(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != videoPlayerClient){
            videoPlayerClient.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != videoPlayerClient){
            videoPlayerClient.onActivityDestroy();
        }
    }
```
视图展示模式，包括全屏和小窗口模式
```
//关联指定的FrameLayout，默认为MATCH_PARENT，这样就自定义播放器位置和大小
videoPlayerClient.attachView(frameLayout);
//全屏展示播放器，默认为横屏
videoPlayerClient.fullScreenShow();
//小窗口模式，允许指定大小，目前默认实现为居中，后续可能增加Gravity支持
videoPlayerClient.smallWindowShow(size,size);
```
