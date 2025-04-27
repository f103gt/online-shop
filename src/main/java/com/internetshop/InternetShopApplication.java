package com.internetshop;

import com.internetshop.configurations.AuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

public class InternetShopApplication {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector(); // This line is important to initialize the connector

        // Set base directory
        File baseDir = new File("build/tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // Context configuration
        String webappDir = "src/main/webapp";
        Context ctx = tomcat.addWebapp("", new File(webappDir).getAbsolutePath());

//        // Proper way to add the listener (using String class name)
//        ctx.addApplicationListener("org.apache.tomcat.websocket.server.WsContextListener");

        // Add Application Listener for Initialization
        ctx.addApplicationListener("com.internetshop.configurations.AppInitializer");

        // Add JSP support
        ctx.addServletContainerInitializer(new org.apache.jasper.servlet.JasperInitializer(), null);

        // Register AuthenticationFilter
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("AuthenticationFilter");
        filterDef.setFilterClass(AuthenticationFilter.class.getName());
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("AuthenticationFilter");
        filterMap.addURLPattern("/*");
        filterMap.setDispatcher(DispatcherType.REQUEST.name());
        ctx.addFilterMap(filterMap);

        // Add InstanceManager
        ctx.getServletContext().setAttribute(
                "org.apache.tomcat.InstanceManager",
                new org.apache.tomcat.SimpleInstanceManager()
        );

        System.out.println("Configuring app with basedir: " + new File(webappDir).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }
}