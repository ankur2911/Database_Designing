package datatypes;

import common.Constants;
import datatypes.basevalues.Dt_Numeric;

import java.util.Date;

public class DateTime extends Dt_Numeric<Long> {

    public DateTime() {
        this(0, true);
    }

    public DateTime(Long value) {
        this(value == null ? 0 : value, value == null);
    }

    public DateTime(long value, boolean isNull) {
        super(Constants.DATETIMEcode, Constants.eightbytecode, Long.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    public String getStringValue() {
        Date date = new Date(this.value);
        return date.toString();
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
}
