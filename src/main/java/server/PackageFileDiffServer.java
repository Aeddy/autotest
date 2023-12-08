package server;

import com.alibaba.fastjson2.JSONObject;
import constants.BusinessConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import utils.*;
import vo.ContentDiffListVo;
import vo.ContentDiffVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/10/19 18:20
 */
@Slf4j
public class PackageFileDiffServer {

    /**
     * 获取文件对比差异
     *
     * @param serviceName      服务名
     * @param sourceFileRouter 资源文件路径
     *                         <p>
     *                         注：文件对比差异优先查询是否存在这个对比差异文件，如果存在那就直接读取文件返回；
     *                         否则需要调用diffString算法实现文件内容差异对比功能
     */
    public static void contentDiffDetail(String serviceName, String sourceFileRouter) {
        try {
            sourceFileRouter = sourceFileRouter.replace("/", "\\");

            String fileOriginal = "D:\\work code\\ackage\\" + serviceName + "\\source" + sourceFileRouter;
            String fileRevised = "D:\\work code\\ackage\\" + serviceName + "\\target" + sourceFileRouter;

            List<String> diffString = DiffHandleUtils.diffString(fileOriginal, fileRevised);
            //在 D:\diff\ 目录下生成一个 diff.html 文件，打开便可看到两个文件的对比
            String droducDirPath = "D:\\work code\\ackage\\" + serviceName + "\\content-diff\\";
            DiffHandleUtils.generateDiffHtml(diffString, droducDirPath);

            //把所需的 js和 css 从 resource 资源目录复制到 droducDirPath 目录下
            //FileCoypUtils.copyfile(droducDirPath);
            System.out.println("对比完成，请打开 " + droducDirPath + "\\contentDiff.html 查看");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sourceName 源资源名称
     * @param sourceUrl  源资源路径
     * @param targetName 目标资源名称
     * @param targetUrl  目标资源路径
     * @return 资源差异对比文件结果列表
     * @throws IOException io异常
     */
    public static ContentDiffListVo contentDiffList(String sourceName, String sourceUrl, String targetName, String targetUrl) throws IOException {

        JSONObject jsonObjectSource = new JSONObject();
        jsonObjectSource.put("saveDir", BusinessConstants.SOURCE_DIR);
        jsonObjectSource.put("filename", sourceName);
        jsonObjectSource.put("url", sourceUrl);

        JSONObject jsonObjectTarget = new JSONObject();
        jsonObjectTarget.put("saveDir", BusinessConstants.TARGET_DIR);
        jsonObjectTarget.put("filename", targetName);
        jsonObjectTarget.put("url", targetUrl);

        List<JSONObject> jsonObjects = new ArrayList<>(2);
        jsonObjects.add(jsonObjectSource);
        jsonObjects.add(jsonObjectTarget);

        //对比文件差异前序工作（下载压缩文件包、执行simg2img、执行erofs_unpack.sh）
        String sourceCotaPkgPath = beforeDiff(jsonObjects);
        String targetCotaPkgPath = beforeDiff(jsonObjects);

        //获取资源包差异列表
        List<ContentDiffVo> contentDiffVos = contentDiffList2(sourceName, sourceCotaPkgPath, targetName, targetCotaPkgPath);

        //执行入库获取diff_list id
        int diffListId = 1;
        //执行文件对比差异详情
        //diff 算法分析

        ContentDiffListVo contentDiffListVo = ContentDiffListVo.builder().diffListId(diffListId).sourceName(sourceName).targeName(targetName).contentDiffVos(contentDiffVos).build();
        if (CollectionUtils.isEmpty(contentDiffVos)) {
            contentDiffListVo.setTotalNum(0);
        } else {
            contentDiffListVo.setTotalNum(contentDiffVos.size());
        }
        return contentDiffListVo;
    }

    /**
     * @param sourceName 源资源名称
     * @param sourceUrl  源资源路径
     * @param targetName 目标资源名称
     * @param targetUrl  目标资源路径
     * @return 资源差异对比文件结果列表
     * @throws IOException io异常
     */
    public static void contentDiffHistory(String sourceName, String sourceUrl, String targetName, String targetUrl) {

        //do query database

    }

    /**
     * 获取两个镜像解压之后的文件差异列表
     *
     * @return 文件差异列表（后续包装成ResultVo）
     * @throws IOException io异常
     */
    private static List<ContentDiffVo> contentDiffList2(String sourceName, String sourceUrl, String targetName, String targetUrl) throws IOException {
        List<ContentDiffVo> contentDiffList = FolderCompareUtil.contentDiffList(sourceName, sourceUrl, targetName, targetUrl);
        if (!CollectionUtils.isEmpty(contentDiffList)) {
            contentDiffList.forEach(c -> {
                c.setFilename(c.getFilename().replace("\\", "/"));
            });
        }
        return contentDiffList;
    }


    /**
     * 下载img压缩包
     *
     * @param jsonObjects 对比入参集合
     */
    private static String beforeDiff(List<JSONObject> jsonObjects) {

        AtomicReference<String> cotaPkgPath = new AtomicReference<>("");
        jsonObjects.forEach(j -> {
            String downloadUrl = j.getString("url");
            String filename = j.getString("filename");
            String saveDir = j.getString("saveDir");
            //下载zip文件
            downloadZip(downloadUrl, filename, saveDir);
            //解压zip文件到指定目录
            unzip(downloadUrl, filename, saveDir);
            try {
                //解压android img文件
                String folder = filename.substring(0, filename.length() - 4);
                //文件名是啥 img是否有规律可循
                String imgFile = FindFolderFileUti.searchFiles(folder, ".img");
                cotaPkgPath.set(decomAndroidImg2(folder, imgFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return cotaPkgPath.get();
    }


    private static void downloadZip(String urlStr, String filename, String saveDir) {
        try {
            log.info("开始下载压缩包zip, fileName:{}, url:{}", filename, urlStr);
            DownloadUtil.downloadByUrl(urlStr, saveDir, filename);
            log.info("下载zip完成, fileName:{}, url:{}", filename, urlStr);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载zip失败, fileName:{}, url:{}", filename, urlStr);
        }
    }

    private static void unzip(String urlStr, String filename, String saveDir) {
        try {
            log.info("开始解压zip, fileName:{}, url:{}", filename, urlStr);
            String unzipRoot = saveDir;
            String command = "unzip -d " + saveDir + " " + filename;
            LinuxCommandUnzipUtil.runCommand(command);
            log.info("解压zip完成, fileName:{}, url:{}", filename, urlStr);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解压zip失败, fileName:{}, url:{}", filename, urlStr);
        }
    }


    /**
     * 解压android img文件
     * 1. 执行simg2img 例：simg2img xxx.img raw.img
     * 2. 执行erofs_unpack.sh脚本 例：erofs_unpack.sh raw.img anythingaaa cota_pkg
     * 注意：脚本执行提供绝对路径 command需要使用绝对路径
     *
     * @param dir      文件目录
     * @param filename 镜像文件名
     * @throws Exception 执行linux命令异常
     */
    private static void decomAndroidImg(String dir, String filename) throws Exception {

        log.info("开始解压android img文件 文件目录dir：{}，filename:{}", dir, filename);
        String filePath = dir + filename;
        String[] filenames = filename.split("\\.");
        String fileNameRaw = filenames[0] + "_raw" + filenames[1];
        String command1 = "simg2img" + filePath + " " + dir + fileNameRaw;
        //执行simg2img
        LinuxCommandUnzipUtil.runCommand(command1);

        log.info("开始解压android img文件 文件目录dir：{}，filename:{}", dir, filename);

        String directory1 = "anythingaaa";
        String afterDecomName = "cota_pkg";
        String command2 = "sh" + BusinessConstants.DECOM_UTIL_BIN + "erofs_unpack.sh"
                + dir + filename + " " + directory1 + afterDecomName;
        //执行erofs_unpack.sh
        LinuxCommandUnzipUtil.runCommand(command2);
    }

    private static String decomAndroidImg2(String dir, String imgFilePath) throws Exception {

        log.info("开始复制android img文件 imgFilePath:{}", imgFilePath);
        String[] filenames = imgFilePath.split("\\.");
        String fileNameRaw = filenames[0] + "_raw" + filenames[1];
        String command1 = "simg2img" + imgFilePath + " " + dir + fileNameRaw;
        //执行simg2img
        LinuxCommandUnzipUtil.runCommand(command1);

        log.info("开始解压android img文件 filePath:{}", dir + fileNameRaw);

        String directory1 = "anythingaaa";
        String afterDecomName = "cota_pkg";
        String command2 = "sh" + BusinessConstants.DECOM_UTIL_BIN + "erofs_unpack.sh"
                + dir + fileNameRaw + " " + directory1 + afterDecomName;
        //执行erofs_unpack.sh
        LinuxCommandUnzipUtil.runCommand(command2);

        return dir + "/" + "cota_pkg";
    }

}
