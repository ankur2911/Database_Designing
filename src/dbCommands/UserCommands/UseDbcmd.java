package dbCommands.UserCommands;

import common.DatabaseHelper;
import dbCommands.Querycommand;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdOutput.Result;

/**
 * Created by ankur
 */

public class UseDbcmd implements QueryDAO {
    public String databaseName;

    public UseDbcmd(String db) {
        this.databaseName = db;
    }

    @Override
    public Result runCommand() {
        Querycommand.CurrentDatabaseName = this.databaseName;
        System.out.println("Database selected");
        return null;
    }

    @Override
    public boolean Validatecmd() {
        boolean chkdb = DatabaseHelper.getDatabaseHelper().databasecheck(this.databaseName);
        if(!chkdb){
            System.out.println("Some error has occured");
        }

        return chkdb;
    }
}
