//package googlepoodle.com;
package classes;

import java.util.Scanner;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import redis.clients.jedis.Transaction;

public class SearchEngine {
    
    private TermCounter termCounter;
    private static JedisIndex jedisIndex;

    private static void crawl() throws IOException {
        String source= "https://en.wikipedia.org/wiki/Cat";
        JedisMaker jedisMaker = new JedisMaker();
        Jedis jedis = jedisMaker.make();
        jedisIndex = new JedisIndex(jedis);
        WikiCrawler crawler = new WikiCrawler(source, jedisIndex);
        //crawler.crawl(false);
        String res;
        do {
            res = crawler.crawl(false);
        } while (res != null);

    }
    
    private static int executeSearch(int mode, String query) throws IOException {
        
        
        return 1;
        
    }
    
    
    public static void main(String[] args) throws IOException {
        
        String query;
        String firstTerm;
        String boolTerm;
        String secondTerm;
        boolean twoSearchTerms = false; // default assume there's one word
        int andOrMinus = -1;  // if twoSearchTerms is 1 this signals and/or/minus as 0/1/2
        
        Scanner scanner = new Scanner(System.in).useDelimiter("\\n");
        System.out.println("Welcome to Google Poodle search engine! Please "+
                           "enter one search term or two separated by a boolean operator");
        System.out.println("Ex. \"cat\" or \"cat or dog\" (we support at most two search terms)");
        
        while(true){
            System.out.println("Your query:");
            query = scanner.next();
            String[] parsedQuery = query.split("\\s+");
            
            if(parsedQuery.length > 3){
                System.out.println("Sorry, we currently support at most two search terms");
                continue;
            }
            if(parsedQuery.length == 2){
                System.out.println("Two terms should be separated by a boolean operator");
                continue;
            }
            if(parsedQuery.length == 1){
                twoSearchTerms = false; // redundant for the sake of being explicit
                break;
            }
            if(parsedQuery.length == 3){
                twoSearchTerms = true;
                if(parsedQuery[1].equals("+")  ||
                   parsedQuery[1].equals("&")  ||
                   parsedQuery[1].equals("&&") ||
                   parsedQuery[1].toLowerCase().equals("and") ||
                   parsedQuery[1].toLowerCase().equals("intersection")){
                    andOrMinus = 0; // an "and" query
                } else if(parsedQuery[1].equals("/")  ||
                          parsedQuery[1].equals("|")  ||
                          parsedQuery[1].equals("||") ||
                          parsedQuery[1].toLowerCase().equals("or") ||
                          parsedQuery[1].toLowerCase().equals("union")){
                    andOrMinus = 1; // an "or" query
                } else if(parsedQuery[1].equals("-")  ||
                          parsedQuery[1].equals("~")  ||
                          parsedQuery[1].toLowerCase().equals("not") ||
                          parsedQuery[1].toLowerCase().equals("minus") ||
                          parsedQuery[1].toLowerCase().equals("difference")){
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
        //crawl();
        executeSearch(searchMode, query);
    }
}