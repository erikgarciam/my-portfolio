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
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

    ArrayList<String> commentlist = new ArrayList<String>();
    ArrayList<String> comment = new ArrayList<String>();
  
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String json = new Gson().toJson(comment);
        response.setContentType("application/json;");
        commentlist.add(json);

        for(int i=0; i< commentlist.size(); i++){
            response.getWriter().println(commentlist.get(i));
        }
        
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = getParameter(request, "text-input", "");

        // Break the text into individual words.
        // Name, LastName,Comment
        String[] words = text.split("\\s*,\\s*");
        comment.clear();
        comment.add(words[0]);
        comment.add(words[1]);
        comment.add(words[2]);

    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
  
}
