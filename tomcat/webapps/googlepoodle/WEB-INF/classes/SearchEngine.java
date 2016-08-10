//package googlepoodle.com;
package classes;

import java.util.Scanner;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import redis.clients.jedis.Transaction;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SearchEngine {
    
    private TermCounter termCounter;
    private static JedisIndex jedisIndex;
    
    private static void crawl() throws IOException {
        String source= "https://en.wikipedia.org/wiki/Cat";
        WikiCrawler crawler = new WikiCrawler(source, jedisIndex);
        //crawler.crawl(false);
        String res;
        do {
            res = crawler.crawl(false);
        } while (res != null);
    }

    public static int validString(String query) {
        
        String[] parsedQuery = query.split("\\s+");
            
        if(parsedQuery.length > 3){
            System.out.println("Sorry, we currently support at most two search terms");
        }
        if(parsedQuery.length == 2){
            System.out.println("Two terms should be separated by a boolean operator");
        }
        if(parsedQuery.length == 1){
            return 0;
        }
        if(parsedQuery.length == 3){
            if(parsedQuery[1].equals("+")  ||
                parsedQuery[1].equals("&")  ||
                parsedQuery[1].equals("&&") ||
                parsedQuery[1].toLowerCase().equals("and") ||
                parsedQuery[1].toLowerCase().equals("intersection")){
                return 1; // an "and" query
            } else if(parsedQuery[1].equals("/")  ||
                parsedQuery[1].equals("|")  ||
                parsedQuery[1].equals("||") ||
                parsedQuery[1].toLowerCase().equals("or") ||
                parsedQuery[1].toLowerCase().equals("union")){
                return 2; // an "or" query
            } else if(parsedQuery[1].equals("-")  ||
                parsedQuery[1].equals("~")  ||
                parsedQuery[1].toLowerCase().equals("not") ||
                parsedQuery[1].toLowerCase().equals("minus") ||
                parsedQuery[1].toLowerCase().equals("difference")){
                return 3; // a "minus" query
            } else {
                System.out.println("Invalid boolean operator. Try again!");
            }
        }
        return -1;
    }
    
    public static List<Entry<String, Double>> executeSearch(int mode, String query, int parse) throws IOException {
        WikiSearch search;
        initializeJedisIndex();
        int sortMode;
        if( mode == 1 ){
            sortMode = 1;
        } else {
            sortMode = 0;
        }
        
        if(mode == 4){ // Only for poodle mode
            String[] parsedQuery = query.split("\\s+");
            WikiSearch search1 = WikiSearch.search(parsedQuery[0], jedisIndex, mode);
            WikiSearch search2 = WikiSearch.search("poodle", jedisIndex, mode);
            search = search1.and(search2, mode);
        }
        else if(parse!=0) {
            //get the two WikiSearches
            String[] parsedQuery = query.split("\\s+");
            WikiSearch search1 = WikiSearch.search(parsedQuery[0], jedisIndex, mode);
            WikiSearch search2 = WikiSearch.search(parsedQuery[2], jedisIndex, mode);
            if(parse==1) {
                //and query
                search = search1.and(search2, mode);
            } else if(parse==2) {
                //or query
                search = search1.or(search2, mode);
            } else {
                //minus query
                search = search1.minus(search2);
            }
        } else {
            //just one wiki search
            search = WikiSearch.search(query, jedisIndex, mode);
        }
        
        //sort the wikisearch
        List<Entry<String, Double>> sortedResults = search.sort(sortMode);
        for (int i=0; i<sortedResults.size(); i++) {
            Entry<String, Double> entry = sortedResults.get(i);
            System.out.println("Key: "+entry.getKey()+" Value: "+entry.getValue());
        }
       return sortedResults;
    }

    public static void initializeJedisIndex() throws IOException {
        JedisMaker jedisMaker = new JedisMaker();
        Jedis jedis = jedisMaker.make();
        jedisIndex = new JedisIndex(jedis);
        return;
    }
    
    
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in).useDelimiter("\\n");
        
        //Spoken greeting for fun :)
        Runtime rt = Runtime.getRuntime();
        String greeting = "say Welcome to the Google Poodle search engine!";
        Process proc = rt.exec(greeting);
        
        System.out.println("Welcome to Google Poodle search engine! Please "+
                           "enter one search term or two separated by a boolean operator");
        System.out.println("Ex. \"apple\" or \"socrates or banana\" (we support at most two search terms)");
        
        int parsedValue = -1;
        String query="";
        while(parsedValue==-1) {
            System.out.println("Your query:");
            query = scanner.next();
            parsedValue = validString(query);
        }

        System.out.println("Which search mode would you like to use? Enter an option 0-4." +
                    "If you'd like more information about the modes, please enter 'help'." +
                    "If you don't want to complete this search, enter 'quit'.");
        int validmode=0;
        int searchMode=-1;
        while(validmode==0) {
            String mode = scanner.next();
            if(mode.equals("help") || mode.equals("h") ||
               mode.equals("Help") || mode.equals("H")) {
                String mode0="Mode 0: Term Frequency (TF)";
                String mode1="Mode 1: Term Infrequency";
                String mode2="Mode 2: Log-Scaled Term Frequency";
                String mode3="Mode 3: Term Frequency Inverse Document Frequency (TF-IDF)";
                String mode4="Mode 4: Poodle Mode (works best with one search term)";
                System.out.println(mode0+"\n"+mode1+"\n"+mode2+"\n"+mode3+"\n"+mode4+"\n");
                System.out.println("Which mode would you like to search in?");
            } else if(mode.equals("0") || mode.equals("1") || mode.equals("2") ||
                      mode.equals("3" )|| mode.equals("4")) {
                searchMode = Integer.parseInt(mode);
                String searchModeStr;
                switch(searchMode){
                    case 0:  searchModeStr = "TF";
                        break;
                    case 1:  searchModeStr = "Term Infrequency";
                        break;
                    case 2: searchModeStr = "Log TF";
                        break;
                    case 3: searchModeStr = "TF-IDF";
                        break;
                    default: searchModeStr = "Poodle mode";
                        break;
                }
                System.out.println("Searching for \""+query+"\" using "+searchModeStr);
                validmode=1;
            } else if (mode.equals("quit") || mode.equals("q") ||
                       mode.equals("Quit") || mode.equals("Q")) {
                return;
            } else {
                System.out.println("Please enter a valid mode between 0 and 4.");
            }
        }
        //now we have a searchMode and query
        //crawl();
        executeSearch(searchMode, query, parsedValue);
    }
}