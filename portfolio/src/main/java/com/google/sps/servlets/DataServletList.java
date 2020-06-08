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
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/list-comments")
public class DataServletList extends HttpServlet{
    private int getnum = -1;

    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getnum = getUserChoice(request);
        response.sendRedirect("/index.html");
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // Query is declared and sorts comments from newest to oldest.
        Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        PreparedQuery results = datastore.prepare(query);
                
        List<Comment> commentlist = new ArrayList<>();
        
        // If user does not define number iterate through for loop
        // until all contents in query are displayed.
        if(getnum == -1){
         for (Entity entity : results.asIterable()) {

            // Retrieve contents from query/datastore
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String lname = (String) entity.getProperty("lname");
            String comment = (String) entity.getProperty("comment");

            // Create class then convert to string. After converting
            // to JSON string add to List to display later.
            Comment commentcontainer = new Comment(name, lname, comment, id);
            commentlist.add(commentcontainer);
         }
        }
        // If user defines number iterate through for loop that many
        // times to display user input value.
        else{
         for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(getnum))) {

            // Retrieve contents from query/datastore
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String lname = (String) entity.getProperty("lname");
            String comment = (String) entity.getProperty("comment");

            // Create class then convert to string. After converting
            // to JSON string add to List to display later.
            Comment commentcontainer = new Comment(name, lname, comment, id);
            commentlist.add(commentcontainer);
         }
        }
        
        Gson gson =new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(commentlist));
    }


    private int getUserChoice(HttpServletRequest request) {
    // Get the input from the form.
    String userChoiceString = request.getParameter("user-choice");

    // Convert the input to an int.
    int userChoice;
    try {
      userChoice = Integer.parseInt(userChoiceString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + userChoiceString);
      return -1;
    }

    // Check that the input is less than or equal to 0.
    if (userChoice <= 0) {
      System.err.println("Player choice is out of range: " + userChoiceString);
      return -1;
    }

    return userChoice;
  }
}