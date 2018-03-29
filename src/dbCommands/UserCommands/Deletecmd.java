package dbCommands.UserCommands;

import common.DatabaseHelper;
import common.Utils;
import datatypes.basevalues.DataType;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.InternalCondition;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdOutput.Result;
import dbCommands.DAO.QueryDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankur
 */

public class Deletecmd implements QueryDAO {
    public String databaseName;
    public String tableName;
    public ArrayList<ConditionClass> conditions;
    public boolean isInternal = false;

    public Deletecmd(String dbname, String tabname, ArrayList<ConditionClass> conditions){
        this.databaseName = dbname;
        this.tableName = tabname;
        this.conditions = conditions;
    }

    public Deletecmd(String dbname, String tabname, ArrayList<ConditionClass> conditions, boolean intchk){
        this.databaseName = dbname;
        this.tableName = tabname;
        this.conditions = conditions;
        this.isInternal = intchk;
    }

    @Override
    public Result runCommand() {

        try {
            int rowCount;
            RandomfileManager rfm = new RandomfileManager();

            if (conditions == null) {
                rowCount = rfm.removerec(databaseName, tableName, (new ArrayList<>()));
            } else {
                List<InternalCondition> conditionList = new ArrayList<>();
                InternalCondition internalCondition;

                for (ConditionClass condition : this.conditions) {
                    internalCondition = new InternalCondition();
                    List<String> retrievedColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableCols(this.databaseName, tableName);
                    int idx = retrievedColumns.indexOf(condition.column);
                    internalCondition.setIndex((byte) idx);

                    DataType dataType = DataType.CreateDT(condition.value);
                    internalCondition.setValue(dataType);

                    internalCondition.setConditionType(Utils.ConvertFromOperator(condition.operator));
                    conditionList.add(internalCondition);
                }

                rowCount = rfm.removerec(databaseName, tableName, conditionList);

            }

            return new Result(rowCount, this.isInternal);
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Some error has occured2");
        }
        return null;
    }

    @Override
    public boolean Validatecmd() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            if (!rfm.tabchk(this.databaseName, tableName)) {
                System.out.println("Some error has occured");
                return false;
            }

            if (this.conditions != null) {
                List<String> fetchColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableCols(this.databaseName, tableName);
                HashMap<String, Integer> coldatamap = DatabaseHelper.getDatabaseHelper().fetchtblDataTypes(this.databaseName, tableName);

                for (ConditionClass condition : this.conditions) {
                    if (!chkcolValidity(fetchColumns)) {
                        return false;
                    }

                    if (!Utils.checkConditionValueDataTypeValidity(coldatamap, fetchColumns, condition)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Some error has occured");
            return false;
        }
        return true;
    }


    private boolean chkcolValidity(List<String> retrievedColumns) {
        boolean columnsValid = true;
        String invalidColumn = "";

        for (ConditionClass condition : this.conditions) {
            String tableColumn = condition.column;
            if (!retrievedColumns.contains(tableColumn.toLowerCase())) {
                columnsValid = false;
                invalidColumn = tableColumn;
            }

            if (!columnsValid) {
                System.out.println("Some error has occured");
                return false;
            }
        }

        return true;
    }
}
