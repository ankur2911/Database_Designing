package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;

/**
 * Created by ankur
 */
public class TinyInt extends Dt_Numeric<Byte> {

    public TinyInt() {
        this((byte) 0, true);
    }

    public TinyInt(Byte value) {
        this(value == null ? 0 : value, value == null);
    }

    public TinyInt(byte value, boolean isNull) {
        super(Constants.serialTinyIntcode, Constants.onebytecode, Byte.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Byte value) {
        this.value = (byte)(this.value + value);
    }

    @Override
    public boolean compare(Dt_Numeric<Byte> object2, short condition) {
        if(value == null) return false;
        switch (condition) {
            case Dt_Numeric.EQUALS:
                return value == object2.getValue();

            case Dt_Numeric.GREATER_THAN:
                return value > object2.getValue();

            case Dt_Numeric.LESS_THAN:
                return value < object2.getValue();

            case Dt_Numeric.GREATER_THAN_EQUALS:
                return value >= object2.getValue();

            case Dt_Numeric.LESS_THAN_EQUALS:
                return value <= object2.getValue();

            default:
                return false;
        }
    }

    public boolean compare(SmallInt object2, short condition) {
        SmallInt object = new SmallInt(value, false);
        return object.compare(object2, condition);
    }

    public boolean compare(IntType object2, short condition) {
    	IntType object = new IntType(value, false);
        return object.compare(object2, condition);
    }

    public boolean compare(BigInt object2, short condition) {
        BigInt object = new BigInt(value, false);
        return object.compare(object2, condition);
    }
}
