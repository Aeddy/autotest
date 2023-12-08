import constants.BusinessConstants;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LinuxCommandUnzipUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/upload")
public class FileUploadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LinuxCommandUnzipUtil.class);
    private static final long serialVersionUID = 7525671995700987977L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 判断上传的文件是普通表单还是带文件的表单
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果是一个普通文件，我们可以直接返回
            //如果通过了这个if，说明我们的表单是带文件上传的
            return;
        }
        // 创建上传文件的保存路径。建议保存在WEB-INF下，安全，用户无法直接访问上传的文件
        //String uploadPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        String uploadPath = "D:/root/tt/utils";
        System.out.println("文件保存路径-->" + uploadPath);
        File uploadFile = new File(uploadPath);
        // 如果此文件夹不存在就直接创建一个
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }

        // 临时路径,假如文件超过了预期大小，我们就把他放入一个临时文件中
        //String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        String tempPath = "D:/root/tt/temp";
        System.out.println("临时文件保存路径-->" + tempPath);
        File tempFile = new File(tempPath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        // 处理上传的文件，一般需要通过流来获取，我们可以使用request.getInputStream(),原生态的文件上传流获取，十分麻烦
        //但是我们都建议使用Apache的文件上传组件来实现，common-fileupload,他需要依赖commons-io组件
        /**
         *  1.创建DiskFileItemFactory对象：处理文件上传路径或者大小限制的
         */
        DiskFileItemFactory factory = getDiskFileItemFactory(tempFile);

        /**
         * 2.获取ServletFileUpload
         * ServletFileUpload负责处理上传的文件数据，并将表单中每个输入项封装成一个FileItem对象
         * 在使用ServletFileUpload对象解析请求时需要DiskFileItemFactory对象
         * 所以，我们需要在进行解析工作前构造好DiskFileItemFactory对象
         *通过ServletFileUpload对象的构造方法或setFileItemFactory()方法设置ServletFileUpload对象的fileItemFactory属性
         */
        ServletFileUpload upload = getServletFileUpload(factory);

        /**
         *  3.处理上传的文件
         */
        String msg = "";
        try {
            msg = uploadParseRequest(upload, request, uploadPath);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        response.getWriter().write(msg);
    }

    public static ServletFileUpload getServletFileUpload(DiskFileItemFactory factory) {
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 监听上传进度
        upload.setProgressListener(new ProgressListener() {
            @Override
            // pBytesRead:已经读到的大小
            // pContentLength:文件大小
            public void update(long pBytesRead, long pContentLength, int i) {
                System.out.println("总大小：" + pContentLength + "已经上传：" + pBytesRead);
            }
        });
        // 处理乱码
        upload.setHeaderEncoding("UTF-8");
        // 设置单个文件的最大值
        upload.setFileSizeMax(1024 * 1024 * 100);
        // 设置总共能够上传文件的大小
        upload.setSizeMax(1024 * 1024 * 1000);
        return upload;
    }

    public static DiskFileItemFactory getDiskFileItemFactory(File tempFile) {
        // 通过该工厂设置一个缓冲区，当上传文件大于缓冲区时，将他放到临时文件中
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置缓冲区大小为10M
        factory.setSizeThreshold(1024 * 1024 * 10);
        // 临时目录的保存目录
        factory.setRepository(tempFile);
        return factory;
    }

    public static String uploadParseRequest(ServletFileUpload upload, HttpServletRequest request, String uploadPath)
            throws FileUploadException, IOException {
        String msg = "";
        // 3.把前端请求解析，封装成一个FileItem对象
        List<FileItem> fileItems = upload.parseRequest(request);
        for (FileItem fileItem : fileItems) {
            // 判断上传的文件是普通的表单还是带文件的表单
            if (fileItem.isFormField()) {
                // getFieldName指的是前端表单控件的name；
                String name = fileItem.getFieldName();
                // 处理乱码
                String value = fileItem.getString("UTF-8");
                System.out.println(name + ":" + value);
            } else {
                // 判断它是上传的文件
                // =======================处理文件===============================//
                // 拿到文件名字
                String uploadFileName = fileItem.getName();
                System.out.println("上传的文件名：" + uploadFileName);
                if (uploadFileName.trim().equals("") || uploadFileName == null) {
                    continue;
                }
                // 获得上传的文件名 /images/girl/paojie.png
                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                // 获得文件的后缀名
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                /*
                 * 如果文件后缀名 fileExtName 不是我们所需要的 就直接return，不处理，告诉用户文件类型不对。
                 */
                System.out.println("文件信息 [件名：" + fileName + "---文件类型" + fileExtName + "]");

                // 可以使用UUID（唯一识别的通用码），保证文件名唯一；
                // UUID.randomUUID()，随机生一个唯一识别的通用码；
                //String uuidPath = UUID.randomUUID().toString();
                // 获得文件上传的流
                InputStream inputStream = fileItem.getInputStream();

                // 创建一个文件输出流
                // 差了一个文件; 加上输出文件的名字+"/" + fileName
                FileOutputStream fos = new FileOutputStream(uploadPath + fileName);
                // 创建一个缓冲区
                byte[] buffer = new byte[1024 * 1024];

                // 判断是否读取完毕
                int len = 0;
                // 如果大于0说明还存在数据；
                while ((len = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                // 关闭流
                fos.close();
                inputStream.close();

                msg = "file upload success";
                fileItem.delete(); // 上传成功，清除临时文件
                // =======================文件传输完毕===============================//
            }
        }
        return msg;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);

        try {
            decomUtilFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压工具包
     */
    private void decomUtilFile() {

        log.info("解压文件开始 fileName：{}", BusinessConstants.DECOM_FILE_NAME);
        try {
            String command = "tar -xvf " + BusinessConstants.DECOM_UTIL_ADDR + BusinessConstants.DECOM_FILE_NAME + "-C" + BusinessConstants.DECOM_UTIL_ADDR;
            LinuxCommandUnzipUtil.runCommand(command);
            log.info("文件解压完成 fileName：{}， 文件地址：{}", BusinessConstants.DECOM_FILE_NAME, BusinessConstants.DECOM_UTIL_ADDR);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件解压失败 fileName：{}， 文件地址：{}", BusinessConstants.DECOM_FILE_NAME, BusinessConstants.DECOM_UTIL_ADDR);
        }
    }
}
