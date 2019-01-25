package ch.uzh.marugoto.core.service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.RepliedMail;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.RepliedMailNotFoundException;

public interface ReplyMailService {
    RepliedMail saveRepliedMail(RepliedMail replyMail);
    RepliedMail sendReplyMail(User user, Mail mail, String body);
    List<RepliedMail> getAllRepliedMails(User user);
    RepliedMail getRepliedMail(User user, String mailId) throws RepliedMailNotFoundException;
}
