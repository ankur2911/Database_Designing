package dbCommands.UserCommands;

import common.Constants;
import dbCommands.Resstructure.cmdCondition.ColClass;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdOutput.Result;
import common.InitialDatabaseHelper;
import RandomFilesrc.RandomfileManager;
import RandomFilesrc.FileStructure.InternalColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankur
 */

public class CreateTablecmd implements QueryDAO {
    public String tableName;
    public ArrayList<ColClass> columns;
    private boolean hasPrimaryKey;
    public String databaseName;

    public CreateTablecmd(String databaseName, String tableName, ArrayList<ColClass> columns, boolean hasPrimaryKey){
        this.tableName = tableName;
        this.columns = columns;
        this.hasPrimaryKey = hasPrimaryKey;
        this.databaseName = databaseName;
    }

    @Override
    public Result runCommand() {
        return new Result(1);
    }

    @Override
    public boolean Validatecmd() {
        try {
            RandomfileManager rfm = new RandomfileManager();

            if (!rfm.dbchk(this.databaseName)) {
                System.out.println("Some error has occured");
                return false;
            }

            if (rfm.tabchk(this.databaseName, tableName)) {
                System.out.println("Some error has occured");
                return false;
            }

            if (dupcolchk(columns)) {
                System.out.println("Some error has occured");
                return false;
            }


            List<InternalColumn> colList = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                InternalColumn inColumn = new InternalColumn();

                ColClass col = columns.get(i);
                inColumn.setName(col.name);
                inColumn.setDataType(col.type.toString());

                if (hasPrimaryKey && i == 0) {
                	inColumn.setPrimary(true);
                } else {
                	inColumn.setPrimary(false);
                }

                if (hasPrimaryKey && i == 0) {
                	inColumn.setNullable(false);
                } else if (col.isNull) {
                	inColumn.setNullable(true);
                } else {
                	inColumn.setNullable(false);
                }

                colList.add(inColumn);
            }

            boolean status = rfm.createTable(this.databaseName, tableName + Constants.FILE_EXTENSION);
            if (status) {
                InitialDatabaseHelper databaseHelper = new InitialDatabaseHelper();
                int startingRowId = databaseHelper.SysTableupdate(this.databaseName, tableName, columns.size());
                boolean systemTableUpdateStatus = databaseHelper.updateSystemColumnsTable(this.databaseName, tableName, startingRowId, colList);

                if (!systemTableUpdateStatus) {
                    System.out.println("Some error has occured");
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

    private boolean dupcolchk(ArrayList<ColClass> columnArrayList) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < columnArrayList.size(); i++) {
        	ColClass column = columnArrayList.get(i);
            if (map.containsKey(column.name)) {
                return true;
            }
            else {
                map.put(column.name, i);
            }
        }

        return false;
    }
}
