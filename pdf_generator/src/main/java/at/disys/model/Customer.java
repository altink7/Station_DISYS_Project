package at.disys.model;

/**
 * This class represents a customer.
 * <i> <br>
 *    id SERIAL PRIMARY KEY, <br>
 *    first_name VARCHAR(255) NOT NULL, <br>
 *    last_name VARCHAR(255) NOT NULL</i>
 */


public class Customer {
    private Long id;

    private String firstName;

    private String lastName;

    public Customer() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
