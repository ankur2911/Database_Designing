package RandomFilesrc.FileStructure;


public class PointerRecord {

    private int leftpage;

    private int key;

    private int pno;

    private short flag;

    public PointerRecord() {
    	leftpage = -1;
        key = -1;
        flag = -1;
        pno = -1;
    }

    public int getleftpage() {
        return leftpage;
    }

    public void setleftpage(int leftpage) {
        this.leftpage = leftpage;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getSize() {
        return Integer.BYTES + Integer.BYTES;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public int getPno() {
        return pno;
    }

    public void setPno(int pno) {
        this.pno = pno;
    }
}
