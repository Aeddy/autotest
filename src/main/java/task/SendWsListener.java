package task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
public class SendWsListener implements ServletContextListener  {
 
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("定时任务已启动");
        new TimerManager();
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("定时任务已销毁");
    }
}