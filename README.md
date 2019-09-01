# LittleVideoDemo
这是一个仿抖音小视频的demo

我的AS是3.4.2版本，所以里面引用的包都是androidx版本的，没support什么事儿，不用大惊小怪

看看效果图：

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![image](https://github.com/weioule/LittleVideoDemo/blob/master/app/src/main/java/com/example/img/gifhome_320x675_33s.gif) 　

1 首页是RecycleryView实现的瀑布流列表，详情页是用RecycleryView + PagerSnapHelper实现的ViewPager滑动效果，也就是抖音的小视频页面效果。

2 详情页里有双击点赞控件、炫酷点赞控件、加载进度条控件，加载进度条控件是加载与显示进度一起的，播放前内容加载是加载动画，播放时是进度条,上面的gif压缩了没看清，视频播放使用的是android原生的VideoView，使用方法和代码细节就不细说了，都在源码里，注释还是有些的。

3 列表与详情页使用的是同一个数据源，在详情页里观看和点赞后返回列表页，会通过EventBus来进行刷新联动。

4 刷新控件是用的SmartRefreshLayout，不过下拉刷新控件是自定义的仿头条的下拉刷新效果，以及刷新后的状态提示。

5 网络框架是rxJava2 + retrofit2 

6 最后是 统一页面加载框 和 单例Toast 与一些MVP的base基础类库的封装和一些工具类，哦对了，还有RecyclerView的分割线的定制。
