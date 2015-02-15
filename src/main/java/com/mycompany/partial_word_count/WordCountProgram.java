package com.mycompany.partial_word_count;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Word Count Program
 * 
 * <p>
 * This word count program is unlike the traditional word count program where this
 * ignore words that are partial matches of longer words.
 * 
 * <p>
 * For e.g.
 * 
 * <p>
 * "A mate material may maybe right maybe"
 * 
 * <p>
 * The words 'A', 'mate', and 'may' will be discarded as they contain in the other
 * longer words.
 *
 * <p>
 * This program takes in an input file and outputs the word counts with the words sorted
 * by the longest word in descending order.
 * 
 * @author colin
 *
 */
public class WordCountProgram 
{
	
	private static final String NEW_LINE_SEPARATER = "\n";
	private static Logger log = null;
	
	static {
		
		log = Logger.getLogger(WordCountProgram.class);
		
		configureLogging();
	}
	
	/**
	 * Main
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args){
		
		WordCountProgram wordCountProgram = new WordCountProgram();
		wordCountProgram.process(args);
		
	}
    
	/**
	 * 
	 * <p>
	 * This method takes in a list of arguments (should only be 1) of String array
	 * which the 1st argument will be the path to the input file. There is no checking
	 * on the type of file it takes in but only 1 argument is allowed.
	 * 
	 * <p>
	 * The internal logic for this programs is as follows:
	 * <ol>
	 * <li>Reads in input from a file
	 * <li>count the times the words appear
	 * <li>removes the partial matches to longer words
	 * <li>Writes the result to console (sorts by longest words in descending order)
	 * </ol>
	 * 
	 * @param args arguments supplied from command line
	 */
	public void process(String[] args){
		
		log.info("Starting processing");
		
		if(args.length != 1){
			log.error("Invalid number of arguments supplied");
			
			printConsoleErrorMessage();
			
			printUsage();
			return;
		}
		
		// read in the words from the file into a list
		List<String> words = readInput(args[0]);
		
		// count up how many times the words appear in the sentence
		Map<String, Integer> wordCounts = countWords(words);
		
		// discards the words that matches partially with longer words
		Map<String, Integer> wordsWithoutPartials = removePartialMatches(words, wordCounts);
		
		// display the results sorted by longest words in descending order
		String result = produceResults(wordsWithoutPartials);
		log.info(result);
		System.out.print(result);
		
		log.info("Finished processing word counts");
		
	}
	
	private static void printConsoleErrorMessage(){
		System.out.println("There were some errors - please see wordcountprogram.log for more "
				+ "information");
	}
	
	/*
	 * Sets up logging
	 */
	private static void configureLogging(){
		PropertyConfigurator.configure("config/logging.properties");
	}
	
	/*
	 * A helper utility method to print out the usage for this program
	 */
	private static void printUsage(){
		StringBuilder sb = new StringBuilder(NEW_LINE_SEPARATER);
		sb.append("Usage:"+NEW_LINE_SEPARATER);
		sb.append("java WordCountProgram <input file path>"+NEW_LINE_SEPARATER);
		sb.append("----------------------------------------"+NEW_LINE_SEPARATER);
		sb.append(NEW_LINE_SEPARATER);
		sb.append("where " + NEW_LINE_SEPARATER);
		sb.append("input file path is the path to the input file");
		System.out.println(sb.toString());
	}
	
	/**
	 * Reads in the input from the given input file and dumps the words 
	 * into a list
	 * 
	 * @param inputFile the input file
	 * @return a list of all the words in the file
	 */
	List<String> readInput(String inputFile){
		
		List<String> words = new ArrayList<>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(inputFile))){
			
			String line = null;
			
			while((line = reader.readLine()) != null){
				for(String word : line.split(" ")){
					
					log.trace("Adding " + word + " to list of words parsed");
					
					words.add(word);
				}
			}
			
		} 
		catch (FileNotFoundException e) {
			printConsoleErrorMessage();
			log.error(e.getMessage() 
									+ NEW_LINE_SEPARATER 
									+"please check the path to the above file", e);
			System.exit(-1);
		} 
		catch (IOException e) {
			printConsoleErrorMessage();
			log.error("Unexpected IO error occurred" 
									+ NEW_LINE_SEPARATER + e.getMessage(), e);
			System.exit(-1);
		}
		catch (Exception e){
			printConsoleErrorMessage();
			log.error("Unknown exception occurred" + NEW_LINE_SEPARATER , e);	
			System.exit(-1);
		}
		
		return words;
	}
				
	
	
	/**
	 * Takes in a list of words and counts the number of times the 
	 * word has appeared. The results are returned in a stored mapping of the
	 * word-count:
	 * 
	 * <p>
	 * [word, count]
	 * 
	 * @param words the list of words to count
	 * @return a map of the words by count
	 */
	Map<String, Integer> countWords(List<String> words){
		
		Map<String, Integer> wordCounts = new HashMap<>();
		
		for(String word : words){
			
			Integer count = wordCounts.get(word);
			
			// add in word to map if we haven't encountered this word before
			if(count == null){
				wordCounts.put(word, 1);
				
				log.debug("Put new encountered word: " + word 
								+ " into internal mapping of [word,count]");
			}
			else{ // otherwise, update the count of the word
				wordCounts.put(word, ++count);
				
				log.debug("Have seen this word: " + word 
									+ " before - updating count to " + count);
			}
			
		}
		
		return wordCounts;
	}
	
	/**
	 * This method performs the discarding of words which partial matches the longer 
	 * words
	 * 
	 * @param words the list of words
	 * 
	 * @param wordCounts the mappings of words to its count
	 * 
	 * @return a new modified mappings of words containing only the words that doesn't have
	 *         a partial match
	 */
	Map<String, Integer> removePartialMatches(List<String> words, Map<String, Integer> wordCounts){
		
		for(int i = 0; i < words.size(); i++){
			
			for(int j = 0; j < words.size(); j++){
				
				String wordToCompare = words.get(j).toLowerCase();
				String wordToCompareAgainst = words.get(i).toLowerCase();
				
				if(wordToCompareAgainst.equals(wordToCompare)){
					continue;
				}
				
				if(wordToCompareAgainst.contains(wordToCompare)){
					// need to remove this
					
					log.debug("Removing word " + words.get(j) + " from map because this word " 
							+ "has partial match");
					
					wordCounts.remove(words.get(j));
					
					
				}
			}
			
		}
		
		return wordCounts;
	}

	
	/**
	 * Takes in a mappings of the word counts and produces a String representation of
	 * the results where the words are sorted by longest length in descending order. Each word
	 * is in a new line. Where words are equal in length, it is order by alphabetically.
	 * 
	 * @param wordCounts the mapping of the words with its counts
	 * 
	 * @return a String representation of the results in the format of <br>
	 *         "word: [count]"<br>
	 *         "word: [count]"<br>
	 *         etc...
	 */
	String produceResults(Map<String, Integer> wordCounts){
		
		TreeMap<String, Integer> results = new TreeMap<>(
				new Comparator<String>() {

						/*
						 * (non-Javadoc)
						 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
						 */
						@Override
						public int compare(String s1, String s2) {
							
							if(s1.length() < s2.length()){
								return 1;
							}
							else if(s1.length() > s2.length()){
								return -1;
							}
							else{
								return s1.compareTo(s2);
							}
							
						}
						
				});
		
		// sorts the words in descending order by its length
		results.putAll(wordCounts);
		log.trace("Sorting results in order of longest word descending");
		
		StringBuilder sb = new StringBuilder();
		
		for(Map.Entry<String, Integer> entry : results.entrySet()){
			sb.append(entry.getKey() + ": " + entry.getValue() + NEW_LINE_SEPARATER);
		}
		
		return sb.toString();
		
		
	}
	
}
