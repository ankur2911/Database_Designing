package dbCommands.UserCommands;

import common.Constants;
import common.DatabaseHelper;
import common.Utils;
import dbCommands.Querycommand;
import dbCommands.DAO.QueryDAO;
import dbCommands.UserCommands.Deletecmd;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdOutput.Result;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ankur
 */
public class DropDbcmd implements QueryDAO {
    public String databaseName;

    public DropDbcmd(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public Result runCommand() {

        File db = new File(Utils.fetchDbPath(databaseName));
        ConditionClass con = ConditionClass.CreateCondition(String.format("database_name = '%s'", this.databaseName));
        ArrayList<ConditionClass> conditions = new ArrayList<>();
        conditions.add(con);
        QueryDAO delQuery = new Deletecmd(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, conditions, true);
        delQuery.runCommand();
        boolean flag = true;
        delQuery = new Deletecmd(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conditions, flag);
        delQuery.runCommand();

        boolean delchk = Utils.RecDelete(db);

        if(!delchk){
            System.out.println("Some error has occured");
            return null;
        }

        if(Querycommand.CurrentDatabaseName == this.databaseName){
            Querycommand.CurrentDatabaseName = "";
        }

        Result res = new Result(1);
        return res;
    }

    @Override
    public boolean Validatecmd() {
        boolean dbchk = DatabaseHelper.getDatabaseHelper().databasecheck(this.databaseName);

        if(!dbchk){
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }
}
