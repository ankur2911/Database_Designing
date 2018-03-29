package common;

import datatypes.IntType;
import datatypes.TextType;
import dbCommands.Resstructure.cmdCondition.DataTypes;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.DataRecord;
import RandomFilesrc.FileStructure.InternalColumn;
import RandomFilesrc.FileStructure.InternalCondition;
import RandomFilesrc.FileStructure.PageDef;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankur.
 */
public class InitialDatabaseHelper {

   
    public static final byte COL_TBL_ST_ROWID = 4;
    public static final byte nxt_COL_TBL_ROWID = 5;
    public static final byte TABLES_TABLE_SCHEMA_ROWID = 0;
    public static final byte TABLES_TABLE_SCHEMA_DATABASE_NAME = 1;
    public static final byte TABLES_TABLE_SCHEMA_TABLE_NAME = 2;
    public static final byte TABLES_TABLE_SCHEMA_RECORD_COUNT = 3;
    public static final byte COLUMNS_TABLE_SCHEMA_ROWID = 0;
    public static final byte COLUMNS_TABLE_SCHEMA_DATABASE_NAME = 1;
    public static final byte COLUMNS_TABLE_SCHEMA_TABLE_NAME = 2;
    public static final byte COLUMNS_TABLE_SCHEMA_COLUMN_NAME = 3;
    public static final byte COLUMNS_TABLE_SCHEMA_DATA_TYPE = 4;
    public static final byte COLUMNS_TABLE_SCHEMA_COLUMN_KEY = 5;
    public static final byte COLUMNS_TABLE_SCHEMA_ORDINAL_POSITION = 6;
    public static final byte COLUMNS_TABLE_SCHEMA_IS_NULLABLE = 7;

  

    public static final String PRIMARYKEY_IDENTIFIER = "PRI";

    public static void InitialDatabase() {
        File baseDir = new File("data");
        if(!baseDir.exists()) {
        	System.out.println("test");
            File catalogDirextory = new File("data/catalog");
            if(!catalogDirextory.exists()) {
                if(catalogDirextory.mkdirs()) {
                    new InitialDatabaseHelper().createDatabase();
                }
            }
        }

    }

    public boolean updateSystemColumnsTable(String databaseName, String tableName, int startingRowId, List<InternalColumn> columns) {
        try {
      
            RandomfileManager manager = new RandomfileManager();
            if (columns != null && columns.size() == 0) return false;
            int i = 0;
            for (; i < columns.size(); i++) {
                DataRecord record = new DataRecord();
                record.setRowId(startingRowId++);
                record.getColVal().add(new IntType(record.getId()));
                record.getColVal().add(new TextType(databaseName));
                record.getColVal().add(new TextType(tableName));
                record.getColVal().add(new TextType(columns.get(i).getName()));
                record.getColVal().add(new TextType(columns.get(i).getDataType()));
                record.getColVal().add(new TextType(columns.get(i).getStringIsPrimary()));
                record.getColVal().add(new IntType(i + 1));
                record.getColVal().add(new TextType(columns.get(i).getStringIsNullable()));
                record.populateSize();
                if (!manager.insertRe(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, record)) {
                    break;
                }
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
        }
        return false;
    }
    
    public static int updateRowCount(String dbName, String tbName, int rowCnt) {
        try {
            RandomfileManager rfm = new RandomfileManager();
            List<InternalCondition> conditions = new ArrayList<>();
            conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
            conditions.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));
            List<Byte> updateColumnsIndexList = new ArrayList<>();
            updateColumnsIndexList.add(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_RECORD_COUNT);
            List<Object> updateValueList = new ArrayList<>();
            updateValueList.add(new IntType(rowCnt));
            return rfm.recchange(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, conditions, updateColumnsIndexList, updateValueList, true);
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
        }
        return -1;
    }
    


    // reference taken from stackoverflow and www.journaldev.com
    public boolean createDatabase() {
        try {
            RandomfileManager rfm = new RandomfileManager();
            rfm.createTable(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES + Constants.FILE_EXTENSION);
            rfm.createTable(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS + Constants.FILE_EXTENSION);
            int strtid = this.SysTableupdate(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, 6);
            strtid *= this.SysTableupdate(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, 8);
            if (strtid >= 0) {
                List<InternalColumn> columnslist = new ArrayList<>();
                columnslist.add(new InternalColumn("rowid", DataTypes.INT.toString(), false, false));
                columnslist.add(new InternalColumn("database_name", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("table_name", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("record_count", DataTypes.INT.toString(), false, false));
                columnslist.add(new InternalColumn("col_tbl_st_rowid", DataTypes.INT.toString(), false, false));
                columnslist.add(new InternalColumn("nxt_avl_col_tbl_rowid", DataTypes.INT.toString(), false, false));
                this.updateSystemColumnsTable(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, 1, columnslist);
                columnslist.clear();
                columnslist.add(new InternalColumn("rowid", DataTypes.INT.toString(), false, false));
                columnslist.add(new InternalColumn("database_name", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("table_name", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("col umn_name", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("data_type", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("column_key", DataTypes.TEXT.toString(), false, false));
                columnslist.add(new InternalColumn("ordinal_position", DataTypes.TINYINT.toString(), false, false));
                columnslist.add(new InternalColumn("is_nullable", DataTypes.TEXT.toString(), false, false));
                this.updateSystemColumnsTable(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS, 7, columnslist);
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
        }
        return false;
    }

    public int SysTableupdate(String dbName, String tbName, int colCount) {
        try {
   
            RandomfileManager rfm = new RandomfileManager();
            List<InternalCondition> conditionlist = new ArrayList<>();
            conditionlist.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new TextType(tbName)));
            conditionlist.add(InternalCondition.CreateCon(InitialDatabaseHelper.TABLES_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new TextType(dbName)));
            List<DataRecord> result = rfm.fetchrec(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, conditionlist, null,true);
            if (result != null && result.size() == 0) {
                int returnValue = 1;
                PageDef<DataRecord> page = rfm.getLastRecordAndPage(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES);
                //Check if record exists
                DataRecord lastRecord = null;
                if (page.getPagerec().size() > 0) {
                    lastRecord = page.getPagerec().get(0);
                }
                DataRecord datavalrec = new DataRecord();
                if (lastRecord == null) {
                	datavalrec.setRowId(1);
                } else {
                	datavalrec.setRowId(lastRecord.getId() + 1);
                }
                datavalrec.getColVal().add(new IntType(datavalrec.getId()));
                datavalrec.getColVal().add(new TextType(dbName));
                datavalrec.getColVal().add(new TextType(tbName));
                datavalrec.getColVal().add(new IntType(0));
                if (lastRecord == null) {
                	datavalrec.getColVal().add(new IntType(1));
                	datavalrec.getColVal().add(new IntType(colCount + 1));
                } else {
                    IntType startingColumnIndex = (IntType) lastRecord.getColVal().get(InitialDatabaseHelper.nxt_COL_TBL_ROWID);
                    returnValue = startingColumnIndex.getValue();
                    datavalrec.getColVal().add(new IntType(returnValue));
                    datavalrec.getColVal().add(new IntType(returnValue + colCount));
                }
                datavalrec.populateSize();
                rfm.insertRe(Constants.CATALOG_DATABASENAME, Constants.SYSTEM_TABLES, datavalrec);
                return returnValue;
            } else {
                System.out.println("Some error has occured");
                return -1;
            }
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
            return -1;
        }
    }
}

