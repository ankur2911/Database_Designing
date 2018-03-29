package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;

/**
 * Created by ankur
 */
public class SmallInt extends Dt_Numeric<Short> {

    public SmallInt() {
        this((short) 0, true);
    }

    public SmallInt(Short value) {
        this(value == null ? 0 : value, value == null);
    }

    public SmallInt(short value, boolean isNull) {
        super(Constants.smallINTcode, Constants.twobytecode, Short.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Short value) {
        this.value = (short)(this.value + value);
    }

    @Override
    public boolean compare(Dt_Numeric<Short> object2, short condition) {
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

    public boolean compare(TinyInt object2, short condition) {
        SmallInt object = new SmallInt(object2.getValue(), false);
        return this.compare(object, condition);
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
