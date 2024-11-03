
package bakery.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Inventory {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    public void inventoryCRUD(){
        Inventory in = new Inventory();
        String choice;
        do {
            System.out.println("--------------------------------");
            System.out.println("====  Inventory Management  ====");
            System.out.println("--------------------------------");
            
            System.out.println("1. ADD Product");
            System.out.println("2. VIEW Products");
            System.out.println("3. UPDATE Product");
            System.out.println("4. DELETE Product");
            System.out.println("5. BACK to Main Menu");

            System.out.print("\nEnter Action: ");
            choice = sc.nextLine();
            
            switch (choice) {
                case "1":
                    in.addProduct();
                    break;

                case "2":
                    in.viewInventory();
                    break;

                case "3":
                    in.updateProduct();
                    break;

                case "4":
                    in.deleteProduct();

                    break;

                case "5":
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid action! Please select again.");
                    break;
            }
        } while (!choice.equals("5")); 
    }
    
   public void addProduct() {
    System.out.print("Enter Product (Bread): ");
    String pname = sc.nextLine(); // Prompt for product name

    int pqty = 0; // Initialize quantity
    boolean validInput = false; // Flag to track valid quantity input

    // Loop until valid quantity is entered
    while (!validInput) {
        System.out.print("Enter Quantity: ");
        try {
            pqty = sc.nextInt(); // Try to read an integer quantity
            sc.nextLine(); // Clear the buffer to avoid issues with nextLine later
            validInput = true; // Set flag to true if input is valid
        } catch (InputMismatchException e) {
            System.out.println("Invalid input...Please try again!");
            sc.nextLine(); // Clear the invalid input from the scanner
        }
    }

    // Reset the validInput flag for the next validation
    validInput = false;
    float pprice = 0.0f; // Initialize selling price

    // Loop until valid selling price is entered
    while (!validInput) {
        System.out.print("Enter Selling Price: ");
        try {
            pprice = sc.nextFloat(); // Try to read a float selling price
            sc.nextLine(); // Clear the buffer to avoid issues with nextLine later
            validInput = true; // Set flag to true if input is valid
        } catch (InputMismatchException e) {
            System.out.println("Invalid input...Please enter a valid price.");
            sc.nextLine(); // Clear the invalid input from the scanner
        }
    }

    String pstatus = (pqty > 1) ? "In Stock" : "Sold Out";
    String sql = "INSERT INTO tbl_product (p_name, p_qty, p_price, p_status) VALUES (?, ?, ?, ?)";

    conf.addRecord(sql, pname, pqty, pprice, pstatus);
    System.out.println("Product added successfully!");
}

    public void viewInventory() {
        
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("|                                     INVENTORY                                           |");
        
        String sqlQuery = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product";
        String[] columnHeaders = {"Product ID", "Product Name", "Quantity", "Price", "Status"};
        String[] columnNames = {"p_id", "p_name", "p_qty", "p_price", "p_status"};

        conf.viewRecords(sqlQuery, columnHeaders, columnNames);

        System.out.println("\nOptions for Specific Report:");
        System.out.println("1. View product by ID");
        System.out.println("2. View products by stock status (In Stock / Out of Stock)");
        System.out.println("3. Cancel");
        
        System.out.print("\nEnter your choice : ");
        
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Product ID to view details: ");
                    String pid = sc.nextLine();
                    viewProductByProductID();
                    break;
                case 2:
                    System.out.print("Enter stock status to filter by (1 for In Stock / 2 Sold Out): ");
                    int statusChoice = sc.nextInt();
                    sc.nextLine(); 
                    
                    String status = (statusChoice == 1) ? "In Stock" : "Sold Out";
                    viewProductsByStatus(status);
                    break;
                case 3:
                    System.out.println("Canceling...");
                    break; 
                default:
                    System.out.println("Invalid choice. Please select 1 or 2.");
            }
    }
    public void viewProductByProductID() {
        System.out.print("Enter Product ID to view sales history: ");
        String pid = sc.nextLine();

        try {
            // Retrieve product details from tbl_product
            PreparedStatement productSearch = conf.connectDB().prepareStatement("SELECT * FROM tbl_product WHERE p_id = ?");
            productSearch.setString(1, pid);
            ResultSet productResult = productSearch.executeQuery();

            if (!productResult.next()) {
                System.out.println("Product with ID " + pid + " does not exist.");
                productResult.close();
                productSearch.close();
                return;
            }

            String pname = productResult.getString("p_name");
            double price = productResult.getDouble("p_price");
            System.out.println("\nProduct Details:");
            System.out.println("Product ID: " + pid);
            System.out.println("Product Name: " + pname);
            System.out.println("Price: " + price);

            productResult.close();
            productSearch.close();

            // Query sales data for the specified product from tbl_sales
            String sqlQuery = "SELECT s_date, qty_sold FROM tbl_sales WHERE p_id = ?";
            PreparedStatement salesSearch = conf.connectDB().prepareStatement(sqlQuery);
            salesSearch.setString(1, pid);
            ResultSet salesResult = salesSearch.executeQuery();

            int totalQtySold = 0;
            boolean hasSales = false;

            System.out.println("\nSales History:");
            System.out.println("Date of Sale          | Quantity Sold");
            System.out.println("----------------------+--------------\n");

            while (salesResult.next()) {
                hasSales = true;
                String saleDate = salesResult.getString("s_date");
                int qtySold = salesResult.getInt("qty_sold");

                System.out.printf("%-20s | %d\n", saleDate, qtySold);
                totalQtySold += qtySold;
            }

            if (!hasSales) {
                System.out.println("No sales record found for this product.");
            } else {
                System.out.println("\nTotal Quantity Sold: " + totalQtySold);
            }

            salesResult.close();
            salesSearch.close();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewProductsByStatus(String status) {
    String sql = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product WHERE p_status = ?";

        try (PreparedStatement search = conf.connectDB().prepareStatement(sql)) {
            search.setString(1, status);
            ResultSet result = search.executeQuery();
            
            System.out.println("--------------------------------");
            System.out.println("\nProducts with status \"" + status + "\":");
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("| %-13s | %-13s | %-13s | %-13s |\n",
                        "Product ID", "Product Name","Quantity","Price");
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
            System.out.println("Number of Products " + status + " : " +  num_p);
            System.out.println("");

            if (!hasResults) {
                System.out.println("No products found with status \"" + status + "\".");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void updateProduct() {
      
        System.out.print("Enter Product ID to update: ");
        int productId = sc.nextInt();
        sc.nextLine(); 
        
        // Check if the Product Id Exist
        try{
            PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_product WHERE p_id = ?");
            
            search.setInt(1, productId);
            ResultSet result = search.executeQuery();
            
                if (!result.next() ) {
                System.out.println("Product with ID " + productId + " does not exist. Please try again.");
                return; 
            }
        } catch(SQLException e){
            
        }

        System.out.print("Enter new Product Name: ");
        String newName = sc.nextLine();
        System.out.print("Enter new Quantity: ");
        int newQty = sc.nextInt();
        System.out.print("Enter new Selling Price: ");
        float newPrice = sc.nextFloat();

        String newStatus = (newQty > 0) ? "In Stock" : "Sold out";
        String sqlUpdate = "UPDATE tbl_product SET p_name = ?, p_qty = ?, p_price = ?, p_status = ? WHERE p_id = ?";
        
        conf.updateRecord(sqlUpdate, newName, newQty, newPrice, newStatus, productId);  
    }
     
    public void deleteProduct(){
        Scanner s = new Scanner(System.in);

        System.out.print("Enter ID to Delete: ");
        int cid = s.nextInt();

        String qry = "DELETE FROM tbl_product WHERE p_id = ?";

        conf.deleteRecord(qry, cid);  
    }
}

