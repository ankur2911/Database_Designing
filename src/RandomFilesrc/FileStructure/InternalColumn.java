package RandomFilesrc.FileStructure;


public class InternalColumn {

    private int ind;

    private Object val;

    private String name;

    private String dtype;

    private boolean pkchk;

    private boolean Nullchk;

    private byte ordinalPos;

    public InternalColumn() {

    }

    public InternalColumn(String name, String dataType, boolean isPrimary, boolean isNullable) {
        this.name = name;
        this.dtype = dataType;
        this.pkchk = isPrimary;
        this.Nullchk = isNullable;
    }

    public int getIndex() {
        return ind;
    }

    public void setIndex(int index) {
        this.ind = index;
    }

    public Object getValue() {
        return val;
    }

    public void setValue(Object value) {
        this.val = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dtype;
    }

    public void setDataType(String dataType) {
        this.dtype = dataType;
    }

    public boolean isPrimary() {
        return pkchk;
    }

    public void setPrimary(boolean primary) {
    	pkchk = primary;
    }

    public String getStringIsPrimary() {
        return pkchk ? "PRI" : null;
    }

    public boolean isNullable() {
        return Nullchk;
    }

    public void setNullable(boolean nullable) {
    	Nullchk = nullable;
    }

    public String getStringIsNullable() {
        return Nullchk ? "YES" : "NO";
    }

    public byte getOrdinalPosition() {
        return ordinalPos;
    }

    public void setOrdinalPosition(byte ordinalPosition) {
        this.ordinalPos = ordinalPosition;
    }
}
