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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.cassandra.net.Header;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.standalone.cassandra.EmbeddedServerHelper;
import org.usergrid.standalone.tomcat.EmbeddedTomcatHelper;

public class Server {

  public static final boolean INSTALL_JSP_SERVLETS = true;

  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  

  public static Server instance = null;

  protected CommandLine line = null;

  boolean initializeDatabaseOnStart = false;
  boolean startDatabaseWithServer = false;

  protected EmbeddedTomcatHelper embeddedTomcat = null;
  protected EmbeddedServerHelper embeddedCassandra = null;
  
  protected int cliPort = 0 ;

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
        cliPort = ((Number) line.getParsedOptionValue("port")).intValue();
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

    try {
      startTomcat();
    } catch (Exception e) {
      logger.error("Unable to start tomcat", e);
      return;
    }

    if (initializeDatabaseOnStart) {
      initSystem();
    }

    if (daemon) {
      Object lock = new Object();

      synchronized (lock)
      {
        try
        {
          lock.wait();
        } catch (InterruptedException exception)
        {
          throw new Error("InterruptedException on wait Indefinitely lock:" + exception.getMessage(),
              exception);
        }
      }
    }
  }

  private void initSystem() {

    try {
      Properties props = new Properties();

      loadCustomProps(props, "classpath:/usergrid-custom.properties");
      loadCustomProps(props, "file:./usergrid-custom-standalone.properties");

      String username = props.getProperty("usergrid.sysadmin.login.name");
      String password = props.getProperty("usergrid.sysadmin.login.password");

      String url = String.format("http://localhost:%d/system/database/setup", embeddedTomcat.getPort());

      DefaultHttpClient httpclient = new DefaultHttpClient();
      try {
        httpclient.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", embeddedTomcat.getPort()),
            new UsernamePasswordCredentials(username, password));

        HttpGet httpget = new HttpGet(url);

        logger.info("executing request {}" , httpget.getRequestLine());
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
 
        logger.info("Response was \n{} \n{}", response.getStatusLine(), EntityUtils.toString(entity));
      } finally {
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
      }

    } catch (Exception e) {
      logger.error("Unable to initialize system", e);
      throw new RuntimeException("Unable to initialize system", e);
    }

    // request.
  }

  public synchronized void stopServer() {

    if(embeddedTomcat != null){
      embeddedTomcat.stop();
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
    return embeddedTomcat.isRunning();
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
  
  public synchronized void startTomcat(){
    if(embeddedTomcat == null){
      embeddedTomcat = new EmbeddedTomcatHelper();
      
      if(cliPort != 0){
        embeddedTomcat.setPort(cliPort);
      }
      
      embeddedTomcat.setup();
    }
    
    embeddedTomcat.start();
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

  
  private void loadCustomProps(Properties props, String location) throws IOException {
    InputStream stream = null;

    if (location.startsWith("classpath:/")) {
      stream = Server.class.getClassLoader().getResourceAsStream(location.replace("classpath:/", ""));
    } else if (location.startsWith("file:/")) {
      stream = new FileInputStream(location.replace("file:/", ""));
    }

    if (stream == null) {
      return;
    }

    props.load(stream);
    stream.close();
  }
  

}
