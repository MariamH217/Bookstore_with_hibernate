import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer saleID;
    @ManyToOne
    @JoinColumn(name = "bookID", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer;

    @Column(name = "quantity_sold")
    private int quantitySold;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "date_of_sale")
    private LocalDate dateOfSale;


    public Integer getSaleID() {
        return saleID;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(LocalDate dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public static void processNewSale(Session session) {
        Transaction transaction = null;

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the customer ID for the sale:");
            int customerId = scanner.nextInt();


            Customer customer = session.get(Customer.class, customerId);

            if (customer != null) {
                scanner.nextLine();

                System.out.println("Enter the book ID for the sale:");
                int bookId = scanner.nextInt();


                Book book = session.get(Book.class, bookId);

                if (book != null) {
                    System.out.println("Enter the quantity for the sale:");
                    int quantity = scanner.nextInt();


                    if (isEnoughQuantityInStock(session, bookId, quantity)) {
                        double bookPrice = book.getPrice().doubleValue();
                        double totalPrice = bookPrice * quantity;


                        transaction = session.beginTransaction();

                        // Update quantity in stock
                        book.setQuantityInStock(book.getQuantityInStock() - quantity);
                        session.update(book);

                        // Create a new Sale entity
                        Sale sale = new Sale();
                        sale.setCustomer(customer);
                        sale.setBook(book);
                        sale.setQuantitySold(quantity);
                        sale.setTotalPrice(BigDecimal.valueOf(totalPrice));
                        sale.setDateOfSale(LocalDate.now());

                        // Save the Sale entity
                        session.save(sale);

                        // Commit the transaction
                        transaction.commit();

                        System.out.println("Sale processed successfully.");
                    } else {
                        System.out.println("Not enough quantity in stock for the sale.");
                    }
                } else {
                    System.out.println("Book with ID " + bookId + " does not exist.");
                }
            } else {
                System.out.println("Customer with ID " + customerId + " does not exist.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Error processing sale. Rolling back transaction.");
            e.printStackTrace();
        }
    }

    private static boolean isEnoughQuantityInStock(Session session, int bookId, int requestedQuantity) {
        Book book = session.get(Book.class, bookId);

        if (book != null) {
            int availableQuantity = book.getQuantityInStock();
            return availableQuantity >= requestedQuantity;
        }

        return false;
    }




    public static void calculateTotalRevenueByGenre(Session session) {
        String revenueByGenreQuery = "SELECT b.genre, SUM(s.totalPrice) AS totalRevenue " +
                "FROM Sale s " +
                "JOIN s.book b " +
                "GROUP BY b.genre";

        try {
            Query query = session.createQuery(revenueByGenreQuery);

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            System.out.println("Total Revenue by Genre:");

            for (Object[] result : results) {
                String genre = (String) result[0];
                BigDecimal totalRevenue = (BigDecimal) result[1];

                System.out.println("Genre: " + genre + ", Total Revenue: $" + totalRevenue);
            }

        } catch (Exception e) {
            System.out.println("Error calculating total revenue by genre.");
            e.printStackTrace();
        }
    }


}
