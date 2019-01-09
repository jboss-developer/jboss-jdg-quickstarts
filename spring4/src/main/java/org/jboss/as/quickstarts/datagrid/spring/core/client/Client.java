package org.jboss.as.quickstarts.datagrid.spring.core.client;

/**
 * Main domain object.
 *
 * @author Sebastian Laskawiec
 */
public class Client {

    private int id;
    private String firstName;
    private String lastName;
    private String favoriteCoffee;
    private int numberOfOrders;

    public Client(int id, String firstName, String lastName, String favoriteCoffee, int numberOfOrders) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteCoffee = favoriteCoffee;
        this.numberOfOrders = numberOfOrders;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFavoriteCoffee() {
        return favoriteCoffee;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }
}
