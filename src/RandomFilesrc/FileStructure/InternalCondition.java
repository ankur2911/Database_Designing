package RandomFilesrc.FileStructure;



public class InternalCondition {

    public static final short EQUALS = 0;
    public static final short LESS_THAN = 1;
    public static final short GREATER_THAN = 2;
    public static final short LESS_THAN_EQUALS = 3;
    public static final short GREATER_THAN_EQUALS = 4;

    private byte ind;

    private short contype;

    private Object val;


    public static InternalCondition CreateCon(int ind, short contype, Object val) {
        InternalCondition condition = new InternalCondition(ind, contype, val);
        return condition;
    }
 
    public InternalCondition() {
    	
    }



    private InternalCondition(int index, short conditionType, Object value) {
        this.ind = (byte) index;
        this.contype = conditionType;
        this.val = value;
    }

    public byte getIndex() {
        return ind;
    }

    public void setIndex(byte index) {
        this.ind = index;
    }

    public short getConditionType() {
        return contype;
    }

    public void setConditionType(short conditionType) {
        this.contype = conditionType;
    }

    public Object getValue() {
        return val;
    }

    public void setValue(Object value) {
        this.val = value;
    }
}
