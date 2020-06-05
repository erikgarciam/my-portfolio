// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
 class Comment{
    private String name;
    private String lastname;
    private String comment;

    Comment(String n, String ln, String c){
        name = n;
        lastname = ln;
        comment = c; 
    }

    public void setName(String name_){name = name_;}
    public void setLName(String lastname_){lastname = lastname_;}
    public void setComment(String comment_){comment = comment_;}
}

@WebServlet("/comments")
public class DataServlet extends HttpServlet {
         
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // Query is declared and sorts comments from newest to oldest.
        Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        
        List<String> commentlist = new ArrayList<>();

        Gson gson =new Gson();
        for (Entity entity : results.asIterable()) {

            // Retrieve contents from query/datastore
            String name = (String) entity.getProperty("name");
            String lname = (String) entity.getProperty("lname");
            String comment = (String) entity.getProperty("comment");

            // Create class then convert to string. After converting
            // to JSON string add to List to display later.
            Comment commentcontainer = new Comment(name, lname, comment);
            String json = gson.toJson(commentcontainer);
            commentlist.add(json);
        }

        response.setContentType("application/json;");

        // Display the comments as vertical rows
        for(int i=0; i< commentlist.size(); i++){
            response.getWriter().println(commentlist.get(i));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name_input = getParameter(request, "name-input", "");
        String lname_input = getParameter(request, "lname-input", "");
        String comment_input = getParameter(request, "comment-input", "");
        long timestamp = System.currentTimeMillis();

        // Name, LastName,Comment
        // Datastoring the Data.
        Entity taskEntity = new Entity("Task");                                        
        taskEntity.setProperty("name", name_input);
        taskEntity.setProperty("lname", lname_input);
        taskEntity.setProperty("comment", comment_input);
        taskEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);

        response.sendRedirect("/index.html");
    }

    // Check parameters to see if they are null.
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
