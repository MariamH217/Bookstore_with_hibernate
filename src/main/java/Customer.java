import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerID;
    private String name;
    private String email;
    private String phone;

    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Customer() {

    }

    public Integer getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static void updateCustomer(Session session) {
        Transaction transaction = null;

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the customer ID you want to update:");
            int customerId = scanner.nextInt();

            // Check if the customer with the given ID exists
            Customer customerToUpdate = session.get(Customer.class, customerId);

            if (customerToUpdate != null) {
                scanner.nextLine();

                System.out.println("Enter the new customer name (or press Enter to keep existing):");
                String newCustomerName = scanner.nextLine().trim();
                if (!newCustomerName.isEmpty()) {
                    customerToUpdate.setName(newCustomerName);
                }

                System.out.println("Enter the new customer email (or press Enter to keep existing):");
                String newCustomerEmail = scanner.nextLine().trim();
                if (!newCustomerEmail.isEmpty()) {
                    customerToUpdate.setEmail(newCustomerEmail);
                }

                System.out.println("Enter the new customer phone (or press Enter to keep existing):");
                String newCustomerPhone = scanner.nextLine().trim();
                if (!newCustomerPhone.isEmpty()) {
                    customerToUpdate.setPhone(newCustomerPhone);
                }


                transaction = session.beginTransaction();
                session.update(customerToUpdate);
                transaction.commit();

                System.out.println("Customer information updated successfully.");
            } else {
                System.out.println("Customer with ID " + customerId + " not found.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Error updating customer information.");
            e.printStackTrace();
        }
    }

    public static void viewCustomerPurchaseHistory(Session session) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the customer ID to view purchase history:");
            int customerId = scanner.nextInt();

            Customer customer = session.get(Customer.class, customerId);

            if (customer != null) {
                String purchaseHistoryQuery = "SELECT s.saleID, b.title, s.quantitySold, s.totalPrice, s.dateOfSale " +
                        "FROM Sale s " +
                        "JOIN s.book b " +
                        "WHERE s.customer.customerID = :customerId " +
                        "ORDER BY s.dateOfSale DESC";

                Query<Object[]> query = session.createQuery(purchaseHistoryQuery, Object[].class);
                query.setParameter("customerId", customerId);

                List<Object[]> result = query.list();

                System.out.println("Customer Purchase History:");

                for (Object[] row : result) {
                    int saleId = (int) row[0];
                    String bookTitle = (String) row[1];
                    int quantitySold = (int) row[2];
                    BigDecimal total_price = (BigDecimal) row[3];
                    String dateOfSale = row[4].toString();

                    System.out.println("Sale ID: " + saleId + ", Book: " + bookTitle +
                            ", Quantity Sold: " + quantitySold + ", Total Price: $" + total_price +
                            ", Date of Sale: " + dateOfSale);
                }
            } else {
                System.out.println("Customer with ID " + customerId + " not found.");
            }

        } catch (Exception e) {
            System.out.println("Error retrieving customer purchase history.");
            e.printStackTrace();
        }
    }

}
