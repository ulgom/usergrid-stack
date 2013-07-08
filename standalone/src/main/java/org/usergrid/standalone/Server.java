/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.usergrid.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.standalone.cassandra.EmbeddedServerHelper;

public class Server {

  public static final boolean INSTALL_JSP_SERVLETS = true;

  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  public static Server instance = null;

  CommandLine line = null;

  boolean initializeDatabaseOnStart = false;
  boolean startDatabaseWithServer = false;

  protected Tomcat tomcat = null;

  EmbeddedServerHelper embeddedCassandra = null;

  int port = Integer.parseInt(System.getProperty("standalone.port", "8080"));

  boolean daemon = true;

  public Server() {
    instance = this;
  }

  public static void main(String[] args) {
    instance = new Server();
    instance.startServerFromCommandLine(args);
  }

  public static Server getInstance() {
    return instance;
  }

  public void startServerFromCommandLine(String[] args) {
    CommandLineParser parser = new GnuParser();
    line = null;
    try {
      line = parser.parse(createOptions(), args);
    } catch (ParseException exp) {
      printCliHelp("Parsing failed.  Reason: " + exp.getMessage());
    }

    if (line == null) {
      return;
    }

    startDatabaseWithServer = line.hasOption("db");
    initializeDatabaseOnStart = line.hasOption("init");

    if (line.hasOption("port")) {
      try {
        port = ((Number) line.getParsedOptionValue("port")).intValue();
      } catch (ParseException exp) {
        printCliHelp("Parsing failed.  Reason: " + exp.getMessage());
        return;
      }
    }
    startServer(args);
  }

  public synchronized void startServer(String[] args) {

    if (startDatabaseWithServer) {
      startCassandra();
    }

    // TODO T.N. This fails, need to figure this out
    try {
      if (tomcat == null) {

        File webappDir = extractWarFile();

        tomcat = new Tomcat();
        tomcat.setPort(port);

        tomcat.addWebapp("/", webappDir.getAbsolutePath());
      }

       tomcat.start();
    } catch (Exception e) {
      logger.error("Unable to start tomcat", e);
    }

    if (daemon) {

      while (true) {
        try {
          synchronized (this) {
            wait();
          }
        } catch (InterruptedException e) {
        }
      }
    }
  }

  /**
   * Extract the war file and return the directory it is in
   * 
   * @param zipFile
   * @param outputFolder
   * @return
   * @throws IOException
   */
  private static File extractWarFile() throws IOException {
    
    //check if the system property to run the standalone is set, if so do nothing
    String developDirectory = System.getProperty("devdir");
    
    //its been overridden via the system param.  Just run from the specified directory
    if(developDirectory != null) {
      return new File(developDirectory);
    }
    
    Properties runtimeProps = new Properties();
    
    runtimeProps.load(Server.class.getClassLoader().getResourceAsStream("tomcat.standalone.properties"));
    
    String buildTime = runtimeProps.getProperty("build.timestamp");
    
    String warFileName= runtimeProps.getProperty("warfile");
    
    String outputPath = String.format("%s", buildTime);
    String complete = String.format("%s/extract.complete", outputPath);
    
    // create output directory is not exists
    File outputDir = new File(outputPath);
    File completed = new File(complete);

    //nothing to do, we've already extracted it
    if(outputDir.exists() && completed.exists()){
      return outputDir;
    }
    
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    InputStream fileInWar = Server.class.getClassLoader().getResourceAsStream(warFileName);

    if (fileInWar == null) {
      throw new FileNotFoundException(String.format("Could not find file %s on the classpath", fileInWar));
    }

    byte[] buffer = new byte[1024^2];

    // get the zip file content
    ZipInputStream zis = new ZipInputStream(fileInWar);
    // get the zipped file list entry
    ZipEntry ze = null;
    

    while ((ze = zis.getNextEntry()) != null) {

      String fileName = ze.getName();
      File newFile = new File(String.format("%s/%s", outputPath , fileName));

      System.out.println("file unzip : " + newFile.getAbsoluteFile());
      
      //no file data to write, skip to the next
      if(ze.isDirectory()){
        zis.closeEntry();
        newFile.mkdirs();
        continue;
      }

      FileOutputStream fos = new FileOutputStream(newFile);

      int len;

      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }

      fos.flush();
      fos.close();
      zis.closeEntry();
    }

    zis.closeEntry();
    zis.close();

    //write the complete marker
    FileOutputStream fos = new FileOutputStream(complete);
    fos.write(0);
    fos.flush();
    fos.close();

    return outputDir;

  }

  //
  // private int getThreadSizeFromSystemProperties() {
  // // the default value is number of cpu core * 2.
  // // see
  // //
  // org.glassfich.grizzly.strategies.AbstractIOStrategy.createDefaultWorkerPoolconfig()
  // int threadSize = Runtime.getRuntime().availableProcessors() * 2;
  //
  // String threadSizeString = System.getProperty("server.threadSize");
  // if (threadSizeString != null) {
  // try {
  // threadSize = Integer.parseInt(threadSizeString);
  // } catch (Exception e) {
  // // ignore all Exception
  // }
  // }
  // else {
  // try {
  // threadSize = Integer.parseInt(System.getProperty("server.threadSizeScale"))
  // * Runtime.getRuntime().availableProcessors();
  // } catch (Exception e) {
  // // ignore all Exception
  // }
  // }
  //
  // return threadSize;
  //
  // }

  //
  // private void setupJspMappings() {
  // if (!INSTALL_JSP_SERVLETS) {
  // return;
  // }
  //
  // JspFactoryImpl factory = new JspFactoryImpl();
  // JspFactory.setDefaultFactory(factory);
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.TestResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/TestResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.TestResource.test_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/TestResource/test.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UsersResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UsersResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UsersResource.resetpw_005femail_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UsersResource/resetpw_email_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UsersResource.resetpw_005femail_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UsersResource/resetpw_email_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.activate_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/activate.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.confirm_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/confirm.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.resetpw_005femail_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/resetpw_email_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.resetpw_005femail_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/resetpw_email_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.resetpw_005fset_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/resetpw_set_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.users.UserResource.resetpw_005fset_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/users/UserResource/resetpw_set_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.organizations.OrganizationResource.activate_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/organizations/OrganizationResource/activate.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.organizations.OrganizationResource.confirm_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/organizations/OrganizationResource/confirm.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.organizations.OrganizationResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/organizations/OrganizationResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.ManagementResource.authorize_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/ManagementResource/authorize_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.management.ManagementResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/management/ManagementResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UsersResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UsersResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UsersResource.resetpw_005femail_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UsersResource/resetpw_email_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UsersResource.resetpw_005femail_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UsersResource/resetpw_email_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.activate_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/activate.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.confirm_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/confirm.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/error.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.resetpw_005femail_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/resetpw_email_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.resetpw_005femail_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/resetpw_email_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.resetpw_005fset_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/resetpw_set_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.users.UserResource.resetpw_005fset_005fsuccess_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/users/UserResource/resetpw_set_success.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.ApplicationResource.authorize_005fform_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/ApplicationResource/authorize_form.jsp");
  //
  // mapServlet(
  // "jsp.WEB_002dINF.jsp.org.usergrid.rest.applications.ApplicationResource.error_jsp",
  // "/WEB-INF/jsp/org/usergrid/rest/applications/ApplicationResource/error.jsp");
  //
  // }
  //
  // private void mapServlet(String cls, String mapping) {
  //
  // try {
  // Servlet servlet = (Servlet) ClassLoaderUtil.load(cls);
  // if (servlet != null) {
  // ServletHandler handler = new ServletHandler(servlet);
  // handler.setServletPath(mapping);
  // httpServer.getServerConfiguration().addHttpHandler(handler,
  // mapping);
  // }
  //
  // } catch (Exception e) {
  // logger.error("Unable to add JSP page: " + mapping);
  // }
  //
  // logger.info("jsp: " + JspFactory.getDefaultFactory());
  // }

  public synchronized void stopServer() {
    // Collection<NetworkListener> listeners = httpServer.getListeners();
    // for(NetworkListener listener : listeners) {
    // try {
    // listener.stop();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // if (httpServer != null) {
    // httpServer.stop();
    // httpServer = null;
    // }

    try {
      tomcat.stop();
    } catch (LifecycleException e) {
      logger.error("Unable to stop tomcat", e);
    }

    if (embeddedCassandra != null) {
      stopCassandra();
      embeddedCassandra = null;
    }

    // if (ctx instanceof XmlWebApplicationContext) {
    // ((XmlWebApplicationContext) ctx).close();
    // }
  }

  public void setDaemon(boolean daemon) {
    this.daemon = daemon;
  }

  public boolean isRunning() {
    return (tomcat != null && tomcat.getServer().getState() == LifecycleState.STARTED);
  }

  public void printCliHelp(String message) {
    System.out.println(message);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        "java -jar usergrid-standalone-0.0.1-SNAPSHOT.jar ",
        createOptions());
    System.exit(-1);
  }

  public Options createOptions() {

    Options options = new Options();
    OptionBuilder.withDescription("Initialize database");
    Option initOption = OptionBuilder.create("init");

    OptionBuilder.withDescription("Start database");
    Option dbOption = OptionBuilder.create("db");

    OptionBuilder.withDescription("Http port");
    OptionBuilder.hasArg();
    OptionBuilder.withArgName("PORT");
    OptionBuilder.withLongOpt("port");
    OptionBuilder.withType(Number.class);
    Option portOption = OptionBuilder.create('p');

    options.addOption(initOption);
    options.addOption(dbOption);
    options.addOption(portOption);

    return options;
  }

  public synchronized void startCassandra() {
    if (embeddedCassandra == null) {
      embeddedCassandra = new EmbeddedServerHelper();

      if (initializeDatabaseOnStart) {
        logger.info("Initializing Cassandra");
        try {
          embeddedCassandra.setup();
        } catch (Exception e) {
          logger.error("Unable to initialize Cassandra", e);
          System.exit(0);
        }
      }

    }
    logger.info("Starting Cassandra");
    try {
      embeddedCassandra.start();
    } catch (Exception e) {
      logger.error("Unable to start Cassandra", e);
      System.exit(0);
    }

  }

  public synchronized void stopCassandra() {
    logger.info("Stopping Cassandra");
    embeddedCassandra.stop();
  }

  public boolean isInitializeDatabaseOnStart() {
    return initializeDatabaseOnStart;
  }

  public void setInitializeDatabaseOnStart(boolean initializeDatabaseOnStart) {
    this.initializeDatabaseOnStart = initializeDatabaseOnStart;
  }

  public boolean isStartDatabaseWithServer() {
    return startDatabaseWithServer;
  }

  public void setStartDatabaseWithServer(boolean startDatabaseWithServer) {
    this.startDatabaseWithServer = startDatabaseWithServer;
  }

}
