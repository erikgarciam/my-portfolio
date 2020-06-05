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


import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
 class Comment{
    private String name;
    private String lastname;
    private String comment;

    public void setName(String name_){name = name_;}
    public void setLName(String lastname_){lastname = lastname_;}
    public void setComment(String comment_){comment = comment_;}
}

@WebServlet("/comments")
public class DataServlet extends HttpServlet {
    Comment commentcontainer = new Comment();
    ArrayList<String> commentlist = new ArrayList<String>();
         
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Gson gson =new Gson();
        String json = gson.toJson(commentcontainer);
        response.setContentType("application/json;");
        if(json.length()> 38){
            commentlist.add(json);
        }
        for(int i=0; i< commentlist.size(); i++){
            response.getWriter().println(commentlist.get(i));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name_input = getParameter(request, "name-input", "");
        String lname_input = getParameter(request, "lname-input", "");
        String comment_input = getParameter(request, "comment-input", "");

        // Name, LastName,Comment
        commentcontainer.setName(name_input);
        commentcontainer.setLName(lname_input);
        commentcontainer.setComment(comment_input);

    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
  
}
