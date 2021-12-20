# time-helper-automation-exec

|作者|zzz1999|
|:---:|:---:|
|E-mail|1173572640@qq.com|
****
**自用打卡java程序,仅供学习参考使用**
 - 使用screen开启窗口，然后使用export命令设置环境变量
 - 运行jar包，挂机即可完成每日打卡
 - 在同目录下的logs文件下，记录着每日打卡的日志。
 - 如果打卡失败，则会过会再次打卡

### 功能
 - [x] 随机时间打卡,每天上午7点到11点之间进行打卡
 - [x] 打卡日志会被存放至同目录下的`logs`文件夹中.打卡成功的话文件内容为空,否则为错误输出.
 - [x] 打卡失败时,会在5-30分钟内再次打卡(由代码随机再次打卡时间).当打卡连续失败3次后,程序退出运行

### API
```java_holder_method_tree
    /**
     * 获取明天的打卡时间
     * @return 下次打卡时间
     */
    public static LocalDateTime getNextSignDate()

    /**
     * 获取打卡失败累计次数
     * @return 打卡失败累计次数
     */
    public static AtomicInteger getErrorCounter()
```


![错误日志演示](https://github.com/zzz1999zzz1999/time-helper-automation-exec/raw/master/src/main/resources/log_list.png)