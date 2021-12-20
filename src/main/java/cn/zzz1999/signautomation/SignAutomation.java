package cn.zzz1999.signautomation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SignAutomation {
    private static final AtomicInteger errorCounter = new AtomicInteger();
    private static final int ERROR_TERMINATION_TIME = 3;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm");
    private static LocalDateTime nextSignDate = null;
    private static boolean exitFlag = false;

    public static void main(String[] args) throws InterruptedException {
        init();
        while (!exitFlag) {
            if (sign()) {
                System.out.println("签到成功!");
                LocalDateTime now = LocalDateTime.now();
                // 随机下一天签到时间
                LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
                dateTime = dateTime.withHour(randInt(7, 12));
                dateTime = dateTime.withMinute(randInt(0, 60));
                dateTime = dateTime.withSecond(randInt(0, 60));
                nextSignDate = dateTime;
                System.out.println("下次签到时间被设定为:" + nextSignDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒")));
                long nextMills = nextSignDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long nowMills = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                //System.out.println(nextMills - nowMills);
                Thread.sleep(nextMills - nowMills);
            }
        }
    }

    public static void init() {
        //Runtime runtime = Runtime.getRuntime();
        try {
            File logFolder = new File(System.getProperty("user.dir") + File.separator + "logs");
            if (!logFolder.isDirectory()) {
                if (!logFolder.mkdirs()) {
                    System.err.println("创建日志文件夹失败");
                    exitFlag = true;
                }
            }
            // 应该是腾讯云屏蔽了，修改不了环境变量
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "TYPE=password"}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "USERNAME="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "PASSWORD="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "APP_ID=pneumonia"}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "SCKEY="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "MSG_KEY="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "MSG_URL="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "TEXT_OK="}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "LOCATION=119.213553,34.65137,0.00001"}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "MODEL=Xiao Mi"}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "MODEL_CODE=\"MI 9\""}, null, null);
            //runtime.exec(new String[]{"/bin/sh", "-c", "export", "SYSTEM_VERSION=12"}, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean sign() {
        boolean successFlag = true;
        System.out.println("当前时间:" + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        System.out.println("正在签到中...");
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[]{"/bin/sh", "-c", "node dist/tools/main.js > logs/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒打卡记录")) + ".txt"}); // 日志
            //ReceiveOutputThread errorStream = new ReceiveOutputThread(process.getErrorStream());
            //errorStream.start();
            //ReceiveOutputThread receiveOutputThread = new ReceiveOutputThread(process.getOutputStream());
            // System.out.println("errorCount" + errorCount);
            // waitFor 如果出错，为255
            // int outputCount = process.getOutputStream();
            int len;
            byte[] bytes = new byte[256];
            InputStream inputStream = process.getErrorStream();
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes, 0, bytes.length)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            if (!"".equals(sb.toString())) {
                System.out.println("错误输出信息:" + sb.toString());
            }
            inputStream.close();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                System.err.println("命令执行没有正常结束(值:" + exitValue + "),当前错误次数:" + errorCounter.addAndGet(1));
                successFlag = false;
            } else {
                resetErrorCounter();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("执行打卡命令时出现错误,当前错误次数" + errorCounter.addAndGet(1));
            successFlag = false;
        } catch (InterruptedException e) {
            // waitFor
            e.printStackTrace();
            System.err.println("获取命令退出状态时出现错误,当前错误次数" + errorCounter.addAndGet(1));
            successFlag = false;
        } finally {
            if (!successFlag) {
                if (errorCounter.get() < ERROR_TERMINATION_TIME) {
                    int minuteOffset = randInt(50, 235);
                    System.out.println("打卡状态异常,将在下次重试打卡时间(" + LocalDateTime.now().plusMinutes(minuteOffset).format(DATE_TIME_FORMATTER) + ")再次打卡");
                    try {
                        TimeUnit.MINUTES.sleep(minuteOffset);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("打卡失败次数过多,程序退出");
                    exitFlag = true;
                }
            }
        }
        return successFlag;
    }

    public static void resetErrorCounter() {
        errorCounter.set(0);
    }

    public static int randInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public static LocalDateTime getNextSignDate() {
        return nextSignDate;
    }
}
