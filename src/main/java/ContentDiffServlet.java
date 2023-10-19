import utils.DiffHandleUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/9/28 17:34
 */
@WebServlet("/content-diff/detail")
public class ContentDiffServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String serviceName = request.getParameter("serviceName");
        String sourceFileRouter = request.getParameter("sourceFileRouter");
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

        //response.setContentType("multipart/form-data");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        // 2.设置文件头：最后一个参数是设置下载文件名
        response.setHeader("Content-Disposition", "attachment;fileName=" + "contentDiff.html");
        FileInputStream fis = new FileInputStream(new File(droducDirPath + "\\contentDiff.html"));
        response.getOutputStream().write(toByteArray(fis));
        response.getOutputStream().flush();
        response.getOutputStream().close();
        fis.close();//使用流之后一定要记得close，避免资源占用。
        //return "对比完成，请打开 " + droducDirPath + "\\diff.html 查看";
    }
}
