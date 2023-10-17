import utils.LinuxCommandUnzipUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/9/28 14:21
 */
@WebServlet("/decom")
public class DecomImgServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        String directoryPath = request.getParameter("directoryPath");
        String linuxCommand = request.getParameter("linuxCommand");
        String fileName = request.getParameter("fileName");

        try {
            String command = linuxCommand + directoryPath + fileName + "-C" + directoryPath;
            LinuxCommandUnzipUtil.runCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
