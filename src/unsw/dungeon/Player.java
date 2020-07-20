package unsw.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * The player entity
 * @author Robert Clifton-Everest
 *
 */
public class Player extends Moveable {
    private List<Entity> inventory;
    /**
     * Create a player positioned in square (x,y)
     * @param x
     * @param y
     */
    public Player(Dungeon dungeon, int x, int y) {
        super(dungeon, x, y);
        this.inventory = new ArrayList<>();
    }   

    public void teleport(int x, int y) {
        x().set(x);
        y().set(y);
    }

    public List<Entity> getInventory() {
        return this.inventory;
    }
    
    /** Moves an item from the dungeon entity list to the player inventory. */
    public void collectItem(Entity e) {
        inventory.add(e);
        dungeon.removeEntity(e);
    }

    public void removeCollectable(Entity e) {
        inventory.remove(e);
    }

    public boolean hasKey() {
        for (Entity e: inventory) {
            if (e instanceof Key) {
                return true;
            }
        }
        return false;
    }
    
    public int getKeyID() {
        for (Entity e: inventory) {
            if (e instanceof Key) {
                Key key = (Key)e;
                return key.getKeyID();
            }
        }
        return -1;
    }

    public void useKey() {
        inventory.removeIf(e -> e instanceof Key);
    }

    public int countTreasure() {
        int count = 0;
        for(Entity e: inventory ) {
            if (e instanceof Treasure) {
                count++;
            }
        }
        return count;
    }
    
    public void addPotion(Potion potion) {
        
        // If we have just added a potion, make the player invincible and make enemies retreat
        if (!isInvincible()) {
            dungeon.setEnemiesToAttack();
        }
        inventory.add(potion);
        dungeon.removeEntity(potion);
    }   
    
    public void removePotion(Potion potion) {
        inventory.remove(potion);
        
        // If we have removed a potion and we have none left, make the enemies attack
        if (!isInvincible()) {
            dungeon.setEnemiesToRetreat();
        }
    }
    
    public boolean isInvincible() {
        for (Entity e: inventory) {
            if (e instanceof Potion) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasWeapon() {
        for (Entity e: inventory) {
            if (e instanceof Weapon) {
                return true;
            }
        }
        return false;
    }
    
    public void addWeapon(Weapon w) {
        inventory.add(w);
        dungeon.removeEntity(w);
    }
    
    public void removeWeapon() {
        inventory.removeIf(e -> e instanceof Weapon);
    }
    
    public void reduceWeaponHealth() {
        for (Entity e: inventory) {
            if (e instanceof Weapon) {
                ((Weapon) e).decrementHealth();
            }
        }
    }
    
    public int getWeaponHealth() {
        for (Entity e: inventory) {
            if (e instanceof Weapon) {
                return ((Weapon) e).getHealth();
            }
        }
        return -1;
    }
    
}
