# RTCPlayer 播放器

一个基于Android客户端的的RTC播放器


## 项目特点

- 低延迟
- [X]支持[ZLMediakit](https://github.com/ZLMediaKit/ZLMediaKit)流媒体
- [ ]支持[SRS](https://github.com/ossrs/srs)
- [ ]支持[Janus](https://github.com/meetecho/janus-gateway)


<center class="half">
    <img src="doc/home.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/player.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/pusher.jpg" width="240"/>
</center>

## 延迟情况

- **网页端推流，Android端播放**

<center class="half">
    <img src="doc/delay_play1.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/delay_play2.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/delay_play3.jpg" width="240"/>
</center>

三次延迟分别为：490ms、526ms、560ms


- **Android端推屏幕流，网页端播放**

<center class="half">
    <img src="doc/delay_push1.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/delay_push2.jpg" width="240"/>&nbsp;&nbsp;&nbsp;
    <img src="doc/delay_push3.jpg" width="240"/>
</center>

三次延迟分别为：440ms、430ms、387ms


## 接口说明

### Player

### Pusher