package com.google.apigee.onlineboutique.shipping;

public class QuoteRequestDetails {
    private Address address;
    private Item[] items;

    public void setAddress(Address address) {
        this.address=address;
    }

    public void set(Item[] items) {
        this.items=items;
    }

    public Address getAddress() {
        return address;
    }
    public Item[] getItems() {
        return items;
    }
}
