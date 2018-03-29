package datatypes.basevalues;

import common.Constants;
import common.Utils;
import datatypes.*;
import dbCommands.Resstructure.cmdCondition.LiteralClass;


public abstract class DataType<param> {

    protected param value;

    protected boolean isNull;

    protected final byte valueSerialCode;

    protected final byte nullSerialCode;

    
    
    public static DataType createSysDT(String value, byte dataType) {
        switch(dataType) {
            case Constants.tinyInt:
                return new TinyInt(Byte.valueOf(value));
            case Constants.DOUBLE:
                return new DoubleType(Double.valueOf(value));
            case Constants.DATETIME:
                return new DateTime(Utils.getDateEpoc(value, false));
            case Constants.DATE:
                return new DateType(Utils.getDateEpoc(value, true));
            case Constants.smallInt:
                return new SmallInt(Short.valueOf(value));
            case Constants.BIGINT:
                return new BigInt(Long.valueOf(value));
            case Constants.INT:
                return new IntType(Integer.valueOf(value));
            case Constants.REAL:
                return new Real(Float.valueOf(value));
            case Constants.TEXT:
                return new TextType(value);
        }

        return null;
    }
    public static DataType CreateDT(LiteralClass value) {
        switch(value.type) {
        
        case REAL:
            return new Real(Float.valueOf(value.value));
        case DOUBLE:
            return new DoubleType(Double.valueOf(value.value));
        case DATETIME:
            return new DateTime(Utils.getDateEpoc(value.value, false));
        case DATE:
            return new DateType(Utils.getDateEpoc(value.value, true));
        case TEXT:
            return new TextType(value.value);
            case TINYINT:
                return new TinyInt(Byte.valueOf(value.value));
            case SMALLINT:
                return new SmallInt(Short.valueOf(value.value));
            case BIGINT:
                return new BigInt(Long.valueOf(value.value));
            case INT:
                return new IntType(Integer.valueOf(value.value));
           
        }

        return null;
    }

   

    public String getStringValue() {
        if(value == null) {
            return "NULL";
        }
        return value.toString();
    }

  
    

    public byte getValueSerialCode() {
        return valueSerialCode;
    }

    public byte getNullSerialCode() {
        return nullSerialCode;
    }

    public void setValue(param value) {
        this.value = value;
         if (value != null) {
             this.isNull = false;
         }
    }

    public boolean isNull() {
        return isNull;
    }
    
    public void setNull(boolean aNull) {
        isNull = aNull;
    }
    protected DataType(int valueSerialCode, int nullSerialCode) {
        this.valueSerialCode = (byte) valueSerialCode;
        this.nullSerialCode = (byte) nullSerialCode;
    }

    public param getValue() {
        return value;
    }


}
