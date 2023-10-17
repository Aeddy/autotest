import utils.LinuxCommandUnzipUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/9/28 17:34
 */
@WebServlet("/content-diff/detail")
public class ContentDiffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        String linuxCommand = request.getParameter("linuxCommand");
        String fileName = request.getParameter("fileName");

        try {
            //String command = linuxCommand + directoryPath + fileName + "-C" + directoryPath;
            String command = "";
            LinuxCommandUnzipUtil.runCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
