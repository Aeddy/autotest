//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import constants.BusinessConstants;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import utils.DownloadUtil;
//import utils.FolderCompareUtil;
//import utils.HttpServletRequestReader;
//import utils.LinuxCommandUnzipUtil;
//import vo.ContentDiffVo;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @description:
// * @author: zhenqinl
// * @date: 2023/9/28 14:41
// */
//@WebServlet("/content-diff/list")
//public class FileContentDiffBeforeServlet extends HttpServlet {
//
//    private static final Logger log = LoggerFactory.getLogger(LinuxCommandUnzipUtil.class);
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        JSONObject jsonObjectBase = HttpServletRequestReader.readAsCharsToCotaJsonObject(request);
//        JSONObject jsonObjectSource = jsonObjectBase.getJSONObject("cotaSource");
//        jsonObjectSource.put("saveDir", BusinessConstants.SOURCE_DIR);
//        String filenameSource = jsonObjectSource.getString("url")
//                .split("\\\\")[jsonObjectSource.getString("url").split("\\\\").length - 1];
//        jsonObjectSource.put("filename", filenameSource);
//        JSONObject jsonObjectTarget = jsonObjectBase.getJSONObject("cotaTarget");
//        jsonObjectTarget.put("saveDir", BusinessConstants.TARGET_DIR);
//        String filenameTarget = jsonObjectTarget.getString("url")
//                .split("\\\\")[jsonObjectTarget.getString("url").split("\\\\").length - 1];
//        jsonObjectTarget.put("filename", filenameTarget);
//        List<JSONObject> jsonObjects = new ArrayList<>(2);
//        jsonObjects.add(jsonObjectSource);
//        jsonObjects.add(jsonObjectTarget);
//
//        //对比文件差异前序工作（下载压缩文件包、执行simg2img、执行erofs_unpack.sh）
//        beforeDiff(jsonObjects);
//
//        //执行文件夹对比算法，获取差异文件夹列表
//        response.getWriter().write(JSON.toJSONString(contentDiffList()));
//
//
//        //执行文件内容差异对比算法，diff,单独作为一个接口开发
//        //contentDiff(String sourceFile, String targetFile)
//    }
//
//    /**
//     * 获取两个镜像解压之后的文件差异列表
//     * @return 文件差异列表（后续包装成ResultVo）
//     * @throws IOException io异常
//     */
//    private Map<String, Object> contentDiffList() throws IOException {
//        Map<String, Object> map = new HashMap<>(2);
//        List<ContentDiffVo> contentDiffList = FolderCompareUtil.contentDiffList();
//        int totalNum = 0;
//        if (!CollectionUtils.isEmpty(contentDiffList)) {
//            totalNum = contentDiffList.size();
//            contentDiffList.forEach(c -> {
//                c.setFilename(c.getFilename().replace("\\", "/"));
//            });
//            map.put("contentDiffList", contentDiffList);
//            map.put("TotalNum", totalNum);
//        }
//        return map;
//    }
//
//
//    /**
//     * 下载img压缩包
//     *
//     * @param jsonObjects 对比入参集合
//     */
//    private void beforeDiff(List<JSONObject> jsonObjects) {
//
//        jsonObjects.forEach(j -> {
//            String downloadUrl = j.getString("url");
//            String filename = j.getString("filename");
//            String saveDir = j.getString("saveDir");
//            //下载img文件
//            downloadImg(downloadUrl, filename, saveDir);
//            try {
//                //解压android img文件
//                decomAndroidImg(saveDir, filename);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//
//    private void downloadImg(String urlStr, String filename, String saveDir) {
//        try {
//            log.info("开始下载压缩包img, fileName:{}, url:{}", filename, urlStr);
//            DownloadUtil.downloadByUrl(urlStr, saveDir, filename);
//            log.info("下载img完成, fileName:{}, url:{}", filename, urlStr);
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.info("下载img失败, fileName:{}, url:{}", filename, urlStr);
//        }
//    }
//
//
//    /**
//     * 解压android img文件
//     * 1. 执行simg2img 例：simg2img xxx.img raw.img
//     * 2. 执行erofs_unpack.sh脚本 例：erofs_unpack.sh raw.img anythingaaa cota_pkg
//     * 注意：脚本执行提供绝对路径 command需要使用绝对路径
//     *
//     * @param dir      文件目录
//     * @param filename 镜像文件名
//     * @throws Exception 执行linux命令异常
//     */
//    private void decomAndroidImg(String dir, String filename) throws Exception {
//
//        log.info("开始解压android img文件 文件目录dir：{}，filename:{}", dir, filename);
//        String filePath = dir + filename;
//        String[] filenames = filename.split("\\.");
//        String fileNameRaw = filenames[0] + "_raw" + filenames[1];
//        String command1 = "simg2img" + filePath + " " + dir + fileNameRaw;
//        //执行simg2img
//        LinuxCommandUnzipUtil.runCommand(command1);
//
//        log.info("开始解压android img文件 文件目录dir：{}，filename:{}", dir, filename);
//
//        String directory1 = "anythingaaa";
//        String afterDecomName = "cota_pkg";
//        String command2 = "sh" + BusinessConstants.DECOM_UTIL_BIN + "erofs_unpack.sh"
//                + dir + filename + " " + directory1 + afterDecomName;
//        //执行erofs_unpack.sh
//        LinuxCommandUnzipUtil.runCommand(command2);
//    }
//}
