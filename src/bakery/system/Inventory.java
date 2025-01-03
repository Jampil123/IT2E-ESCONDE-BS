
package bakery.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Inventory {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    public void inventoryCRUD(){
        String choice;
        do {
            System.out.println("\n================================================================================================================================================================");
            System.out.println("\n+----------------------------------------------------------+"
                             + "\n|                 INVENTORY MANAGEMENT                     |"
                             + "\n+----------------------------------------------------------+"
                             + "\n| 1.  | Add Product                                        |"
                             + "\n| 2.  | View Product                                       |"
                             + "\n| 3.  | Update Product                                     |"
                             + "\n| 4.  | Delete Product                                     |"
                             + "\n| 5.  | BACK to Main Menu                                  |"
                             + "\n|----------------------------------------------------------|");
            System.out.print("| Enter Action: ");
            choice = sc.nextLine();
            System.out.println("+----------------------------------------------------------+");

            do {
               if (!choice.matches("[1-5]")) {
                   System.out.print("Invalid choice! Please select again : ");
                   choice = sc.nextLine();
               }
            } while (!choice.matches("[1-5]"));
           
            switch (choice) {
                case "1":
                    addProduct();
                    break;

                case "2":
                    viewInventory();
                    break;

                case "3":
                    viewInventory();
                    updateProduct();
                    viewInventory();
                    break;

                case "4":
                    viewInventory();
                    deleteProduct();
                    break;

                case "5":
                    System.out.println("Returning to Main Menu...\n");
                    break;

                default:
                    System.out.println("Invalid action! Please select again.");
                    break;
            }
        } while (!choice.equals("5")); 
    }
    
    public void addProduct() {
        System.out.println("\n------------------------------------------------------------"
                         + "\n| Enter Product Details:                                   |"
                         + "\n------------------------------------------------------------");
        
        System.out.print("Enter Product (Bread): ");
        String pname = sc.nextLine(); 

        int pqty;
        
        System.out.print("Enter Quantity: ");
        while (true) {
            if (sc.hasNextInt()) {
                pqty = sc.nextInt();
                sc.nextLine(); 
                break;
            } else {
                System.out.print("Invalid input! Please try Again : ");
                sc.next(); 
            }
        }

        float pprice;
        
        System.out.print("Enter Selling Price: ");
        while (true) {
            if (sc.hasNextFloat()) {
                pprice = sc.nextFloat();
                sc.nextLine(); 
                break;
            } else {
                System.out.print("Invalid input! Please try Again : ");
                sc.next(); 
            }
        }
        System.out.println("");
    String pstatus = (pqty > 1) ? "In Stock" : "Sold Out";
    String sql = "INSERT INTO tbl_product (p_name, p_qty, p_price, p_status) VALUES (?, ?, ?, ?)";

    conf.addRecord(sql, pname, pqty, pprice, pstatus);
    System.out.println("Product added successfully!!!");
}

    public void viewInventory() {
        
        System.out.println("\n-------------------------------------------------------------------------------------------");
        System.out.println("|                                     INVENTORY                                           |");
        
        String sqlQuery = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product";
        String[] columnHeaders = {"Product ID", "Product Name", "Quantity", "Price", "Status"};
        String[] columnNames = {"p_id", "p_name", "p_qty", "p_price", "p_status"};

        conf.viewRecords(sqlQuery, columnHeaders, columnNames);
 
    try {
        PreparedStatement search = conf.connectDB().prepareStatement(" SELECT p_name, p_qty FROM tbl_product ");
        
        ResultSet result = search.executeQuery();
        int totalProducts = 0;
        int totalqty = 0;
        while(result.next()){
                totalProducts += result.getInt("p_name");
                totalqty += result.getInt("p_qty");
                
                totalProducts++;
                totalqty++; 
            }
        System.out.println("|   Product Inventory Overview                                                            |"
                         + "\n|---------------------------------------                                                  |"
                         + "\n| Total number of products : " +totalProducts+ "                                                           |"
                         + "\n| Overall total Quantity : " +totalqty+ "                                                            |");
        System.out.println("===========================================================================================");
        
    } catch (Exception e) {
        System.out.println("Error retrieving total product count: " + e.getMessage());
    }
}

    public void updateProduct() {
      
        int productId;
    
        while (true) {
            System.out.print("\nEnter Product ID to update: ");
            if (sc.hasNextInt()) {
                productId = sc.nextInt();
                sc.nextLine(); // clear newline
                String sql = "SELECT p_id FROM tbl_product WHERE p_id = ?";
                if (conf.getSingleValue(sql, productId) != 0) {
                    break;
                } else {
                    System.out.print("Product with ID " + productId + " does not exist. Please try again.");
                }
            } else {
                System.out.print("Invalid input. Please enter a numeric Product ID.");
                sc.next();
            }
        }

        System.out.print("Enter new Product Name: ");
        String newName = sc.nextLine();
        
        int newQty;
        
        System.out.print("Enter new Quantity: ");
        while (true) {
            if (sc.hasNextInt()) {
                newQty = sc.nextInt();
                sc.nextLine(); 
                break;
            } else {
                System.out.print("Invalid input!!!"
                        + "\nPlease try Again: ");
                sc.next(); 
            }
        }

        float newPrice;
        
        System.out.print("Enter new Selling Price: ");
        while (true) {
            if (sc.hasNextFloat()) {
                newPrice = sc.nextFloat();
                sc.nextLine(); 
                break;
            } else {
                System.out.print("Invalid input!!!"
                        + "\nPlease try Again: ");
                sc.next(); 
            }
        }
        System.out.println("");
        String newStatus = (newQty > 0) ? "In Stock" : "Sold out";
        String sqlUpdate = "UPDATE tbl_product SET p_name = ?, p_qty = ?, p_price = ?, p_status = ? WHERE p_id = ?";
        
        conf.updateRecord(sqlUpdate, newName, newQty, newPrice, newStatus, productId); 
        System.out.println("Product Updated successfully!!!");
        
    }
     
    public void deleteProduct() {
        int productId;
        System.out.print("\nEnter Product ID to Delete: ");

        while (true) {
            
            if (sc.hasNextInt()) {
                productId = sc.nextInt();
                sc.nextLine(); 

                String sql = "SELECT p_id FROM tbl_product WHERE p_id = ?";
                if (conf.getSingleValue(sql, productId) != 0) {
                    break; 
                } else {
                    System.out.print("Product with ID " + productId + " does not exist!"
                            + " Please try again : ");
                }
            } else {
                System.out.print("Invalid input! Please enter a numeric Product ID :");
                sc.next(); 
            }
        }
        System.out.println("");
        String qry = "DELETE FROM tbl_product WHERE p_id = ?";
        conf.deleteRecord(qry, productId);
        System.out.println("Product Deleted successfully!!!");
    }
}

