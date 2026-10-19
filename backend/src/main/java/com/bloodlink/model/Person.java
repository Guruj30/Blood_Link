package com.bloodlink.model;

import java.util.Objects;

/**
 * Base abstract class representing an individual within the BloodLink healthcare domain.
 * Demonstrates OOP Inheritance and Encapsulation with protected/private state and validation.
 */
public abstract class Person {

    private String id;
    private String name;
    private String phone;
    private String city;

    protected Person() {
    }

    protected Person(String id, String name, String phone, String city) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        this.city = city.trim();
    }

    /**
     * Abstract method enforcing polymorphic role description across domain entities.
     */
    public abstract String getRoleDescription();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
