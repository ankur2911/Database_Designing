package dbCommands.UserCommands;

import common.Constants;
import common.DatabaseHelper;
import datatypes.basevalues.DataType;
import common.Utils;
import datatypes.*;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.DataRecord;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdOutput.Result;

import java.util.*;

public class Insertcmd implements QueryDAO {
    public String tableName;
    public ArrayList<String> columns;
    private ArrayList<LiteralClass> values;
    public String databaseName;

    public Insertcmd(String databaseName, String tableName, ArrayList<String> columns, ArrayList<LiteralClass> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
        this.databaseName = databaseName;
    }



    @Override
    public boolean Validatecmd() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            if (!rfm.tabchk(this.databaseName, tableName)) {
                System.out.println("Some error has occured");
                return false;
            }

            List<String> fetchcol = DatabaseHelper.getDatabaseHelper().fetchAllTableCols(this.databaseName, tableName);
            HashMap<String, Integer> colmapping = DatabaseHelper.getDatabaseHelper().fetchtblDataTypes(this.databaseName, tableName);

            if (columns == null) {
                if (values.size() > fetchcol.size()) {
                    System.out.println("Some error has occured");
                    return false;
                }

                Utils utils = new Utils();
                if (!utils.checkValidity(colmapping, fetchcol, values)) {
                    return false;
                }
            } else {
                if (columns.size() > fetchcol.size()) {
                    System.out.println("Some error has occured");
                    return false;
                }

                boolean colvalidchk = chkcolval(fetchcol);
                if (!colvalidchk) {
                    return false;
                }

                boolean areColumnsDataTypeValid = colchk(colmapping);
                if (!areColumnsDataTypeValid) {
                    return false;
                }
            }

            boolean isNullConstraintValid = constraintchk(fetchcol);
            if (!isNullConstraintValid) {
                return false;
            }

            boolean pkchk = pkchkfunction(fetchcol);
            if (!pkchk) {
                return false;
            }
        }
        catch ( Exception e) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

    @Override
    public Result runCommand() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            List<String> fetchcols = DatabaseHelper.getDatabaseHelper().fetchAllTableCols(this.databaseName, tableName);
            HashMap<String, Integer> columnDataTypeMapping = DatabaseHelper.getDatabaseHelper().fetchtblDataTypes(this.databaseName, tableName);

            DataRecord record = new DataRecord();
            createrecord(record.getColVal(), columnDataTypeMapping, fetchcols);

            int id = fetchid(fetchcols);
            record.setRowId(id);
            record.populateSize();

            Result res = null;
            boolean status = rfm.insertRe(this.databaseName, tableName, record);
            if (status) {
                res = new Result(1);
            } else {
                System.out.println("Some error has occured");
            }

            return res;
        }
        catch ( Exception e) {
            System.out.println("Some error has occured");
        }
        return null;
    }
    private boolean colchk(HashMap<String, Integer> colmap) {
        return dtypevalchk(colmap);
    }

    private boolean chkcolval(List<String> retrievedColumns) {
        boolean columnsValid = true;
        String invalidColumn = "";

        for (String tableColumn : columns) {
            if (!retrievedColumns.contains(tableColumn.toLowerCase())) {
                columnsValid = false;
                invalidColumn = tableColumn;
                break;
            }
        }

        if (!columnsValid) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

    private boolean constraintchk(List<String> fetchcols)   {
        HashMap<String, Integer> collist = new HashMap<>();

        if (columns != null) {
            for (int i = 0; i < columns.size(); i++) {
            	collist.put(columns.get(i), i);
            }
        }
        else {
            for (int i = 0; i < values.size(); i++) {
            	collist.put(fetchcols.get(i), i);
            }
        }

        return DatabaseHelper.getDatabaseHelper().checkNull(this.databaseName, tableName, collist);
    }

    private boolean pkchkfunction(List<String> fetchcols)   {

        String pkcol = DatabaseHelper.getDatabaseHelper().getpk(databaseName, tableName);
        List<String> collist = (columns != null) ? columns : fetchcols;

        if (pkcol.length() > 0) {
                if (collist.contains(pkcol.toLowerCase())) {
                    int pkindex = collist.indexOf(pkcol);
                    if (DatabaseHelper.getDatabaseHelper().checkpk(this.databaseName, tableName, Integer.parseInt(values.get(pkindex).value))) {
                        System.out.println("Some error has occured");
                        return false;
                    }
                }
        }

        return true;
    }

    private boolean dtypevalchk(HashMap<String, Integer> colmap) {
        String colchk = "";

        for (String columnName : columns) {
            int dtindex = colmap.get(columnName);
            int idx = columns.indexOf(columnName);
            LiteralClass literal = values.get(idx);

            if (literal.type != Utils.DataTypeToModelDataType((byte)dtindex)) {
                if (Utils.UpdateDataType(literal, dtindex)) {
                    continue;
                }

                colchk = columnName;
                break;
            }
        }

        boolean valid = colchk.length() <= 0;

        if (!valid) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

    private void createrecord(List<Object> collist, HashMap<String, Integer> colmap, List<String> fetchcol) {
        for (int i=0; i < fetchcol.size(); i++) {
            String column = fetchcol.get(i);

            if (columns != null) {
                if (columns.contains(column)) {
                    Byte dt = (byte)colmap.get(column).intValue();

                    int idx = columns.indexOf(column);

                    datatypes.basevalues.DataType obj = getdataobj(dt);
                    String val = values.get(idx).toString();

                    obj.setValue(getDataTypeValue(dt, val));
                    collist.add(obj);
                } else {
                    Byte dt = (byte)colmap.get(column).intValue();
                    DataType obj = getdataobj(dt);
                    obj.setNull(true);
                    collist.add(obj);
                }
            }
            else {

                if (i < values.size()) {
                    Byte dt = (byte) colmap.get(column).intValue();

                    int columnIndex = fetchcol.indexOf(column);
                    DataType obj = getdataobj(dt);
                    String val = values.get(columnIndex).toString();

                    obj.setValue(getDataTypeValue(dt, val));
                    collist.add(obj);
                }
                else {
                    Byte dt = (byte)colmap.get(column).intValue();
                    DataType obj = getdataobj(dt);
                    obj.setNull(true);
                    collist.add(obj);
                }
            }
        }
    }

    private DataType getdataobj(byte dt) {

        switch (dt) {
            case Constants.tinyInt: {
                return new TinyInt();
            }
            case Constants.smallInt: {
                return new SmallInt();
            }
            case Constants.INT: {
                return new IntType();
            }
            case Constants.BIGINT: {
                return new BigInt();
            }
            case Constants.REAL: {
                return new Real();
            }
            case Constants.DOUBLE: {
                return new DoubleType();
            }
            case Constants.DATE: {
                return new DateType();

            }
            case Constants.DATETIME: {
                return new DateTime();
            }
            case Constants.TEXT: {
                return new TextType();
            }
            default: {
                return new TextType();
            }
        }
    }

    private Object getDataTypeValue(byte dt, String val) {

        switch (dt) {
            case Constants.tinyInt: {
                return Byte.parseByte(val);
            }
            case Constants.smallInt: {
                return Short.parseShort(val);
            }
            case Constants.INT: {
                return Integer.parseInt(val);
            }
            case Constants.BIGINT: {
                return Long.parseLong(val);
            }
            case Constants.REAL: {
                return Float.parseFloat(val);
            }
            case Constants.DOUBLE: {
                return Double.parseDouble(val);
            }
            case Constants.DATE: {
                return Utils.getDateEpoc(val, true);
            }
            case Constants.DATETIME: {
                return Utils.getDateEpoc(val, false);
            }
            case Constants.TEXT: {
                return val;
            }
            default: {
                return val;
            }
        }
    }

    private int fetchid (List<String> retrievedList)   {
        DatabaseHelper obj = DatabaseHelper.getDatabaseHelper();
        int rowCount = obj.getCount(this.databaseName, tableName);
        String primaryKeyColumnName = obj.getpk(databaseName, tableName);
        if (primaryKeyColumnName.length() > 0) {
            int primaryKeyIndex = (columns != null) ? columns.indexOf(primaryKeyColumnName) : retrievedList.indexOf(primaryKeyColumnName);
            return Integer.parseInt(values.get(primaryKeyIndex).value);
        }
        else {
            return rowCount + 1;
        }
    }
}