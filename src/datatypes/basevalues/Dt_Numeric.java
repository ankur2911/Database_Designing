package datatypes.basevalues;

/**
 * Created by ankur
 */
public abstract class Dt_Numeric<param> extends DataType<param> {

    //Comparison keywords;
    public static final short EQUALS = 0;
    public static final short LESS_THAN = 1;
    public static final short GREATER_THAN = 2;
    public static final short LESS_THAN_EQUALS = 3;
    public static final short GREATER_THAN_EQUALS = 4;

    protected final byte SIZE;

    protected Dt_Numeric(int valueSerialCode, int nullSerialCode, int size) {
        super(valueSerialCode, nullSerialCode);
        this.SIZE = (byte) size;
    }

    public byte getSerialCode() {
        if(isNull)
            return nullSerialCode;
        else
            return valueSerialCode;
    }

    public byte getSIZE() {
        return SIZE;
    }

    public abstract void increment(param value);

    public abstract boolean compare(Dt_Numeric<param> object2, short condition);
}