package dbCommands.UserCommands;

import common.Constants;
import common.DatabaseHelper;
import datatypes.basevalues.DataType;
import common.InitialDatabaseHelper;
import common.Utils;
import datatypes.TextType;
import javafx.util.Pair;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.DataRecord;
import RandomFilesrc.FileStructure.InternalCondition;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdOutput.Record;
import dbCommands.Resstructure.cmdOutput.Result;
import dbCommands.Resstructure.cmdOutput.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankur
 */

public class Selectcmd implements QueryDAO {
    public String databaseName;
    public String tableName;
    public ArrayList<String> columns;
    private boolean isSelectAll;
    private ArrayList<ConditionClass> conditions = new ArrayList<>();

    public Selectcmd(String databaseName, String tableName, ArrayList<String> columns, ArrayList<ConditionClass> conditions, boolean isSelectAll) {
        this.databaseName = databaseName;
        //System.out.println("table name in constructor : "+ tableName);
        this.tableName = tableName;
        this.columns = columns;
        this.conditions = conditions;
        this.isSelectAll = isSelectAll;
    }

    @Override
    public Result runCommand() {
        try {
            ResultSet resultSet = ResultSet.CreateResultSet();

            ArrayList<Record> rec = fetchdata();
            resultSet.setColumns(this.columns);
            for (Record record : rec) {
                resultSet.addRecord(record);
            }

            return resultSet;
        }
        catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Some error has occured2");
        }
        return null;
    }
    private ArrayList<Record> fetchdata() throws Exception {
        ArrayList<Record> records = new ArrayList<>();
        Pair<HashMap<String, Integer>, HashMap<Integer, String>> idtocol = colnamemap(this.tableName);
        HashMap<String, Integer> columnToIdMap = idtocol.getKey();
        ArrayList<Byte> columnsList = new ArrayList<>();
        List<DataRecord> internalRecords;
        RandomfileManager rfm = new RandomfileManager();

        List<InternalCondition> conditions = new ArrayList<>();
        InternalCondition internalCondition = null;

        if(this.conditions != null){
            for(ConditionClass condition : this.conditions) {
                internalCondition = new InternalCondition();
                if (columnToIdMap.containsKey(condition.column)) {
                    internalCondition.setIndex(columnToIdMap.get(condition.column).byteValue());
                }

                datatypes.basevalues.DataType dt = DataType.CreateDT(condition.value);
                internalCondition.setValue(dt);

                Short opshort = Utils.ConvertFromOperator(condition.operator);
                internalCondition.setConditionType(opshort);
                conditions.add(internalCondition);
            }
        }

        if(this.columns == null) {
            internalRecords = rfm.fetchrec(this.databaseName,
                    this.tableName, conditions, null,false);

            HashMap<Integer, String> idToColumnMap = idtocol.getValue();
            this.columns = new ArrayList<>();
            for (int i=0; i<columnToIdMap.size();i++) {
                if(idToColumnMap.containsKey(i)){
                    columnsList.add((byte)i);
                    this.columns.add(idToColumnMap.get(i));
                }
            }
        }
        else {
            for (String column : this.columns) {
                if (columnToIdMap.containsKey(column)) {
                    columnsList.add(columnToIdMap.get(column).byteValue());
                }
            }

            internalRecords = rfm.fetchrec(this.databaseName,
                    this.tableName, conditions, columnsList, false);
        }

        Byte[] columnIds = new Byte[columnsList.size()];
        int k = 0;
        for(Byte column : columnsList){
            columnIds[k] = column;
            k++;
        }

        HashMap<Integer, String> idcolmap = idtocol.getValue();
        for(DataRecord internalRecord : internalRecords){
            Object[] dataTypes = new DataType[internalRecord.getColVal().size()];
            k=0;
            for(Object columnValue : internalRecord.getColVal()){
                dataTypes[k] = columnValue;
                k++;
            }
            Record record = Record.CreateRecord();
            for(int i=0;i<columnIds.length;i++) {
            	LiteralClass literal;
                if(idcolmap.containsKey((int)columnIds[i])) {
                    literal = LiteralClass.CreateLiteral((DataType)dataTypes[i], Utils.fetchtype(dataTypes[i]));
                    record.put(idcolmap.get((int)columnIds[i]), literal);
                }
            }
            records.add(record);
        }

        return records;
    }
    @Override
    public boolean Validatecmd() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            //System.out.println("check : "+this.databaseName+"   " + tableName);
            if (!rfm.tabchk(this.databaseName, tableName)) {
                System.out.println("Some error has occured1");
                return false;
            }

            Pair<HashMap<String, Integer>, HashMap<Integer, String>> idcolmap = colnamemap(this.tableName);
            HashMap<String, Integer> colmap = idcolmap.getKey();
            HashMap<String, Integer> datamap = DatabaseHelper.getDatabaseHelper().fetchtblDataTypes(this.databaseName, tableName);

            if (conditions != null) {
                List<String> retrievedColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableCols(this.databaseName, tableName);

                for (ConditionClass condition : conditions) {
                    if (!Utils.checkConditionValueDataTypeValidity(datamap, retrievedColumns, condition)) {
                        return false;
                    }
                }
            }

            if (this.columns != null) {
                for (String column : this.columns) {
                    if (!colmap.containsKey(column)) {
                    	//System.out.println(column);
                        System.out.println("Some error has occured3");
                        return false;
                    }
                }
            }

            if (conditions != null) {
                for (ConditionClass condition : conditions) {
                    if (!colmap.containsKey(condition.column)) {
                    	//System.out.println(condition.column);

                        System.out.println("Some error has occured4");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Some error has occured5");
            return false;
        }
        return true;
    }

   


    private Pair<HashMap<String, Integer>, HashMap<Integer, String>> colnamemap(String tableName) throws Exception {
        HashMap<Integer, String> idcolmap = new HashMap<>();
        HashMap<String, Integer> colidmap = new HashMap<>();
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tableName)));

        RandomfileManager rfm = new RandomfileManager();
        List<DataRecord> rec = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conditions,null, false);

        for (int i = 0; i < rec.size(); i++) {
            DataRecord record = rec.get(i);
            Object obj = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            idcolmap.put(i, ((DataType) obj).getStringValue());
            colidmap.put(((DataType) obj).getStringValue(), i);
        }

        return new Pair<>(colidmap, idcolmap);
    }
}
