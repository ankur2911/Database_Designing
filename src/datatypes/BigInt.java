package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;


public class BigInt extends Dt_Numeric<Long> {

    public BigInt() {
        this(0, true);
    }

    public BigInt(Long val) {
        this(val == null ? 0 : val, val == null);
    }

    public BigInt(long val, boolean checknull) {
        super(Constants.bigINTcode, Constants.eightbytecode, Long.BYTES);
        this.value = val;
        this.isNull = checknull;
    }

    @Override
    public void increment(Long value) {
        this.value += value;
    }

    @Override
    public boolean compare(Dt_Numeric<Long> object2, short condition) {
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
        BigInt object = new BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(SmallInt object2, short condition) {
        BigInt object = new BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(IntType object2, short condition) {
        BigInt object = new BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }
}
