package at.disys.station_javafx_app.model;

import javafx.scene.control.Button;

/**
 * Represents an invoice.
 */
public class Invoice {
    private final String customerId;
    private final Button viewInvoiceButton;
    public Invoice(String customerId, Button viewInvoiceButton) {
        this.customerId = customerId;
        this.viewInvoiceButton = viewInvoiceButton;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Button getViewInvoiceButton() {
        return viewInvoiceButton;
    }
}