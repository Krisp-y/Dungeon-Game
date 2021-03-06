package unsw.dungeon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 
     * @return
     */
    public Dungeon load() {
        int width = json.getInt("width");
        int height = json.getInt("height");

        Dungeon dungeon = new Dungeon(width, height);

        JSONArray jsonEntities = json.getJSONArray("entities");
        Map<String, List<Entity>> entitiesMap = loadEntities(dungeon, jsonEntities);

        // Only try and greate a goal condition if it is specified in json.
        if (json.has("goal-condition")) {
            JSONObject goals = json.getJSONObject("goal-condition");
            Goal mainGoal = loadGoals(entitiesMap, dungeon, goals);
            dungeon.setMainGoal(mainGoal);
        }

        return dungeon;
    }

    private Goal loadGoals(Map<String, List<Entity>> entitiesMap, Dungeon dungeon, JSONObject goals)
            throws InvalidGoalException {

        String goalType = goals.getString("goal");
        BasicGoal g;
        switch (goalType) {
            // Leaf Goals are here. Base case of the recursion. Fail loudly when
            // there are no entities available to complete the goals.
            case "exit":
                if (entitiesMap.get("exit") == null) {
                    throw new InvalidGoalException("Exit goal specified but no exit tiles provided.");
                }
                g = dungeon.getExitGoal();
                if (g != null) {
                    return g;
                }
                g = new ExitGoal(dungeon, entitiesMap.get("exit"));
                dungeon.addBasicGoal(g);
                return g;
            case "boulders":
                if (entitiesMap.get("switch") == null) {
                    throw new InvalidGoalException("Boulder goal specified but no switches provided.");
                } else if (entitiesMap.get("boulder") == null) {
                    throw new InvalidGoalException("Boulder goal specified but no boulders provided.");
                }
                g = dungeon.getBouldersGoal();
                if (g != null) {
                    return g;
                }
                g = new BoulderGoal(dungeon, entitiesMap.get("switch"), entitiesMap.get("boulder"));
                dungeon.addBasicGoal(g);
                return g;
            case "enemies":
                if (entitiesMap.get("enemy") == null) {
                    throw new InvalidGoalException("Enemy goal specified but not enemies provided.");
                }
                g = dungeon.getEnemyGoal();
                if (g != null) {
                    return g;
                }
                entitiesMap.get("enemy").addAll(entitiesMap.get("dragon"));
                g = new EnemyGoal(dungeon, entitiesMap.get("enemy"));
                dungeon.addBasicGoal(g);
                return g;
            case "treasure":
                if (entitiesMap.get("treasure") == null) {
                    throw new InvalidGoalException("Treasure goal specified but no treasure provided.");
                }
                g = dungeon.getTreasureGoal();
                if (g != null) {
                    return g;
                }
                g =  new TreasureGoal(dungeon, entitiesMap.get("treasure"));
                dungeon.addBasicGoal(g);
                return g;
            
            // Composite goals are here. Loop through all the subgoals
            // in the conjunction and recursively call loadGoals on each
            // of the children, adding the resulting goal to each child.
            case "AND":
                JSONArray subGoals = goals.getJSONArray("subgoals");
                LogicalAndGoal andGoal = new LogicalAndGoal();
                for (int i = 0; i < subGoals.length(); i++) {
                    // Recursive goal creation
                    Goal subGoal = loadGoals(entitiesMap, dungeon, subGoals.getJSONObject(i));
                    andGoal.addSubGoal(subGoal);
                }
                return andGoal;
            case "OR":
                subGoals = goals.getJSONArray("subgoals");
                LogicalOrGoal orGoal = new LogicalOrGoal();
                for (int i = 0; i < subGoals.length(); i++) {
                    // Recursive goal creation
                    Goal subGoal = loadGoals(entitiesMap, dungeon, subGoals.getJSONObject(i));
                    orGoal.addSubGoal(subGoal);
                }
                return orGoal;
            default:
                throw new JSONException("Invalid goal type entered.");
        }
    }

    private Map<String, List<Entity>> loadEntities(Dungeon dungeon, JSONArray jsonEntities) {
        Map<String, List<Entity>> entitiesMap = new HashMap<String, List<Entity>>();
        for (int i = 0; i < jsonEntities.length(); i++) {
            JSONObject entityJson = jsonEntities.getJSONObject(i);
            Entity e = loadEntity(dungeon, entityJson);
            dungeon.addEntity(e);
            // Fill the dictionary of "type": "List<Entities>"
            String type = entityJson.getString("type");
            if (entitiesMap.get(type) == null) {
                entitiesMap.put(type, new ArrayList<Entity>(Arrays.asList(e)));
            } else {
                entitiesMap.get(type).add(e);
            }
        }
        return entitiesMap;
    }

    private Entity loadEntity(Dungeon dungeon, JSONObject json) {
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
            case "exit":
                Exit exit = new Exit(x, y);
                onLoad(exit);
                entity = exit;
                break;
            case "portal":
                // portal id is given as JSON object
                int id = json.getInt("id");
                Portal portal = new Portal(dungeon, x, y, id);
                onLoad(portal);
                entity = portal;
                break;
            case "switch":
                FloorSwitch floorSwitch = new FloorSwitch(dungeon, x, y);
                onLoad(floorSwitch);
                entity = floorSwitch;
                break;
            case "enemy":
                Enemy enemy = new Enemy(dungeon, x, y);
                onLoad(enemy);
                entity = enemy;
                break;
            case "key":
                //portal id is given as JSON object 
                int kid = json.getInt("id");
                Key key = new Key(x, y, kid);
                onLoad(key);
                entity = key;
                break;
            case "door":
                //portal id is given as JSON object 
                int did = json.getInt("id");
                Door door = new Door(dungeon, x, y, did);
                onLoad(door);
                entity = door;
                break;
            case "treasure":
                Treasure treasure = new Treasure(x, y);
                onLoad(treasure);
                entity = treasure;
                break;
            case "potion":
                Potion potion = new Potion(x, y);
                onLoad(potion);
                entity = potion;
                break;
            case "weapon":
                Weapon weapon = new Weapon(x, y);
                onLoad(weapon);
                entity = weapon;
                break;
            case "ghost":
                Ghost ghost = new Ghost(dungeon, x, y);
                onLoad(ghost);
                entity = ghost;
                break;
            case "dragon":
                Dragon dragon = new Dragon(dungeon, x, y);
                onLoad(dragon);
                entity = dragon;
                break;
            case "landmine":
                LandMine landmine = new LandMine(x, y);
                onLoad(landmine);
                entity = landmine;
                break;
                
        }
        return entity;
    }

    public abstract void onLoad(Player player);

    public abstract void onLoad(Wall wall);

    public abstract void onLoad(Boulder boulder);

    public abstract void onLoad(Exit exit);

    public abstract void onLoad(Portal portal);

    public abstract void onLoad(FloorSwitch floorSwitch);
    
    public abstract void onLoad(Enemy enemy);
    
    public abstract void onLoad(Key key);

    public abstract void onLoad(Door door);

    public abstract void onLoad(Treasure treasure);
    
    public abstract void onLoad(Potion potion);
    
    public abstract void onLoad(Weapon potion);

    public abstract void onLoad(Ghost ghost);

    public abstract void onLoad(FireBall fireball);

    public abstract void onLoad(Dragon dragon);

    public abstract void onLoad(LandMine landmine);
}
