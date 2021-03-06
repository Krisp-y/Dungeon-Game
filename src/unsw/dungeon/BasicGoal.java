package unsw.dungeon;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicGoal implements Goal, GoalSubscriber, GoalPublisher {
    protected List<GoalView> gvListeners;
    protected Dungeon dungeon;
    public BasicGoal(Dungeon dungeon, List<Entity> entities) {
        for (Entity entity : entities) {
            // This is a sanity check, any time we create a basic goal we should
            // ensure that the entities it is subscribing too implement the goal
            // subcriber interface
            if (entity instanceof GoalPublisher) {
                GoalPublisher gp = (GoalPublisher) entity;
                gp.subscribe(this);
            }
        }
        gvListeners = new ArrayList<GoalView>();
        subscribe(dungeon);
    }
    
    public void subscribe(GoalSubscriber gs) {
        this.dungeon = (Dungeon) gs;
    }

    public void unsubscribe(GoalSubscriber gs) {
        this.dungeon = null;
    }
    
    @Override
    public void notifySubscribers() {
        dungeon.goalUpdate();
    }
    
    public Goal getGoal() {
        return this;
    }
    
    public void addListener(GoalView gv) {
        gvListeners.add(gv);
    }
    
    public void updateListener(boolean result) {
        if (gvListeners != null) {
            for (GoalView listener: gvListeners) {
                listener.update(result);
            }
        }
    }

}