package com.graph;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springside.modules.test.jetty.JettyFactory;
import org.springside.modules.test.spring.Profiles;

import java.util.List;

/**
 * Created by ACT-NJ on 2017/3/15.
 */
public class QuickStartServer {

    public static final String CONTEXT = "/";
    private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";
    public static final int PORT = 8084;
    public static final String[]
            TLD_JAR_NAMES =
            new String[]{"spring-webmvc", "shiro-web", "springside-core", "rapid", "jstl"};
    public static Server createServerInSource(int port, String contextPath) {
        Server server = new Server(port);
        // 设置在JVM退出时关闭Jetty的钩子
        server.setStopAtShutdown(true);
        //这是http的连接器
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        // 解决Windows下重复启动Jetty居然不报告端口冲突的问题
        connector.setReuseAddress(true);
        server.setConnectors(new Connector[] { connector });

        WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, contextPath);
        webContext.setDescriptor(DEFAULT_WEBAPP_PATH+"/WEB-INF/web.xml");
        // 设置webapp的位置
        webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
        webContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
//        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
//        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
//                "org.eclipse.jetty.annotations.AnnotationConfiguration");
//        webContext.setConfigurationClasses(classlist);
        webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(webContext);
        return server;
    }

    /**
     * 设置除jstl-*.jar外其他含tld文件的jar包的名称. jar名称不需要版本号，如sitemesh, shiro-web
     */
    public static void setTldJarNames(Server server, String... jarNames) {
        WebAppContext context = (WebAppContext) server.getHandler();
        List<String>
                jarNameExprssions = Lists.newArrayList(".jstl-[^/]*\\.jar$", "..*taglibs[^/]*\\.jar$");
        for (String jarName : jarNames) {
            jarNameExprssions.add("." + jarName + "-[^/]*\\.jar$");
        }
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                StringUtils.join(jarNameExprssions, '|'));
    }

    public static void main(String[] args) throws Exception {
        Log.setLog(new org.eclipse.jetty.util.log.Slf4jLog());
        // 设定Spring的profile
        Profiles.setProfileAsSystemProperty(Profiles.PRODUCTION);
        // 启动Jetty
        Server server = createServerInSource(PORT, CONTEXT);
        setTldJarNames(server, TLD_JAR_NAMES);
        try {
//            server.stop();
            server.start();
//            server.join();
            System.out.println("[INFO] Server running at http://localhost:" + PORT + CONTEXT);
            System.out.println("[HINT] Hit Enter to reload the application quickly");
            // 等待用户输入回车重载应用.
            while (true) {
                char c = (char) System.in.read();
                if (c == '\n') {
                    JettyFactory.reloadContext(server);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
