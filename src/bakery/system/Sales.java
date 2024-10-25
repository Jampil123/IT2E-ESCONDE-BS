
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
        Sales sl = new Sales();
        String choice;
        
        do {
            System.out.println("--------------------------------");
            System.out.println("======= Sales Management =======");
            System.out.println("--------------------------------");
            
            System.out.println("1. ADD Sales Record");
            System.out.println("2. VIEW Sales Records");
            System.out.println("3. UPDATE Sales Record");
            System.out.println("4. DELETE Sales Record");
            System.out.println("5. BACK to Main Menu");

            System.out.print("\nEnter Action: ");
            choice = sc.nextLine();
            
            switch (choice) {
                case "1":
                    sl.addSales();
                    break;

                case "2":
                    sl.viewSales();
                    break;

                case "3":
                    sl.updateSales();
                    break;

                case "4":
                    sl.deleteProduct();
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
    private void addSales(){
        System.out.print("Enter product ID: ");
        int pid = sc.nextInt();
        
        try{
            PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_product WHERE p_id = ?");
            
            search.setInt(1, pid);
            ResultSet result = search.executeQuery();
            
            String pname = result.getString("p_name");
            double price = result.getDouble("p_price");
            int inventoryQTY = result.getInt("p_qty");
            
            System.out.println("Selected product: "+pname
                    + "\nPrice: "+price
                    + "\nQuantity: "+inventoryQTY);
            
            System.out.print("\nEnter quantity sold: ");
            int qty = sc.nextInt();
            
            double revenue = qty * result.getDouble("p_price");
            
            sql = "INSERT INTO tbl_sales (p_name, qty_sold, s_date, s_price, t_revenue) VALUES (?, ?, ?, ?, ?)";
            conf.addRecord(sql, pname, qty, currDate.toString(), price, revenue);
            
            // Update inventory quantity
            int updatedQTY = inventoryQTY-qty;
            
            if(updatedQTY > 0){
                String deductQTY = "UPDATE tbl_product SET p_qty = ? WHERE p_id = ?";
                conf.updateRecord(deductQTY, updatedQTY, pid);
            } else if(updatedQTY == 0){
                String deductQTY = "UPDATE tbl_product SET p_qty = ?, p_status = ? WHERE p_id = ?";
                conf.updateRecord(deductQTY, updatedQTY,"Out of Stock", pid);
            }
            result.close();
        } catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    
    private void viewSales(){
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                                 SALES                                                     |");
        
        String sqlQuery = "SELECT sale_id, p_name, qty_sold, s_date, s_price, t_revenue FROM tbl_sales";
        String[] columnHeaders = {"ID", "Product Name", "Quantity Sold", "Sale Date", "Price", "Revenue"};
        String[] columnNames = {"sale_id", "p_name", "qty_sold", "s_date", "s_price", "t_revenue"};
        
        conf.viewRecords(sqlQuery, columnHeaders, columnNames);
        
        try{
            PreparedStatement getColumn = conf.connectDB().prepareStatement("SELECT t_revenue FROM tbl_sales");
            ResultSet getRow = getColumn.executeQuery();
            
            double totalRevenue = 0;
            
            while(getRow.next()){
                totalRevenue += getRow.getDouble("t_revenue");
            }
            
            System.out.println("\nTotal revenue: "+totalRevenue);
            System.out.println("");
            getRow.close();
        } catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    
    private void updateSales(){
        System.out.print("Enter Sales ID to update: ");
        int salesId = sc.nextInt();
        sc.nextLine(); 
        
        // Check if the Product Id Exist
        try{
            PreparedStatement search = conf.connectDB().prepareStatement("SELECT * FROM tbl_sales WHERE sale_id = ?");
            
            search.setInt(1, salesId);
            ResultSet result = search.executeQuery();
            
                if (!result.next() ) {
                System.out.println("Product with ID " + salesId + " does not exist. Please try again.");
                return; 
            }
            
        } catch(SQLException e){
            
        }
    }
    public void deleteProduct(){
        Scanner s = new Scanner(System.in);

        System.out.print("Enter ID to Delete: ");
        int sid = s.nextInt();

        String qry = "DELETE FROM tbl_sales WHERE sale_id = ?";

        conf.deleteRecord(qry, sid);  
    }
}

