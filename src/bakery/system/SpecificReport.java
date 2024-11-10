
package bakery.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SpecificReport {
    
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    String choice;
    public void specificReport() {
        
        do {
            System.out.println("                     ----------------------------------------");
            System.out.println("                     ====            Reports             ====");
            System.out.println("                     ----------------------------------------");
            
            Inventory in = new Inventory();
            in.viewInventory();
            
            System.out.println("\nOptions for Specific Report:");
            System.out.println("1. View product by ID");
            System.out.println("2. View products by stock status (In Stock / Out of Stock)");
            System.out.println("3. Exit");
            
            System.out.println("----------------------------------------");
            System.out.print("Enter Choice: ");
            choice = sc.nextLine();
            
            do {
               if (!choice.matches("[1-3]")) {
                   System.out.print("Invalid choice! Please select again : ");
                   choice = sc.nextLine();
               }
            } while (!choice.matches("[1-3]"));
            System.out.println("----------------------------------------");
            
             switch (choice) {
                case "1":
                    viewProductByProductID();
                    break;
                    
                case "2":
                    viewProductsByStatus();
                    break;
                    
                case "3":
                    System.out.println("Returning to Main Menu...");
                    break; 
            }

        } while (!choice.equals("3")); 
    }    
   public void viewProductByProductID() {
    int pid = -1;

    // Validate product ID input and check if it exists in tbl_product
    while (true) {
        System.out.print("Enter Product ID to view: ");
        if (sc.hasNextInt()) {
            pid = sc.nextInt();
            sc.nextLine(); // Clear the newline character after integer input

            try (PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_product WHERE p_id = ?")) {
                search.setInt(1, pid);
                ResultSet result = search.executeQuery();

                if (!result.next()) {
                    System.out.println("Product with ID " + pid + " does not exist. Please try again.");
                } else {
                    result.close();
                    break; // Product ID exists; break out of loop
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid input. Please enter a valid numeric Product ID.");
            sc.next(); // Consume the invalid input
        }
    }

    try {
        // Retrieve product details from tbl_product
        PreparedStatement productSearch = conf.connectDB().prepareStatement("SELECT * FROM tbl_product WHERE p_id = ?");
        productSearch.setInt(1, pid);
        ResultSet productResult = productSearch.executeQuery();

        if (productResult.next()) {
            String pname = productResult.getString("p_name");
            double price = productResult.getDouble("p_price");

            System.out.println("----------------------------------------");
            System.out.println("| Product Details:                     |");
            System.out.println("----------------------------------------");
            System.out.println("Product ID: " + pid);
            System.out.println("Product Name: " + pname);
            System.out.println("Price: " + price);
            System.out.println("----------------------------------------");
        }
        productResult.close();
        productSearch.close();

        // Query sales data for the specified product from tbl_sales
        String sqlQuery = "SELECT s_date, qty_sold, t_revenue FROM tbl_sales WHERE p_id = ?";
        PreparedStatement salesSearch = conf.connectDB().prepareStatement(sqlQuery);
        salesSearch.setInt(1, pid);
        ResultSet salesResult = salesSearch.executeQuery();

        int totalQtySold = 0;
        boolean hasSales = false;
        double totalRevenue = 0;

        System.out.println("\nSales History:");
        System.out.println("----------------------+----------------+---------------");
        System.out.println("|    Date of Sale     | Quantity Sold |    Revenue    |");
        System.out.println("----------------------+----------------+---------------");

        while (salesResult.next()) {
            hasSales = true;
            String saleDate = salesResult.getString("s_date");
            int qtySold = salesResult.getInt("qty_sold");
            double revenue = salesResult.getDouble("t_revenue");

            System.out.printf("| %-20s| %-14d | %-12.2f |\n", saleDate, qtySold, revenue);
            totalQtySold += qtySold;
            totalRevenue += revenue;
        }
        System.out.println("----------------------+----------------+---------------");

        if (!hasSales) {
            System.out.println("No sales record found for this product.");
        } else {
            System.out.printf("Total Quantity Sold: %d\nTotal Revenue of the Product: %.2f\n", totalQtySold, totalRevenue);
        }
        System.out.println("-------------------------------------------------------");
        System.out.println("");

        salesResult.close();
        salesSearch.close();

    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
}

    public void viewProductsByStatus() {
        String sql = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product WHERE p_status = ?";
        
        String status;
        System.out.print("Enter stock status to filter by "
                + "\n1.for In Stock "
                + "\n2.for Sold Out\n ");
        
        System.out.printf("\nEnter Choice: ");
        int statusChoice = sc.nextInt();
        sc.nextLine(); 

        status = (statusChoice == 1) ? "In Stock" : "Sold Out";
        
        try (PreparedStatement search = conf.connectDB().prepareStatement(sql)) {
            search.setString(1, status);
            ResultSet result = search.executeQuery();

            System.out.println("--------------------------------");
            System.out.println("\nProducts with status \"" + status + "\":");
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("| %-13s | %-13s | %-13s | %-13s |\n",
                        "Product ID", "Product Name", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------------");

            boolean hasResults = false;
            int num_p = 0;

            while (result.next()) {
                hasResults = true;
                int id = result.getInt("p_id");
                String pname = result.getString("p_name");
                int qty = result.getInt("p_qty");
                double price = result.getDouble("p_price");

                System.out.printf("| %-13d | %-13s | %-13d | %-13.2f |\n",
                         id, pname, qty, price);
                num_p++;
            }
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Number of Products " + status + " : " + num_p);
            System.out.println("");

            if (!hasResults) {
                System.out.println("No products found with status \"" + status + "\".");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
    



