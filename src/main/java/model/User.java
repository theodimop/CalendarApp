package model;

/**
 * User class.
 */
public class User {
    private String name;
    private String email;

    /**
     * Constructor for a name and email.
     * @param name the name.
     * @param email the email.
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Getter.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter.
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter.
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter.
     * @param email the email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
