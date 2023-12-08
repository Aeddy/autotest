package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @description: 从网络Url中下载文件
 * @author: zhenqinl
 * @date: 2023/9/19 15:34
 */
public class DownloadUtil {

    public static void main(String[] args) throws IOException {
        String urlStr = "https://img2.doubanio.com/view/status/l/public/142605515-a26d520a5d06573.jpg";
        String fileName = "142605515-a26d520a5d06573.jpg";
        String dir = "/root/cota/";
        downloadByUrl(urlStr, dir, fileName);
        System.out.println("压缩文件下载完成");
    }

    public static void downloadByUrl(String urlStr, String propertiesFile, String fileName) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(5 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //获取项目根目录地址
        //String propertiesFile = "D:/work code/tar-files/tar/";
        //文件保存位置
        File saveDir = new File(propertiesFile);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        fos.close();
        inputStream.close();
        System.out.println("文件下载完成");
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     * @throws IOException io异常
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}


