import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {

    private final AntiCheat antiCheat;

    public EventListener(AntiCheat antiCheat) {
        this.antiCheat = antiCheat;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Trigger checks for player movement
        antiCheat.getChecks().forEach(check -> check.executeCheck(event.getPlayer()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Trigger checks for block breaking
        antiCheat.getChecks().forEach(check -> check.executeCheck(event.getPlayer()));
    }
}