package xyz.lincolnsmp.main.listeners;

import com.zaxxer.hikari.pool.ProxyConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.lincolnsmp.main.MainPlugin;
import xyz.lincolnsmp.main.api.External;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    private final MainPlugin plugin;

    public PlayerJoinListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String username = event.getPlayer().getName();
        try {
            External database = plugin.external;
            if (database != null) {
                if (database.isConnected()) {
                    ProxyConnection conn = (ProxyConnection) database.getHikari().getConnection();
                    PreparedStatement pstmt = conn.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE username = ?, last_seen = CURRENT_TIMESTAMP");
                    pstmt.setString(1, uuid);
                    pstmt.setString(2, username);
                    pstmt.setString(3, username);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
