package googlepoodle;

import java.util.Scanner;

public class SearchEngine {

  private JedisIndex jedisIndex;


  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    System.out.println("Welcome to Google Poodle search engine!");
    System.out.println("What would you like to search?");    
    String query = scanner.next();
    System.out.println("Your query is " + query);
    System.out.println("Which search mode would you like to use? Enter an option 0-4. If you'd like more information about the modes, please enter 'help'. If you don't want to complete this search, enter 'quit'.");
    int validmode=0;
    while(validmode==0) {
      String mode = scanner.next();
      if(mode.equals("help") || mode.equals("h") ||mode.equals("Help") || mode.equals("H")) {
        String mode0="Mode 0: Description to go here";
        String mode1="Mode 1: Description to go here";
        String mode2="Mode 2: Description to go here";
        String mode3="Mode 3: Description to go here";
        String mode4="Mode 4: Description to go here";
        System.out.println(mode0+"\n"+mode1+"\n"+mode2+"\n"+mode3+"\n"+mode4+"\n");
        System.out.println("Now, which mode would you like to search in?");
      } else if(mode.equals("0") || mode.equals("1") || mode.equals("2") || mode.equals("3" )|| mode.equals("4")) {
        int searchMode = Integer.parseInt(mode);
        System.out.println("Great, we're searching in mode "+searchMode+" for your query.");
        validmode=1;
      } else if (mode.equals("quit") || mode.equals("q") ||mode.equals("Quit") || mode.equals("Q")) {
        return;
      } else {
        System.out.println("Please enter a valid mode between 0 and 4.");
      }
    }
    //now we have a searchMode



  }
}