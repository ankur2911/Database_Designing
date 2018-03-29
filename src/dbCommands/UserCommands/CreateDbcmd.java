package dbCommands.UserCommands;

import common.DatabaseHelper;
import common.Utils;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdOutput.Result;

import java.io.File;

/**
 * Created by ankur
 */

public class CreateDbcmd implements QueryDAO {
    public String databaseName;

    public CreateDbcmd(String databaseName){
        this.databaseName = databaseName;
    }

    @Override
    public Result runCommand() {
        File db = new File(Utils.fetchDbPath(this.databaseName));
        boolean createchk = db.mkdir();

        if(!createchk){
            System.out.println(String.format("Unable to create database '%s'", this.databaseName));
            return null;
        }

        Result result = new Result(1);
        return result;
    }

    @Override
    public boolean Validatecmd() {
        boolean dbcheck = DatabaseHelper.getDatabaseHelper().databasecheck(this.databaseName);

        if(dbcheck){
            System.out.println(String.format("Database '%s' already exists", this.databaseName));
            return false;
        }

        return true;
    }
}
