package at.disys.model;

/**
 * This class represents a station central database.
 * <i> <br>
 * CREATE TABLE IF NOT EXISTS station (<br>
 *    id SERIAL PRIMARY KEY,<br>
 *    db_url VARCHAR(255) NOT NULL,<br>
 *    lat REAL NOT NULL,<br>
 *    lng REAL NOT NULL<br>
 * ); </i>
 */

public class Station {
    private Long id;

    private String dbUrl;

    private Double lat;

    private Double lng;

    public Station() {
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public StringBuilder toCsv() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append(",");
        stringBuilder.append(dbUrl);
        stringBuilder.append(",");
        stringBuilder.append(lat);
        stringBuilder.append(",");
        stringBuilder.append(lng);
        return stringBuilder;
    }
}
