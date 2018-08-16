package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.Component;
import ch.uzh.marugoto.backend.data.entity.TextComponent;

/**
 * @author nemtish
 *
 */
public interface ComponentRepository extends ArangoRepository<Component> {

	TextComponent save(TextComponent textComponent);
}
