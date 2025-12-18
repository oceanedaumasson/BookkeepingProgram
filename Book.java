package a2_OceaneDaumasson_40275138;

import java.io.Serializable;

/**
 * Class to create a book object, serializable
 */
public class Book implements Serializable {
	private String title;
	private String author;
	private double price;
	private String isbn;
	private String genre;
	private int year;
	
	/**
	 * Parameterized constructor to create object and initializes variables from user input
	 * 
	 * @param title of the book
	 * @param author of the book
	 * @param price of the book
	 * @param isbn (either 10 or 13 digits) of the book
	 * @param genre of the book
	 * @param year book was published
	 */
	public Book(String title, String author, double price, String isbn, String genre, int year) {
		this.title = title;
	    this.author = author;
	    this.price = price;
	    this.isbn = isbn;
	    this.genre = genre;
	    this.year = year;
	}
	
	/**
	 * returns title of the book
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets title to passed value
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * returns author of the book
	 * 
	 * @return author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * sets author to passed value
	 * 
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * returns price of the book
	 * 
	 * @return price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * sets price to passed value
	 * 
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * returns isbn of the book
	 * 
	 * @return isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * sets isbn to passed value
	 * 
	 * @param isbn
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * returns genre of the book
	 * 
	 * @return genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * sets genre to passed value
	 * 
	 * @param genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * returns year of the book
	 * 
	 * @return
	 */
	public int getYear() {
		return year;
	}

	/**
	 * sets year to passed value
	 * 
	 * @param year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Prints object details
	 * 
	 * @return a string displaying the book's information
	 */
	public String toString() {
		return "Title: " + title + "\nAuthor: " + author + "\nPrice: $" + price + "\nISBN: " + isbn + "\nGenre: " + genre + "\n";
	}
	
	/**
	 * Compares one book to another object. Returns false if passed object is not a Book, true if all values are equal.
	 * 
	 * @param other another object to compare to this Helicopter
	 * @return true if the books are equal
	 */
	public boolean equals(Object other) {
		if (other == null || getClass() !=other.getClass())
			return false;
		Book otherBook = (Book) other;	
		return this.title.equals(otherBook.title)
				&& this.author.equals(otherBook.author) 
				&& this.price==otherBook.price 
				&& this.isbn.equals(otherBook.isbn)
				&& this.genre.equals(otherBook.genre)
				&& this.year==otherBook.year;
	}					
}
