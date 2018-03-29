package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;


public class DoubleType extends Dt_Numeric<Double> {

    public DoubleType() {
        this(0, true);
    }

    public DoubleType(Double value) {
        this(value == null ? 0 : value, value == null);
    }

    public DoubleType(double value, boolean isNull) {
        super(Constants.serialDbcode, Constants.eightbytecode, Double.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Double value) {
        this.value += value;
    }

    @Override
    public boolean compare(Dt_Numeric<Double> object2, short condition) {
        if(value == null) return false;
        switch (condition) {
            case Dt_Numeric.EQUALS:
                return Double.doubleToLongBits(value) == Double.doubleToLongBits(object2.getValue());

            case Dt_Numeric.GREATER_THAN:
                return value > object2.getValue();

            case Dt_Numeric.LESS_THAN:
                return value < object2.getValue();

            case Dt_Numeric.GREATER_THAN_EQUALS:
                return Double.doubleToLongBits(value) >= Double.doubleToLongBits(object2.getValue());

            case Dt_Numeric.LESS_THAN_EQUALS:
                return Double.doubleToLongBits(value) <= Double.doubleToLongBits(object2.getValue());

            default:
                return false;
        }
    }

    public boolean compare(Real object2, short condition) {
        DoubleType object = new DoubleType(object2.getValue(), false);
        return this.compare(object, condition);
    }

}
