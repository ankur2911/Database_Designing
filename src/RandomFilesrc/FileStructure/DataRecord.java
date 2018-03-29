package RandomFilesrc.FileStructure;

import common.Constants;
import common.Utils;
import datatypes.*;

import java.util.ArrayList;
import java.util.List;


public class DataRecord {

	 public DataRecord() {
	    	length = 0;
	        colval = new ArrayList<>();
	        pageloc = -1;
	        flag = -1;
	    }
	
	
    private List<Object> colval;
    private int id;

    private int pageloc;

    private short flag;
    private short length;

  

   

    public List<Object> getColVal() {
        return colval;
    }

    public short getLength() {
        return length;
    }

    public void setlength(short size) {
        this.length = size;
    }

    public short getHeaderSize() {
        return (short)(Short.BYTES + Integer.BYTES);
    }

    public void populateSize() {
        this.length = (short) (this.colval.size() + 1);
        for(Object obj : colval) {
            if(obj .getClass().equals(TinyInt.class)) {
                this.length += ((TinyInt) obj ).getSIZE();
            }
            else if(obj .getClass().equals(SmallInt.class)) {
                this.length += ((SmallInt) obj ).getSIZE();
            }
            else if(obj .getClass().equals(IntType.class)) {
                this.length += ((IntType) obj ).getSIZE();
            }
            else if(obj .getClass().equals(BigInt.class)) {
                this.length += ((BigInt) obj ).getSIZE();
            }
            else if(obj .getClass().equals(Real.class)) {
                this.length += ((Real) obj ).getSIZE();
            }
            else if(obj .getClass().equals(DoubleType.class)) {
                this.length += ((DoubleType) obj ).getSIZE();
            }
            else if(obj .getClass().equals(DateTime.class)) {
            	length += ((DateTime) obj ).getSIZE();
            }
            else if(obj .getClass().equals(DateType.class)) {
                this.length += ((DateType) obj ).getSIZE();
            }
            else if(obj .getClass().equals(TextType.class)) {
                this.length += ((TextType) obj ).getSize();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setRowId(int rowId) {
        this.id = rowId;
    }

    public int getPageLoc() {
        return pageloc;
    }

    public void setPageLoc(int pageLocated) {
        this.pageloc = pageLocated;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short offset) {
        this.flag = offset;
    }

    public byte[] getSerialTypeCodes() {
        byte[] serialCodes = new byte[colval.size()];
        byte count = 0;
        for(Object obj: colval) {
            switch (Utils.fetchtype(obj)) {
            
            case Constants.REAL:
                serialCodes[count++] = ((Real) obj).getSerialCode();
                break;

            case Constants.DOUBLE:
                serialCodes[count++] = ((DoubleType) obj).getSerialCode();
                break;

            case Constants.DATETIME:
                serialCodes[count++] = ((DateTime) obj).getSerialCode();
                break;

            case Constants.DATE:
                serialCodes[count++] = ((DateType) obj).getSerialCode();
                break;

                case Constants.tinyInt:
                    serialCodes[count++] = ((TinyInt) obj).getSerialCode();
                    break;

                case Constants.smallInt:
                    serialCodes[count++] = ((SmallInt) obj).getSerialCode();
                    break;

                case Constants.INT:
                    serialCodes[count++] = ((IntType) obj).getSerialCode();
                    break;

                case Constants.BIGINT:
                    serialCodes[count++] = ((BigInt) obj).getSerialCode();
                    break;

                
                case Constants.TEXT:
                    serialCodes[count++] = ((TextType) obj).getSerialCode();
                    break;
            }
        }
        return serialCodes;
    }
}
