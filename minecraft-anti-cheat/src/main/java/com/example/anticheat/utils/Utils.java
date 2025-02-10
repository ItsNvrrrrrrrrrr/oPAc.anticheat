public class Utils {
    public static void log(String message) {
        System.out.println("[AntiCheat] " + message);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static boolean isPlayerInGame(Player player) {
        return player != null && player.isOnline();
    }

    public static void savePlayerData(Player player) {
        // Implementation for saving player data
    }
}