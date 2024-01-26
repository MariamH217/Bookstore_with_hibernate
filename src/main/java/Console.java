import org.hibernate.Session;
import java.util.Scanner;

public class Console {
     static void menu(Session session) {
        int answer;

        do {
            manuOptions();
            Scanner sc = new Scanner(System.in);
            answer = sc.nextInt();

            switch (answer) {
                case 0:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                case 1:
                    Book.updateBookDetails(session);
                    break;
                case 2:
                    Book.listBooksByGenre(session);
                    break;
                case 3:
                    Book.listBooksByAuthor(session);
                    break;
                case 4:
                    Customer.updateCustomer(session);
                    break;
                case 5:
                    Customer.viewCustomerPurchaseHistory(session);
                    break;
                case 6:
                    Sale.processNewSale(session);
                    break;
                case 7:
                    Sale.calculateTotalRevenueByGenre(session);
                    break;
                case 8:
                    Book.generateBooksSoldReport(session);
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
            }
        } while (answer != 0);
    }

    private static void manuOptions() {
        System.out.println("0. Exit the program");
        System.out.println("1. Update book details");
        System.out.println("2. List books by genre");
        System.out.println("3. List books by author");
        System.out.println("4. Update customer information");
        System.out.println("5. View customer purchase history");
        System.out.println("6. Make a new sale");
        System.out.println("7. Report of total revenue by genre");
        System.out.println("8. Report of all sold books");
    }
}
