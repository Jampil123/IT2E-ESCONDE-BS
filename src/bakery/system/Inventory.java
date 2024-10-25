
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
        String pname = sc.nextLine();
       int pqty = 0; // Initialize quantity
        boolean validInput = false; // Flag to track valid quantity input
        
        // Loop until valid quantity is entered
        while (!validInput) {
            System.out.print("Enter Quantity: ");
            try {
                pqty = sc.nextInt(); // Try to read an integer quantity
                validInput = true; // Set flag to true if input is valid
            } catch (InputMismatchException e) {
                System.out.println("Invalid input...Please try Again!!!.");
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
                validInput = true; // Set flag to true if input is valid
            } catch (InputMismatchException e) {
                System.out.println("Invalid input...Please enter a valid price.");
                sc.nextLine(); // Clear the invalid input from the scanner
            }
        }
        String pstatus = (pqty > 1) ? "Available" : "Out of Stock";
        String sql = "INSERT INTO tbl_product (p_name, p_qty, p_price, p_status) VALUES (?, ?, ?, ?)";

        conf.addRecord(sql, pname, pqty, pprice, pstatus);
    }

    public void viewInventory() {
        
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("|                                     INVENTORY                                           |");
        String sqlQuery = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product"; 
        String[] columnHeaders = {"Product ID", "Product Name", "Quantity", "Price", "Status"}; 
        String[] columnNames = {"p_id", "p_name", "p_qty", "p_price", "p_status"};

        conf.viewRecords(sqlQuery, columnHeaders, columnNames);    
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

        String newStatus = (newQty > 0) ? "Available" : "Out of Stock";
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

