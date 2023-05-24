package at.disys.model;

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
