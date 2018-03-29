package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;


public class IntType extends Dt_Numeric<Integer> {

    public IntType() {
        this(0, true);
    }

    public IntType(Integer value) {
        this(value == null ? 0 : value, value == null);
    }

    public IntType(int value, boolean isNull) {
        super(Constants.serialINTcode, Constants.fourbytecode, Integer.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Integer value) {
        this.value += value;
    }

    @Override
    public boolean compare(Dt_Numeric<Integer> object2, short condition) {
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
        IntType object = new IntType(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(SmallInt object2, short condition) {
        IntType object = new IntType(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(BigInt object2, short condition) {
        BigInt object = new BigInt(value, false);
        return object.compare(object2, condition);
    }
}
