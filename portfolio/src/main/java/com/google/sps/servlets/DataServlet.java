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
    ArrayList<String> templist = new ArrayList<String>();
        
/*
    public DataServlet(){
        commentlist.add("Hello this");
        commentlist.add("is a");
        commentlist.add("test");
    }
*/
  
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    
        //String json = convertToJson(commentlist);
        String json = new Gson().toJson(templist);
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
        templist.clear();
        templist.add(words[0]);
        templist.add(words[1]);
        templist.add(words[2]);

    }

    private String convertToJson(ArrayList<String> wordspass) {
        String json = "{";
        json += "\"Name\": ";
        json += "\"" + wordspass.get(0) + "\"";
        json += ", ";
        json += "\"Last Name\": ";
        json += "\"" + wordspass.get(1) + "\"";
        json += ", ";
        json += "\"Comment\": ";
        json += wordspass.get(2);
        json += "}";
        return json;
    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
  
}
