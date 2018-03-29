package dbCommands.UserCommands;

import common.DatabaseHelper;
import RandomFilesrc.FileStructure.InternalCondition;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdOutput.Result;
import common.Utils;
import datatypes.basevalues.DataType;
import RandomFilesrc.RandomfileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankur
 */

public class Updatecmd implements QueryDAO {
    public String databaseName;
    public String tableName;
    private String columnName;
    public LiteralClass value;
    public ConditionClass condition;

    public Updatecmd(String db, String tbname, String colname, LiteralClass val, ConditionClass con){
        this.databaseName = db;
        this.tableName = tbname;
        this.columnName = colname;
        this.value = val;
        this.condition = con;
    }

    @Override
    public Result runCommand() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper();

            HashMap<String, Integer> coldatamap = helper.fetchtblDataTypes(this.databaseName, tableName);
            List<String> fetchcol = helper.fetchAllTableCols(this.databaseName, tableName);
            InternalCondition incond = getcon(fetchcol, coldatamap);
            List<Byte> colindex = getcolindex(fetchcol);
            List<Object> updatecol = getcollist(coldatamap);

            int rowcnt = rfm.recchange(databaseName, tableName, incond, colindex, updatecol, false);

            return new Result(rowcnt);
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
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

            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper();

            List<String> fetchcol = helper.fetchAllTableCols(this.databaseName, tableName);
            HashMap<String, Integer> coldatamap = helper.fetchtblDataTypes(this.databaseName, tableName);

            if (this.condition == null) {

                return chkcol(fetchcol, false)
                        && chkdatatype(coldatamap, fetchcol, false);

            } else {

                if (!chkcol(fetchcol, true)) {
                    return false;
                }

                if (!chkcol(fetchcol, false)) {
                    return false;
                }

                if (!chkdatatype(coldatamap, fetchcol, true)) {
                    return false;
                }

                if (!chkdatatype(coldatamap, fetchcol, false)) {
                    return false;
                }
            }

            return true;
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
        }
        return false;
    }

   
    private boolean chkcol(List<String> retrievedColumns, boolean isConditionCheck) {
        boolean columnsValid = true;
        String invalidColumn = "";

        String tableColumn = isConditionCheck ? condition.column : columnName;
        if (!retrievedColumns.contains(tableColumn.toLowerCase())) {
            columnsValid = false;
            invalidColumn = tableColumn;
        }

        if (!columnsValid) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

    private InternalCondition getcon(List<String> fetchcol, HashMap<String, Integer> coldatamap) {
        InternalCondition intcon = new InternalCondition();
        if(condition != null) {
        	intcon.setIndex((byte) fetchcol.indexOf(condition.column));
            byte dataindex = (byte)coldatamap.get(this.condition.column).intValue();
            DataType dtobj = DataType.createSysDT(this.condition.value.value, dataindex);
            intcon.setValue(dtobj);
            intcon.setConditionType(Utils.ConvertFromOperator(condition.operator));
        }
        return intcon;
    }

    private List<Byte> getcolindex(List<String>fetchlist) {
        List<Byte> collist = new ArrayList<>();
        int idx = fetchlist.indexOf(columnName);
        collist.add((byte)idx);

        return collist;
    }
    private boolean chkdatatype(HashMap<String, Integer> coldatamap, List<String> collist, boolean chkcon) {
        String invalcol = "";

        String column = chkcon ? condition.column : columnName;
        LiteralClass colval = chkcon ? condition.value : value;

        if (collist.contains(column)) {
            int dataindex = coldatamap.get(column);

            if (colval.type != Utils.DataTypeToModelDataType((byte)dataindex)) {

                if (Utils.UpdateDataType(colval, dataindex)) {
                    return true;
                }
                invalcol = column;
            }
        }

        boolean valid = invalcol.length() <= 0;
        if (!valid) {
            System.out.println("Some error has occured");
        }

        return valid;
    }

    private List<Object> getcollist(HashMap<String, Integer> coldatamap) {
        List<Object> collist = new ArrayList<>();
        byte dataTypeIndex = (byte) coldatamap.get(columnName).intValue();

        DataType dt = DataType.createSysDT(value.value, dataTypeIndex);
        collist.add(dt);

        return collist;
    }
}
