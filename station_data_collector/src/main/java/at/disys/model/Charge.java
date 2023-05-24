package at.disys.model;


/**
 * This class represents a charge of a customer.
 * <i> <br>
 *    id SERIAL PRIMARY KEY, <br>
 *    kwh REAL NOT NULL,     <br>
 *    customer_id INTEGER NOT_NULL</i>
 */

public class Charge {

    private Long id;

    private Long kwh;

    private Long customerId;

    public Charge() {
    }

    public Long getKwh() {
        return kwh;
    }

    public void setKwh(Long kwh) {
        this.kwh = kwh;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public StringBuilder toCsv() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append(",");
        stringBuilder.append(kwh);
        stringBuilder.append(",");
        stringBuilder.append(customerId);
        return stringBuilder;
    }

}
