/**
 * This program goes through a collection of book records using three main methods.
 * In part 1, it reads from multiple text files and checks each file's records for syntax errors.
 * If there is an error, it writes the record into a syntax error file. If not, it writes it to a corresponding genre file.
 * In part 2, it reads from those genre files and checks each record for semantic errors.
 * If there is an error, it writes the record into a semantic error file. If not, it turns it into a serializable book object.
 * Those objects are then written into corresponding genre binary files.
 * In part 3, it reads from those binary files and turns each record back into a book object.
 * Has an interactive program that allows the user to browse through each file and its records, specifying a range.
 */

package a2_OceaneDaumasson_40275138;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.ObjectInputStream;

/**
 * Driver for the assignment 
 */
public class Driver {

	/**
	 * Main method, executes part 1, part 2, part 3
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			do_part1();
			do_part2();
			do_part3();
	}
	
	/**
	 * Part 1 - Reads input file, containing amount of files and list of file names by year 
	 * Reads each file, separating book records by line and each record's fields by commas
	 * Checks records for exceptions and writes invalid record to syntaxError file
	 * Writes valid records to genre specific output file
	 */
	public static void do_part1() {
		BufferedReader fileListReader = null;

		PrintWriter ccbWriter = null;
		PrintWriter hcbWriter = null;
		PrintWriter mtvWriter = null;
		PrintWriter mrbWriter = null;
		PrintWriter nebWriter = null;
		PrintWriter otrWriter = null;
		PrintWriter ssmWriter = null;
		PrintWriter tpaWriter = null;
		    
		PrintWriter syntaxErrorWriter = null;

		try {
			fileListReader = new BufferedReader(new FileReader("part1_input_file_names.txt"));

			// Open all output files			
			ccbWriter = new PrintWriter(new FileWriter("Cartoons_Comics.csv"));
			hcbWriter = new PrintWriter(new FileWriter("Hobbies_Collectibles.csv"));
			mtvWriter = new PrintWriter(new FileWriter("Movies_TV_Books.csv"));
			mrbWriter = new PrintWriter(new FileWriter("Music_Radio_Books.csv"));
			nebWriter = new PrintWriter(new FileWriter("Nostalgia_Eclectic_Books.csv"));
			otrWriter = new PrintWriter(new FileWriter("Old_Time_Radio_Books.csv"));
			ssmWriter = new PrintWriter(new FileWriter("Sports_Sports_Memorabilia.csv"));
			tpaWriter = new PrintWriter(new FileWriter("Trains_Planes_Automobiles.csv"));
			
			syntaxErrorWriter = new PrintWriter(new FileWriter("syntax_error_file.txt"));
					
			PrintWriter[] writers = {ccbWriter, hcbWriter, mtvWriter, mrbWriter, nebWriter, otrWriter, ssmWriter, tpaWriter};
			
			// Get number of files from first line of input
			int numOfFiles = Integer.parseInt(fileListReader.readLine());
			for (int i=0; i<numOfFiles; i++) {
				String fileName = fileListReader.readLine();
				
				BufferedReader recordReader = null;
				try {
					recordReader = new BufferedReader(new FileReader(fileName));
					String record = recordReader.readLine();
			
					while (record != null) {
						try {
							String[] fields = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

							if (fields.length > 6)
								throw new TooManyFieldsException("Too many fields");
							if (fields.length < 6)
								throw new TooFewFieldsException("Too few fields");
							
							String[] possibleFields = {"title", "author", "price", "isbn", "genre", "year"};
							for (int j=0; j<fields.length; j++) {
								if (fields[j].trim().isEmpty())
									throw new MissingFieldException("Missing field (" + possibleFields[j] + ")");
							}
							
							// Store all book fields separately
							String title = fields[0];
							String author = fields[1];
							String price = fields[2];
							String isbn = fields[3];
							String genre = fields[4];
							String year = fields[5];
							
							String[] possibleGenres = {"CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA"};
							Boolean knownGenre = false;
							for (int j=0; j<possibleGenres.length; j++)
								if (genre.equals(possibleGenres[j])) {
									knownGenre = true;
									break;
								}
					
							if (!knownGenre) 
								throw new UnknownGenreException("Unknown Genre");
							
							switch (genre) {
								case "CCB":
									ccbWriter.println(record);
									break;
								case "HCB":
									hcbWriter.println(record);
									break;
								case "MTV":
									mtvWriter.println(record);
									break;
								case "MRB":
									mrbWriter.println(record);
									break;
								case "NEB":
									nebWriter.println(record);
									break;
								case "OTR":
									otrWriter.println(record);
									break;
								case "SSM":
									ssmWriter.println(record);
									break;
								case "TPA":
									tpaWriter.println(record);
									break;
							}
					
						} catch (TooManyFieldsException | TooFewFieldsException |
						         MissingFieldException | UnknownGenreException e) {
						    
						    syntaxErrorWriter.println("Syntax error in file: " + fileName);
						    syntaxErrorWriter.println("=====================");
						    syntaxErrorWriter.println("Error: " + e.getMessage());
						    syntaxErrorWriter.println("Record: " + record);
						    syntaxErrorWriter.println();
						}
					    record = recordReader.readLine();
					}
					recordReader.close();

				} catch (IOException e) {
					System.out.println("Error reading " + fileName);
				}
			}
			// Close all files
			for (int i=0; i<writers.length; i++)
				if (writers[i] != null)
					writers[i].close();
			if (fileListReader != null)
				fileListReader.close();
			if (syntaxErrorWriter != null)
				syntaxErrorWriter.close();
			
		} catch (IOException e) {
			System.out.println("Error opening input list file or output files for part 1.");
		}
	}
	
	/**
	 * Part 2 - Reads genre files from part 1, check for semantic errors in each line
	 * For valid records, turns them into Book objects, serializes them and writes to genre specific object output files (binary)
	 * For invalid records, adds them to semanticError file
	 */
	public static void do_part2() {
		BufferedReader ccbReader = null;
		BufferedReader hcbReader = null;
		BufferedReader mtvReader = null;
		BufferedReader mrbReader = null;
		BufferedReader nebReader = null;
		BufferedReader otrReader = null;
		BufferedReader ssmReader = null;
		BufferedReader tpaReader = null;
			
		PrintWriter semanticErrorWriter = null;

		ObjectOutputStream ccbWriter = null;
		ObjectOutputStream hcbWriter = null;
		ObjectOutputStream mtvWriter = null;
		ObjectOutputStream mrbWriter = null;
		ObjectOutputStream nebWriter = null;
		ObjectOutputStream otrWriter = null;
		ObjectOutputStream ssmWriter = null;
		ObjectOutputStream tpaWriter = null;
		
		try {
			// Open all input files
			ccbReader = new BufferedReader(new FileReader("Cartoons_Comics.csv"));
			hcbReader = new BufferedReader(new FileReader("Hobbies_Collectibles.csv"));	
			mtvReader = new BufferedReader(new FileReader("Movies_TV_Books.csv"));	
			mrbReader = new BufferedReader(new FileReader("Music_Radio_Books.csv"));	
			nebReader = new BufferedReader(new FileReader("Nostalgia_Eclectic_Books.csv"));	
			otrReader = new BufferedReader(new FileReader("Old_Time_Radio_Books.csv"));	
			ssmReader = new BufferedReader(new FileReader("Sports_Sports_Memorabilia.csv"));	
			tpaReader = new BufferedReader(new FileReader("Trains_Planes_Automobiles.csv"));
						
			// Open all output files
			semanticErrorWriter = new PrintWriter(new FileWriter("semantic_error_file.txt"));
			
			ccbWriter = new ObjectOutputStream(new FileOutputStream("Cartoons_Comics.csv.ser"));
			hcbWriter = new ObjectOutputStream(new FileOutputStream("Hobbies_Collectibles.csv.ser"));
			mtvWriter = new ObjectOutputStream(new FileOutputStream("Movies_TV_Books.csv.ser"));
			mrbWriter = new ObjectOutputStream(new FileOutputStream("Music_Radio_Books.csv.ser"));
			nebWriter = new ObjectOutputStream(new FileOutputStream("Nostalgia_Eclectic_Books.csv.ser"));
			otrWriter = new ObjectOutputStream(new FileOutputStream("Old_Time_Radio_Books.csv.ser"));
			ssmWriter = new ObjectOutputStream(new FileOutputStream("Sports_Sports_Memorabilia.csv.ser"));
			tpaWriter = new ObjectOutputStream(new FileOutputStream("Trains_Planes_Automobiles.csv.ser"));
			
			BufferedReader[] readers = {ccbReader, hcbReader, mtvReader, mrbReader, nebReader, otrReader, ssmReader, tpaReader};
			ObjectOutputStream[] writers = {ccbWriter, hcbWriter, mtvWriter, mrbWriter, nebWriter, otrWriter, ssmWriter, tpaWriter};
			String[] fileNames = {"Cartoons_Comics.csv", 
					"Hobbies_Collectibles.csv", 
					"Movies_TV_Books.csv", 
					"Music_Radio_Books.csv", 
					"Nostalgia_Eclectic_Books.csv",
					"Old_Time_Radio_Books.csv",
					"Sports_Sports_Memorabilia.csv",
					"Trains_Planes_Automobiles.csv"};
			
			int[] validRecordCounter = new int[readers.length];
			
			for (int i=0; i<readers.length; i++) {
				String record =readers[i].readLine();

				while (record != null )	{
					try {
						Book book = checkValidity(record);
						writers[i].writeObject(book);
						validRecordCounter[i]++;
						
					} catch (BadIsbn10Exception | BadIsbn13Exception| BadPriceException | BadYearException e) {
						semanticErrorWriter.println("Semantic error in file: " + fileNames[i]);
						semanticErrorWriter.println("=====================");
						semanticErrorWriter.println("Error: " + e.getMessage());
						semanticErrorWriter.println("Record: " + record);
						semanticErrorWriter.println();
					}
					record =readers[i].readLine();
 				}
			}
			semanticErrorWriter.close();
			for (int i=0; i<readers.length; i++) {
				readers[i].close();
			}
			for (int i=0; i<writers.length; i++) {
				writers[i].close();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Error: Input files not found.");
		} catch (IOException e) {
			System.out.println("Error opening input files or output files for part 2.");
		} 
	}
	
	/**
	 * Method for part 2, checks for each record's semantic validity, throws exceptions for invalid price, year or ISBN
	 * 
	 * @param record each line taken from do_part2
	 * @return returns valid line as a book object
	 * @throws BadPriceException if price is negative
	 * @throws BadYearException if year is not between 1995 and 2010
	 * @throws BadIsbn10Exception if ISBN10 is not of the correct form or a multiple of 11 summed
	 * @throws BadIsbn13Exception if ISBN13 is not of the correct form or a multiple of 10 summed
	 */
	public static Book checkValidity(String record) throws BadPriceException, BadYearException, BadIsbn10Exception, BadIsbn13Exception{
		String[] fields = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

		String title = fields[0];
		String author = fields[1];
		double price = Double.parseDouble(fields[2]);
		String isbn = fields[3];
		String genre = fields[4];
		int year = Integer.parseInt(fields[5]);
		
		// Check Price Validity
		if (price<0)
			throw new BadPriceException("Invalid price: " + price);
		
		// Check Year Validity
		if (year<1995 || year>2010)
			throw new BadYearException("Invalid year: " + year);
		
		// Check ISBN-10 Validity
		if (isbn.length()==10) {
			int sum = 0;
			for (int i=0; i<10; i++) {
				char c = isbn.charAt(i);
				int num;
				if (c == 'X')
					num = 10;
				else
					num = Character.getNumericValue(c);
				sum += (10-i) * num;
			}
			if (sum % 11 != 0)
				throw new BadIsbn10Exception("Invalid ISBN-10: " + isbn);
		}
	
		// Check ISBN-13 Validity
		if (isbn.length()==13) {
			int sum = 0;
			for (int i=0; i<13; i++) {
				char c = isbn.charAt(i);
				int num = Character.getNumericValue(c);
				
				if (i % 2 == 0 )
					sum += 1*num;
				else
					sum += 3*num;	
			}
			if ((sum % 10) != 0)
				throw new BadIsbn13Exception("Invalid ISBN-13: " + isbn);
		}
		
		// Creates book object for each valid book record
		return new Book(title, author, price, isbn, genre, year);
	}
	
	/** 
	 * Part 3 - Reads objects in binary files from part 2, deserializes them
	 * Interactive program for user to select file and navigate through record objects
	 * Allow user to exit program
	 */
	public static void do_part3() {
		Scanner keyIn = new Scanner(System.in);
		String[] fileNames = {"Cartoons_Comics.csv.ser", 
							"Hobbies_Collectibles.csv.ser",
							"Movies_TV_Books.csv.ser",
							"Music_Radio_Books.csv.ser",
							"Nostalgia_Eclectic_Books.csv.ser",
							"Old_Time_Radio_Books.csv.ser",
							"Sports_Sports_Memorabilia.csv.ser",
							"Trains_Planes_Automobiles.csv.ser"};
		
		Book[][] allBooks = new Book[fileNames.length][];
		
		for (int i=0; i<fileNames.length; i++) {
			allBooks[i]  = deserialize(fileNames[i]);
		}
		
		int currentFileIndex = 0;
		boolean exit = false;
		while (!exit) {
			System.out.print("---------------------------\n"
					+ "\tMain Menu\t\n"
					+ "---------------------------\n"
					+ "v  View the selected file: " + fileNames[currentFileIndex] + " (" + allBooks[currentFileIndex].length + " records)\n"
					+ "s  Select a file to view\n"
					+ "x  Exit\n"
					+ "---------------------------\n\n"
					+ "Enter your Choice: "
					);
			String choice = keyIn.next().toLowerCase();
			switch (choice) {
				case "v":
					System.out.println("Viewing: " + fileNames[currentFileIndex] + " (" + allBooks[currentFileIndex].length + " records)");
					
					Book[] currentFile = allBooks[currentFileIndex];
					int currentRecordIndex = 0;
					
					while (true) {
						System.out.print("Enter range of records you would like to view, or enter 0 to exit: "); 
						int n = keyIn.nextInt();
					
						if (n==0)
							break;
					
						int startIndex = currentRecordIndex;
						int endIndex = currentRecordIndex;
		            
						if (n > 0) {
							boolean reachedEOF = false;
							endIndex = currentRecordIndex + n - 1; 
							
							if (endIndex >= currentFile.length) {
								endIndex = currentFile.length - 1;
								reachedEOF = true;
							}
							
							System.out.println("\nDisplaying records:\n");
							for (int i=currentRecordIndex; i <=endIndex; i++)
								System.out.println(currentFile[i]);
							
							if (reachedEOF)
								System.out.println("EOF has been reached.");
							
							currentRecordIndex = endIndex;
						}
						else {
							boolean reachedBOF = false;
							startIndex = currentRecordIndex + n + 1;
							
							if (startIndex < 0) {
								startIndex =0;
								reachedBOF = true;
							}
							
							System.out.println("\nDisplaying records:\n");
							for (int i=startIndex; i <=currentRecordIndex; i++)
								System.out.println(currentFile[i]);
								
							if (reachedBOF)
								System.out.println("BOF has been reached.");
							
							currentRecordIndex = startIndex;						
							}
					}
					
					break;
					
				case "s":
					boolean gettingInput = true;
					while (gettingInput ) {
						System.out.print("----------------------------------\n"
								+ "\tFile Sub-Menu\t\n"
								+ "----------------------------------\n");
						for (int i = 0; i < fileNames.length; i++) {
						    System.out.printf("%d. %-40s (%d records)%n", 
						                      (i + 1), fileNames[i], allBooks[i].length);
						}
						
						System.out.print("9  Exit"
								+ "\n----------------------------------\n" 
								+ "Enter your Choice: ");
						
						int newFileIndex;
						try {
							newFileIndex = keyIn.nextInt();
							System.out.println();
							
							if (newFileIndex==(fileNames.length+1)) {
								System.out.println();
								gettingInput = false;
							}
							else if (newFileIndex >=1 && newFileIndex<=8)  {
								currentFileIndex = newFileIndex-1;
								gettingInput = false;
							}
							else
								System.out.println("Invalid choice. Please pick a number between 1 and " + (fileNames.length+1));

						} catch (InputMismatchException e) {
							System.out.println("Error: Input needs to be an integer");
							keyIn.nextLine();
						}
					}
					break;
					
				case "x":
					exit = true;
					break;
			}
		}
		keyIn.close();
	}
	
	/**
	 * Method for do_part3 that turns binary objects into deserialized objects
	 * 
	 * @param fileName to read through each file
	 * @return Book array of deserialized records
	 */
	// For part 3, turning all serialized binary inputs back into Book objects
	public static Book[] deserialize(String fileName) {
		ObjectInputStream reader = null;
		Book[] tempBooks = new Book[1000];
		int count = 0;
		
		try {
			reader = new ObjectInputStream(new FileInputStream(fileName));
			while (true) {
				try {
				Book book = (Book) reader.readObject();
				tempBooks[count++] = book;
				} catch (EOFException e) {
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error opening file: " + fileName);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
		}
		
		Book[] books = new Book[count];
		for (int i=0; i<count; i++)
			books[i]=tempBooks[i];
		
		return books;

	}
}


//Exceptions classes used in part 1
/**
* Exception thrown when record contains too many fields
*/

class TooManyFieldsException extends Exception {
	public TooManyFieldsException(String message) {
     super(message);
 }
	
	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when record contains too fiew fields
*/
class TooFewFieldsException extends Exception {
	public TooFewFieldsException(String message) {
     super(message);
 }
	
	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when record is missing a field
*/
class MissingFieldException extends Exception {
	public MissingFieldException(String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when record genre is not recorgnized
*/
class UnknownGenreException extends Exception {
	public UnknownGenreException(String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

//Exceptions classes used in part 2

/**
* Exception thrown when ISBN10 value is invalid
*/
class BadIsbn10Exception extends Exception {
	public BadIsbn10Exception (String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when ISBN13 value is invalid
*/
class BadIsbn13Exception extends Exception {
	public BadIsbn13Exception(String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when price is invalid
*/
class BadPriceException extends Exception {
	public BadPriceException(String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

/**
* Exception thrown when year is invalid
*/
class BadYearException extends Exception {
	public BadYearException(String message) {
     super(message);
 }

	public String getMessage() {
		return super.getMessage();
	}
}

