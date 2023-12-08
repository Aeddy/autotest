package task;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
 
public class TimerManager {
 
    //时间间6个月
    private static final long PERIOD_DAY = 6 * 30 * 24 * 60 * 60 * 1000;

    public TimerManager() {
        Calendar calendar = Calendar.getInstance();
 
        //定制每日7点00执行
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //第一次执行定时任务的时间
        Date date = calendar.getTime();
        //如果当前时间已经过去所定时的时间点，则在第二天时间点开始执行
        if (date.before(new Date())) {
            date = this.addDay(date, 1);
        }
        Timer timer = new Timer();
        TimerTaskService task = new TimerTaskService();
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(task, date, PERIOD_DAY);
    }
 
    // 增加或减少天数
    private Date addDay(Date date, int num) {
        Calendar startDt = Calendar.getInstance();
        startDt.setTime(date);
        startDt.add(Calendar.DAY_OF_MONTH, num);
        return startDt.getTime();
    }
}