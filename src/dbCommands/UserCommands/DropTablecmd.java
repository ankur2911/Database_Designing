package dbCommands.UserCommands;

import common.Constants;
import common.DatabaseHelper;
import common.Utils;
import dbCommands.DAO.QueryDAO;
import dbCommands.UserCommands.Deletecmd;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdOutput.Result;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ankur
 */
public class DropTablecmd implements QueryDAO {

    public String databaseName;
    public String tableName;

    public DropTablecmd(String dbname, String tbname) {
        this.databaseName = dbname.trim();
        this.tableName = tbname.trim();
    }

    @Override
    public Result runCommand() {

        ArrayList<ConditionClass> conList = new ArrayList<>();
        conList.add(ConditionClass.CreateCondition(String.format("database_name = '%s'", this.databaseName)));
        conList.add(ConditionClass.CreateCondition(String.format("table_name = '%s'", this.tableName)));

        QueryDAO deleteQuery = new Deletecmd(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, conList, true);
        deleteQuery.runCommand();

        deleteQuery  = new Deletecmd(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conList, true);
        deleteQuery.runCommand();

        File tab = new File(String.format("%s/%s/%s%s", Constants.DATA_DIRNAME, this.databaseName, this.tableName, Constants.FILE_EXTENSION));

        if(!Utils.RecDelete(tab)){
        
            System.out.println("Some error has occured1");
            return null;
        }
        return new Result(1);
    }

    @Override
    public boolean Validatecmd() {
        if(!DatabaseHelper.getDatabaseHelper().tableExists(this.databaseName, this.tableName)){
        	System.out.println(this.databaseName +" "+ this.tableName);
            System.out.println("Some error has occured3");
            return false;
        }

        return true;
    }
}
