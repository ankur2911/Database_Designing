package dbCommands.Resstructure.cmdCondition;

import common.Constants;
import common.Utils;



public class LiteralClass {
    public DataTypes type;
    public String value;

    public static LiteralClass CreateLiteral(datatypes.basevalues.DataType value, Byte type) {
        if(type == Constants.invalidcode) {
            return null;
        }
        else if (value.isNull()) {
            return new LiteralClass(DataTypes.DOUBLE_DATETIME_NULL, value.getStringValue());
        }

        switch(type) {
            case Constants.tinyInt:
                return new LiteralClass(DataTypes.TINYINT, value.getStringValue());
            case Constants.smallInt:
                return new LiteralClass(DataTypes.SMALLINT, value.getStringValue());
            case Constants.INT:
                return new LiteralClass(DataTypes.INT, value.getStringValue());
            case Constants.BIGINT:
                return new LiteralClass(DataTypes.BIGINT, value.getStringValue());
            case Constants.REAL:
                return new LiteralClass(DataTypes.REAL, value.getStringValue());
            case Constants.DOUBLE:
                return new LiteralClass(DataTypes.DOUBLE, value.getStringValue());
            case Constants.DATE:
                return new LiteralClass(DataTypes.DATE, Utils.getDateEpocAsString((long)value.getValue(), true));
            case Constants.DATETIME:
                return new LiteralClass(DataTypes.DATETIME, Utils.getDateEpocAsString((long)value.getValue(), false));
            case Constants.TEXT:
                return new LiteralClass(DataTypes.TEXT, value.getStringValue());
        }

        return null;
    }

    public static LiteralClass CreateLiteral(String literalString){
        if(literalString.startsWith("'") && literalString.endsWith("'")){
            literalString = literalString.substring(1, literalString.length()-1);

            if (Utils.isvalidDateTimeFormat(literalString)) {
                return new LiteralClass(DataTypes.DATETIME, literalString);
            }

            if (Utils.checkformat(literalString)) {
                return new LiteralClass(DataTypes.DATE, literalString);
            }

            return new LiteralClass(DataTypes.TEXT, literalString);
        }

        if(literalString.startsWith("\"") && literalString.endsWith("\"")){
            literalString = literalString.substring(1, literalString.length()-1);

            if (Utils.isvalidDateTimeFormat(literalString)) {
                return new LiteralClass(DataTypes.DATETIME, literalString);
            }

            if (Utils.checkformat(literalString)) {
                return new LiteralClass(DataTypes.DATE, literalString);
            }

            return new LiteralClass(DataTypes.TEXT, literalString);
        }

        try{
            Integer.parseInt(literalString);
            return new LiteralClass(DataTypes.INT, literalString);
        }
        catch (Exception e){}

        try{
            Long.parseLong(literalString);
            return new LiteralClass(DataTypes.BIGINT, literalString);
        }
        catch (Exception e){}

        try{
            Double.parseDouble(literalString);
            return new LiteralClass(DataTypes.REAL, literalString);
        }
        catch (Exception e){}

        System.out.println("Some error has occured");
        return null;
    }

    private LiteralClass(DataTypes type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        if (this.type == DataTypes.TEXT) {
            return this.value;
        } else if (this.type == DataTypes.INT || this.type == DataTypes.TINYINT ||
                this.type == DataTypes.SMALLINT || this.type == DataTypes.BIGINT) {
            return this.value;
        } else if (this.type == DataTypes.REAL || this.type == DataTypes.DOUBLE) {
            return String.format("%.2f", Double.parseDouble(this.value));
        } else if (this.type == DataTypes.INT_REAL_NULL || this.type == DataTypes.SMALL_INT_NULL || this.type == DataTypes.TINY_INT_NULL || this.type == DataTypes.DOUBLE_DATETIME_NULL) {
            return "NULL";
        } else if (this.type == DataTypes.DATE || this.type == DataTypes.DATETIME) {
            return this.value;
        }

        return "";
    }
}
