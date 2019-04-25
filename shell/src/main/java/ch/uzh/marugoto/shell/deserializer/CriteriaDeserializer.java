package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageCriteriaType;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class CriteriaDeserializer extends StdDeserializer<Criteria> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CriteriaDeserializer() {
        this(null);
    }

    public CriteriaDeserializer(Class<?> vc) {
        super(vc);		
    }

    public Criteria deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Criteria criteria = new Criteria();

        if (node.has("affectedExercise")) {
            var affectedExercise = node.get("affectedExercise");
            var exerciseCriteria = node.get("exerciseCriteria");

            if (exerciseCriteria.isTextual()) {
                criteria.setExerciseCriteria(ExerciseCriteriaType.valueOf(exerciseCriteria.asText()));
            }

            if (affectedExercise.isObject()) {
                var exercise = (Exercise) BeanUtil.getBean(ComponentRepository.class).findById(affectedExercise.get("id").asText()).orElse(null);
                criteria.setAffectedExercise(exercise);
            }
        }
        
        if (node.has("affectedPage")) {
        	var affectedPage = node.get("affectedPage");
        	var pageCriteria = node.get("pageCriteria");

        	if (pageCriteria.isTextual()) {
                criteria.setPageCriteria(PageCriteriaType.valueOf(pageCriteria.asText()));
            }

        	if (affectedPage.isObject()) {
                var page = (Page)BeanUtil.getBean(PageRepository.class).findById(affectedPage.get("id").asText()).orElse(null);
                criteria.setAffectedPage(page);
            }
        }
        
        if (node.has("affectedPagesIds")) {
        	var affectedPages = node.get("affectedPagesIds");
        	var pageCriteria = node.get("pageCriteria");
        	
        	if (pageCriteria.isTextual()) {
                criteria.setPageCriteria(PageCriteriaType.valueOf(pageCriteria.asText()));
            }
        	
        	Iterator<JsonNode> itr = affectedPages.iterator();
        	List<String>pageIds = new ArrayList<String>();
        	
        	while(itr.hasNext()) {
        		var page = (Page) BeanUtil.getBean(PageRepository.class).findById(itr.next().asText()).orElse(null);
        		pageIds.add(page.getId());
        		criteria.setAffectedPagesIds(pageIds);
        	}
        }
        
        
        if (node.has("affectedMail")) {
            var affectedMail = node.get("affectedMail");
            var mailCriteria = node.get("mailCriteria");

            if (mailCriteria.isTextual()) {
                criteria.setMailCriteria(MailCriteriaType.valueOf(mailCriteria.asText()));
            }

            if (affectedMail.isObject()) {
                var mail = (Mail) BeanUtil.getBean(NotificationRepository.class).findById(affectedMail.get("id").asText()).orElse(null);
                criteria.setAffectedMail(mail);
            }
        }

        if (node.has("affectedDialogResponse")) {
            var affectedDialogResponse = node.get("affectedDialogResponse");

            if (affectedDialogResponse.isObject()) {
                var dialogResponse = (DialogResponse) BeanUtil.getBean(DialogResponseRepository.class).findById(affectedDialogResponse.get("id").asText()).orElse(null);
                criteria.setAffectedDialogResponse(dialogResponse);
            }
        }

        return criteria;
    }
}
