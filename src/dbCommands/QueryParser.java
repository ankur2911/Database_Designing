package dbCommands;

import java.util.ArrayList;

import dbCommands.DAO.QueryDAO;
import dbCommands.UserCommands.CreateDbcmd;
import dbCommands.UserCommands.CreateTablecmd;
import dbCommands.UserCommands.Deletecmd;
import dbCommands.UserCommands.DropDbcmd;
import dbCommands.UserCommands.DropTablecmd;
import dbCommands.UserCommands.ShowDbcmd;
import dbCommands.UserCommands.ShowTablecmd;
import dbCommands.UserCommands.Updatecmd;
import dbCommands.UserCommands.Insertcmd;
import dbCommands.UserCommands.Selectcmd;
import dbCommands.UserCommands.UseDbcmd;
import dbCommands.Resstructure.cmdCondition.ColClass;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdCondition.DataTypes;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdCondition.OpClass;

/*
 * Created by ankur
 */
public class QueryParser {

	static final String SELECT = "SELECT";
    static final String DROP_TABLE = "DROP TABLE";
    static final String DROP_DATABASE = "DROP DATABASE";
    static final String HELP= "HELP";
    static final String EXIT = "EXIT";
    static final String SHOW_TABLES = "SHOW TABLES";
    static final String SHOW_DATABASES = "SHOW DATABASES";
    static final String INSERT = "INSERT INTO";
    static final String DELETE = "DELETE FROM";
    static final String UPDATE = "UPDATE";
    static final String CREATE_TABLE = "CREATE TABLE";
    static final String CREATE_DATABASE = "CREATE DATABASE";
    static final String USE_DATABASE = "USE";
    public static final String NO_DATABASE_SELECTED_MESSAGE = "No database selected";
    public static final String USE_HELP_MESSAGE = "\nType 'help;' to display supported commands.";
	
	
    public static boolean isExit = false;

    public static void parseCommand(String userquery) {
       
      if(userquery.toLowerCase().equals("EXIT".toLowerCase())){

            System.out.println("Exiting Database...");
            isExit = true;
        }
        else if(userquery.toLowerCase().equals("SHOW DATABASES".toLowerCase())){
            QueryDAO query = new ShowDbcmd();
            
            Querycommand.ExecuteQuery(query);
        }
        else if(userquery.toLowerCase().equals("HELP".toLowerCase())){
            Querycommand.helpcommand();
        }
        
        else if(userquery.toLowerCase().equals("SHOW TABLES".toLowerCase())){
        	
        	if(!Querycommand.CurrentDatabaseName.equals("")){
            QueryDAO query = new ShowTablecmd(Querycommand.CurrentDatabaseName);
            Querycommand.ExecuteQuery(query);
        	}else {
        		System.out.println("Currently No Database selected ");
        	}
            
        }
      
        else if(userquery.toLowerCase().startsWith("USE".toLowerCase())){
          

            String databaseName = (userquery.substring(QueryParser.USE_DATABASE.length())).trim();
            QueryDAO query =  new UseDbcmd(databaseName);
            Querycommand.ExecuteQuery(query);
        }
        
        
        else if(userquery.toLowerCase().startsWith(QueryParser.DROP_DATABASE.toLowerCase())){


            String databaseName = userquery.substring(QueryParser.DROP_DATABASE.toLowerCase().length());
            try {
            QueryDAO query = new DropDbcmd(databaseName.trim());
            Querycommand.ExecuteQuery(query);
            }catch(Exception e) {
            	e.printStackTrace();
            }
        }
        else if(userquery.toLowerCase().startsWith(QueryParser.SELECT.toLowerCase())){
        	
        	 if(Querycommand.CurrentDatabaseName.equals("")){
                 System.out.println("Currently No Database selected ");
                 return;
             }

            int index = userquery.toLowerCase().indexOf("from");
            if(index == -1) {
                System.out.println("Some error has occured");
                return;
            }

            String attributes = userquery.substring(QueryParser.SELECT.length(), index).trim();
            String remquery = userquery.substring(index + "from".length());
            ArrayList<String> columns = new ArrayList<String>();
            for(String attribute : attributes.split(",")){
                columns.add(attribute.trim());
            }
            boolean SelectAll = false;

            
            if(columns.size() == 1 && columns.get(0).equals("*")) {
                SelectAll = true;
                columns = null;
            }
            index = remquery.toLowerCase().indexOf("where");
            String tableName = remquery.substring(0, index+1);

            String condition = remquery.substring(index + "where".length());
            if(index == -1) {
                String tableName1 = remquery.trim();
                QueryDAO query = new Selectcmd(Querycommand.CurrentDatabaseName, tableName1, columns, null, SelectAll);
                Querycommand.ExecuteQuery(query);
            }

          
           
            
            else if(condition.equals("")){
               
               
                QueryDAO query = new Selectcmd(Querycommand.CurrentDatabaseName, tableName.trim(), columns, null, SelectAll);
                Querycommand.ExecuteQuery(query);
            }else {
            	ConditionClass conditioncmd = ConditionClass.CreateCondition(condition);
                if(conditioncmd != null) {
                	ArrayList<ConditionClass> conditionList = new ArrayList<>();
                    conditionList.add(conditioncmd);
                    //System.out.println("in condition case");
                    tableName= tableName.trim().split(" ")[0];
                    //System.out.println(Querycommand.CurrentDatabaseName +" " +tableName.trim()+" "+ columns +" "+ conditionList+" "+ SelectAll);
                    QueryDAO query = new Selectcmd(Querycommand.CurrentDatabaseName, tableName.trim(), columns, conditionList, SelectAll);
                    Querycommand.ExecuteQuery(query);
                    
                }
            }


        }
        
        else if(userquery.toLowerCase().startsWith(QueryParser.DROP_TABLE.toLowerCase())){

            String tableName = userquery.substring(QueryParser.DROP_TABLE.length());
            QueryDAO query = new DropTablecmd(Querycommand.CurrentDatabaseName, tableName);
            Querycommand.ExecuteQuery(query);
        }
      
      
        else if(userquery.toLowerCase().startsWith(QueryParser.INSERT.toLowerCase())){


            String tabname = "";
            String columns = "";

            int valueIndex = userquery.toLowerCase().indexOf("values");
            if(valueIndex == -1) {
                System.out.println("Some error has occured");
                return;
            }

            String columnOptions = userquery.toLowerCase().substring(0, valueIndex);
            int openIndex = columnOptions.indexOf("(");

            if(openIndex != -1) {
            	tabname = userquery.substring(QueryParser.INSERT.length(), openIndex).trim();
                int closeIndex = userquery.indexOf(")");
                if(closeIndex <0) {
                    System.out.println("Some error has occured");
                    return;
                }

                columns = userquery.substring(openIndex + 1, closeIndex).trim();
            }

            if(tabname.equals("")) {
            	tabname = userquery.substring(QueryParser.INSERT.length(), valueIndex).trim();
            }

            String values = userquery.substring(valueIndex + "values".length()).trim();
            if(!values.startsWith("(")){
                System.out.println("Some error has occured");
                return;
            }

            if(!values.endsWith(")")){
                System.out.println("Some error has occured");
                return;
            }

            values = values.substring(1, values.length()-1);
           
            if(Querycommand.CurrentDatabaseName.equals("")){
                System.out.println(QueryParser.NO_DATABASE_SELECTED_MESSAGE);
                return;
            }

            QueryDAO query = null;
            ArrayList<String> columnlist = null;
            ArrayList<LiteralClass> valueslist = new ArrayList<>();

            if(!columns.equals("")) {
            	columnlist = new ArrayList<>();
                String[] columnList = columns.split(",");
                for(String column : columnList){
                	columnlist.add(column.trim());
                }
            }

            for(String value : values.split(",")){
            	LiteralClass literal = LiteralClass.CreateLiteral(value.trim());
                if(literal == null) return;
                valueslist.add(literal);
            }

            if(columnlist != null && columnlist.size() != valueslist.size()){
                System.out.println("Some error has occured");
                return;
            }
            
         
            query =new Insertcmd(Querycommand.CurrentDatabaseName, tabname, columnlist, valueslist);
            Querycommand.ExecuteQuery(query);
        }
        
        else if(userquery.toLowerCase().startsWith(QueryParser.UPDATE.toLowerCase())){

        	 String conditions = "";
             int setIndex = userquery.toLowerCase().indexOf("set");
             if(setIndex == -1) {
                 System.out.println("Some error has occured");
                 return;
             }

             String tbName = userquery.substring(QueryParser.UPDATE.length(), setIndex).trim();
             String clauses = userquery.substring(setIndex + "set".length());
             int whereIndex = userquery.toLowerCase().indexOf("where");
             if(whereIndex == -1){
            	 //QueryDAO query = Querycommand.Updatecmd(tableName, clauses, conditions);
                 //Querycommand.ExecuteQuery(query);
            	 System.out.println("Please include a where clause while updating");
                 return;
             }

             clauses = userquery.substring(setIndex + "set".length(), whereIndex).trim();
             conditions = userquery.substring(whereIndex + "where".length());
             
           
             
             if(Querycommand.CurrentDatabaseName.equals("")){
                 System.out.println(QueryParser.NO_DATABASE_SELECTED_MESSAGE);
                 return;
             }

             QueryDAO query;

             ConditionClass clause = ConditionClass.CreateCondition(clauses);
             if(clause == null) return;

             if(clause.operator != OpClass.EQUALS){
                 System.out.println("Some error has occured");
                 return;
             }

             if(conditions.equals("")){
                 query = new Updatecmd(Querycommand.CurrentDatabaseName, tbName, clause.column, clause.value, null);
                 
             }

             ConditionClass condition = ConditionClass.CreateCondition(conditions);
             if(condition == null) return;

             query = new Updatecmd(Querycommand.CurrentDatabaseName, tbName, clause.column, clause.value, condition);
             
             //
             Querycommand.ExecuteQuery(query);
        }
       
        else if(userquery.toLowerCase().startsWith(QueryParser.CREATE_TABLE.toLowerCase())){

            int openBracketIndex = userquery.toLowerCase().indexOf("(");
            if(openBracketIndex < 0) {
                System.out.println("Some error has occured");
                return;
            }

            if(!userquery.endsWith(")")){
                System.out.println("Some error has occured");
                return;
            }

            String tableName = userquery.substring(QueryParser.CREATE_TABLE.length(), openBracketIndex).trim();
            String columnsPart = userquery.substring(openBracketIndex + 1, userquery.length()-1);
            
            //
            if(Querycommand.CurrentDatabaseName.equals("")){
                System.out.println(QueryParser.NO_DATABASE_SELECTED_MESSAGE);
                return;
            }

            //QueryDAO query;
            boolean hasPrimaryKey = false;
            ArrayList<ColClass> columns = new ArrayList<>();
            String[] columnsList = columnsPart.split(",");

            for(String columnEntry : columnsList){
            	ColClass column = ColClass.CreateCol(columnEntry.trim());
                if(column == null) return;
                columns.add(column);
            }

            for (int i = 0; i < columnsList.length; i++) {
                if (columnsList[i].toLowerCase().endsWith("primary key")) {
                    if (i == 0) {
                        if (columns.get(i).type == DataTypes.INT) {
                            hasPrimaryKey = true;
                        } else {
                            System.out.println("Some error has occured");
                            return;
                        }
                    }
                    else {
                        System.out.println("Some error has occured");
                        return;
                    }

                }
            }
            
            
            
            QueryDAO query = new CreateTablecmd(Querycommand.CurrentDatabaseName, tableName, columns, hasPrimaryKey);
            Querycommand.ExecuteQuery(query);
        } else if(userquery.toLowerCase().startsWith(QueryParser.CREATE_DATABASE.toLowerCase())){


            String databaseName = userquery.substring(QueryParser.CREATE_DATABASE.length());
            QueryDAO query = new CreateDbcmd(databaseName.trim());
            Querycommand.ExecuteQuery(query);
        }
        
        else if(userquery.toLowerCase().startsWith(QueryParser.DELETE.toLowerCase())){


            String tableName = "";
            String conditionquery = "";
            int index = userquery.toLowerCase().indexOf("where");
            if(index == -1) {
                tableName = userquery.substring(QueryParser.DELETE.length()).trim();
                //QueryDAO query = Querycommand.Deletecommand(tableName, condition);
                
                if(Querycommand.CurrentDatabaseName.equals("")){
                    System.out.println(QueryParser.NO_DATABASE_SELECTED_MESSAGE);
                    return;
                }
                QueryDAO query = new Deletecmd(Querycommand.CurrentDatabaseName, tableName, null);
                Querycommand.ExecuteQuery(query);
                return;
            }

            if(tableName.equals("")) {
                tableName = userquery.substring(QueryParser.DELETE.length(), index).trim();
            }

            conditionquery = userquery.substring(index + "where".length());
            
            ConditionClass condition = ConditionClass.CreateCondition(conditionquery);
            if(condition == null) return;

            ArrayList<ConditionClass> conditions = new ArrayList<>();
            conditions.add(condition);
            
            QueryDAO query = new Deletecmd(Querycommand.CurrentDatabaseName, tableName, conditions);
            Querycommand.ExecuteQuery(query);
        }
        else{
            System.out.println("Some error has occured");
        }
    }

}
