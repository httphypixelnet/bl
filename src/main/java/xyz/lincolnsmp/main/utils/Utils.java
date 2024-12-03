package xyz.lincolnsmp.main.utils;
import com.zaxxer.hikari.pool.ProxyConnection;
import xyz.lincolnsmp.main.api.API;
import xyz.lincolnsmp.main.api.External;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static xyz.lincolnsmp.main.utils.Constants.WORLDS;

public class Utils {
    public static final class DecodedNumbers {
        public int[] coords;
        public API.PlayerInfo player;
        public String world;
        public DecodedNumbers(int[] numbers, API.PlayerInfo p, String w) {
            coords = numbers;
            player = p;
            world = w;
        }
    }
    public static final class UsernameCompressor {

        public static String compressUsername(String username, External external) {
            try {
                Connection conn = external.getHikari().getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                rs.next();
                return String.valueOf(rs.getInt("id"));
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return String.valueOf(99);
        }

        public static API.PlayerInfo decompressUsername(String compressed, External external) {
            try {
                ProxyConnection conn = (ProxyConnection) external.getHikari().getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE id = ?");
                ps.setInt(1, Integer.parseInt(compressed));
                ResultSet rs = ps.executeQuery();
                rs.next();
                API.PlayerInfo pi = new API.PlayerInfo(rs.getString("username"), rs.getString("uuid"));
                conn.close();
                ps.close();
                return pi;
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return new API.PlayerInfo("hypixel", "f7c77d99-9f15-4a66-a87d-c4a51ef30d19");
        }
    }
    private static final String NUMBERS = "0123456789,";
    private static final String SUBSTITUTION = "ABCDEFGHIJKL";
    public static String encodeNumbers(int num1, int num2, int num3, UUID playername, String worldname, External external) {
        // Step 1: Convert to string
        String data = String.format("%d,%d,%d", num1, num2, num3);

        // Step 2: Simple substitution cipher
        StringBuilder encoded = new StringBuilder();
        for (char c : data.toCharArray()) {
            int index = NUMBERS.indexOf(c);
            encoded.append(index >= 0 ? SUBSTITUTION.charAt(index) : c);
        }
        encoded.append(";").append(UsernameCompressor.compressUsername(String.valueOf(playername), external));
        encoded.append(";").append(WORLDS.indexOf(worldname));
        // Step 3: Base64 encode
        String base64 = Base64.getEncoder().encodeToString(encoded.toString().getBytes());

        // Step 4: Make URL-safe
        return base64.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
    }
    public static DecodedNumbers decodeNumbers(String encoded, External external) {
        // Step 1: Reverse URL-safe encoding
        String base64 = encoded.replace('-', '+').replace('_', '/');
        while (base64.length() % 4 != 0) {
            base64 += '=';
        }

        // Step 2: Base64 decode
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        String[] decoded = new String(decodedBytes).split(";");
        // Step 3: Reverse substitution cipher
        StringBuilder original = new StringBuilder();
        for (char c : decoded[0].toCharArray()) {
            int index = SUBSTITUTION.indexOf(c);
            original.append(index >= 0 ? NUMBERS.charAt(index) : c);
        }
        API.PlayerInfo playerInfo = UsernameCompressor.decompressUsername(decoded[1], external);
        System.out.println(Arrays.toString(decoded));
        String worldname = WORLDS.get(parseInt(decoded[2]));
        // Step 4: Split into numbers and parse
        String[] numberStrings = original.toString().split(",");
        int[] numbers = new int[3];
        for (int i = 0; i < 3; i++) {
            numbers[i] = parseInt(numberStrings[i]);
        }

        return new DecodedNumbers(numbers, playerInfo, worldname);
    }

}
