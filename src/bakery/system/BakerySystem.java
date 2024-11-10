
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
            System.out.println("3. Report");
            System.out.println("4. Exit");
            System.out.println("");
            
            System.out.print("Enter Choice: ");
            choice = sc.nextLine();   
            do {
               if (!choice.matches("[1-4]")) {
                   System.out.print("Invalid choice! Please select again : ");
                   choice = sc.nextLine();
               }
            } while (!choice.matches("[1-4]"));
            
           
            switch (choice) {
                case "1": // Inventory 
                    Inventory in = new Inventory();
                    in.inventoryCRUD();
                    break;

                case "2": // Sales 
                    Sales sl = new Sales();
                    sl.salesCRUD();
                    break;
                    
                case "3": // Report
                    SpecificReport sr = new SpecificReport();
                    sr.specificReport();
                    break;

                case "4": // Exit option
                    System.out.println("Exiting the application...");
                    break;         
            }

        } while (!choice.equals("4")); 

        sc.close();
    }
}
    

