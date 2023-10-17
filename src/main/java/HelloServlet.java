import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * // 把当前的类和HTTP请求的路径关联起来
 * // 根据url中的路径，通过这个注解，就可以确定调用服务器中的哪个类
 * // 路径和servlet是一一对应关系
 *
 * 注意！！！！！
 * 验证程序
 * 首先启动Tomcat服务器，然后浏览器中输入url：http://127.0.0.1:8080/hello_servlet/hello 。
 * Tomcat端口默认为8080端口。
 * 注意这里的两级路径。第一级是webapps目录下的war包文件名。第二级路径是我们之前写的注解。
 * 请求就会根据这两级路径找到服务器中具体的类。
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    // 这里的doGet方法不需要自己手动调用，交给tomcat调用
    // tomcat收到get请求，就会触发doGet方法，tomcat会构造好两个参数req和resp
    // resp：空对象（输出型参数）
    // req：TCP Socket中读出来的字符串，按照HTTP协议解析，得到的对象
    // doGet做的工作就是根据请求构造响应
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //req：tomcat针对请求，已经解析构造好了
        //服务端控制台打印
        System.out.println("hello world");
        //构造resp对象，写回到客户端
        //写入resp中的body。当整个对象构造好，最终由tomcat写入网卡，发送至客户端
        resp.getWriter().write("hello world");
    }
}