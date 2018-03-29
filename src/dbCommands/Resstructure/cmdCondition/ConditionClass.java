package dbCommands.Resstructure.cmdCondition;

/**
 * Created by ankur
 */

public class ConditionClass {
    public String column;
    public OpClass operator;
    public LiteralClass value;

    public static ConditionClass CreateCondition(String conditionString) {
    	OpClass operator = GetOperator(conditionString);
        if(operator == null) {
            System.out.println("Some error has occured");
            return null;
        }

        ConditionClass condition = null;

        switch (operator){
            case GREATER_THAN:
                condition = getConditionInternal(conditionString, operator, ">");
                break;
            case LESS_THAN:
                condition = getConditionInternal(conditionString, operator, "<");
                break;
            case LESS_THAN_EQUAL:
                condition = getConditionInternal(conditionString, operator, "<=");
                break;
            case GREATER_THAN_EQUAL:
                condition = getConditionInternal(conditionString, operator, ">=");
                break;
            case EQUALS:
                condition = getConditionInternal(conditionString, operator, "=");
                break;
        }

        return condition;
    }

    private static ConditionClass getConditionInternal(String conditionString, OpClass operator, String operatorString) {
        String[] parts;
        String column;
        LiteralClass literal;
        ConditionClass condition;
        parts = conditionString.split(operatorString);
        if(parts.length != 2) {
            System.out.println("Some error has occured");
            return null;
        }

        column = parts[0].trim();
        literal = LiteralClass.CreateLiteral(parts[1].trim());

        if (literal == null) {
            return null;
        }

        condition = new ConditionClass(column, operator, literal);
        return condition;
    }

    private ConditionClass(String column, OpClass operator, LiteralClass value){
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    private static OpClass GetOperator(String conditionString) {

        if(conditionString.contains("<=")){
            return OpClass.LESS_THAN_EQUAL;
        }

        if(conditionString.contains(">=")){
            return OpClass.GREATER_THAN_EQUAL;
        }

        if(conditionString.contains(">")){
            return OpClass.GREATER_THAN;
        }

        if(conditionString.contains("<")){
            return OpClass.LESS_THAN;
        }

        if(conditionString.contains("=")){
            return OpClass.EQUALS;
        }

        return null;
    }
}
