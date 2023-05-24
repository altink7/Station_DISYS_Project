package at.disys.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Invoice {
    private Long invoiceNumber;
    private Long customerId;
    private Set<Station> locations;
    private LocalDate invoiceDate;
    private Long totalKwh;

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Set<Station> getLocations() {
        if (locations == null)
            locations = new HashSet<>();

        return locations;
    }

    public void setLocations(Set<Station> locations) {
        this.locations = locations;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Long getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(Long totalKwh) {
        this.totalKwh = totalKwh;
    }

    public StringBuilder toCsv() {
        StringBuilder stringBuilder = new StringBuilder();
        locations.forEach(station -> {
            stringBuilder.append(station.toCsv());
        });
        stringBuilder.append(", ");
        stringBuilder.append(invoiceNumber);
        stringBuilder.append(",");
        stringBuilder.append(customerId);
        stringBuilder.append(",");
        stringBuilder.append(invoiceDate);
        stringBuilder.append(",");
        stringBuilder.append(totalKwh);
        return stringBuilder;
    }
}

