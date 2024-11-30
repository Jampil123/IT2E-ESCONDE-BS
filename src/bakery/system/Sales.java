
package bakery.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Sales {
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    LocalDate currDate = LocalDate.now();
    
    String sql;
    
    public void salesCRUD(){
        String choice;
        
        do {
            System.out.println("\n================================================================================================================================================================");
            System.out.println("\n+----------------------------------------------------------+"
                             + "\n|                 SALES MANAGEMENT                         |"
                             + "\n+----------------------------------------------------------+"
                             + "\n| 1.  | Add Sales                                          |"
                             + "\n| 2.  | View Sales                                         |"
                             + "\n| 3.  | Update Sales                                       |"
                             + "\n| 4.  | Delete Sales                                       |"
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
                    addSales();
                    break;

                case "2":
                    viewSales();
                    break;

                case "3":
                    viewSales();
                    updateSales();
                    
                    break;

                case "4":
                    viewSales();
                    deleteProduct();
                break;

                case "5":
                    System.out.println("Returning to Main Menu...\n");
                    break;
            }

        } while (!choice.equals("5"));
    }
    private void addSales(){
        
    System.out.println("\nProduct list: ");
    Inventory in = new Inventory();
    in.viewInventory();

    int pid = 0;
    boolean validProductId = false;

    System.out.print("\nEnter product ID: ");
    while (!validProductId) {
        
        if (sc.hasNextInt()) {
            pid = sc.nextInt();
//            sc.nextLine();

            try {
                String sql = "SELECT * FROM tbl_product WHERE p_id = ?";
                PreparedStatement search = conf.connectDB().prepareStatement(sql);
                search.setInt(1, pid);
                ResultSet result = search.executeQuery();

                if (result.next()) {
                    validProductId = true; 
                    int id = result.getInt("p_id");
                    String pname = result.getString("p_name");
                    double price = result.getDouble("p_price");
                    int inventoryQTY = result.getInt("p_qty");

                    System.out.println("---------------------------------------"
                                     + "\n| Selected product     : " + pname  
                                     + "\n| Price                : " + price  
                                     + "\n| Quantity             : " + inventoryQTY );
                    System.out.println("---------------------------------------");
                } else {
                    System.out.print("Product with ID " + pid + " does not exist! Please try again: ");
                }
                result.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.print("Invalid input! Please try again: ");
            sc.next(); 
        }
    }
    int qty = 0;
    boolean validQuantity = false;
    
    
    while (!validQuantity) {
        System.out.print("\nEnter quantity sold: ");
        
 
        if (sc.hasNextInt()) {
            qty = sc.nextInt();
            sc.nextLine(); 

            if (qty > 0) {
                try {
                   
                    String sql = "SELECT p_qty FROM tbl_product WHERE p_id = ?";
                    PreparedStatement search = conf.connectDB().prepareStatement(sql);
                    search.setInt(1, pid);
                    ResultSet result = search.executeQuery();

                    if (result.next()) {
                        int inventoryQTY = result.getInt("p_qty");
                        if (inventoryQTY >= qty) {
                            validQuantity = true; // Valid quantity entered
                        } else {
                            System.out.println("Insufficient stock. Only " + inventoryQTY + " units available. ");
                            
                            return ;
                        }
                    }
                    result.close();
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid input! Quantity must be a positive integer.");
            }
        } else {
            System.out.println("Invalid input! Please enter a valid quantity (integer).");
            sc.next(); // Clear the invalid input
        }
    }

    // Calculate revenue and proceed with the sale
        try {
            // Fetch the product details for sale
            String sql = "SELECT * FROM tbl_product WHERE p_id = ?";
            PreparedStatement search = conf.connectDB().prepareStatement(sql);
            search.setInt(1, pid);
            ResultSet result = search.executeQuery();

            if (result.next()) {
                double price = result.getDouble("p_price");
                double revenue = qty * price;
                String pname = result.getString("p_name");

                // Insert sale record
                String insertSale = "INSERT INTO tbl_sales (p_name, p_id, qty_sold, s_date, s_price, t_revenue) VALUES (?, ?, ?, ?, ?, ?)";
                conf.addRecord(insertSale, pname, pid, qty, currDate.toString(), price, revenue);

                // Update inventory
                int inventoryQTY = result.getInt("p_qty");
                int updatedQTY = inventoryQTY - qty;

                if (updatedQTY > 0) {
                    String updateInventory = "UPDATE tbl_product SET p_qty = ? WHERE p_id = ?";
                    conf.updateRecord(updateInventory, updatedQTY, pid);
                } else if (updatedQTY == 0) {
                    String updateInventory = "UPDATE tbl_product SET p_qty = ?, p_status = ? WHERE p_id = ?";
                    conf.updateRecord(updateInventory, updatedQTY, "Sold Out", pid);
                }
            }
            System.out.println("");
            result.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
            System.out.println("Sales Added successfully!!!");
    }

    private void viewSales(){
        System.out.println("\nSales Table: ");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                                            SALES                                                            |");
        
        String sqlQuery = "SELECT sale_id, p_id, p_name, qty_sold, s_date, s_price, t_revenue FROM tbl_sales";
        String[] columnHeaders = {"ID" ,"Product ID" ,"Product Name", "Quantity Sold", "Sale Date", "Price", "Revenue"};
        String[] columnNames = {"sale_id","p_id" , "p_name", "qty_sold", "s_date", "s_price", "t_revenue"};
        
        conf.viewRecords(sqlQuery, columnHeaders, columnNames);
        
        try {
            PreparedStatement getColumn = conf.connectDB().prepareStatement("SELECT p_name, qty_sold, t_revenue FROM tbl_sales");
            ResultSet getRow = getColumn.executeQuery();

                double totalRevenue = 0;
                int totalSales = 0;
                int totalQuantitySold = 0;
                double averageRevenue = 0;
                String mostSoldProduct = "";
                int mostSoldQuantity = 0;

            while (getRow.next()) {
                
                double revenue = getRow.getDouble("t_revenue");
                int quantitySold = getRow.getInt("qty_sold");
                String productName = getRow.getString("p_name");

                totalRevenue += revenue;
                totalSales++;
                totalQuantitySold += quantitySold;

                if (quantitySold > mostSoldQuantity) {
                    
                    mostSoldQuantity = quantitySold;
                    mostSoldProduct = productName;
                }
            }

            if (totalSales > 0) {
                averageRevenue = totalRevenue / totalSales;
            }

            System.out.println("| SALES SUMMARY                                                                                                               |"
                       + "\n|-----------------------------------------------------------------------------------------------------------------------------|"
                    + "\n| Total Sales Transactions       : " + totalSales + "                                                                                         |"
                    + "\n| Total Quantity Sold            : " + totalQuantitySold + "                                                                                        |"
                    + "\n| Average Sales Revenue          : " + String.format("%.2f", averageRevenue) + "                                                                                     |"
                    + "\n| Most Sold Product              : " + mostSoldQuantity + " units sold)" + "                                                                            |"
                    + "\n| Total Revenue                  : " + String.format("%.2f", totalRevenue) + "                                                                                    |"
                    + "\n-------------------------------------------------------------------------------------------------------------------------------");
            
            getRow.close();
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void updateSales() {
       System.out.println("\nAdministrator authorization is required to update sales.");
       System.out.print("\nEnter administrator password: ");
       String adminPassword = sc.nextLine();

       // Replace "admin123" with your actual admin password logic
       if (!adminPassword.equals("admin123")) {
           System.out.println("Authorization failed! Contact the administrator.");
           return;
       }

       int salesId = -1;
       while (true) {
           System.out.print("Enter Sales ID to update: ");
           if (sc.hasNextInt()) {
               salesId = sc.nextInt();
               sc.nextLine();
               try (PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_sales WHERE sale_id = ?")) {
                   search.setInt(1, salesId);
                   ResultSet result = search.executeQuery();

                   if (!result.next()) {
                       System.out.println("Sale with ID " + salesId + " does not exist.");
                       return;
                   }
                   result.close();
                   break;
               } catch (SQLException e) {
                   System.out.println("Error: " + e.getMessage());
               }
           } else {
               System.out.println("Invalid input. Please enter a valid numeric Sales ID.");
               sc.next();
           }
       }

       try (PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_sales WHERE sale_id = ?")) {
           search.setInt(1, salesId);
           ResultSet result = search.executeQuery();

           if (result.next()) {
               String currentProduct = result.getString("p_name");
               int currentQty = result.getInt("qty_sold");
               double currentPrice = result.getDouble("s_price");

               System.out.println("--------------------------------");
               System.out.println("Current Sale Record: " + currentProduct 
                       + "\nQuantity Sold: " + currentQty  
                       + "\nPrice: " + currentPrice);
               System.out.println("--------------------------------");

               int newQty = -1;
               while (true) {
                   System.out.print("Enter new quantity sold: ");
                   if (sc.hasNextInt()) {
                       newQty = sc.nextInt();
                       sc.nextLine(); // Clear newline

                       if (newQty <= 0) {
                           System.out.println("Quantity must be positive. Please enter a valid number.");
                       } else {
                           double newRevenue = newQty * currentPrice;
                           sql = "UPDATE tbl_sales SET qty_sold = ?, t_revenue = ? WHERE sale_id = ?";
                           conf.updateRecord(sql, newQty, newRevenue, salesId);
                           break;
                       }
                   } else {
                       System.out.println("Invalid input. Please enter a valid numeric quantity.");
                       sc.next();
                   }
               }
           }
           System.out.println("");
       } catch (SQLException e) {
           System.out.println("Error: " + e.getMessage());
       }
       System.out.println("Sales Updated successfully!!!");
   }


    public void deleteProduct() {
       System.out.println("\nAdministrator authorization is required to delete sales.");
       System.out.print("\nEnter administrator password: ");
       String adminPassword = sc.nextLine();

       // Replace "admin123" with your actual admin password logic
       if (!adminPassword.equals("admin123")) {
           System.out.println("Authorization failed! Contact the administrator.");
           return;
       }

       int productId;
       System.out.print("Enter Product ID to Delete: ");
       while (true) {
           if (sc.hasNextInt()) {
               productId = sc.nextInt();
               sc.nextLine(); 

               String sql = "SELECT sale_id FROM tbl_sales WHERE sale_id = ?";
               if (conf.getSingleValue(sql, productId) != 0) {
                   break; 
               } else {
                   System.out.print("Product with ID " + productId + " does not exist. "
                           + "\nPlease try again: ");
               }
           } else {
               System.out.print("Invalid input. Please enter a numeric Product ID."
                       + "\nPlease try again: ");
               sc.next(); 
           }
       }
       String qry = "DELETE FROM tbl_sales WHERE sale_id = ?";
       conf.deleteRecord(qry, productId);
       System.out.println("Product Deleted successfully!!!");
   }

}

