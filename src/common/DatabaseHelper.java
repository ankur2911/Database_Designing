package common;

import datatypes.IntType;
import datatypes.TextType;
import datatypes.basevalues.DataType;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.DataRecord;
import RandomFilesrc.FileStructure.InternalCondition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseHelper {

    private static DatabaseHelper databaseHelper = null;

    public static DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null) {
            return new DatabaseHelper();
        }
        return databaseHelper;
    }

    private RandomfileManager rfm;

    private DatabaseHelper() {
    	rfm = new RandomfileManager();
    }

    public boolean databasecheck(String dbName) {

        if (dbName == null || dbName.length() == 0) {
            System.out.println("\nType 'help;' to display supported commands.");
            return false;
        }

        return new RandomfileManager().dbchk(dbName);
    }

    
    public boolean checkNull(String dbName, String tbName, HashMap<String, Integer> colMap)   {

        List<InternalCondition> cdts = new ArrayList<>();
        cdts.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
        cdts.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));

        List<DataRecord> records = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, cdts,null, false);

        for (DataRecord record : records) {
            Object nullValueObject = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_IS_NULLABLE);
            Object object = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);

            String isNullStr = ((DataType) nullValueObject).getStringValue().toUpperCase();
            boolean isNullable = isNullStr.equals("YES");

            if (!colMap.containsKey(((DataType) object).getStringValue()) && !isNullable) {
                System.out.println("Some error has occured");
                return false;
            }

        }

        return true;
    }
    
    public boolean checkpk(String dbName, String tbName, int value)  {
        RandomfileManager manager = new RandomfileManager();
        InternalCondition condition = InternalCondition.CreateCon(0, InternalCondition.EQUALS, new IntType(value));

        List<DataRecord> records = manager.fetchrec(dbName, tbName, condition,null, false);;
        return records.size() > 0;
    }
    
    public int getCount(String dbName, String tbName)  {
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));

        List<DataRecord> vallist = rfm.fetchrec(dbName, tbName, conditions, null, true);
        
        int recordCount = 0;

        if(vallist.size() > 0) {
            DataRecord record = vallist.get(0);
            Object object = record.getColVal().get(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_RECORD_COUNT);
            recordCount = Integer.valueOf(((DataType) object).getStringValue());
        }

        return recordCount;
    }
    public boolean tableExists(String dbName, String tbName) {
        if (tbName == null || dbName == null || tbName.length() == 0 || dbName.length() == 0) {
            System.out.println("\nType 'help;' to display supported commands.");
            return false;
        }

        return new RandomfileManager().tabchk(dbName, tbName);
    }

    public List<String> fetchAllTableCols(String dbName, String tbName)   {
        List<String> columnNames = new ArrayList<>();
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));

        List<DataRecord> records = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conditions,null, false);

        for (DataRecord record : records) {
            Object object = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            columnNames.add(((DataType) object).getStringValue());
        }

        return columnNames;
    }

   


    public String getpk(String dbName, String tbName)   {
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_KEY, InternalCondition.EQUALS, new TextType(InitialDatabaseHelper.PRIMARYKEY_IDENTIFIER)));

        List<DataRecord> rcds = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conditions, null,true);
        String columnName = "";
        if(rcds.size() > 0) {
            DataRecord record = rcds.get(0);
            Object object = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            columnName = ((DataType) object).getStringValue();
        }

        return columnName;
    }

    

    public HashMap<String, Integer> fetchtblDataTypes(String dbName, String tbName)   {
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
        conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));

        List<DataRecord> records = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, conditions,null, false);
        HashMap<String, Integer> columDataTypeMapping = new HashMap<>();

        for (DataRecord record : records) {
            Object object = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            Object dataTypeObject = record.getColVal().get(InitialDatabaseHelper.COLUMNS_TABLE_SCHEMA_DATA_TYPE);

            String columnName = ((DataType) object).getStringValue();
            int columnDataType = Utils.stringToDataType(((DataType) dataTypeObject).getStringValue());
            columDataTypeMapping.put(columnName.toLowerCase(), columnDataType);
        }

        return columDataTypeMapping;
    }
    
}
