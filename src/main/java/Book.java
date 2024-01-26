import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookID;
    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    public Book(String title, String author, String genre, BigDecimal price, Integer quantityInStock) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public Book() {

    }

    public Long getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }



    public static void updateBookDetails(Session session) {

        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the book ID you want to update:");
            long bookId = scanner.nextLong();


            Book bookToUpdate = session.get(Book.class, bookId);

            if (bookToUpdate != null) {
                scanner.nextLine();

                System.out.println("Enter the new title (or press Enter to keep existing):");
                String newTitle = scanner.nextLine().trim();
                if (!newTitle.isEmpty()) {
                    bookToUpdate.setTitle(newTitle);
                }

                System.out.println("Enter the new author (or press Enter to keep existing):");
                String newAuthor = scanner.nextLine().trim();
                if (!newAuthor.isEmpty()) {
                    bookToUpdate.setAuthor(newAuthor);
                }

                System.out.println("Enter the new genre (or press Enter to keep existing):");
                String newGenre = scanner.nextLine().trim();
                if (!newGenre.isEmpty()) {
                    bookToUpdate.setGenre(newGenre);
                }

                System.out.println("Enter the new price (or enter 0 to keep existing):");
                BigDecimal newPrice = scanner.nextBigDecimal();
                if (!newPrice.equals(BigDecimal.ZERO)) {
                    bookToUpdate.setPrice(newPrice);
                }

                System.out.println("Enter the new quantity in stock (or enter 0 to keep existing):");
                int newQuantityInStock = scanner.nextInt();
                if (newQuantityInStock != 0) {
                    bookToUpdate.setQuantityInStock(newQuantityInStock);
                }


                transaction.commit();
                System.out.println("Book details updated successfully.");
            } else {
                System.out.println("Book with ID " + bookId + " not found.");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void listBooksByAuthor(Session session) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the author to list books:");
        String author = scanner.nextLine();

        // List books by author
        String hql = "FROM Book WHERE author = :author";
        try {
            Query<Book> query = session.createQuery(hql, Book.class);
            query.setParameter("author", author);

            List<Book> books = query.list();

            if (books.isEmpty()) {
                System.out.println("No books found for the author: " + author);
            } else {
                for (Book book : books) {
                    System.out.println("Book ID: " + book.getBookID());
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("Genre: " + book.getGenre());
                    System.out.println("Price: " + book.getPrice());
                    System.out.println("Quantity in Stock: " + book.getQuantityInStock());
                    System.out.println("---------------------------");
                }
            }

        } catch (Exception e) {
            System.out.println("Error listing books by author.");
            e.printStackTrace();
        }
    }
    public static void listBooksByGenre(Session session) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the genre to list books:");
        String genre = scanner.nextLine();

        // List books by genre
        String hql = "FROM Book WHERE genre = :genre";
        try {
            Query<Book> query = session.createQuery(hql, Book.class);
            query.setParameter("genre", genre);

            List<Book> books = query.list();

            if (books.isEmpty()) {
                System.out.println("No books found for the genre: " + genre);
            } else {
                for (Book book : books) {
                    System.out.println("Book ID: " + book.getBookID());
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("Author: " + book.getAuthor());
                    System.out.println("Price: " + book.getPrice());
                    System.out.println("Quantity in Stock: " + book.getQuantityInStock());
                    System.out.println("---------------------------");
                }
            }

        } catch (Exception e) {
            System.out.println("Error listing books by genre.");
            e.printStackTrace();
        }
    }

    public static void generateBooksSoldReport(Session session) {
        String booksSoldReportQuery = "SELECT b.title AS book_title, c.name, s.dateOfSale " +
                "FROM Sale s " +
                "JOIN s.book b " +
                "JOIN s.customer c " +
                "ORDER BY s.dateOfSale DESC";

        try {
            Query<Object[]> query = session.createQuery(booksSoldReportQuery, Object[].class);

            List<Object[]> result = query.list();

            System.out.println("Books Sold Report:");

            for (Object[] row : result) {
                String bookTitle = (String) row[0];
                String customerName = (String) row[1];
                String dateOfSale = row[2].toString();

                System.out.println("Book Title: " + bookTitle + ", Customer Name: " + customerName +
                        ", Date of Sale: " + dateOfSale);
            }

        } catch (Exception e) {
            System.out.println("Error generating books sold report.");
            e.printStackTrace();
        }
    }

}
