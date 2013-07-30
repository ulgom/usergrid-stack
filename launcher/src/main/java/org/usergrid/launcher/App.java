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
package org.usergrid.launcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.standalone.Server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class App {

  /**
   * 
   */
  private static final String AUTO_LOGIN = "autoLogin";

  /**
   * 
   */
  private static final String ADMIN_USER_PASSWORD = "adminUserPassword";

  /**
   * 
   */
  private static final String ADMIN_USER_EMAIL = "adminUserEmail";

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private static final String GH_PORTAL_URL = "http://apigee.github.io/usergrid-portal/";

  public static boolean MAC_OS_X = (System.getProperty("os.name")
      .toLowerCase().startsWith("mac os x"));

  LogViewerFrame logViewer = null;
  LauncherFrame launcher = null;
  ExecutorService executor = Executors.newSingleThreadExecutor();
  boolean initializeDatabaseOnStart = true;
  boolean startDatabaseWithServer = true;
  Preferences prefs;
  String adminUserEmail = null;
  String adminUserPassword = null;
  boolean autoLogin = true;

  public App() {
    /*
     * super("Launcher"); addComponentsToPane(); pack(); setVisible(true);
     */
  }

  public static void main(String[] args) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        "Usergrid Launcher");
    System.setProperty("apple.awt.antialiasing", "true");
    System.setProperty("apple.awt.textantialiasing", "true");
    System.setProperty("apple.awt.graphics.UseQuartz", "true");
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (MAC_OS_X) {
      AppleUtils.initMacApp();
    }

    App app = new App();
    app.launch();
  }

  public void launch() {
    loadPrefs();

    try {
      logViewer = new LogViewerFrame(this);
    } catch (IOException e) {
      e.printStackTrace();
    }

    launcher = new LauncherFrame(this);
    

    logger.info("App started");
    // org.usergrid.standalone.Server.main(new String[0]);
  }

  public static ArrayNode getJsonArray(Set<String> strings) {
    ArrayNode node = JsonNodeFactory.instance.arrayNode();
    for (String string : strings) {
      node.add(string);
    }
    return node;
  }

  public void storeUrlsInPreferences(Set<String> urls) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      prefs.put("urlList", mapper.writeValueAsString(getJsonArray(urls)));
    } catch (Exception e) {
    }
  }

  public void storeUrlsInPreferences(String[] urls) {
    storeUrlsInPreferences(new LinkedHashSet<String>(Arrays.asList(urls)));
  }

  public Set<String> getUrlSetFromPreferences() {
    Set<String> urls = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    urls.add(GH_PORTAL_URL);
    ObjectMapper mapper = new ObjectMapper();
    String json = null;
    try {
      json = prefs.get("urlList", null);
    } catch (Exception e) {
    }
    if (json == null) {
      return urls;
    }
    List<String> strings = null;
    try {
      strings = mapper.readValue(json, new TypeReference<List<String>>() {
      });
    } catch (Exception e) {
    }
    if (strings == null) {
      return urls;
    }
    urls = new LinkedHashSet<String>(strings);
    urls.addAll(strings);
    return urls;
  }

  public String[] getUrlsFromPreferences() {
    Set<String> urls = getUrlSetFromPreferences();
    return urls.toArray(new String[urls.size()]);
  }

  Server server = null;

  public Server getServer() {
    if (server == null) {
      synchronized (this) {
        if (server == null) {
          server = new Server();
          server.setDaemon(false);
        }
      }

    }
    return server;
  }

  public void startServer() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        if (!getServer().isRunning()) {
          launcher.setStatusYellow();
          getServer().setInitializeDatabaseOnStart(
              initializeDatabaseOnStart);
          getServer().setStartDatabaseWithServer(
              startDatabaseWithServer);
          getServer().startServer(new String[] {});
          launcher.setStatusGreen();
        }
      }
    });
  }

  public synchronized void stopServer() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        if (getServer().isRunning()) {
          getServer().stopServer();
          launcher.setStatusRed();
        }
      }
    });
  }

  public LogViewerFrame getLogViewer() {
    return logViewer;
  }

  public LauncherFrame getLauncher() {
    return launcher;
  }

  public void showLogView() {
    logViewer.setVisible(true);
  }

  public boolean isInitializeDatabaseOnStart() {
    return initializeDatabaseOnStart;
  }

  public void setInitializeDatabaseOnStart(boolean initializeDatabaseOnStart) {
    this.initializeDatabaseOnStart = initializeDatabaseOnStart;
    prefs.putBoolean("initializeDatabaseOnStart", initializeDatabaseOnStart);
  }

  public boolean isStartDatabaseWithServer() {
    return startDatabaseWithServer;
  }

  public void setStartDatabaseWithServer(boolean startDatabaseWithServer) {
    this.startDatabaseWithServer = startDatabaseWithServer;
    prefs.putBoolean("startDatabaseWithServer", startDatabaseWithServer);
  }

  public String getAdminUserEmail() {
    return adminUserEmail;
  }

  public void setAdminUserEmail(String adminUserEmail) {
    this.adminUserEmail = adminUserEmail;
    prefs.put(ADMIN_USER_EMAIL, adminUserEmail);
  }

  public String getAdminUserPassword(){
    return adminUserPassword;
  }
  
  public void setAdminUserPassword(String adminUserEmail) {
    this.adminUserEmail = adminUserEmail;
    prefs.put(ADMIN_USER_PASSWORD, adminUserEmail);
  }

  public boolean isAutoLogin() {
    return autoLogin;
  }

  public void setAutoLogin(boolean autoLogin) {
    this.autoLogin = autoLogin;
    prefs.putBoolean(AUTO_LOGIN, autoLogin);
  }

  public String getAccessToken() {
    String url = "http://localhost:8080/management/token";
    DefaultHttpClient httpclient = new DefaultHttpClient();

    try {

      HttpPost httpPost = new HttpPost(url);

      String json = String.format("{\"grant_type\":\"password\", \"username\": \"%s\", \"password\",\"%s\"}",
          adminUserEmail, adminUserPassword);

      StringEntity requestEntity = new StringEntity(json, HTTP.UTF_8);
      requestEntity.setContentType("application/json");
      httpPost.setEntity(requestEntity);

      logger.info("executing request {}", httpPost.getRequestLine());
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity entity = response.getEntity();

      String responseBody = EntityUtils.toString(entity);
      logger.info("Response was \n{} \n{}", response.getStatusLine(), responseBody);
      
      
      Pattern p = Pattern.compile("\"access_token\" : \"(.*)\"");
      
      Matcher matcher = p.matcher(responseBody);
      
      String group1 = matcher.group(0);
      String group2 = matcher.group(1);
      
      return group2;
      
    } catch (Exception e) {
      logger.error("Unable to get admin token", e);
      throw new RuntimeException("Unable to get admin token", e);
    } finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      httpclient.getConnectionManager().shutdown();
    }

  }

  //
  // public UUID getAdminUUID(){
  // return server.getAdminUUID(adminUserEmail);
  // }

  public boolean serverIsStarted() {
    return (server != null) && server.isRunning();
  }

  public void loadPrefs() {
    prefs = Preferences.userNodeForPackage(org.usergrid.launcher.App.class);
    initializeDatabaseOnStart = prefs.getBoolean(
        "initializeDatabaseOnStart", true);
    startDatabaseWithServer = prefs.getBoolean("startDatabaseWithServer",
        true);
    adminUserEmail = prefs.get(ADMIN_USER_EMAIL, "test@usergrid.com");
    adminUserEmail = prefs.get(ADMIN_USER_PASSWORD, "test@usergrid.com");
    autoLogin = prefs.getBoolean(AUTO_LOGIN, true);

  }

}
