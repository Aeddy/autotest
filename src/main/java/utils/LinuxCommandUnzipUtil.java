package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @description: linux命令执行服务类
 * @author: zhenqinl
 * @date: 2023/9/25 10:54
 */
public class LinuxCommandUnzipUtil {

    private static final Logger log = LoggerFactory.getLogger(LinuxCommandUnzipUtil.class);

    public static int runCommand(String linuxCommand) throws Exception {

        try {
            log.info("执行命令：{}", linuxCommand);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", linuxCommand);
            Process process = processBuilder.start();
            int errCode = process.waitFor();
            log.info(String.format("finished local driver code2: %s", errCode));
            log.info("linux 命令执行成功！");
            return 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.info("linux 命令执行失败！");
            return 500;
        }
    }

}
