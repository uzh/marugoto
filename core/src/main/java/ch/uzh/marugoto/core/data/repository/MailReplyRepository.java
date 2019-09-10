package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import ch.uzh.marugoto.core.data.entity.state.MailReply;

public interface MailReplyRepository extends ArangoRepository<MailReply> {
}
