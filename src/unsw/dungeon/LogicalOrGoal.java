package unsw.dungeon;

public class LogicalOrGoal extends ConjunctionGoal {

    public LogicalOrGoal() {
        super();
    }

    @Override
    public boolean isComplete() {
        boolean result = false;
        for (Goal subGoal : subGoals) {
            result = result || subGoal.isComplete();
        }
        updateListener(result);
        return result;
    }
    
    @Override
    public String toString() {
        String result = "(" + subGoals.get(0).toString();
        for (int i = 1; i < subGoals.size(); i++) {
            result += " OR " + subGoals.get(i);
        }
        result += ")";
        return result;
    }
}