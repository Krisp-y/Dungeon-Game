package unsw.dungeon;

import java.util.List;

public class Door extends Moveable implements Collider {
    private int ID;
    public Door(Dungeon dungeon, int x, int y, int ID) {
        super(dungeon, x, y);
        this.ID = ID;

    }

    @Override
    public void handleCollision(Moveable m) {
        if (m instanceof Player) {
            doorCollision((Player) m);
        }
    } 
    
    private void doorCollision(Player p) {
        Direction playerMoveDirection = p.getDirection();
        Collider collidingEntity;

        switch (playerMoveDirection) {
            case UP:
                collidingEntity = dungeon.getCollidingEntity(getX(), getY() - 1);
                // If there is no colliding entity above the door, move the player up
                if (collidingEntity == null) {
                    if(p.hasKey() && p.getKeyID() == this.getDoorID()) {
                        p.moveUp();
                        p.useKey();
                    }
                    
                }
                break;
            case DOWN:
                collidingEntity = dungeon.getCollidingEntity(getX(), getY() + 1);
                // If there is no colliding entity below the door, move the player down
                if (collidingEntity == null) {
                    if(p.hasKey() && p.getKeyID() == this.getDoorID()) {
                        p.moveDown();
                        p.useKey();
                    }
                    
                }
                break;
            case LEFT:
                collidingEntity = dungeon.getCollidingEntity(getX() - 1, getY());
                // If there is no colliding entity left of the boulder, move the
                // boulder left and the player left
                if (collidingEntity == null) {
                    if(p.hasKey() && p.getKeyID() == this.getDoorID()) {
                        p.moveLeft();
                        p.useKey();
                    }
                    
                }
                break;
            case RIGHT:
                // If there is no colliding entity right of  the boulder, move the
                // boulder right and the player right
                collidingEntity = dungeon.getCollidingEntity(getX() + 1, getY());
                if (collidingEntity == null) {
                    if(p.hasKey() && p.getKeyID() == this.getDoorID()) {
                        p.moveRight();
                        p.useKey();
                    }
                    
                }
                break;
            default:
                return;
        }
        

    }

    public int getDoorID() {
        return this.ID;
    }

    
}