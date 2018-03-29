package dbCommands.DAO;

import dbCommands.Resstructure.cmdOutput.Result;

/**
 * Created by ankur
 */

public interface QueryDAO {
	boolean Validatecmd();
    Result runCommand();
    
}
