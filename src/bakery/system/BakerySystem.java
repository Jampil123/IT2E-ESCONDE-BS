
package bakery.system;

import java.util.Scanner;

public class BakerySystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
     
        String choice;

        do {
            // Main menu
            System.out.println("================================================================================================================================================================");
            System.out.println("\n+----------------------------------------------------------+"
                             + "\n|               Bakery Management System                   |"
                             + "\n+----------------------------------------------------------+"
                             + "\n| 1.   |   Inventory                                       |"
                             + "\n| 2.   |   Sales                                           |"
                             + "\n| 3.   |   Report                                          |"
                             + "\n| 4.   |   Exit                                            |"
                             + "\n|----------------------------------------------------------|");
            System.out.print("| Enter Action: ");
            choice = sc.nextLine();   
            System.out.println("+----------------------------------------------------------+");
           
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
    

