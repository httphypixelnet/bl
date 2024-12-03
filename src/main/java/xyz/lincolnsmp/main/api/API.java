package xyz.lincolnsmp.main.api;

import com.zaxxer.hikari.pool.ProxyConnection;
import io.javalin.Javalin;
import xyz.lincolnsmp.main.MainPlugin;
import xyz.lincolnsmp.main.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static xyz.lincolnsmp.main.utils.Utils.decodeNumbers;

public class API {
    private final MainPlugin plugin;
    private final External external;
    Javalin app = Javalin.create();
    public API(MainPlugin plugin, External external) {
        this.plugin = plugin;
        this.external = external;
    }
    public void stopRestServer() {
        app.stop();
    }
    public void startRestServer() {
        app.start(plugin.getConfig().getInt("api-port"));
        app.beforeMatched(ctx -> ctx.header("Access-Control-Allow-Origin", "*"));
        app.get("/api/logs/{coords}", ctx -> {
            String encodedCoords = ctx.pathParam("coords");
            Utils.DecodedNumbers stuff = decodeNumbers(encodedCoords, external);
            int[] coords = stuff.coords;
            List<LogEntry> logs = getLogsByCoordinates(coords[0], coords[1], coords[2]);
            s r = new s(logs, stuff.player);
            ctx.json(r);
        });
        app.get("/api/testing", ctx -> {
            Utils.DecodedNumbers stuff = decodeNumbers("SktBS0o7MTQ7NQ", external);
            int[] coords = stuff.coords;
            List<LogEntry> logs = getLogsByCoordinates(coords[0], coords[1], coords[2]);
            s r = new s(logs, stuff.player);
            ctx.json(r);
        });
    }
    public static class s {
        List<LogEntry> logs;
        PlayerInfo pi;
        public s(List<LogEntry> logs, PlayerInfo pi) {
            this.logs = logs;
            this.pi = pi;
        }

        public List<LogEntry> getLogs() {
            return logs;
        }

        public PlayerInfo getPi() {
            return pi;
        }
    }
    public static class PlayerInfo {
        String name;
        String uuid;
        public PlayerInfo(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }
        public String getName() {
            return name;
        }
        public String getUuid() {
            return uuid;
        }
    }
    public List<LogEntry> getLogsByCoordinates(int x, int y, int z) {
        List<LogEntry> logs = new ArrayList<>();
        String query = "SELECT 'block_break' AS type, server_name, date, world, player_name, block, x, y, z, is_staff " +
                "FROM block_break WHERE x = ? AND y = ? AND z = ? " +
                "UNION ALL " +
                "SELECT 'block_place' AS type, server_name, date, world, player_name, block, x, y, z, is_staff " +
                "FROM block_place WHERE x = ? AND y = ? AND z = ? " +
                "ORDER BY date DESC LIMIT 100";

        try (Connection conn = (ProxyConnection) external.getHikari().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set parameters for both subqueries
            for (int i = 0; i < 2; i++) {
                pstmt.setInt(i * 3 + 1, x);
                pstmt.setInt(i * 3 + 2, y);
                pstmt.setInt(i * 3 + 3, z);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new LogEntry(
                            rs.getString("type"),
                            rs.getString("server_name"),
                            rs.getTimestamp("date").getTime(),
                            rs.getString("world"),
                            rs.getString("player_name"),
                            rs.getString("block"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z"),
                            rs.getBoolean("is_staff")
                    ));
                }
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider proper error handling here
        }

        return logs;
    }

    // Updated inner class to represent a log entry
    public static class LogEntry {
        private final String type;
        private final String serverName;
        private final long date;
        private final String world;
        private final String playerName;
        private final String block;
        private final int x;
        private final int y;
        private final int z;
        private final boolean isStaff;

        public LogEntry(String type, String serverName, long date, String world, String playerName,
                        String block, int x, int y, int z, boolean isStaff) {
            this.type = type;
            this.serverName = serverName;
            this.date = date;
            this.world = world;
            this.playerName = playerName;
            this.block = block;
            this.x = x;
            this.y = y;
            this.z = z;
            this.isStaff = isStaff;
        }

        // Getters
        public String getType() { return type; }
        public String getServerName() { return serverName; }
        public long getDate() { return date; }
        public String getWorld() { return world; }
        public String getPlayerName() { return playerName; }
        public String getBlock() { return block; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public boolean isStaff() { return isStaff; }
    }
}