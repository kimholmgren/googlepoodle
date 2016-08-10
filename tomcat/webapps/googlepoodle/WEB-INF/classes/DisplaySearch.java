//package classes;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import classes.SearchEngine;
import classes.WikiFetcher;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

 
public class DisplaySearch extends HttpServlet {

   public String css = "<style>body {background:white;margin:0px;}\n .text {color:#1C4587; text-align:center; font-family:Verdana;} h1 {color:#1C4587; text-align:center;}</style>";


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
         out.println(css);

         //css for header poodle thing
         out.println("<style>");
         out.println("p {margin:0px;font-family:Helvetica;}");
         out.println("a { color:#1C4587;text-decoration:none;font-size:24px;font-family:Helvetica; }");
         out.println("a:hover { color:green; }");
         out.println(".urlText { color:#FFAB40;font-size:13px; }");
         out.println(".header img { float:left; width:5%; }");
         out.println(".header h1 { position:relative; top:18px; left:10px; }");
         out.println(".img-hor {"+
       " -moz-transform: scaleX(-1);"+
        "-o-transform: scaleX(-1);"+
       " -webkit-transform: scaleX(-1);"+
       " transform: scaleX(-1);"+
       " filter: FlipH;"+
       " -ms-filter: \"FlipH\";"+
         "}");

         //css for search results
         out.println(".result { margin-left:4%;margin-right:4%; }");

         out.println("</style>");

         out.println("<head><title>Search Results</title></head>");
         out.println("<body>");

         //make the header with the poodle
         out.println("<div style=\"background-color:#fbf3d1;height:15%;\" class=\"header\">");
         out.println("<img src=\"poodle.png\" alt=\"logo\" style=\"position:absolute;left:100px;top:15px;\" />");
         out.println("<h1>Search Results for \""+query+"\"</h1>");
         out.println("<img class=\"img-hor\" src=\"poodle.png\" alt=\"logo\" style=\"position:absolute;right:100px;top:15px;\" />");
         out.println("</div><br>");


     
         for(int i=0; i<results.size(); i++) {
            Entry<String, Double> entry = results.get(i);
            String url = entry.getKey();
            String[] info = WikiFetcher.findTitleAndFirstSentence(url);
            out.println("<div class=\"result\">");
            out.println("<a href=\""+url+"\">"+info[0]+"</a>");
            out.println("<p class=\"urlText\">"+url+"</p>");
            out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+info[1]+"</p><br>");
            out.println("</div>");
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