module at.disys.station_javafx_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires itextpdf;

    opens at.disys.station_javafx_app to javafx.fxml;
    opens at.disys.station_javafx_app.model to javafx.base;
    exports at.disys.station_javafx_app.service to javafx.base;
    exports at.disys.station_javafx_app;
}