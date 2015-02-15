package com.mycompany.partial_word_count;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mycompany.partial_word_count.WordCountProgram;

import junit.framework.TestCase;


/**
 * Unit test for WordCountProgram
 */
public class WordCountProgramTest extends TestCase
{
	
	private WordCountProgram wordCountProgram = null;
	
	// test fixtures
	private List<String> words = null;
	private Map<String, Integer> wordCounts = null;
	private Map<String, Integer> wordCountsWithoutPartialMatches = null;
	private final String finalResult = "material: 1\nmaybe: 2\nright: 1\n";
	
	private String fileName = "input.txt";
	private String testInputFile = System.getProperty("user.dir") + "/src/test/resources/"
			             				+ fileName;
										
	
    /**
     * Constructor
     *
     * @param testName name of the test case
     */
    public WordCountProgramTest( String testName ){
        super(testName);
    }
    
    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp(){
    	
    	wordCountProgram = new WordCountProgram();
    	
    	words = Arrays.asList(new String[]{
									"A", 
									"mate",
									"material",
									"may",
									"maybe",
									"right",
									"maybe"});
    	
    	wordCounts = new HashMap<String,Integer>()
    			{
					private static final long serialVersionUID = 1L;

				{
		    		put("A", 1);
		    		put("mate",1);
		    		put("material",1);
		    		put("may",1);
		    		put("maybe",2);
		    		put("right",1);
    			}};

    	
    	wordCountsWithoutPartialMatches = new HashMap<String, Integer>()
    			{
					private static final long serialVersionUID = 1L;

				{
    				put("material",1);
    				put("maybe",2);
    				put("right",1);
    			}};
    	
    }
    
    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown(){
    	wordCountProgram = null;
    	words = null;
    	wordCounts = null;
    	wordCountsWithoutPartialMatches = null;
    }
    
    /**
     * Unit test for the process(...) method
     */
    @Test
    public void testProcess(){

    	PrintStream ps = System.out;
    	OutputStream os = new ByteArrayOutputStream();
    	PrintStream ps2 = new PrintStream(os);
    	System.setOut(ps2);
    	
    	String[] programArguments = new String[]{testInputFile};
    	wordCountProgram.process(programArguments);
    	String expectedOutput = finalResult;
    	
    	assertEquals(expectedOutput, os.toString());
    	
    	System.setOut(ps);
    }
    
    /**
     * Unit test for the readInput(...) method
     */
    @Test
    public void testReadInput(){
    	
    	List<String> actualInputList = wordCountProgram.readInput(testInputFile);
    	
    	assertTrue(words.size() == actualInputList.size());
    	
    	for(int i = 0; i < actualInputList.size(); i++){
    		assertEquals(words.get(i), actualInputList.get(i));
    	}

    }
    
    /**
     * Unit test for the countWords(...) method
     */
    @Test
    public void testCountWords(){
    	
    	Map<String, Integer> actual = wordCountProgram.countWords(words);
    	
    	// first make sure the size are the same
    	assertTrue(wordCounts.size() == actual.size());
    	
    	// next ensure each entry is exactly as expected
    	assertEquals(wordCounts.entrySet(), actual.entrySet());

    }
    
    /**
     * Unit test for the removePartialMatches(...) method
     */
    @Test
    public void testRemovePartialMatches(){
    	
    	Map<String, Integer> actual = wordCountProgram.removePartialMatches(words, wordCounts);
    	
    	// first make sure the size are the same
    	assertTrue(wordCountsWithoutPartialMatches.size() == actual.size());
    	
    	// next ensure each entry is exactly as expected
    	assertEquals(wordCountsWithoutPartialMatches.entrySet(), actual.entrySet());
    	
    }
    

    
    /**
     * Unit test for the produceResult(...) method
     */
    @Test
    public void testProduceResults(){

    	String expectedOutput = finalResult;
    	String actualOutput = wordCountProgram.produceResults(wordCountsWithoutPartialMatches);
    	
    	assertEquals(expectedOutput, actualOutput);
    	
    }
}
