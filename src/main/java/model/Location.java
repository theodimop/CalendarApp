package model;

/**
 * Location class.
 */
public class Location {
    private String locationName;
    private int capacity;

    /**
     * Creates a location with name and capacity.
     * @param locationName the name.
     * @param capacity the capacity.
     */
    public Location(String locationName, int capacity) {
        this.locationName = locationName;
        this.capacity = capacity;
    }

    /**
     * Getter.
     * @return the name.
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Setter.
     * @param locationName the name.
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Getter.
     * @return the capacity.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Setter.
     * @param capacity the capacity.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
