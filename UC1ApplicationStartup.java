/**
 * Entry point for the Hotel Booking application.
 * <p>
 * This class demonstrates how a standalone Java program starts execution
 * through the {@code main} method and writes output to the console.
 * </p>
 *
 * @author Admin
 * @version 1.0
 */
public class UC1ApplicationStartup {

    private static final String APPLICATION_NAME = "Hotel Booking System";
    private static final String APPLICATION_VERSION = "v1.0";

    /**
     * Starts the Hotel Booking application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the Hotel Booking application!");
        System.out.println("Application: " + APPLICATION_NAME + " " + APPLICATION_VERSION);
    }
}
