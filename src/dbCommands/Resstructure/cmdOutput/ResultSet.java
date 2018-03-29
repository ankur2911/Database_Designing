package dbCommands.Resstructure.cmdOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ankur
 */

public class ResultSet extends Result {
    private ArrayList<String> columns;
    private ArrayList<Record> records;

   
    public void addRecord(Record record){
        if(record == null) return;

        if(this.records == null) {
            this.records = new ArrayList<>();
        }

        this.records.add(record);
    }

    public void setColumns(ArrayList<String> columns){
        this.columns = columns;
    }
    public ResultSet(int rowAffected) {
        super(rowAffected);
        this.records = new ArrayList<>();
    }

    public static ResultSet CreateResultSet(){
        return new ResultSet(0);
    }

    @Override
    public void Display(){
        if(this.columns == null && this.columns.size() == 0) return;
        HashMap<String, Integer> columnSize = new HashMap<>();

        if(this.records == null || this.records.size() == 0){
            System.out.println("Nothing to display");
            return;
        }

        for(String column : this.columns){
            int maxLength = column.length();
            for(Record record : records){
                if(record.valueMap.containsKey(column)){
                    String value = record.valueMap.get(column).toString();
                    if(value.length() > maxLength){
                        maxLength = value.length();
                    }
                }
            }

            columnSize.put(column, maxLength);
        }

        System.out.println();
        String line = DisplayLine(columns, columnSize);
        System.out.println(line);
        String columns = DisplayColumns(columnSize);
        System.out.println(columns);
        System.out.println(line);

        for(Record record : this.records) {
            String recordString = DisplayRecord(record, this.columns, columnSize);
            System.out.println(recordString);
        }

        System.out.println(line);
        System.out.println("Query Successful");
       
        System.out.println();
    }

   

    private String DisplayLine(ArrayList<String> columns, HashMap<String, Integer> columnSizeMap) {
        StringBuffer buffer = new StringBuffer();

        for(String column : columns){
            buffer.append("|");

            String filler = FillerString('-', columnSizeMap.get(column) + 2);
            buffer.append(filler);
        }

        buffer.append("|");
        return buffer.toString();
    }

    private String FillerString(char character, int size) {
        char[] repeatCharacters = new char[size];
        Arrays.fill(repeatCharacters, character);
        return new String(repeatCharacters);
    }
    
    private String DisplayRecord(Record record, ArrayList<String> columns, HashMap<String, Integer> columnSizeMap) {
        StringBuffer buffer = new StringBuffer();

        for(String column : this.columns){
            buffer.append("| ");
            if(record.valueMap.containsKey(column)) {
                String value = record.get(column);
                buffer.append(value);
                int size = columnSizeMap.get(column);
                String filler = FillerString(' ', size - value.length() + 1);
                buffer.append(filler);
            }
        }
        buffer.append("|");

        return buffer.toString();
    }

    private String DisplayColumns(HashMap<String, Integer> columnSizeMap) {
        StringBuffer buffer = new StringBuffer();

        for(String column : this.columns){
            buffer.append("| ");
            buffer.append(column);
            int size = columnSizeMap.get(column);
            String filler = FillerString(' ', size-column.length() + 1);
            buffer.append(filler);
        }

        buffer.append("|");

        return buffer.toString();
    }
}
