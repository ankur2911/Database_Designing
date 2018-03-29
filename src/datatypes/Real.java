package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;

/**
 * Created by ankur
 */
public class Real extends Dt_Numeric<Float> {

    public Real() {
        this(0, true);
    }

    public Real(Float value) {
        this(value == null ? 0 : value, value == null);
    }

    public Real(float value, boolean isNull) {
        super(Constants.serialREALcode, Constants.fourbytecode, Float.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Float value) {
        this.value += value;
    }

    @Override
    public boolean compare(Dt_Numeric<Float> object2, short condition) {
        if(value == null) return false;
        switch (condition) {
            case Dt_Numeric.EQUALS:
                return Float.floatToIntBits(value) == Float.floatToIntBits(object2.getValue());

            case Dt_Numeric.GREATER_THAN:
                return value > object2.getValue();

            case Dt_Numeric.LESS_THAN:
                return value < object2.getValue();

            case Dt_Numeric.GREATER_THAN_EQUALS:
                return Float.floatToIntBits(value) >= Float.floatToIntBits(object2.getValue());

            case Dt_Numeric.LESS_THAN_EQUALS:
                return Float.floatToIntBits(value) <= Float.floatToIntBits(object2.getValue());

            default:
                return false;
        }
    }

    public boolean compare(DoubleType object2, short condition) {
    		DoubleType object = new DoubleType(value, false);
        return object.compare(object2, condition);
    }
}
