package at.disys.model;

/**
 * This class represents a station.
 * <i> <br>
 *   URLString VARCHAR(255) NOT NULL, <br>
 *   total_kwh INTEGER NOT NULL <br>
 *   </i>
 */
public class Station {
    public String URLString;
    public Long totalKwh;

    public String getURLString() {
        return URLString;
    }

    public void setURLString(String URLString) {
        this.URLString = URLString;
    }

    public Long getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(Long totalKwh) {
        this.totalKwh = totalKwh;
    }

    public StringBuilder toCsv() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URLString);
        stringBuilder.append(";");
        stringBuilder.append(totalKwh);
        stringBuilder.append("|");
        return stringBuilder;
    }
}
