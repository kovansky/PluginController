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
import java.util.List;

public class SpigetClient {
  private static final String spigetURL = "https://api.spiget.org/v2/";
  
  public class ListItem {
    public int id;
    public String name;
    public String tag;
    public String contributors;
    public String description;
    public int likes;
    public boolean premium;
    
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
  
  public static ListItem[] listItems() {
    return listItems(20, 0);
  }
  
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
  
  public static ArrayList<ListItem> search(String query) {
    return search(query, 10, 0);
  }
  
  public static ArrayList<ListItem> search(String query, int size, int page) {
    return search(query, size, page, false);
  }
  
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
    return false;
  }
  
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
