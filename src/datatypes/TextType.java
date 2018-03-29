package datatypes;

import common.Constants;
import datatypes.basevalues.DataType;

/**
 * Created by ankur
 */
public class TextType extends DataType<String> {

    public TextType() {
        this("", true);
    }

    public TextType(String value) {
        this(value, value == null);
    }

    public TextType(String value, boolean isNull) {
        super(Constants.textcode, Constants.onebytecode);
        this.value = value;
        this.isNull = isNull;
    }

    public byte getSerialCode() {
        if(isNull)
            return nullSerialCode;
        else
            return (byte)(valueSerialCode + this.value.length());
    }

    public int getSize() {
        if(isNull)
            return 0;
        return this.value.length();
    }
}
