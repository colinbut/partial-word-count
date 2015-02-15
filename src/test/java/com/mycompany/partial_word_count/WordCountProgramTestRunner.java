/**
 * 
 */
package com.mycompany.partial_word_count;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * A test runner class for running the unit tests
 * 
 * @author colin
 *
 */
public class WordCountProgramTestRunner {

	/**
	 * Runs the unit tests
	 * 
	 * @param args test runner arguments
	 */
	public static void main(String[] args) {
		
		System.out.println("Started executing Unit Tests");
		System.out.println("Unit Test Summary Report");
		
		Result result = JUnitCore.runClasses(WordCountProgramTest.class);
		for(Failure failure : result.getFailures()){
			System.out.println(failure.getDescription());
		}
		
		
		System.out.format("Number of tests run: %d%n", result.getRunCount());
		System.out.format("Number of failures: %d%n", result.getFailureCount());
		System.out.format("Number of ignored tests: %d%n", result.getIgnoreCount());
		System.out.format("Time to run all tests: %dms%n", result.getRunTime());
		
		System.out.println(result.wasSuccessful()  
								? "TestCase was a success - all tests passed"
								: "TestCase contains failures");
		
		System.out.println("Finished executing Unit Tests");
		
		
	}

}
