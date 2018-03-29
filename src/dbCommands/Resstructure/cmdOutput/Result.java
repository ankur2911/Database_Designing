package dbCommands.Resstructure.cmdOutput;

/**
 * Created by ankur
 */

public class Result {
    int rowsAffected;
    private boolean isInternal = false;

   
    public void Display() {
        if(this.isInternal) return;
        System.out.println("Query Successful");

        System.out.println();
    }
    
    public Result(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    public Result(int rowsAffected, boolean isInternal) {
        this.rowsAffected = rowsAffected;
        this.isInternal = isInternal;
    }

}
