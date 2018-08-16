package ch.uzh.marugoto.backend.data.repository;

import ch.uzh.marugoto.backend.data.entity.TextComponent;

/**
 * @author nemtish
 *
 */
public interface TextComponentRepository extends ComponentRepository {

	public Iterable<TextComponent> findByPage(String pageId);
}
