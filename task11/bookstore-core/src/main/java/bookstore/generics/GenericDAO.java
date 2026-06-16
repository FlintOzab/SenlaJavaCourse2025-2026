package bookstore.generics;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface.
 * Defines standard CRUD operations for all entities.
 * 
 * @param <T> the entity type
 * @param <ID> the ID type
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Finds an entity by its ID.
     * 
     * @param id the entity ID
     * @return an Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Finds all entities.
     * 
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Saves an entity.
     * 
     * @param entity the entity to save
     * @return the saved entity with generated ID
     */
    T save(T entity);
    
    /**
     * Updates an existing entity.
     * 
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);
    
    /**
     * Deletes an entity by its ID.
     * 
     * @param id the ID of the entity to delete
     */
    void delete(ID id);
}