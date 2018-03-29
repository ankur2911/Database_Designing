package dbCommands.UserCommands;

import common.Constants;
import dbCommands.DAO.QueryDAO;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdOutput.Record;
import dbCommands.Resstructure.cmdOutput.Result;
import dbCommands.Resstructure.cmdOutput.ResultSet;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ankur
 */

public class ShowDbcmd implements QueryDAO {
	
	 @Override
	    public boolean Validatecmd() {
	        return true;
	    }

	    private ArrayList<Record> getDB(){
	        ArrayList<Record> rec = new ArrayList<>();

	        File f = new File(Constants.DATA_DIRNAME);

	        for(File data : f.listFiles()){
	            if(!data.isDirectory()) continue;
	            Record dbrec = Record.CreateRecord();
	            dbrec.put("Database", LiteralClass.CreateLiteral(String.format("\"%s\"", data.getName())));
	            rec.add(dbrec);
	        }

	        return rec;
	    }
    @Override
    public Result runCommand() {
        ArrayList<String> col = new ArrayList<>();
        col.add("Database");
        ResultSet rs = ResultSet.CreateResultSet();
        rs.setColumns(col);
        ArrayList<Record> rec = getDB();

        for(Record record : rec){
            rs.addRecord(record);
        }

        return rs;
    }

   
}
