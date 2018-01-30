package com.ansj.summary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class GetGPS {

    static Logger LOG = LoggerFactory.getLogger(GetGPS.class);

    private static HashMap<String, MapRecord> map;

    static {
        map = new HashMap<>();
        try {
            Connection conn = Jdbc.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from demo.map";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String province = rs.getString("province");
                String city = rs.getString("city");
                String district = rs.getString("district");
                String lon = rs.getString("longitude");
                String lat = rs.getString("latitude");
                map.put(district, new MapRecord(province, city, lon, lat));
            }
        } catch (Exception e) {
            LOG.error("Cannot connect to MySQL database");
        }
    }

    public static void main(String [] args) {
        GetGPS gps = new GetGPS();
        System.out.println(gps.getFullLoc("同仁县")[0]);
    }

    public static String[] getFullLoc(String loc) {
        String[] fullLoc = new String[2];
        if (map.containsKey(loc)) {
            MapRecord r = map.get(loc);
            fullLoc[0] = r.province + r.city + loc;
            fullLoc[1] = r.latitude + "`" + r.longitude;
        }  else  {
            fullLoc[0] = "";
            fullLoc[1] = "";
        }
        return fullLoc;
    }

    /**
     * 如果不存在，返回NULL。
     */
    public static MapRecord getFullLocRecord(String loc) {
        return map.get(loc);
    }

    public static String[] getFullLocLegacy(String loc) {
        String[] fullLoc = new String[2];
        try {
            Connection conn = Jdbc.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from demo.map where district=\"" + loc + "\" limit 1";
            ResultSet rs = stmt.executeQuery(sql);
            // ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.first()) {
                String province = rs.getString("province");
                String city = rs.getString("city");
                fullLoc[0] = province + city + loc;
                String lon = rs.getString("longitude");
                String lat = rs.getString("latitude");
                fullLoc[1] = lat + "`" + lon;
            } else {
                fullLoc[0] = "";
                fullLoc[1] = "";
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullLoc;
    }

    public static class MapRecord {
        public String province;
        public String city;
        public String longitude;
        public String latitude;
        MapRecord(String  province, String city, String longitude, String latitude) {
            this.province = province;
            this.city = city;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
}