package org.poo.main.structures;

/**
 * Represents a merchant with a name and description.
 */
public class Merchant {
    private String name;
    private String description;

    /**
     * Constructs a Merchant with the specified name and description.
     *
     * @param name the name of the merchant
     * @param description a brief description of the merchant
     */
    public Merchant(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Retrieves the name of the merchant.
     *
     * @return the merchant's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the merchant.
     *
     * @param name the new name of the merchant
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Retrieves the description of the merchant.
     *
     * @return the merchant's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the merchant.
     *
     * @param description the new description of the merchant
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the Merchant object.
     *
     * @return a string containing the name and description of the merchant
     */
    @Override
    public String toString() {
        return "Merchant{"
                + "name='" + name + '\''
                + ", description='" + description
                + '\'' + '}';
    }
}
