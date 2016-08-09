//package googlepoodle.com;
package classes;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, Double> map;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Double> map) {
		this.map = map;
	}
	
	/**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Double getRelevance(String url) {
		Double relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private  void print(int mode) {
		List<Entry<String, Double>> entries = sort(mode);
		for (Entry<String, Double> entry: entries) {
			System.out.println(entry);
		}
	}

	// for print without argument

	private void print() {
		print(0);
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that, int mode) {
		Map<String, Double> union = new HashMap<String, Double>(map);
		for (String term: that.map.keySet()) {
			double relevance = totalRelevance(this.getRelevance(term), that.getRelevance(term), mode);
			union.put(term, relevance);
		}
		return new WikiSearch(union);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that, int mode) {
		Map<String, Double> intersection = new HashMap<String, Double>();
		for (String term: map.keySet()) {
			if (that.map.containsKey(term)) {
				double relevance = totalRelevance(this.map.get(term), that.map.get(term), mode);
				intersection.put(term, relevance);
			}
		}
		return new WikiSearch(intersection);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		Map<String, Double> difference = new HashMap<String, Double>(map);
		for (String term: that.map.keySet()) {
			difference.remove(term);
		}
		return new WikiSearch(difference);
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected double totalRelevance(Double rel1, Double rel2, int mode) {
		// simple starting place: relevance is the sum of the term frequencies.
        if(mode == 0 || mode == 1 || mode == 4){
            return (double)rel1 + rel2;
        }
        // if relevance is log scaled, totalrelevance is the log of e^rel1 + e^rel2
        return (Math.log(Math.exp(rel1) + Math.exp(rel2)));
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Double>> sort(int sortMode) {
        
		// make a list of entries
		List<Entry<String, Double>> entries =
				new LinkedList<Entry<String, Double>>(map.entrySet());
		
		// make a Comparator object for sorting
		Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
              if(sortMode==0) { // descending order
                  return e2.getValue().compareTo(e1.getValue());
              } else if(sortMode==1) { // ascending order
                  return e1.getValue().compareTo(e2.getValue());
              } else if(sortMode==2) { // no order
                  return 0;
              } else {
              	//this should throw an exception but for now ill do random sortMode
              	return (int) Math.round(Math.random() * 255);
              }
            }
        };
        
        // sort and return the entries
		Collections.sort(entries, comparator);
		return entries;
	}


	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index, int mode) {
		Map<String, Double> map = index.getCounts(term, mode);
		return new WikiSearch(map);
	}

	public static void main(String[] args) throws IOException {
		
		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		
		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index, 0);
		search1.print();
		
		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index, 0);
		search2.print();
		
		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2, 0);
		intersection.print();
	}
}
