
package bakery.system;

import java.util.Scanner;

public class BakerySystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
     
        String choice;

        do {
            // Main menu 
            System.out.println("+----------------------------------------------------------+");
            System.out.println("|               Bakery Management System                   |");
            System.out.println("+----------------------------------------------------------+");
            
            System.out.println("1. Inventory");
            System.out.println("2. Sales");
            System.out.println("3. Exit");
            System.out.println("");
            System.out.print("Enter Choice: ");
            choice = sc.nextLine();
            
           
            switch (choice) {
                case "1": // Inventory 
                    Inventory in = new Inventory();
                    in.inventoryCRUD();
                    break;

                case "2": // Sales 
                    Sales sl = new Sales();
                    sl.salesCRUD();
                    break;

                case "3": // Exit option
                    System.out.println("Exiting the application...");
                    break;

                default:
                    System.out.println("Invalid choice! Please select again.");
                    break;
            }

        } while (!choice.equals("3")); 

        sc.close();
    }
}
    

