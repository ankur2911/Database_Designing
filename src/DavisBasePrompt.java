import common.InitialDatabaseHelper;

import java.util.Scanner;

import static dbCommands.QueryParser.isExit;
import static dbCommands.QueryParser.parseCommand;

public class DavisBasePrompt {

	
	
    private static Scanner scanner = new Scanner(System.in).useDelimiter(";");

    public static void main(String[] args) {

        InitialDatabaseHelper.InitialDatabase();
        splashScreen();

        while(!isExit) {
            System.out.print("davisql> ");
            String command = scanner.next().replace("\n", "").replace("\r", " ").trim().toLowerCase();
            parseCommand(command);
        }
    }

    private static void splashScreen() {
        System.out.println("Welcome to DavisBase");
        showversion();
        System.out.println("\nType 'help;' to display the supported commands.");
        System.out.println("--------------------------------------------------");
    }
    
    
    public static void showversion() {
        System.out.println("Davis Base Version v1.0");
        System.out.println("©Fall 2017 Ankur - axs178532");
    }
}