package utils;

import com.alibaba.fastjson2.JSON;
import model.FileModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import vo.ContentDiffVo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class FolderCompareUtil {

    /**
     * 根据路径获取所有的文件夹和文件,及文件的md5值
     *
     * @param path 路径
     */
    private static Map<String, FileModel> getFiles(String path) throws IOException {

        Map<String, FileModel> map = new HashMap<String, FileModel>();
        File folder = new File(path);
        Object[] files = getFileList(folder).toArray();
        Arrays.sort(files);

        for (Object obj : files) {
            File file = (File) obj;
            // 去掉根目录,正则的\\\\,转义为java的\\,再转义为\
            String key = file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(path, "");
            String md5 = "";// 文件夹不比较md5值
            if (file.isFile()) {
                md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
            }
            FileModel fileModel = new FileModel(file, md5);
            map.put(key, fileModel);
        }
        return map;
    }

    /**
     * 递归获取路径下所有文件夹和文件
     *
     * @param folder 文件路径
     */
    private static List<File> getFileList(File folder) {

        List<File> list = new ArrayList<File>();
        File[] files = folder.listFiles();

        for (File file : files) {
            list.add(file);
            if (file.isDirectory()) {
                List<File> fileList = getFileList(file);
                list.addAll(fileList);
            }
        }

        return list;
    }

    /**
     * 比较两个文件集合的不同
     *
     * @param fileMap1 文件集合
     * @param fileMap2 文件集合
     */
    public static List<FileModel> compareFile(Map<String, FileModel> fileMap1, Map<String, FileModel> fileMap2) {

        List<FileModel> list = new ArrayList<FileModel>();
        for (String key : fileMap1.keySet()) {
            FileModel fileModel1 = fileMap1.get(key);
            FileModel fileModel2 = fileMap2.get(key);
            // 将fileMap2中没有的文件夹和文件,添加到结果集中
            if (fileModel2 == null) {
                list.add(fileModel1);
                continue;
            }

            // 文件的md5值不同则add到比较结果集中
            if (fileModel1.getFile().isFile() && !fileModel1.getMd5().equals(fileModel2.getMd5())) {
                list.add(fileModel1);
            }
        }
        return list;
    }

    public static List<ContentDiffVo> contentDiffList(String sourceName, String path1, String targetName, String path2) throws IOException {

        List<String> names = new ArrayList<>(2);
        names.add(path1.split("\\\\")[path1.split("\\\\").length - 1]);
        names.add(path2.split("\\\\")[path2.split("\\\\").length - 1]);

        // 获取路径下所有文件夹和文件,及文件的md5值
        Map<String, FileModel> fileMap1 = getFiles(path1);
        Map<String, FileModel> fileMap2 = getFiles(path2);
        List<FileModel> resultList = new ArrayList<FileModel>();
        // 得到fileMap2中没有的文件夹和文件,及md5值不同的文件
        resultList.addAll(compareFile(fileMap1, fileMap2));
        // 得到fileMap1中没有的文件夹和文件,及md5值不同的文件
        resultList.addAll(compareFile(fileMap2, fileMap1));

        Map<String, Boolean> resultMap = new HashMap<>();
//        List<String> source = new ArrayList<>();
//        List<String> target = new ArrayList<>();

        Map<String, String> sourceMap = new HashMap<>();
        Map<String, String> targetMap = new HashMap<>();

        List<ContentDiffVo> contentDiffList = new ArrayList<>();

        // 与目标文件有差异的源文件
        for (FileModel fileModel : resultList) {
            System.out.println(fileModel.getFile().getAbsolutePath() + " " + fileModel.getMd5());
            if (fileModel.getFile().getAbsolutePath().contains("source")) {
                sourceMap.put(fileModel.getFile().getAbsolutePath().split("cota_pkg")[1], fileModel.getMd5());
            } else if (fileModel.getFile().getAbsolutePath().contains("target")) {
                targetMap.put(fileModel.getFile().getAbsolutePath().split("cota_pkg")[1], fileModel.getMd5());
            }
        }
        System.out.println("源文件map:" + JSON.toJSONString(sourceMap));
        System.out.println("目标文件map:" + JSON.toJSONString(targetMap));
        sourceMap.forEach((k, v) -> {
            if (targetMap.containsKey(k)) {
                if (!v.equals(targetMap.get(k))) {
                    resultMap.put(k, true);
                    //获取文件相对目录
                    String fileNameOx = "/" + sourceName + "/" + "cota_pkg/" + v;
                    ContentDiffVo contentDiffVo = ContentDiffVo.builder().filename(fileNameOx).build();
                    contentDiffList.add(contentDiffVo);
                } else {
                    resultMap.put(k, false);
                }
            } else if (!StringUtils.isEmpty(v)) {
                resultMap.put(k, true);
                //获取文件相对目录
                String fileNameOx = "/" + sourceName + "/" + "cota_pkg/" + v;
                ContentDiffVo contentDiffVo = ContentDiffVo.builder().filename(fileNameOx).build();
                contentDiffList.add(contentDiffVo);
            }
        });

        System.out.println("差异文件contentDiffList:" + JSON.toJSONString(contentDiffList));
        System.out.println("差异文件map:" + JSON.toJSONString(resultMap));

        return contentDiffList;
    }
}