package common;

import datatypes.basevalues.Dt_Numeric;
import dbCommands.Resstructure.cmdCondition.ConditionClass;
import dbCommands.Resstructure.cmdCondition.DataTypes;
import dbCommands.Resstructure.cmdCondition.LiteralClass;
import dbCommands.Resstructure.cmdCondition.OpClass;
import datatypes.*;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankur
 */
public class Utils {

    public static String fetchDbPath(String dbName) {
        return "data" + "/" + dbName;
    }



    

    static byte stringToDataType(String string) {
        if(string.compareToIgnoreCase("TINYINT") == 0) {
            return Constants.tinyInt;
        }
        else if(string.compareToIgnoreCase("SMALLINT") == 0) {
            return Constants.smallInt;
        }
        else if(string.compareToIgnoreCase("INT") == 0) {
            return Constants.INT;
        }
        else if(string.compareToIgnoreCase("BIGINT") == 0) {
            return Constants.BIGINT;
        }
        else if(string.compareToIgnoreCase("REAL") == 0) {
            return Constants.REAL;
        }
        else if(string.compareToIgnoreCase("DOUBLE") == 0) {
            return Constants.DOUBLE;
        }
        else if(string.compareToIgnoreCase("DATE") == 0) {
            return Constants.DATE;
        }
        else if(string.compareToIgnoreCase("DATETIME") == 0) {
            return Constants.DATETIME;
        }
        else if(string.compareToIgnoreCase("TEXT") == 0) {
            return Constants.TEXT;
        }
        else {
            return Constants.invalidcode;
        }
    }

    public static DataTypes DataTypeToModelDataType(byte param) {
        switch (param) {
            case Constants.tinyInt:
                return DataTypes.TINYINT;
            case Constants.smallInt:
                return DataTypes.SMALLINT;
            case Constants.INT:
                return DataTypes.INT;
            case Constants.BIGINT:
                return DataTypes.BIGINT;
            case Constants.REAL:
                return DataTypes.REAL;
            case Constants.DOUBLE:
                return DataTypes.DOUBLE;
            case Constants.DATE:
                return DataTypes.DATE;
            case Constants.DATETIME:
                return DataTypes.DATETIME;
            case Constants.TEXT:
                return DataTypes.TEXT;
            default:
                return null;
        }
    }
    public static Short ConvertFromOperator(OpClass op) {
        switch (op){
            case EQUALS: return Dt_Numeric.EQUALS;
            case GREATER_THAN_EQUAL: return Dt_Numeric.GREATER_THAN_EQUALS;
            case GREATER_THAN: return Dt_Numeric.GREATER_THAN;
            case LESS_THAN_EQUAL: return Dt_Numeric.LESS_THAN_EQUALS;
            case LESS_THAN: return Dt_Numeric.LESS_THAN;
        }

        return null;
    }
    public static boolean checkformat(String d) {
        DateFormat frm = new SimpleDateFormat("yyyy-MM-dd");
        frm.setLenient(false);
        try {
        	frm.parse(d);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean isvalidDateTimeFormat(String d) {
        DateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        frm.setLenient(false);
        try {
            frm.parse(d);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

   

    public static boolean checkConditionValueDataTypeValidity(HashMap<String, Integer> columnDataTypeMapping, List<String> columnsList, ConditionClass condition) {
        String invalidColumn = "";
        LiteralClass literal = null;

        if (columnsList.contains(condition.column)) {
            int dataTypeIndex = columnDataTypeMapping.get(condition.column);
            literal = condition.value;

            if (literal.type != Utils.DataTypeToModelDataType((byte)dataTypeIndex)) {
                if (Utils.UpdateDataType(literal, dataTypeIndex)) {
                    return true;
                }
            }
        }

        boolean valid = invalidColumn.length() <= 0;
        if (!valid) {
            System.out.println("Some error has occured");
        }

        return valid;
    }
    public static byte fetchtype(Object obj) {
        if(obj.getClass().equals(TinyInt.class)) {
            return Constants.tinyInt;
        }
        else if(obj.getClass().equals(SmallInt.class)) {
            return Constants.smallInt;
        }
        else if(obj.getClass().equals(IntType.class)) {
            return Constants.INT;
        }
        else if(obj.getClass().equals(BigInt.class)) {
            return Constants.BIGINT;
        }
        else if(obj.getClass().equals(Real.class)) {
            return Constants.REAL;
        }
        else if(obj.getClass().equals(DoubleType.class)) {
            return Constants.DOUBLE;
        }
        else if(obj.getClass().equals(DateType.class)) {
            return Constants.DATE;
        }
        else if(obj.getClass().equals(DateTime.class)) {
            return Constants.DATETIME;
        }
        else if(obj.getClass().equals(TextType.class)) {
            return Constants.TEXT;
        }
        else {
            return Constants.invalidcode;
        }
    }
    public static long getDateEpoc(String value, Boolean isDate) {
        DateFormat formatter;
        if (isDate) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        else {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        formatter.setLenient(false);
        Date date;
        try {
            date = formatter.parse(value);

            ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(),
                    ZoneId.systemDefault());

            return zdt.toInstant().toEpochMilli() / 1000;
        }
        catch (ParseException ex) {
            return 0;
        }
    }

    public static String getDateEpocAsString(long value, Boolean isDate) {
        ZoneId zoneId = ZoneId.of ("America/Chicago" );

        Instant i = Instant.ofEpochSecond (value);
        ZonedDateTime zdt2 = ZonedDateTime.ofInstant (i, zoneId);
        Date date = Date.from(zdt2.toInstant());

        DateFormat formatter;
        if (isDate) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        else {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        formatter.setLenient(false);

        return formatter.format(date);
    }

    public boolean checkValidity(HashMap<String, Integer> columnMapping, List<String> columns, List<LiteralClass> val) {
        String invalidColumn = "";
        LiteralClass invalidLit = null;

        for (int i =0; i < val.size(); i++) {
            String columnName = columns.get(i);

            int dataTypeId = columnMapping.get(columnName);

            int idx = columns.indexOf(columnName);
            LiteralClass lit = val.get(idx);
            invalidLit = lit;

            if (lit.type != Utils.DataTypeToModelDataType((byte)dataTypeId)) {

                if (Utils.UpdateDataType(lit, dataTypeId)) {
                    continue;
                }

                invalidColumn = columnName;
                break;
            }

            if (lit.type != Utils.DataTypeToModelDataType((byte)dataTypeId)) {
                invalidColumn = columnName;
                break;
            }
        }

        boolean valid = invalidColumn.length() <= 0;
        if (!valid) {
            System.out.println("Some error has occured");
            return false;
        }

        return true;
    }

   
    public static boolean UpdateDataType(LiteralClass lit, int colType) {
        if (colType == Constants.tinyInt) {
            if (lit.type == DataTypes.INT) {
                if (Integer.parseInt(lit.value) <= Byte.MAX_VALUE) {
                	lit.type = DataTypes.TINYINT;
                    return true;
                }
            }
        }  else if (colType == Constants.DOUBLE) {
            if (lit.type == DataTypes.REAL) {
            	lit.type = DataTypes.DOUBLE;
                return true;
            }
        } else if (colType == Constants.BIGINT) {
            if (lit.type == DataTypes.INT) {
                if (Integer.parseInt(lit.value) <= Long.MAX_VALUE) {
                	lit.type = DataTypes.BIGINT;
                    return true;
                }
            }
        }else if (colType == Constants.smallInt) {
            if (lit.type == DataTypes.INT) {
                if (Integer.parseInt(lit.value) <= Short.MAX_VALUE) {
                	lit.type = DataTypes.SMALLINT;
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean RecDelete(File f){
        if(f == null) return true;
        boolean isDeleted;

        if(f.isDirectory()) {
            for (File childFile : f.listFiles()) {
                if (childFile.isFile()) {
                    isDeleted = childFile.delete();
                    if (!isDeleted) return false;
                } else {
                    isDeleted = RecDelete(childFile);
                    if (!isDeleted) return false;
                }
            }
        }

        return f.delete();
    }

}
