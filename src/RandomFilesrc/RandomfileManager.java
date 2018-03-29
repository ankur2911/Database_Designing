package RandomFilesrc;

import common.Constants;
import common.InitialDatabaseHelper;
import common.Utils;
import datatypes.*;
import datatypes.basevalues.DataType;
import datatypes.basevalues.Dt_Numeric;
import RandomFilesrc.FileStructure.DataRecord;
import RandomFilesrc.FileStructure.InternalCondition;
import RandomFilesrc.FileStructure.PageDef;
import RandomFilesrc.FileStructure.PointerRecord;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by ankur.

 */

public class RandomfileManager {

    public boolean dbchk(String db) {
        File dbdir = new File(Utils.fetchDbPath(db));
        return  dbdir.exists();
    }

    public boolean createTable(String dbname, String tabname)   {
        try {
            File dirFile = new File(Utils.fetchDbPath(dbname));
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File f = new File(Utils.fetchDbPath(dbname) + "/" + tabname);
            if (f.exists()) {
                return false;
            }
            if (f.createNewFile()) {
                RandomAccessFile raf;
                PageDef<DataRecord> pd = PageDef.newPage(new DataRecord());
                raf = new RandomAccessFile(f, "rw");
                raf.setLength(PageDef.PAGE_SIZE);
                boolean tabchk = addheader(raf, pd);
                raf.close();
                return tabchk;
            }
            return false;
        }   
        catch (Exception e) {
System.out.println("Error occured");       
}
		return false;
    }

    public boolean tabchk(String dbname, String tabname) {
        boolean dbchk = this.dbchk(dbname);
        //System.out.println(dbchk);
        
        //System.out.println(Utils.fetchDbPath(dbname) + "/" + tabname + Constants.FILE_EXTENSION);
        boolean chkfile = new File(Utils.fetchDbPath(dbname) + "/" + tabname + Constants.FILE_EXTENSION).exists();
        //System.out.println(chkfile);

//System.out.println(databaseExists +"  "+ fileExists);
        return (dbchk && chkfile);
    }

    public boolean insertRe(String dbname, String tabname, DataRecord rec)   {
        RandomAccessFile raf;
        try {
            File file = new File(Utils.fetchDbPath(dbname) + "/" + tabname + Constants.FILE_EXTENSION);
            if (file.exists()) {
            	raf = new RandomAccessFile(file, "rw");
                PageDef page = fetchpage(raf, rec, 0);
                if (page == null) return false;
                if (!chkspace(page, rec)) {
                    int pageCount = (int) (raf.length() / PageDef.PAGE_SIZE);
                    switch (pageCount) {
                        case 1:
                            PointerRecord pointerRecord = splitPage(raf, page, rec, 1, 2);
                            PageDef<PointerRecord> pointerRecordPage = PageDef.newPage(pointerRecord);
                            pointerRecordPage.setPno(0);
                            pointerRecordPage.settypepage(PageDef.INTERIOR_TABLE_PAGE);
                            pointerRecordPage.setCellnum((byte) 1);
                            pointerRecordPage.setInitadd((short) (pointerRecordPage.getInitadd() - pointerRecord.getSize()));
                            pointerRecordPage.setRightnode(2);
                            pointerRecordPage.getRecadd().add((short) (pointerRecordPage.getInitadd() + 1));
                            pointerRecord.setPno(pointerRecordPage.getPno());
                            pointerRecord.setFlag((short) (pointerRecordPage.getInitadd() + 1));
                            this.addheader(raf, pointerRecordPage);
                            this.addrec(raf, pointerRecord);
                            break;

                        default:
                            if(pageCount > 1) {
                                PointerRecord pointer = pagediv(raf, fetchheader(raf, 0), rec);
                                if(pointer != null && pointer.getleftpage() != -1)  {
                                	PageDef<PointerRecord> rootPage = PageDef.newPage(pointer);
                                    rootPage.setPno(0);
                                    rootPage.settypepage(PageDef.INTERIOR_TABLE_PAGE);
                                    rootPage.setRightnode(pointer.getPno());
                                    rootPage.getRecadd().add((short) (rootPage.getInitadd() + 1));
                                    rootPage.setCellnum((byte) 1);
                                    rootPage.setInitadd((short)(rootPage.getInitadd() - pointer.getSize()));
                                    pointer.setFlag((short) (rootPage.getInitadd() + 1));
                                    this.addheader(raf, rootPage);
                                    this.addrec(raf, pointer);
                                }
                            }
                            break;
                    }
                    
                    
                    InitialDatabaseHelper.updateRowCount(dbname, tabname, 1);
                    raf.close();
                    return true;
                }
                short add = (short) getadd(file, rec.getId(), page.getPno());
                page.setCellnum((byte)(page.getCellnum() + 1));
                page.setInitadd((short) (page.getInitadd() - rec.getLength() - rec.getHeaderSize()));
                if(add == page.getRecadd().size())
                    page.getRecadd().add((short)(page.getInitadd() + 1));
                else
                    page.getRecadd().add(add, (short)(page.getInitadd() + 1));
                rec.setPageLoc(page.getPno());
                rec.setFlag((short) (page.getInitadd() + 1));
                this.addheader(raf, page);
                this.addrec(raf, rec);
                InitialDatabaseHelper.updateRowCount(dbname, tabname, 1);
                raf.close();
            } else {
            	System.out.println("Error occured");       
            }
            return true;
        }   
        catch (Exception e) {
        	System.out.println("Error occured");       
        }
		return false;
    }

    private boolean chkspace(PageDef page, DataRecord rec) {
        if (page != null && rec != null) {
        	
            short startadd = (short) (PageDef.getHeaderFixedLength() + (page.getRecadd().size() * Short.BYTES));

            short endadd = page.getInitadd();
            return (rec.getLength() + rec.getHeaderSize() + Short.BYTES) <= (endadd - startadd);
        }
        return false;
    }

    private boolean chkreq(PageDef page, PointerRecord rec) {
        if(page != null && rec != null) {
            short startadd = (short) (PageDef.getHeaderFixedLength() + (page.getRecadd().size() * Short.BYTES));

            short endadd = page.getInitadd();
            return (rec.getSize() + Short.BYTES) <= (endadd - startadd);
        }
        return false;
    }

    private PointerRecord splitPage(RandomAccessFile raf, PageDef page, DataRecord rec, int p1, int p2) {
        try {
            if (page != null && rec != null) {
                int loc;
                PointerRecord pointer = new PointerRecord();
                if (page.gettypepage() == PageDef.INTERIOR_TABLE_PAGE) {
                    return null;
                }
                loc = searchloc(raf, rec.getId(), page.getCellnum(), ((page.getPno() * PageDef.PAGE_SIZE) + PageDef.getHeaderFixedLength()), page.gettypepage(), false);
                raf.setLength(PageDef.PAGE_SIZE * (p2 + 1));
                if (loc == page.getCellnum()) {
                	PageDef<DataRecord> initpage = new PageDef<>(p1);
                	initpage.settypepage(page.gettypepage());
                   
                	initpage.setInitadd(page.getInitadd());
                    
                	initpage.setCellnum(page.getCellnum());
                	initpage.setRightnode(p2);
                	initpage.setRecadd(page.getRecadd());
                    this.addheader(raf, initpage);
                    List<DataRecord> recs = cpRecords(raf, (page.getPno() * PageDef.PAGE_SIZE), page.getRecadd(), (byte) 0, page.getCellnum(), initpage.getPno(), rec);
                    for (DataRecord object : recs) {
                        this.addrec(raf, object);
                    }
                    PageDef<DataRecord> nxtpage = new PageDef<>(p2);
                    nxtpage.settypepage(page.gettypepage());
                    nxtpage.setCellnum((byte) 1);
                    nxtpage.setRightnode(page.getRightnode());
                    nxtpage.setInitadd((short) (nxtpage.getInitadd() - rec.getLength() - rec.getHeaderSize()));
                    nxtpage.getRecadd().add((short) (nxtpage.getInitadd() + 1));
                    this.addheader(raf, nxtpage);
                    rec.setPageLoc(nxtpage.getPno());
                    rec.setFlag((short) (nxtpage.getInitadd() + 1));
                    this.addrec(raf, rec);
                    pointer.setKey(rec.getId());
                } else {
                    boolean chkorder = false;
                    if (loc < (page.getRecadd().size() / 2)) {
                    	chkorder = true;
                    }
                    raf.setLength(PageDef.PAGE_SIZE * (p2 + 1));

                    //Page 1
                    PageDef<DataRecord> initpage = new PageDef<>(p1);
                    initpage.settypepage(page.gettypepage());
                    initpage.setPno(p1);
                    List<DataRecord> copyrec = cpRecords(raf, (page.getPno() * PageDef.PAGE_SIZE), page.getRecadd(), (byte) 0, (byte) (page.getCellnum() / 2), initpage.getPno(), rec);
                    if (chkorder) {
                    	rec.setPageLoc(initpage.getPno());
                    	copyrec.add(loc, rec);
                    }
                    initpage.setCellnum((byte) copyrec.size());
                    int index = 0;
                    short offset = PageDef.PAGE_SIZE;
                    for (DataRecord dr : copyrec) {
                        index++;
                        offset = (short) (PageDef.PAGE_SIZE - ((dr.getLength() + dr.getHeaderSize()) * index));
                        dr.setFlag(offset);
                        initpage.getRecadd().add(offset);
                    }
                    initpage.setInitadd((short) (offset - 1));
                    initpage.setRightnode(p2);
                    this.addheader(raf, initpage);
                    for(DataRecord dataRecord : copyrec) {
                        this.addrec(raf, dataRecord);
                    }

                    //Page 2
                    PageDef<DataRecord> nxtpage = new PageDef<>(p2);
                    nxtpage.settypepage(page.gettypepage());
                    List<DataRecord> copyrec1 = cpRecords(raf, (page.getPno() * PageDef.PAGE_SIZE), page.getRecadd(), (byte) ((page.getCellnum() / 2) + 1), page.getCellnum(), p2, rec);
                    if(!chkorder) {
                    	rec.setPageLoc(nxtpage.getPno());
                        int position = (loc - (page.getRecadd().size() / 2) + 1);
                        if(position >= copyrec1.size())
                        	copyrec1.add(rec);
                        else
                        	copyrec1.add(position, rec);
                    }
                    nxtpage.setCellnum((byte) copyrec1.size());
                    nxtpage.setRightnode(page.getRightnode());
                    pointer.setKey(copyrec1.get(0).getId());
                    index = 0;
                    offset = PageDef.PAGE_SIZE;
                    for(DataRecord data : copyrec1) {
                        index++;
                        offset = (short) (PageDef.PAGE_SIZE - ((data.getLength() + data.getHeaderSize()) * index));
                        data.setFlag(offset);
                        nxtpage.getRecadd().add(offset);
                    }
                    nxtpage.setInitadd((short) (offset - 1));
                    this.addheader(raf, nxtpage);
                    for(DataRecord data : copyrec1) {
                        this.addrec(raf, data);
                    }
                }
                pointer.setleftpage(p1);
                return pointer;
            }
        } 
        catch (Exception e) {
        	System.out.println("Error occured");  
        }
        return null;
    }

    private PointerRecord pagediv(RandomAccessFile raf, PageDef page, DataRecord rec){
        try {
            if (page.gettypepage() == PageDef.INTERIOR_TABLE_PAGE) {
                int pno = searchloc(raf, rec.getId(), page.getCellnum(), (page.getBaseAddress() + PageDef.getHeaderFixedLength()), PageDef.INTERIOR_TABLE_PAGE, false);
                PageDef initpage = this.fetchheader(raf, pno);
                PointerRecord point = pagediv(raf, initpage, rec);
                if (point.getPno() == -1)
                    return point;
                if (chkreq(page, point)) {
                    int loc = searchloc(raf, rec.getId(), page.getCellnum(), (page.getBaseAddress() + PageDef.getHeaderFixedLength()), PageDef.INTERIOR_TABLE_PAGE, true);
                    page.setCellnum((byte) (page.getCellnum() + 1));
                    page.setInitadd((short) (page.getInitadd() - point.getSize()));
                    page.getRecadd().add(loc, (short) (page.getInitadd() + 1));
                    page.setRightnode(point.getPno());
                    point.setPno(page.getPno());
                    point.setFlag((short) (page.getInitadd() + 1));
                    this.addheader(raf, page);
                    this.addrec(raf, point);
                    return new PointerRecord();
                } else {
                    int nxtpageno = (int) (raf.length() / PageDef.PAGE_SIZE);
                    page.setRightnode(point.getPno());
                    this.addheader(raf, page);
                    PointerRecord pointerRecord1 = splitPage(raf, page, point, page.getPno(), nxtpageno);
                    return pointerRecord1;
                }
            } else if (page.gettypepage() == PageDef.LEAF_TABLE_PAGE) {
                int nxtpageno = (int) (raf.length() / PageDef.PAGE_SIZE);
                PointerRecord point = splitPage(raf, page, rec, page.getPno(), nxtpageno);
                if (point != null)
                	point.setPno(nxtpageno);
                return point;
            }
            return null;
        }
       
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;
        }
    }

    private PointerRecord splitPage(RandomAccessFile raf, PageDef page, PointerRecord rec, int p1, int p2)   {
        try {
            if (page != null && rec != null) {
                int location;
                boolean isFirst = false;

                PointerRecord pointer;
                if(page.gettypepage() == PageDef.LEAF_TABLE_PAGE) {
                    return null;
                }
                location = searchloc(raf, rec.getKey(), page.getCellnum(), ((page.getPno() * PageDef.PAGE_SIZE) + PageDef.getHeaderFixedLength()), page.gettypepage(), true);
                if (location < (page.getRecadd().size() / 2)) {
                    isFirst = true;
                }

                if(p1 == 0) {
                    p1 = p2;
                    p2++;
                }
                raf.setLength(PageDef.PAGE_SIZE * (p2 + 1));

                //Page 1
                PageDef<PointerRecord> page1 = new PageDef<>(p1);
                page1.settypepage(page.gettypepage());
                page1.setPno(p1);
                List<PointerRecord> leftRecords = cpRecords(raf, (page.getPno() * PageDef.PAGE_SIZE), page.getRecadd(), (byte) 0, (byte) (page.getCellnum() / 2), page1.getPno(), rec);
                if (isFirst)
                    leftRecords.add(location, rec);
                pointer = leftRecords.get(leftRecords.size() - 1);
                pointer.setPno(p2);
                leftRecords.remove(leftRecords.size() - 1);
                page1.setCellnum((byte) leftRecords.size());
                int index = 0;
                short offset = PageDef.PAGE_SIZE;
                for (PointerRecord pointerRecord1 : leftRecords) {
                    index++;
                    offset = (short) (PageDef.PAGE_SIZE - (pointerRecord1.getSize() * index));
                    pointerRecord1.setFlag(offset);
                    page1.getRecadd().add(offset);
                }
                page1.setInitadd((short) (offset - 1));
                page1.setRightnode(pointer.getleftpage());
                this.addheader(raf, page1);
                for(PointerRecord pointerRecord1 : leftRecords) {
                    this.addrec(raf, pointerRecord1);
                }

                //Page 2
                PageDef<PointerRecord> page2 = new PageDef<>(p2);
                page2.settypepage(page.gettypepage());
                List<PointerRecord> rightRecords = cpRecords(raf, (page.getPno() * PageDef.PAGE_SIZE), page.getRecadd(), (byte) ((page.getCellnum() / 2) + 1), page.getCellnum(), p2, rec);
                if(!isFirst) {
                    int position = (location - (page.getRecadd().size() / 2) + 1);
                    if(position >= rightRecords.size())
                        rightRecords.add(rec);
                    else
                        rightRecords.add(position, rec);
                }
                page2.setCellnum((byte) rightRecords.size());
                page2.setRightnode(page.getRightnode());
                rightRecords.get(0).setleftpage(page.getRightnode());
                index = 0;
                offset = PageDef.PAGE_SIZE;
                for(PointerRecord pointerRecord1 : rightRecords) {
                    index++;
                    offset = (short) (PageDef.PAGE_SIZE - (pointerRecord1.getSize() * index));
                    pointerRecord1.setFlag(offset);
                    page2.getRecadd().add(offset);
                }
                page2.setInitadd((short) (offset - 1));
                this.addheader(raf, page2);
                for(PointerRecord pointerRecord1 : rightRecords) {
                    this.addrec(raf, pointerRecord1);
                }
                pointer.setPno(p2);
                pointer.setleftpage(p1);
                return pointer;
            }
        }
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
        return null;
    }

    private <T> List<T> cpRecords(RandomAccessFile raf, long pagestart, List<Short> recadd, byte startIndex, byte endIndex, int pageno, T obj)   {
        try {
            List<T> recs = new ArrayList<>();
            byte norec;
            byte[] serialTypeCodes;
            for (byte i = startIndex; i < endIndex; i++) {
            	raf.seek(pagestart + recadd.get(i));
                if (obj.getClass().equals(PointerRecord.class)) {
                    PointerRecord rec = new PointerRecord();
                    rec.setPno(pageno);
                    
                    rec.setKey(raf.readInt());
                    rec.setFlag((short) (pagestart + PageDef.PAGE_SIZE - 1 - (rec.getSize() * (i - startIndex + 1))));
                    rec.setleftpage(raf.readInt());
                    recs.add(i - startIndex, (T) rec);
                } else if (obj.getClass().equals(DataRecord.class)) {
                    DataRecord data = new DataRecord();
                    data.setPageLoc(pageno);
                  
                    data.setRowId(raf.readInt());
                    data.setFlag(recadd.get(i));
                    data.setlength(raf.readShort());
                    norec = raf.readByte();
                    serialTypeCodes = new byte[norec];
                    for (byte j = 0; j < norec; j++) {
                        serialTypeCodes[j] = raf.readByte();
                    }
                    for (byte j = 0; j < norec; j++) {
                        switch (serialTypeCodes[j]) {
                            //case TinyInt.nullSerialCode is overridden with Text

                            case Constants.onebytecode:
                            	data.getColVal().add(new TextType(null));
                                break;

                            case Constants.twobytecode:
                            	data.getColVal().add(new SmallInt(raf.readShort(), true));
                                break;

                            

                            case Constants.bigINTcode:
                            	data.getColVal().add(new BigInt(raf.readLong()));
                                break;

                            case Constants.serialREALcode:
                            	data.getColVal().add(new Real(raf.readFloat()));
                                break;

                            case Constants.serialDbcode:
                            	data.getColVal().add(new DoubleType(raf.readDouble()));
                                break;

                            case Constants.serialDATEcode:
                            	data.getColVal().add(new DateType(raf.readLong()));
                                break;

                            case Constants.DATETIMEcode:
                            	data.getColVal().add(new DateTime(raf.readLong()));
                                break;
                                
                            case Constants.fourbytecode:
                            	data.getColVal().add(new Real(raf.readFloat(), true));
                                break;

                            case Constants.eightbytecode:
                            	data.getColVal().add(new DoubleType(raf.readDouble(), true));
                                break;

                            case Constants.serialTinyIntcode:
                            	data.getColVal().add(new TinyInt(raf.readByte()));
                                break;

                            case Constants.smallINTcode:
                            	data.getColVal().add(new SmallInt(raf.readShort()));
                                break;

                            case Constants.serialINTcode:
                            	data.getColVal().add(new IntType(raf.readInt()));
                                break;

                            case Constants.textcode:
                            	data.getColVal().add(new TextType(""));
                                break;

                            default:
                                if (serialTypeCodes[j] > Constants.textcode) {
                                    byte length = (byte) (serialTypeCodes[j] - Constants.textcode);
                                    char[] text = new char[length];
                                    for (byte k = 0; k < length; k++) {
                                        text[k] = (char) raf.readByte();
                                    }
                                    data.getColVal().add(new TextType(new String(text)));
                                }
                                break;

                        }
                    }
                    recs.add(i - startIndex, (T) data);
                }
            }
            return recs;
        }
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
    }

    private PageDef fetchpage(RandomAccessFile raf, DataRecord rec, int pno)   {
        try {
        	PageDef page = fetchheader(raf, pno);
            if (page.gettypepage() == PageDef.LEAF_TABLE_PAGE) {
                return page;
            }
            pno = searchloc(raf, rec.getId(), page.getCellnum(), (page.getBaseAddress() + PageDef.getHeaderFixedLength()), PageDef.INTERIOR_TABLE_PAGE, false);
            if (pno == -1) return null;
            return fetchpage(raf, rec, pno);
        }
          
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
    }

    private int getadd(File file, int rowId, int pno)   {
        int loc = -1;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            PageDef page = fetchheader(raf, pno);
            if(page.gettypepage() == PageDef.LEAF_TABLE_PAGE) {
                loc = searchloc(raf, rowId, page.getCellnum(), (page.getBaseAddress() + PageDef.getHeaderFixedLength()), PageDef.LEAF_TABLE_PAGE, false);
                raf.close();
            }
        }
          
        catch (Exception e) {
        	System.out.println("Error occured");  
        }
        return loc;
    }

   

    private int searchloc(RandomAccessFile raf, int key, int norec, long fetchpos, byte type, boolean searchlit)   {
        try {
            int start = 0;
            int end = norec;
            int mid;
            int pnum = -1;
            int rowId;
            short add;

            while(true) {
                if(start > end || start == norec) {
                    if(searchlit || type == PageDef.LEAF_TABLE_PAGE )
                        return start > norec ? norec : start;
                    if(type == PageDef.INTERIOR_TABLE_PAGE) {
                        if (end < 0)
                            return pnum;
                        raf.seek(fetchpos - PageDef.getHeaderFixedLength() + 4);
                        return raf.readInt();
                    }
                }
                mid = (start + end) / 2;
                raf.seek(fetchpos + (Short.BYTES * mid));
                add = raf.readShort();
                raf.seek(fetchpos - PageDef.getHeaderFixedLength() + add);
                if (type == PageDef.LEAF_TABLE_PAGE) {
                	raf.readShort();
                    rowId = raf.readInt();
                    if (rowId == key) return mid;
                    if (rowId > key) {
                        end = mid - 1;
                    } else {
                        start = mid + 1;
                    }
                } else if (type == PageDef.INTERIOR_TABLE_PAGE) {
                	pnum = raf.readInt();
                    rowId = raf.readInt();
                    if (rowId > key) {
                        end = mid - 1;
                    } else {
                        start = mid + 1;
                    }
                }
            }
        }
        catch (Exception e) {
        	System.out.println("Error occured");  
            }
		return -1;
    }

    private PageDef fetchheader(RandomAccessFile raf, int pno)   {
        try {
        	PageDef pdef;
        	raf.seek(PageDef.PAGE_SIZE * pno);
            byte pt = raf.readByte();
            if (pt == PageDef.INTERIOR_TABLE_PAGE) {
            	pdef = new PageDef<PointerRecord>();
            } else {
            	pdef = new PageDef<DataRecord>();
            }
            pdef.settypepage(pt);
            pdef.setPno(pno);
            pdef.setCellnum(raf.readByte());
            pdef.setInitadd(raf.readShort());
            pdef.setRightnode(raf.readInt());
            for (byte i = 0; i < pdef.getCellnum(); i++) {
            	pdef.getRecadd().add(raf.readShort());
            }
            return pdef;
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
    }

    private boolean addheader(RandomAccessFile raf, PageDef page)   {
        try {
            raf.seek(page.getPno() * PageDef.PAGE_SIZE);
            raf.writeByte(page.gettypepage());
            raf.writeByte(page.getCellnum());
            raf.writeShort(page.getInitadd());
            raf.writeInt(page.getRightnode());
            for (Object flag : page.getRecadd()) {
                raf.writeShort((short) flag);
            }
            return true;
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return false;        }
    }

    private boolean addrec(RandomAccessFile raf, DataRecord rec)   {
        try {
            raf.seek((rec.getPageLoc() * PageDef.PAGE_SIZE) + rec.getFlag());
            raf.writeShort(rec.getLength());
            raf.writeInt(rec.getId());
            raf.writeByte((byte) rec.getColVal().size());
            raf.write(rec.getSerialTypeCodes());
            for (Object obj : rec.getColVal()) {
                switch (Utils.fetchtype(obj)) {
                    case Constants.tinyInt:
                        raf.writeByte(((TinyInt) obj).getValue());
                        break;

                    case Constants.smallInt:
                        raf.writeShort(((SmallInt) obj).getValue());
                        break;
                    case Constants.REAL:
                        raf.writeFloat(((Real) obj).getValue());
                        break;

                    case Constants.DOUBLE:
                        raf.writeDouble(((DoubleType) obj).getValue());
                        break;

                    case Constants.DATE:
                        raf.writeLong(((DateType) obj).getValue());
                        break;

                    case Constants.INT:
                        raf.writeInt(((IntType) obj).getValue());
                        break;

                    case Constants.BIGINT:
                        raf.writeLong(((BigInt) obj).getValue());
                        break;

                   
                    case Constants.DATETIME:
                        raf.writeLong(((DateTime) obj).getValue());
                        break;

                    case Constants.TEXT:
                        if (((TextType) obj).getValue() != null)
                            raf.writeBytes(((TextType) obj).getValue());
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return false;        }
        return true;
    }

    private boolean addrec(RandomAccessFile raf, PointerRecord rec)   {
        try {
            raf.seek((rec.getPno() * PageDef.PAGE_SIZE) + rec.getFlag());
            raf.writeInt(rec.getleftpage());
            raf.writeInt(rec.getKey());
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return false;        }
        return true;
    }



    public List<DataRecord> fetchrec(String db, String tbname, InternalCondition condition, List<Byte> indexlist, boolean flag)   {
        List<InternalCondition> conditionList = new ArrayList<>();
        if(condition != null)
            conditionList.add(condition);
        return fetchrec(db, tbname, conditionList, indexlist, flag);
    }



    public List<DataRecord> fetchrec(String db, String tbname, List<InternalCondition> conditionList, List<Byte> colindex, boolean flag)   {
        try {
            File file = new File(Utils.fetchDbPath(db) + "/" + tbname + Constants.FILE_EXTENSION);
            if (file.exists()) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                if (conditionList != null) {
                	PageDef page = getleftleaf(file);
                    DataRecord rec;
                    List<DataRecord> recmatch = new ArrayList<>();
                    boolean isMatch = false;
                    byte columnIndex;
                    short condition;
                    Object value;
                    while (page != null) {
                        for (Object offset : page.getRecadd()) {
                            isMatch = true;
                            rec = readDataRecord(raf, page.getPno(), (short) offset);
                            for(int i = 0; i < conditionList.size(); i++) {
                                isMatch = false;
                                columnIndex = conditionList.get(i).getIndex();
                                value = conditionList.get(i).getValue();
                                condition = conditionList.get(i).getConditionType();
                                if (rec != null && rec.getColVal().size() > columnIndex) {
                                    Object object = rec.getColVal().get(columnIndex);
                                    try {
                                        isMatch = compareobj(object, value, condition);
                                    }
                                   
                                    catch (Exception e) {
                                        raf.close();
                                        e.printStackTrace();
                                        System.out.println("Error occured2");  
                                    	return null;                                    }
                                    if(!isMatch) break;
                                }
                            }

                            if(isMatch) {
                                DataRecord matchrec = rec;
                                if(colindex != null) {
                                	matchrec = new DataRecord();
                                	matchrec.setRowId(rec.getId());
                                	matchrec.setPageLoc(rec.getPageLoc());
                                	matchrec.setFlag(rec.getFlag());
                                    for (Byte index : colindex) {
                                    	matchrec.getColVal().add(rec.getColVal().get(index));
                                    }
                                }
                                recmatch.add(matchrec);
                                if(flag) {
                                    raf.close();
                                    return recmatch;
                                }
                            }
                        }
                        if (page.getRightnode() == PageDef.RIGHTMOST_PAGE)
                            break;
                        page = fetchheader(raf, page.getRightnode());
                    }
                    raf.close();
                    return recmatch;
                }
            } else {
            	System.out.println("Error occured");  
                        return null;
            }
        }
          
        catch (Exception e) {
        	System.out.println("Error occured");  
             }
        return null;
    }

    public int recchange(String db, String tbname, InternalCondition condition, List<Byte> colindex, List<Object> colvallist, boolean flag)   {
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(condition);
        return recchange(db, tbname, conditions, colindex, colvallist, flag);
    }

    public int recchange(String db, String tabname, List<InternalCondition> conditions, List<Byte> colindex, List<Object> colvallist, boolean flag)   {
        int updateRecordCount = 0;
        try {
       // 	System.out.println("2");
            if (conditions == null || colindex == null
                    || colvallist == null)
                return updateRecordCount;
            if (colindex.size() != colvallist.size())
                return updateRecordCount;
            File file = new File(Utils.fetchDbPath(db) + "/" + tabname + Constants.FILE_EXTENSION);
            if (file.exists()) {
            	//System.out.println("3");

                List<DataRecord> recs = fetchrec(db, tabname, conditions, null, true);
                if (recs != null) {
                    if (recs.size() > 0) {
                        byte index;
                        Object obj;
                        RandomAccessFile raf = new RandomAccessFile(file, "rw");
                        for (DataRecord rec : recs) {
                            for (int i = 0; i < colindex.size(); i++) {
                                index = colindex.get(i);
                                obj = colvallist.get(i);
                                if (flag) {
                                    rec.getColVal().set(index, increment((Dt_Numeric) rec.getColVal().get(index), (Dt_Numeric) obj));
                                } else {
                                    rec.getColVal().set(index, obj);
                                }
                            }
                        //	System.out.println("4");

                            this.addrec(raf, rec);
                            updateRecordCount++;
                        }
                        raf.close();
                    //	System.out.println("5:"+updateRecordCount );

                        return updateRecordCount;
                    }
                }
            } else {
            	System.out.println("Error occured1");  
                       }
        }
       
        catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("Error occured1");  
            }
        return updateRecordCount;
    }

    private <T> Dt_Numeric<T> increment(Dt_Numeric<T> obj1, Dt_Numeric<T> obj2) {
        obj1.increment(obj2.getValue());
        return obj1;
    }

    public PageDef<DataRecord> getLastRecordAndPage(String db, String tabname)   {
        try {
            File file = new File(Utils.fetchDbPath(db) + "/" + tabname + Constants.FILE_EXTENSION);
            if (file.exists()) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                PageDef<DataRecord> page = getrightleaf(file);
                if (page.getCellnum() > 0) {
                    raf.seek((PageDef.PAGE_SIZE * page.getPno()) + PageDef.getHeaderFixedLength() + ((page.getCellnum() - 1) * Short.BYTES));
                    short add = raf.readShort();
                    DataRecord rec = readDataRecord(raf, page.getPno(), add);
                    if (rec != null)
                        page.getPagerec().add(rec);
                }
                raf.close();
                return page;
            } else {
            	System.out.println("Error occured");  
            	return null;
            }
        }
          
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;
        }
    }

    private PageDef getrightleaf(File f)   {
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            PageDef page = fetchheader(raf, 0);
            while (page.gettypepage() == PageDef.INTERIOR_TABLE_PAGE && page.getRightnode() != PageDef.RIGHTMOST_PAGE) {
                page = fetchheader(raf, page.getRightnode());
            }
            raf.close();
            return page;
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
    }

    private PageDef getleftleaf(File f)   {
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            PageDef page = fetchheader(raf, 0);
            while (page.gettypepage() == PageDef.INTERIOR_TABLE_PAGE) {
                if (page.getCellnum() == 0) return null;
                raf.seek((PageDef.PAGE_SIZE * page.getPno()) + ((short) page.getRecadd().get(0)));
                page = fetchheader(raf, raf.readInt());
            }
            raf.close();
            return page;
        } catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
    }

    public int removerec(String db, String tabname, List<InternalCondition> conds)   {
        int delcount = 0;
        try {
            File f = new File(Utils.fetchDbPath(db) + "/" + tabname + Constants.FILE_EXTENSION);
            if (f.exists()) {
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                if(conds != null) {
                	PageDef page = getleftleaf(f);
                    DataRecord rec;
                    boolean matchchk;
                    byte colindex;
                    short condition;
                    Object obj;
                    while (page != null) {
                        for (Short offset : new ArrayList<Short>(page.getRecadd())) {
                        	matchchk = true;
                            rec = readDataRecord(raf, page.getPno(), offset);
                            for(int i = 0; i < conds.size(); i++) {
                            	matchchk = false;
                            	colindex = conds.get(i).getIndex();
                            	obj = conds.get(i).getValue();
                                condition = conds.get(i).getConditionType();
                                if (rec != null && rec.getColVal().size() > colindex) {
                                    Object object = rec.getColVal().get(colindex);
                                    try {
                                    	matchchk = compareobj(object, obj, condition);
                                    }
                                    
                                    catch (Exception e) {
                                        raf.close();
                                        System.out.println("Error occured");  
                                                                        }

                                    if(!matchchk) break;
                                }
                            }
                            if(matchchk) {
                                page.setCellnum((byte) (page.getCellnum() - 1));
                                page.getRecadd().remove(offset);
                                if(page.getCellnum() == 0) {
                                    page.setInitadd((short) (page.getBaseAddress() + PageDef.PAGE_SIZE - 1));
                                }
                                this.addheader(raf, page);
                                InitialDatabaseHelper.updateRowCount(db, tabname, -1);
                                delcount++;
                            }
                        }
                        if(page.getRightnode() == PageDef.RIGHTMOST_PAGE)
                            break;
                        page = fetchheader(raf, page.getRightnode());
                    }
                    raf.close();
                    return delcount;
                }
            }
            else {
            	System.out.println("Error occured");  
                        return delcount;
            }
        }
          
       
        catch (Exception e) {
        	System.out.println("Error occured");  
             }
        return delcount;
    }

    private boolean compareobj(Object obj1, Object obj2, short condition)   {
        boolean matchflag = false;
        if(((DataType) obj1).isNull()) matchflag = false;
        else {
            switch (Utils.fetchtype(obj1)) {
                case Constants.tinyInt:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.tinyInt:
                        	matchflag = ((TinyInt) obj1).compare((TinyInt) obj2, condition);
                            break;
                        case Constants.DOUBLE:
                            switch (Utils.fetchtype(obj2)) {
                                case Constants.REAL:
                                	matchflag = ((DoubleType) obj1).compare((Real) obj2, condition);
                                    break;

                                case Constants.DOUBLE:
                                	matchflag = ((DoubleType) obj1).compare((DoubleType) obj2, condition);
                                    break;

                                default:
                                	System.out.println("Error occured");  
                                                 }
                            break;

                        case Constants.DATE:
                            switch (Utils.fetchtype(obj2)) {
                                case Constants.DATE:
                                	matchflag = ((DateType) obj1).compare((DateType) obj2, condition);
                                    break;

                                default:
                                	System.out.println("Error occured");  
                                                   }
                            break;

                        case Constants.DATETIME:
                            switch (Utils.fetchtype(obj2)) {
                                case Constants.DATETIME:
                                	matchflag = ((DateTime) obj1).compare((DateTime) obj2, condition);
                                    break;

                                default:
                                	System.out.println("Error occured");  
                                               }
                            break;
                        case Constants.smallInt:
                        	matchflag = ((TinyInt) obj1).compare((SmallInt) obj2, condition);
                            break;

                        case Constants.INT:
                        	matchflag = ((TinyInt) obj1).compare((IntType) obj2, condition);
                            break;

                        case Constants.BIGINT:
                        	matchflag = ((TinyInt) obj1).compare((BigInt) obj2, condition);
                            break;

                        default:
                        	System.out.println("Error occured");  
                                        }
                    break;

                case Constants.smallInt:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.tinyInt:
                        	matchflag = ((SmallInt) obj1).compare((TinyInt) obj2, condition);
                            break;

                        case Constants.smallInt:
                        	matchflag = ((SmallInt) obj1).compare((SmallInt) obj2, condition);
                            break;

                        case Constants.INT:
                        	matchflag = ((SmallInt) obj1).compare((IntType) obj2, condition);
                            break;

                        case Constants.BIGINT:
                        	matchflag = ((SmallInt) obj1).compare((BigInt) obj2, condition);
                            break;

                        default:
                        	System.out.println("Error occured");  
                                           }
                    break;

                
                case Constants.BIGINT:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.tinyInt:
                        	matchflag = ((BigInt) obj1).compare((TinyInt) obj2, condition);
                            break;

                        case Constants.smallInt:
                        	matchflag = ((BigInt) obj1).compare((SmallInt) obj2, condition);
                            break;

                        case Constants.INT:
                        	matchflag = ((BigInt) obj1).compare((IntType) obj2, condition);
                            break;

                        case Constants.BIGINT:
                        	matchflag = ((BigInt) obj1).compare((BigInt) obj2, condition);
                            break;

                        default:
                        	System.out.println("Error occured");  
                                        }
                    break;
                case Constants.INT:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.tinyInt:
                        	matchflag = ((IntType) obj1).compare((TinyInt) obj2, condition);
                            break;

                        case Constants.smallInt:
                        	matchflag = ((IntType) obj1).compare((SmallInt) obj2, condition);
                            break;

                        case Constants.INT:
                        	matchflag = ((IntType) obj1).compare((IntType) obj2, condition);
                            break;

                        case Constants.BIGINT:
                        	matchflag = ((IntType) obj1).compare((BigInt) obj2, condition);
                            break;

                        default:
                        	System.out.println("Error occured");  
                                           }
                    break;

                case Constants.REAL:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.REAL:
                        	matchflag = ((Real) obj1).compare((Real) obj2, condition);
                            break;

                        case Constants.DOUBLE:
                        	matchflag = ((Real) obj1).compare((DoubleType) obj2, condition);
                            break;

                        default:
                        	System.out.println("Error occured");  
                                      }
                    break;

               

                case Constants.TEXT:
                    switch (Utils.fetchtype(obj2)) {
                        case Constants.TEXT:
                        	//System.out.println("inside case");
                            if (((TextType) obj1).getValue() != null) {
                            //	System.out.println("inside if");
                                if (condition != InternalCondition.EQUALS) {
                                	System.out.println("Error occured3");  
                                                            } else
                               matchflag = ((TextType) obj1).getValue().equalsIgnoreCase(((TextType) obj2).getValue());
                            }
                            break;

                        default:
                        	System.out.println("Error occured4");  
                                     }
                    break;
            }
        }
        return matchflag;
    }

    private DataRecord readDataRecord(RandomAccessFile raf, int pno, short add)   {
        try {
            if (pno >= 0 && add >= 0) {
                DataRecord rec = new DataRecord();
                rec.setPageLoc(pno);
                rec.setFlag(add);
                raf.seek((PageDef.PAGE_SIZE * pno) + add);
                rec.setlength(raf.readShort());
                rec.setRowId(raf.readInt());
                byte numcol = raf.readByte();
                byte[] codes = new byte[numcol];
                for (byte i = 0; i < numcol; i++) {
                	codes[i] = raf.readByte();
                }
                Object obj;
                for (byte i = 0; i < numcol; i++) {
                    switch (codes[i]) {
                        //case TinyInt.nullSerialCode is overridden with Text

                    case Constants.twobytecode:
                    	obj = new SmallInt(raf.readShort(), true);
                        break;
                   

                    case Constants.eightbytecode:
                    	obj = new DoubleType(raf.readDouble(), true);
                        break;
                        case Constants.onebytecode:
                        	obj = new TextType(null);
                            break;


                        case Constants.serialTinyIntcode:
                        	obj = new TinyInt(raf.readByte());
                            break;

                        case Constants.smallINTcode:
                        	obj = new SmallInt(raf.readShort());
                            break;

                        case Constants.serialINTcode:
                        	obj = new IntType(raf.readInt());
                            break;

                        

                        case Constants.DATETIMEcode:
                        	obj = new DateTime(raf.readLong());
                            break;
                        case Constants.bigINTcode:
                        	obj = new BigInt(raf.readLong());
                            break;

                        case Constants.serialREALcode:
                        	obj = new Real(raf.readFloat());
                            break;

                        case Constants.serialDbcode:
                        	obj = new DoubleType(raf.readDouble());
                            break;

                        case Constants.serialDATEcode:
                        	obj = new DateType(raf.readLong());
                            break;

                        case Constants.fourbytecode:
                        	obj = new Real(raf.readFloat(), true);
                            break;
                        case Constants.textcode:
                        	obj = new TextType("");
                            break;

                        default:
                            if (codes[i] > Constants.textcode) {
                                byte length = (byte) (codes[i] - Constants.textcode);
                                char[] text = new char[length];
                                for (byte k = 0; k < length; k++) {
                                    text[k] = (char) raf.readByte();
                                }
                                obj = new TextType(new String(text));
                            } else
                            	obj = null;
                            break;
                    }
                    rec.getColVal().add(obj);
                }
                return rec;
            }
        } 
        catch (Exception e) {
        	System.out.println("Error occured");  
        	return null;        }
        return null;
    }

}
