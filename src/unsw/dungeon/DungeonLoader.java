package unsw.dungeon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Loads a dungeon from a .json file.
 *
 * By extending this class, a subclass can hook into entity creation. This is
 * useful for creating UI elements with corresponding entities.
 *
 * @author Robert Clifton-Everest
 *
 */
public abstract class DungeonLoader {

    private JSONObject json;

    public DungeonLoader(String filename) throws FileNotFoundException {
        json = new JSONObject(new JSONTokener(new FileReader("dungeons/" + filename)));
    }
    
    public DungeonLoader(JSONObject dungeon_json) {
        this.json = dungeon_json;
    }

    /**
     * Parses the JSON to create a dungeon.
     * @return
     */
    public Dungeon load() {
        int width = json.getInt("width");
        int height = json.getInt("height");

        Dungeon dungeon = new Dungeon(width, height);

        JSONArray jsonEntities = json.getJSONArray("entities");

        for (int i = 0; i < jsonEntities.length(); i++) {
            loadEntity(dungeon, jsonEntities.getJSONObject(i));
        }
        
        JSONObject goals = json.getJSONObject("goal-condition");
        Goal mainGoal = loadGoals(goals);
        dungeon.setMainGoal(mainGoal);
        return dungeon;
    }
    
    private Goal loadGoals(JSONObject goals) {
        
        String goalType = goals.getString("goal");
        
        switch(goalType) {
            // Leaf Goals are here. Base case of the recursion.
            case "exit":
                return new ExitGoal(new ArrayList<Entity>());
            case "boulders":
                return new BoulderGoal(new ArrayList<Entity>());
            case "enemies":
                return new EnemyGoal(new ArrayList<Entity>());
            case "treasure":
                return new TreasureGoal(new ArrayList<Entity>());
                
            // Composite goals are here. Loop through all the subgoals
            // in the conjunction and recursively call loadGoals on each
            // of the children, adding the resulting goal to each child.
            case "AND":
                JSONArray subGoals = goals.getJSONArray("subgoals");
                LogicalAndGoal andGoal = new LogicalAndGoal();
                for (int i = 0; i < subGoals.length(); i++) {
                    // Recursive goal creation
                    Goal subGoal = loadGoals(subGoals.getJSONObject(i));
                    andGoal.addSubGoal(subGoal);
                }
                return andGoal;
            case "OR":
                subGoals = goals.getJSONArray("subgoals");
                LogicalOrGoal orGoal = new LogicalOrGoal();
                for (int i = 0; i < subGoals.length(); i++) {
                    // Recursive goal creation
                    Goal subGoal = loadGoals(subGoals.getJSONObject(i));
                    orGoal.addSubGoal(subGoal);
                }
                return orGoal;
            default:
                throw new JSONException("Invalid goal type entered.");
        }
    }

    private void loadEntity(Dungeon dungeon, JSONObject json) {
        String type = json.getString("type");
        int x = json.getInt("x");
        int y = json.getInt("y");

        Entity entity = null;
        switch (type) {
        case "player":
            Player player = new Player(dungeon, x, y);
            dungeon.setPlayer(player);
            onLoad(player);
            entity = player;
            break;
        case "wall":
            Wall wall = new Wall(x, y);
            onLoad(wall);
            entity = wall;
            break;
        case "boulder":
            Boulder boulder = new Boulder(dungeon, x, y);
            onLoad(boulder);
            entity = boulder;
            break;
        }
        dungeon.addEntity(entity);
    }

    public abstract void onLoad(Player player);

    public abstract void onLoad(Wall wall);
    
    // TODO Create additional abstract methods for the other entities
    public abstract void onLoad(Boulder boulder);
}
