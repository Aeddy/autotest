package task;

import utils.FileCleaner;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class TimerTaskService extends TimerTask {

    @Override
    public void run() {

        try {

            System.out.println("定时任务开始执行");

            //主要业务逻辑
            Map<String, String> maps = new HashMap<>(2);
            maps.put("source", "/root/cota/source/");
            maps.put("target", "/root/cota/target/");
            FileCleaner.fileClean(maps);

            //TO DO

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}