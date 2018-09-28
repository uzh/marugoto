package ch.uzh.marugoto.core.data.repository;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PageState;

public interface PageStateRepository extends ArangoRepository<PageState> {}
