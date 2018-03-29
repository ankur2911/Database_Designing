package dbCommands.UserCommands;

import common.Constants;
import common.DatabaseHelper;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdOutput.Result;
import dbCommands.UserCommands.Selectcmd;

import java.util.ArrayList;

/**
 * Created by ankur
 */
public class ShowTablecmd implements QueryDAO {

    public String databaseName;

    public ShowTablecmd(String db) {
        this.databaseName = db;
    }

    public Result runCommand() {
        ArrayList<String> col = new ArrayList<>();
        col.add("table_name");

        ConditionClass cond = ConditionClass.CreateCondition(String.format("database_name = '%s'", this.databaseName));
        ArrayList<ConditionClass> conditionList = new ArrayList<>();
        conditionList.add(cond);

        QueryDAO query = new Selectcmd(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, col, conditionList, false);
        if (query.Validatecmd()) {
            return query.runCommand();
        }

        return null;
    }

    public boolean Validatecmd() {
        boolean chkdb = DatabaseHelper.getDatabaseHelper().databasecheck(this.databaseName);
        if(!chkdb){
            System.out.println("Some error has occured");
        }
        return chkdb;
    }
}
