package RandomFilesrc.FileStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankur
 * reference taken from stackoverflow for hexadecimal values/ b
 */

public class PageDef<T> {

    public static short PAGE_SIZE = 512;
    public static final byte INTERIOR_TABLE_PAGE = 0x05;
    public static final byte LEAF_TABLE_PAGE = 0x0D;
    public static final byte RIGHTMOST_PAGE = 0xFFFFFFFF;


    private byte typepage;

    private byte cellnum;

    private short initadd;

    private int rightnode;

    private List<Short> recadd;

    private List<T> pagerec;

    private int pno;

    public PageDef() {
        this.recadd = new ArrayList<>();
        this.pagerec = new ArrayList<>();
    }

    public PageDef(int pnum) {
        this.recadd = new ArrayList<>();
        this.pagerec = new ArrayList<>();
        this.pno = pnum;
        this.initadd = (short) (PAGE_SIZE - 1);
    }

    public static <T> PageDef<T> newPage(T obj) {
    		PageDef<T> pg = new PageDef<>(0);
    		pg.settypepage(PageDef.LEAF_TABLE_PAGE);
    		pg.setRightnode(PageDef.RIGHTMOST_PAGE);
    		pg.setCellnum((byte)0x00);
    		pg.setRecadd(new ArrayList<>());
    		pg.setPagerec(new ArrayList<>());
        return pg;
    }

    public byte gettypepage() {
        return typepage;
    }

    public void settypepage(byte typepage) {
        this.typepage = typepage;
    }

    public byte getCellnum() {
        return cellnum;
    }

    public long getBaseAddress() {
        return pno * PAGE_SIZE;
    }

    public void setCellnum(byte cellnum) {
        this.cellnum = cellnum;
    }

    public short getInitadd() {
        return initadd;
    }

    public void setInitadd(short initadd) {
        this.initadd = initadd;
    }

    public int getRightnode() {
        return rightnode;
    }

    public void setRightnode(int rightnode) {
        this.rightnode = rightnode;
    }

    public List<Short> getRecadd() {
        return recadd;
    }

    public void setRecadd(List<Short> recadd) {
        this.recadd = recadd;
    }

    public List<T> getPagerec() {
        return pagerec;
    }

    public void setPagerec(List<T> pagerec) {
        this.pagerec = pagerec;
    }

    public int getPno() {
        return pno;
    }

    public void setPno(int pno) {
        this.pno = pno;
    }


    public static int getHeaderFixedLength() {
        return Byte.BYTES + Byte.BYTES + Short.BYTES + Integer.BYTES;
    }
}
