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
package org.usergrid.standalone.tomcat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.juli.ClassLoaderLogManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tnine
 * 
 */
public class EmbeddedTomcatHelper {

  private static final Logger logger = LoggerFactory.getLogger(EmbeddedTomcatHelper.class);

  private static final String TOMCAT_DIR = "tmp/tomcat";

  private static final String ACCESS_LOG_VALVE_FORMAT = "%h %l %u %t \"%r\" %s %b";

  protected Tomcat tomcat = null;

  protected int port = Integer.parseInt(System.getProperty("standalone.port", "8080"));

  /**
   * 
   */
  public EmbeddedTomcatHelper() {
  }

  /**
   * Extract the war file and return the directory it is in
   * 
   * @param zipFile
   * @param outputFolder
   * @return
   * @throws IOException
   */
  private File extractWarFile(File webappsDir) throws IOException {

    // check if the system property to run the standalone is set, if so do
    // nothing
    String developDirectory = System.getProperty("devdir");

    // its been overridden via the system param. Just run from the specified
    // directory
    if (developDirectory != null) {
      return new File(developDirectory);
    }

    Properties runtimeProps = new Properties();

    runtimeProps.load(this.getClass().getClassLoader().getResourceAsStream("tomcat.standalone.properties"));

    String warFileName = runtimeProps.getProperty("warfile");

    File outputDir = new File(webappsDir, "ROOT");
    File completed = new File(outputDir, "extract.complete");

    // nothing to do, we've already extracted it
    if (outputDir.exists() && completed.exists()) {
      return outputDir;
    }

    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    InputStream fileInWar = this.getClass().getClassLoader().getResourceAsStream(warFileName);

    if (fileInWar == null) {
      throw new FileNotFoundException(String.format("Could not find file %s on the classpath", fileInWar));
    }

    byte[] buffer = new byte[1024];

    int len;

    // get the zip file content
    ZipInputStream zis = new ZipInputStream(fileInWar);
    // get the zipped file list entry
    ZipEntry ze = null;

    while ((ze = zis.getNextEntry()) != null) {

      String fileName = ze.getName();
      File newFile = new File(outputDir, fileName);

      // no file data to write, skip to the next
      if (ze.isDirectory()) {
        logger.info("Creating directory : {}", newFile.getAbsoluteFile());
        zis.closeEntry();
        newFile.mkdirs();
        continue;
      }

      logger.info("Extracting file : {}", newFile.getAbsoluteFile());

      FileOutputStream fos = new FileOutputStream(newFile);

      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }

      fos.flush();
      fos.close();
      zis.closeEntry();
    }

    zis.closeEntry();
    zis.close();

    // always copy the properties first, this way they can be updated
    copyProperties(outputDir);

    // write the complete marker
    FileOutputStream fos = new FileOutputStream(completed);
    fos.write(0);
    fos.flush();
    fos.close();

    return outputDir;

  }

  /**
   * Dupe the properties in our classpath to the target webapp. This way we can
   * set properties into the webapp before it starts
   * 
   * @param outputPath
   * @throws IOException
   */
  private void copyProperties(File parent) throws IOException {

    byte[] buffer = new byte[1024];
    int length = 0;

    File newFile = new File(parent, "WEB-INF/classes/usergrid-custom.properties");

    // create the parent path if it doesn't exist
    newFile.getParentFile().mkdirs();

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("usergrid-custom.properties");

    FileOutputStream fos = new FileOutputStream(newFile);

    while ((length = in.read(buffer)) > 0) {
      fos.write(buffer, 0, length);
    }

    fos.flush();
    fos.close();
    in.close();
  }

  /**
   * @throws LifecycleException
   * 
   */
  public void start() {
    try {
      tomcat.start();
    } catch (LifecycleException e) {
      logger.error("Unable to start tomcat", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * @throws IOException
   * 
   */
  public void setup() {
    if (tomcat != null) {
      return;
    }

    tomcat = new Tomcat();

    File extractDirectoryFile = new File(TOMCAT_DIR);

    // create our directory structire for tomcat
    File webappsDir = new File(extractDirectoryFile, "webapps");
    webappsDir.mkdirs();

    File logsDir = new File(extractDirectoryFile, "logs");
    logsDir.mkdirs();

    new File(extractDirectoryFile, "conf").mkdirs();

    new File(extractDirectoryFile, "work").mkdirs();

    File tmpDir = new File(extractDirectoryFile, "temp");
    tmpDir.mkdirs();

    System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());

    System.setProperty("catalina.base", extractDirectoryFile.getAbsolutePath());
    System.setProperty("catalina.home", extractDirectoryFile.getAbsolutePath());

    File createdWebapp = null;
    
    try {
      createdWebapp = extractWarFile(webappsDir);
    } catch (IOException e) {
      logger.error("Unable to create web application", e);
      throw new RuntimeException(e);
    }

    tomcat.getHost().setAppBase(webappsDir.getAbsolutePath());
    tomcat.setPort(port);

    // set up the access logs
    // add a default acces log valve
    AccessLogValve alv = new AccessLogValve();
    alv.setDirectory(logsDir.getAbsolutePath());
    alv.setPattern(ACCESS_LOG_VALVE_FORMAT);
    tomcat.getHost().getPipeline().addValve(alv);

    // now add the webapp
    try {
      tomcat.addWebapp("/", createdWebapp.getAbsolutePath());
    } catch (ServletException e) {
      logger.error("Unable to add root webapp");
      throw new RuntimeException(e);
    }

    Runtime.getRuntime().addShutdownHook(new TomcatShutdownHook());
  }

  /**
   * @return
   */
  public boolean isRunning() {
    return (tomcat != null && tomcat.getServer().getState() == LifecycleState.STARTED);
  }

  /**
   * @throws LifecycleException
   * 
   */
  public void stop() {
    try {
      tomcat.stop();
    } catch (LifecycleException e) {
      logger.error("Unable to stop tomcat", e);
      throw new RuntimeException(e);
    }
  }
  
  

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }



  protected class TomcatShutdownHook
      extends Thread
  {

    protected TomcatShutdownHook()
    {
      // no op
    }

    @Override
    public void run()
    {
      try
      {
        EmbeddedTomcatHelper.this.stop();
      } catch (Throwable ex)
      {
        ExceptionUtils.handleThrowable(ex);
        System.out.println("fail to properly shutdown Tomcat:" + ex.getMessage());
      } finally
      {
        // If JULI is used, shut JULI down *after* the server shuts down
        // so log messages aren't lost
        LogManager logManager = LogManager.getLogManager();
        if (logManager instanceof ClassLoaderLogManager)
        {
          ((ClassLoaderLogManager) logManager).shutdown();
        }
      }
    }
  }

}
