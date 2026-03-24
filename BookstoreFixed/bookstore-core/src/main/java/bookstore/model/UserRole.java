package bookstore.model;

/**
 * Enumeration of possible user roles.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public enum UserRole {
    
    /** Administrator with full access. */
    ADMIN,
    
    /** Regular user with limited access. */
    USER;
    
    /**
     * Returns a string representation of the enum.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        return this.name();
    }
}