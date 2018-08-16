/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class SpigetClient {
  public static final String spigetURL = "https://api.spiget.org/v2/";
  
  /**
   * Spiget resource instance
   */
  public class ListItem {
    public int id;
    public String name;
    public String tag;
    public String contributors;
    public String description;
    public int likes;
    
    public boolean premium;
    public float price;
    public String currency;
    
    public class File {
      public String type;
      public Float size;
      public String sizeUnit;
      public String url;
    }
    
    public File file;
    
    public String[] testedVersions;
    
    public class Rating {
      public int count;
      public Float average;
    }
    
    public Rating rating;
    
    public int releaseDate;
    public int updateDate;
    public int downloads;
    public boolean external;
    
    public class Icon {
      public String url;
      public String data;
    }
    
    public Icon icon;
    
    public class Review {
      public class Author {
        public int id;
        public String name;
        public Icon icon;
      }
      
      public Author author;
      public Rating rating;
      public String message;
      public String responceMessage;
      public String version;
      public int date;
    }
    
    public Review[] reviews;
  }
  
  /**
   * Lists Spigot resources
   *
   * @return <code>ListItem[]</code> of 20 Spigot resources
   */
  public static ListItem[] listItems() {
    return listItems(20, 0);
  }
  
  /**
   * Lists Spigot resources
   *
   * @param size quantity of resources displayed on one page
   * @param page page to display
   * @return <code>ListItem[]</code> of <code>size</code> Spigot resources from <code>page</code>
   * page
   */
  public static ListItem[] listItems(int size, int page) {
    try {
      String json = getJson(spigetURL + "resources?page=" + page + "&size=" + size);
      if(json != null) {
        Gson gson = new Gson();
        return gson.fromJson(json, ListItem[].class);
      }
    } catch(IOException ex) {
      return null;
    }
    
    return null;
  }
  
  /**
   * Searchs for resources with given query
   *
   * @param query string to search
   * @return <code>ArrayList ListItem</code> of 10 Spigot resources matched by query
   */
  public static ArrayList<ListItem> search(String query) {
    return search(query, 10, 0);
  }
  
  /**
   * Searchs for resources with given query
   *
   * @param query string to search
   * @param size  quantity of resources displayed on one page
   * @param page  page to display
   * @return <code>ArrayList ListItem</code> of <code>size</code> Spigot resources matched by
   * query from <code>page</code> page
   */
  public static ArrayList<ListItem> search(String query, int size, int page) {
    return search(query, size, page, false);
  }
  
  /**
   * Searchs for resources with given query
   *
   * @param query    string to search
   * @param size     quantity of resources displayed on one page
   * @param page     page to display
   * @param fullItem <code>true</code> to display plugins with all details, <code>false</code> for
   *                 name, id and tag
   * @return <code>ArrayList ListItem</code> of <code>size</code> Spigot resources matched by
   * query from <code>page</code> page
   */
  public static ArrayList<ListItem> search(String query, int size, int page, boolean fullItem) {
    try {
      String json = getJson(spigetURL + "search/resources/" + query + "?page=" + page + "&size=" + size);
      if(json != null) {
        Gson gson = new Gson();
        ListItem[] list = gson.fromJson(json, ListItem[].class);
        
        if(fullItem) {
          ArrayList<ListItem> fullList = new ArrayList<>();
          
          for(ListItem plugin : list) {
            ListItem fullPlugin = get(plugin.id);
            fullList.add(fullPlugin);
          }
          
          return fullList;
        } else {
          return new ArrayList<>(Arrays.asList(list));
        }
      }
    } catch(IOException ex) {
      return null;
    }
    
    return null;
  }
  
  /**
   * @param id id of Spigot resource
   * @return <code>ListItem</code> of Spigot resource
   */
  public static ListItem get(int id) {
    try {
      String json = getJson(spigetURL + "resources/" + id);
      if(json != null) {
        Gson gson = new Gson();
        return gson.fromJson(json, ListItem.class);
      }
    } catch(IOException ex) {
      return null;
    }
    
    return null;
  }
  
  /**
   * Downloads file from <code>urlString</code> as <code>dest</code>
   *
   * @param urlString URL of file to download
   * @param dest      destination where to save file
   * @return true if file was downloaded successfully, otherwise false
   */
  public static boolean download(String urlString, String dest) {
    try {
      int responseCode;
      do {
        URL url = new URL(urlString);
        
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
        httpURLConnection.connect();
        
        responseCode = httpURLConnection.getResponseCode();
        
        if((responseCode / 100) == 3) {
          String newLocation = httpURLConnection.getHeaderField("Location");
          urlString = newLocation;
        }
      } while((responseCode / 100) == 3);
      
      if(responseCode == 404) {
        return false;
      }
      
      URL download = new URL(urlString);
      Path path = FileSystems.getDefault().getPath(dest);
      
      try(InputStream in = download.openStream()) {
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch(IOException e) {
      return false;
    }
    
    return true;
  }
  
  /**
   * @param urlString URL of JSON file
   * @return returns String with content of JSON file from <code>urlString</code>
   * @throws IOException if connection with <code>urlString</code> could not be opened
   */
  private static String getJson(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection request = (HttpURLConnection) url.openConnection();
    request.connect();
    
    int status = request.getResponseCode();
    
    BufferedReader br;
    StringBuilder sb;
    String line;
    
    switch(status) {
      case 200:
      case 201:
        br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        sb = new StringBuilder();
        while((line = br.readLine()) != null) {
          sb.append(line).append("\n");
        }
        br.close();
        
        return sb.toString();
      default:
        br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        sb = new StringBuilder();
        while((line = br.readLine()) != null) {
          sb.append(line).append("\n");
        }
        br.close();
        
        return null;
    }
  }
}
