package unsw.dungeon;

import org.json.JSONObject;

public class DungeonTestLoader extends DungeonLoader {
    
    public DungeonTestLoader(JSONObject dungeon_json) {
        super(dungeon_json);
    }
    
    @Override
    public void onLoad(Player player) {
        return;
    }

    @Override
    public void onLoad(Wall wall) {
        return;
    }
    
    @Override
    public void onLoad(Boulder boulder) {
        return;
    }
    
    @Override
    public void onLoad(Exit exit) {
        return;
    }
    
    @Override
    public void onLoad(Portal portal) {
        return;
    }
    
    @Override
    public void onLoad(FloorSwitch floorSwitch) {
        return;
    }
    
    @Override
    public void onLoad(Enemy enemy) {
        return;
    }

    @Override
    public void onLoad(Key key) {
        return;
    }
    @Override
    public void onLoad(Door door) {
        return;
    }

    @Override
    public void onLoad(Treasure treasure) {
        return;
    }
    
    @Override
    public void onLoad(Potion potion) {
        return;
    }
    
    @Override
    public void onLoad(Weapon weapon) {
        return;
    }

    @Override
    public void onLoad(Ghost ghost) {
        return;

    }

    @Override
    public void onLoad(LandMine landmine) {
        return;

    }

    @Override
    public void onLoad(Dragon dragon) {
        return;

    }

    @Override
    public void onLoad(FireBall fireball) {
        return;

    }


}