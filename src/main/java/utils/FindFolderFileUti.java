package utils;

import java.io.File;
import java.util.Objects;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/10/26 18:37
 */
public class FindFolderFileUti {

    public static String searchFiles(String path, String type) {

        String imgFile = "";
        File file = new File(path);
        if (file.isDirectory()) {
            //目录
            File[] files = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                //利用递归调用依次查找目录
                searchFiles(files[i].getAbsolutePath(), type);
            }
        } else {
            //文件
            String absolutePath = file.getAbsolutePath();
            //方法一
//            //获取文件的后缀名
//            int index = absolutePath.lastIndexOf(".");
//            String str = absolutePath.substring(index);
//            //逐个匹配指定类型文件
//            for (int i = 0; i < type.length; i++){
//                if (str.equals(type[i])){
//                    System.out.println(absolutePath);
//                }
//            }
            //方法二
            //检查路径后缀是指定的
            if (absolutePath.endsWith(type)) {
                System.out.println(absolutePath);
                imgFile = absolutePath;
            }
        }
        return imgFile;
    }
}
