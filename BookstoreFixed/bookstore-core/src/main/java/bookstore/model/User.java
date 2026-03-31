package bookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a user in the bookstore system.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    
    /**
     * Default constructor.
     */
    public User() {
        this.enabled = true;
        this.role = UserRole.USER;
    }
    
    /**
     * Constructs a new user with username and password.
     * 
     * @param usernameValue the username
     * @param passwordValue the password
     */
    public User(final String usernameValue, final String passwordValue) {
        this.username = usernameValue;
        this.password = passwordValue;
        this.enabled = true;
        this.role = UserRole.USER;
    }
    
    /**
     * Constructs a new user with all fields.
     * 
     * @param idValue the user ID
     * @param usernameValue the username
     * @param passwordValue the password
     * @param roleValue the user role
     * @param emailValue the email
     * @param enabledValue whether the user is enabled
     */
    public User(final Integer idValue, final String usernameValue,
                final String passwordValue, final UserRole roleValue,
                final String emailValue, final boolean enabledValue) {
        this.id = idValue;
        this.username = usernameValue;
        this.password = passwordValue;
        this.role = roleValue;
        this.email = emailValue;
        this.enabled = enabledValue;
    }
    
    /**
     * Lifecycle callback for pre-persist.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    /**
     * Lifecycle callback for pre-update.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param idValue the new ID
     */
    public void setId(final Integer idValue) {
        this.id = idValue;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param usernameValue the new username
     */
    public void setUsername(final String usernameValue) {
        this.username = usernameValue;
    }
    
    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param passwordValue the new password
     */
    public void setPassword(final String passwordValue) {
        this.password = passwordValue;
    }
    
    /**
     * Gets the user role.
     * 
     * @return the role
     */
    public UserRole getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param roleValue the new role
     */
    public void setRole(final UserRole roleValue) {
        this.role = roleValue;
    }
    
    /**
     * Gets the email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email.
     * 
     * @param emailValue the new email
     */
    public void setEmail(final String emailValue) {
        this.email = emailValue;
    }
    
    /**
     * Checks if the user is enabled.
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets the enabled status.
     * 
     * @param enabledValue the new enabled status
     */
    public void setEnabled(final boolean enabledValue) {
        this.enabled = enabledValue;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAtValue the new creation timestamp
     */
    public void setCreatedAt(final Date createdAtValue) {
        this.createdAt = createdAtValue;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last update timestamp.
     * 
     * @param updatedAtValue the new last update timestamp
     */
    public void setUpdatedAt(final Date updatedAtValue) {
        this.updatedAt = updatedAtValue;
    }
    
    @Override
    public String toString() {
        return String.format(
            "User{id=%d, username='%s', role=%s, enabled=%s}",
            id, username, role, enabled);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}