package googlepoodle.com;

import java.util.Scanner;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import redis.clients.jedis.Transaction;

public class SearchEngine {

  private TermCounter termCounter;

  private static int executeSearch(int mode, String query) throws IOException {

    //initialize search
    String source = "";
    JedisMaker jedisMaker = new JedisMaker();
    Jedis jedis = jedisMaker.make();
    JedisIndex jedisIndex = new JedisIndex(jedis);
    WikiCrawler crawler = new WikiCrawler(source, jedisIndex);

    return 1;

  }


  public static void main(String[] args) throws IOException {

    String query;
    String[] parsedQuery;
    boolean twoSearchTerms = false; // default assume there's one word
    int andOrMinus = -1;        // if twoSearchTerms is 1 this signals and/or/minus as 0/1/2
      
    Scanner scanner = new Scanner(System.in);
    System.out.println("Welcome to Google Poodle search engine!");
    System.out.println("Please enter one search term or two separated by a boolean operator");
    System.out.println("Ex. \"cat\" or \"cat or dog\" (we support at most two search terms)");
    
    while(true){
    System.out.println("Your query:");
    query = scanner.next();
      
        //String[] parsedQuery = query.split(" "); // THIS DOES NOT WORK YET, will
        for (String retval: query.split("\\s+")){
            System.out.println(retval);
            parsedQuery.add(retval);
        }
        
        
        if(parsedQuery.length > 3){
            System.out.println("Sorry, currently Poodle supports at most two search terms");
            continue;
        }
        if(parsedQuery.length == 1){
            break;
        }
        if(parsedQuery.length == 3){
            twoSearchTerms = true;
            if(parsedQuery[2].equals("+")  ||
               parsedQuery[2].equals("&")  ||
               parsedQuery[2].equals("&&") ||
               parsedQuery[2].toLowerCase().equals("and") ||
               parsedQuery[2].toLowerCase().equals("intersection")){
                andOrMinus = 0; // an "and" query
                System.out.println("Reached and block");
            } else if(parsedQuery[2].equals("/")  ||
                      parsedQuery[2].equals("|")  ||
                      parsedQuery[2].equals("||") ||
                      parsedQuery[2].toLowerCase().equals("or") ||
                      parsedQuery[2].toLowerCase().equals("union")){
                andOrMinus = 1; // an "or" query
            } else if(parsedQuery[2].equals("-")  ||
                      parsedQuery[2].equals("~")  ||
                      parsedQuery[2].toLowerCase().equals("not") ||
                      parsedQuery[2].toLowerCase().equals("minus") ||
                      parsedQuery[2].toLowerCase().equals("difference")){
                andOrMinus = 2; // a "minus" query
            } else {
                System.out.println("Invalid boolean operator. Try again!");
                continue;
            }
            break;
        }
    }
      
    System.out.println("Which search mode would you like to use? Enter an option 0-4.");
    System.out.println("If you'd like more information about the modes, please enter 'help'.");
    System.out.println("If you don't want to complete this search, enter 'quit'.");
    int validmode=0;
    int searchMode=-1;
    while(validmode==0) {
      String mode = scanner.next();
      if(mode.equals("help") || mode.equals("h") ||mode.equals("Help") || mode.equals("H")) {
        String mode0="Mode 0: Term Frequency (TF)";
        String mode1="Mode 1: Description to go here";
        String mode2="Mode 2: Description to go here";
        String mode3="Mode 3: Description to go here";
        String mode4="Mode 4: Description to go here";
        System.out.println(mode0+"\n"+mode1+"\n"+mode2+"\n"+mode3+"\n"+mode4+"\n");
        System.out.println("Now, which mode would you like to search in?");
      } else if(mode.equals("0") || mode.equals("1") || mode.equals("2") || mode.equals("3" )|| mode.equals("4")) {
        searchMode = Integer.parseInt(mode);
        System.out.println("Great, we're searching in mode "+searchMode+" for \""+query+"\"");
        validmode=1;
      } else if (mode.equals("quit") || mode.equals("q") ||mode.equals("Quit") || mode.equals("Q")) {
        return;
      } else {
        System.out.println("Please enter a valid mode between 0 and 4.");
      }
    }
    //now we have a searchMode and query

    executeSearch(searchMode, query);
  }
}