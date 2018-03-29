package dbCommands;

import dbCommands.DAO.QueryDAO;

import dbCommands.Resstructure.cmdOutput.Result;

/*
 * Created by ankur.
 */

public class Querycommand {



    public static String CurrentDatabaseName = "";



    public static String line(String s, int num) {
        String a = "";
        for(int i=0;i<num;i++) {
            a += s;
        }
        return a;
    }

   


    static void helpcommand() {

    	
    	
    		System.out.println(line("*",80));
		System.out.println("SUPPORTED COMMANDS");

		System.out.println("All commands below are case insensitive");
		System.out.println();
		System.out.println("\tUSE DATABASE databasename;                       Changes current database.");
		System.out.println("\tCREATE DATABASE databasename;                    Creates an empty database.");
		System.out.println("\tSHOW TABLES;                                     Displays all tables in current database.");
		System.out.println("\tCREATE TABLE table_name (                        Creates a table in current database.");
        System.out.println("\t\t<column_name> <datatype> [PRIMARY KEY | NOT NULL]");
		System.out.println("\tSELECT * FROM table_name;                        Display all records in the table.");
		System.out.println("\tSELECT * FROM table_name WHERE rowid = <value>;  Display records whose rowid is <id>.");
		System.out.println("\tDROP TABLE table_name;                           Remove table data and its schema.");
        System.out.println("\tDELETE FROM table_name [WHERE condition];        Deletes a record from a table.");
        System.out.println("\tINSERT INTO table_name                           Inserts a record into the table.");
        System.out.println("\t\t[(<column1>, ...)] VALUES (<value1>, <value2>, ...);");
		System.out.println("\tHELP;                                            Show this help information");
		System.out.println("\tUPDATE table_name set col='val' WHERE col='val'  Update a record in the table.");
		System.out.println("\tEXIT;                                            Exit the program");
		System.out.println();
		
		System.out.println("Please Refer to ReadMe.txt in Webcontent for all the valid command formats with examples");

		System.out.println();
		System.out.println(line("*",80));
    }



  
    public static void ExecuteQuery(QueryDAO query) {
        if(query!= null && query.Validatecmd()){
            Result result = query.runCommand();
            if(result != null){
                result.Display();
            }
        }
    }
}
