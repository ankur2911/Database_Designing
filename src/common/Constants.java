package common;


public interface Constants {

	 String CATALOG_DATABASENAME = "catalog";
	  

    String FILE_EXTENSION = ".tbl";
    String DATA_DIRNAME = "data";
   
    String SYSTEM_TABLES = "davisbase_tables";
    String SYSTEM_COLUMNS = "davisbase_columns";
    
    byte INT = 2;
    byte BIGINT = 3;
    byte REAL = 4;
    byte DOUBLE = 5;

   
    byte smallINTcode = 0x05;
    byte serialINTcode = 0x06;
    byte bigINTcode = 0x07;
    
    byte DATETIMEcode = 0x0A;
    byte serialDATEcode = 0x0B;
    byte textcode = 0x0C;
    byte onebytecode = 0x00;
    byte twobytecode = 0x01;
   
    byte serialREALcode = 0x08;
    byte serialDbcode = 0x09;
    
    byte fourbytecode = 0x02;
    byte eightbytecode = 0x03;
    byte serialTinyIntcode = 0x04;
    

    byte invalidcode = -1;
    byte tinyInt = 0;
    byte smallInt = 1;
   
    byte DATE = 6;
    byte DATETIME = 7;
    byte TEXT = 8;
}
