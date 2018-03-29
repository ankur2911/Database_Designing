package dbCommands.Resstructure.cmdOutput;

import dbCommands.Resstructure.cmdCondition.LiteralClass;

import java.util.HashMap;

/**
 * Created by ankur
 */

public class Record {
    HashMap<String, LiteralClass> valueMap;

   

    public void put(String columnName, LiteralClass value){
        if(columnName.length() == 0) return;
        if(value == null) return;

        this.valueMap.put(columnName, value);
    }

    public String get(String column) {
    	LiteralClass literal = this.valueMap.get(column);
        return literal.toString();
    }
    
    public static Record CreateRecord(){
        return new Record();
    }

    private Record(){
        this.valueMap = new HashMap<>();
    }
}
