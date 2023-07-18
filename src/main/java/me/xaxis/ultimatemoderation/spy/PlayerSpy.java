package me.xaxis.ultimatemoderation.spy;

import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import org.bukkit.entity.Player;

public class PlayerSpy{

    private final Player player;
    private final Player target;
    private final PlayerRollbackManager rollbackManager;
    private boolean left;

    public PlayerSpy(Player player, Player target, PlayerRollbackManager rollbackManager) {
        this.player = player;
        this.target = target;
        this.rollbackManager = rollbackManager;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getTarget() {
        return target;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean hasLeft() {
        return left;
    }

    public void restore(){
        rollbackManager.restore(player);
    }
}
