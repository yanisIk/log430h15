package ca.etsmtl.log430.lab3.systemB;

import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * This class is intended to be a filter that will key on a particular state
 * provided at instantiation.  Note that the stream has to be buffered so that
 * it can be checked to see if the specified severity appears on the stream.
 * If this string appears in the input stream, teh whole line is passed to the
 * output stream.
 * 
 * <pre>
 * Pseudo Code:
 *
 * connect to input pipe
 * connect to output pipe
 *
 * while not end of line
 *
 *		read input pipe
 *
 *		if specified severity appears on line of text
 *			write line of text to output pipe
 *			flush pipe
 *		end if
 *
 * end while
 * close pipes
 * </pre>
 *
 * @author ak34270
 * @version 1.0
 */

public class ProgressFilter extends Thread {

	// Declarations
	int requestedProgress;
	String condition;
	boolean done;

	PipedReader inputPipe = new PipedReader();
	PipedWriter outputPipe = new PipedWriter();

	public ProgressFilter( String condition, int value, PipedWriter inputPipe,
			PipedWriter outputPipe) {

		this.requestedProgress = value;
		this.condition = condition;
		
		try {
			
			// Connect inputPipe
			this.inputPipe.connect(inputPipe);
			System.out.println("ProgressFilter :: connected to upstream filter.");

			// Connect outputPipe
			this.outputPipe = outputPipe;
			System.out.println("ProgressFilter :: connected to downstream filter.");

		} catch (Exception Error) {

			System.out.println("ProgressFilter :: Error connecting to other filters.");

		} // try/catch

	} // Constructor

	// This is the method that is called when the thread is started
	public void run() {

		// Declarations

		char[] characterValue = new char[1];
		// char array is required to turn char into a string
		String lineOfText = "";
		// string is required to look for the keyword
		int integerCharacter; 	// the integer value read from the pipe
		int currentProgress;		// progress to filter
		boolean toFilter;		// check if it's necessary to filter

		try {

			done = false;

			while (!done) {

				integerCharacter = inputPipe.read();
				characterValue[0] = (char) integerCharacter;

				if (integerCharacter == -1) { // pipe is closed

					done = true;

				} else {

					if (integerCharacter == '\n') { // end of line

						System.out.println("ProgressFilter:: received: " 
								+ lineOfText + ".");

						// Set variable to initial value
						currentProgress = 0;
						toFilter = true;

						// Check the progress value
						try
						{
							currentProgress = Integer.parseInt(lineOfText.substring(22, 24));
						} 
						catch (Exception e) 
						{
							System.out.println("Error, value not found");
						}

						// Check all the conditions and then compare
						if(condition.equals("==") 		&& currentProgress == requestedProgress)
							toFilter = false;
						else if(condition.equals(">") 	&& currentProgress > requestedProgress)
							toFilter = false;
						else if(condition.equals("<") 	&& currentProgress < requestedProgress)
							toFilter = false;
						
						// If the form is correct, don't filter
						if(!toFilter)
						{
							System.out.println("ProgressFilter:: sending: "
									+ lineOfText + " to output pipe.");
							lineOfText += new String(characterValue);
							outputPipe
									.write(lineOfText, 0, lineOfText.length());
							outputPipe.flush();
						}

						lineOfText = "";

					} else {

						lineOfText += new String(characterValue);

					} // if //

				} // if

			} // while

		} catch (Exception error) {

			System.out.println("ProgressFilter:: Interrupted.");

		} // try/catch

		try {

			inputPipe.close();
			System.out.println("ProgressFilter:: input pipe closed.");

			outputPipe.close();
			System.out.println("ProgressFilter:: output pipe closed.");

		} catch (Exception error) {

			System.out.println("ProgressFilter:: Error closing pipes.");

		} // try/catch

	} // run

} // class