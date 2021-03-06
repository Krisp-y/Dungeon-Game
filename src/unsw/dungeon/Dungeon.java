/**
 *
 */
package unsw.dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A dungeon in the interactive dungeon player.
 *
 * A dungeon can contain many entities, each occupy a square. More than one
 * entity can occupy the same square.
 *
 * @author Robert Clifton-Everest
 *
 */
public class Dungeon implements GoalSubscriber {

    public static final int WEAPON_HEALTH = 5;
    public static final int POTION_HEALTH = 15;

    private int width, height;
    private List<Entity> entities;
    private Player player;
    private Goal mainGoal;
    private boolean isGameEnded;
    private DungeonController dc;
    private List<BasicGoal> basicGoals;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.entities = new CopyOnWriteArrayList<>();
        this.player = null;
        this.isGameEnded = false;
        this.basicGoals = new ArrayList<BasicGoal>();
        this.dc = null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Player getPlayer() {
        return player;
    }

    public void setMainGoal(Goal mainGoal) {
        this.mainGoal = mainGoal;
    }

    public Goal getMainGoal() {
        return mainGoal;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addEntity(Entity entity) {
        entity.spawn();
        entities.add(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void removeEntity(Entity e) {
        e.despawn();
        entities.remove(e);
    }

    /**
     * Updates the enemy movement strategy for each enemy in the dungeon to
     * RetreatStrategy
     */
    public void setEnemiesToRetreat() {
        for (Entity e : entities) {
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;
                enemy.setStrategy(new RetreatStrategy(this, enemy));
            }
        }
    }

    /**
     * Updates the enemy movement strategy for each enemy in the dungeon to
     * AttackStrategy
     */
    public void setEnemiesToAttack() {
        for (Entity e : entities) {
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;
                enemy.setStrategy(new AttackStrategy(this, enemy));
            }
        }
    }

    /**
     * Used by moveable objects to determine if they are colliding with collidable
     * objects. The function returns a reference to the an entity IF the entity is
     * collidable AND the entities coordinates are (x, y).
     *
     * @param x x-coordinate in the dungeon
     * @param y y-coordinate in the dungeon
     * @return reference to colliding entity.
     */
    public Collider getCollidingEntity(int x, int y) {
        for (Entity e : entities) {
            if (e instanceof Collider && e.getX() == x && e.getY() == y) {
                return (Collider) e;
            }
        }

        return null;
    }

    /**
     * Used by floor switches to determine if there is a boulder on top of them.
     *
     * @param x x-coordinate in the dungeon
     * @param y y-coordinate in the dungeon
     * @return reference to colliding boulder.
     */
    public Boulder getCollidingBoulder(int x, int y) {
        for (Entity e : entities) {
            if (e instanceof Boulder && e.getX() == x && e.getY() == y) {
                return (Boulder) e;
            }
        }
        return null;
    }

    @Override
    public void goalUpdate() {
        // mainGoal is null iff it has not been specified in the json.
        if (mainGoal != null && mainGoal.isComplete()) {
            endGameWon();
        }
        if (dc != null) {
            dc.updateBasicGoals();
        }   
    }
    
    public void highlightDoor(Key k) {
        for (Entity e : entities) {
            if (e instanceof Door) {
                Door d = (Door) e;
                if (d.getDoorID() == k.getKeyID()) {
                    d.changeDoorHighlightImage();
                }
            }
        }
    }

    public void endGameWon() {
        if (this.dc != null) {
            dc.gameWon();
        }
        this.isGameEnded = true;
    }

    public void endGameLost() {
        if (this.dc != null) {
            dc.gameLost();
        }
        this.isGameEnded = true;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public static boolean inSamePosition(Entity a, Entity b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    public boolean isPlayerOn(Entity e) {
        return inSamePosition(player, e);
    }

    public void tick() {
        for (Entity e : entities) {
            if (e instanceof Tickable) {
                ((Tickable) e).tick();
            }
        }
    }

    public int getPlayerPositionX() {
        return player.getX();
    }

    public int getPlayerPositionY() {
        return player.getY();
    }

    public void subscribeController(DungeonController dc) {
        this.dc = dc;
        player.subscribeController(dc);
    }

    public List<BasicGoal> getBasicGoals() {
        return basicGoals;
    }

    public void addBasicGoal(BasicGoal g) {
        basicGoals.add(g);
    }
    
    public BasicGoal getExitGoal() {
        for (BasicGoal bg: basicGoals) {
            if (bg instanceof ExitGoal) {
                return bg;
            }
        }
        return null;
    }
    
    public BasicGoal getTreasureGoal() {
        for (BasicGoal bg: basicGoals) {
            if (bg instanceof TreasureGoal) {
                return bg;
            }
        }
        return null;
    }
    
    public BasicGoal getBouldersGoal() {
        for (BasicGoal bg: basicGoals) {
            if (bg instanceof BoulderGoal) {
                return bg;
            }
        }
        return null;
    }
    
    public BasicGoal getEnemyGoal() {
        for (BasicGoal bg: basicGoals) {
            if (bg instanceof EnemyGoal) {
                return bg;
            }
        }
        return null;
    }
    
    public void addDynamicEntity(Entity e) {
        entities.add(e);
        if (dc != null) {
            dc.dynamicLoad(e);
        }
    }

}
