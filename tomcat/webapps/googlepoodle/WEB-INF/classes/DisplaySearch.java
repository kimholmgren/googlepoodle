//package classes;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import classes.SearchEngine;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

 
public class DisplaySearch extends HttpServlet {

   public String css = "<style>body {background:#add8e6}\n .text {color:navy; text-align:center; font-family:Verdana;}</style>";


   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
 
      // Set the response MIME type of the response message
      response.setContentType("text/html");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();

      //get query and search mode
      String query = request.getParameter("query");
      String modeStr = request.getParameter("searchMode");
      int mode;
      if(modeStr==null) { mode=1; } else { mode=Integer.parseInt(modeStr); }
      int parse = SearchEngine.validString(query);
      //int type=0;
      if(parse==-1) {
         String errorMessage = "We currently support at most two terms, separated by a boolean operator. Please see the <a href=\"http://localhost:9999/googlepoodle/help.html\">help</a> page if you're having trouble.";
         createErrorPage(errorMessage, out); 
         return;   
      }
      List<Entry<String, Double>> results = SearchEngine.executeSearch(mode, query, parse);
 
      // Write the response message, in an HTML page
      try {
         out.println("<html>");
         out.println(css+"h3 {text-shadow: -1.5px 0 navy, 0 1.5px navy, 1.5px 0 navy, 0 -1.5px navy}");
         out.println("<head><title>Search Results</title></head>");


         out.println("<body>");
         for(int i=0; i<results.size(); i++) {
            Entry<String, Double> entry = results.get(i);
            out.println("<p>"+(i+1)+". "+entry.getKey());
         }

         out.println("</body></html>");
      } finally {
         out.close();  // Always close the output writer
      }
   }

 public void createErrorPage(String errorMessage, PrintWriter out) throws IOException, ServletException {
      try {
         out.println("<html>");
         out.println(css);
         out.println("<head><title>Error!</title></head>");
         out.println("<body>");
         out.println("<p class=\"text\" style=\"font-size:60px;position:absolute;top:20%;\">"+errorMessage+"</p>");
         out.println("<a href=\"http://localhost:9999/googlepoodle/\" class=\"text\">Try again</a>");
         out.println("</body></html>");
      } finally {
         out.close();
      }

 }



}