package utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class FileCleaner {


    public static void main(String[] args) {

        toCleanFile("test", "C:\\workTest\\cota\\");

    }

    public static void toCleanFile(String resOf, String zipDir) {

        log.info("清理" + resOf + "六个月前的zip 开始！！！");
        Path path = Paths.get(zipDir);
        //Path path = Paths.get("C:\\workTest\\cota\\");

        try {
            Pattern pattern = Pattern.compile("^.+\\.zip");
            List<Path> paths = Files.walk(path).filter(p -> {
                //如果不是普通的文件，则过滤掉
                if (!Files.isRegularFile(p)) {
                    return false;
                }
                File file = p.toFile();
                Matcher matcher = pattern.matcher(file.getName());
                return matcher.matches();
            }).collect(Collectors.toList());

            System.out.println("过滤后的文件列表：" + JSON.toJSONString(paths));

            paths.forEach(file -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                    LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime oneMonthAgo = LocalDateTime.now().minus(6, ChronoUnit.SECONDS);
                    if (lastModifiedTime.isBefore(oneMonthAgo)) {
                        try {
                            Files.delete(file);
                            System.out.println("Deleted file: " + file);
                            log.info("Deleted file: {}", file);
                        } catch (IOException e) {
                            System.err.println("Failed to delete file: " + file);
                            log.info("Failed to delete file: {}", file);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to read attributes for file: " + file);
                    log.info("Failed to read attributes for file: {}", file);
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to walk through the folder" + path);
            log.info("Failed to walk through the folder: {}", path);
        }
    }

    public static void fileClean(Map<String, String> zipResMap) {

        zipResMap.forEach(FileCleaner::toCleanFile);
    }
}