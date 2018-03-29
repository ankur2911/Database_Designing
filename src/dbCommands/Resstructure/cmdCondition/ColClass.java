package dbCommands.Resstructure.cmdCondition;


/**
 * Created by ankur
 */

public class ColClass {

    public String name;
    public DataTypes type;
    public boolean isNull;

    public static ColClass CreateCol(String colString){
        String pkString = "primary key";
        String notNullString = "not null";
        boolean isNull = true;
        if(colString.toLowerCase().endsWith(pkString)){
        	colString = colString.substring(0, colString.length() - pkString.length()).trim();
        }
        else if(colString.toLowerCase().endsWith(notNullString)){
        	colString = colString.substring(0, colString.length() - notNullString.length()).trim();
            isNull = false;
        }

        String[] parts = colString.split(" ");
        String name;
        if(parts.length > 2){
            System.out.println("Some error has occured, invalid format");
            return null;
        }

        if(parts.length > 1){
            name = parts[0].trim();
            DataTypes type = GetType(parts[1].trim());
            if(type == null){
                System.out.println("Some error has occured");
                return null;
            }

            return new ColClass(name, type, isNull);
        }

        System.out.println("Some error has occured");
        return null;
    }

    private static DataTypes GetType(String TypeString) {
        switch(TypeString){
            case "tinyint": return DataTypes.TINYINT;
            case "smallint": return DataTypes.SMALLINT;
            case "int": return DataTypes.INT;
            case "bigint": return DataTypes.BIGINT;
            case "real": return DataTypes.REAL;
            case "double": return DataTypes.DOUBLE;
            case "datetime": return DataTypes.DATETIME;
            case "date": return DataTypes.DATE;
            case "text": return DataTypes.TEXT;
        }

        return null;
    }

    private ColClass(String name, DataTypes type, boolean checkNull) {
        this.name = name;
        this.type = type;
        this.isNull = checkNull;
    }
}
