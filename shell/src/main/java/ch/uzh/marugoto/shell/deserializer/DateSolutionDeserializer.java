package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.topic.DateSolution;

public class DateSolutionDeserializer extends StdDeserializer<DateSolution> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateSolutionDeserializer() {
        this(null);
    }

    public DateSolutionDeserializer(Class<?> vc) {
        super(vc);		
    }

	@Override
	public DateSolution deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		DateSolution dateSolution = new DateSolution();
		
		if (node.has("correctDate")) {
			var correctDate = node.get("correctDate");
			
			if (correctDate.isTextual()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
				dateSolution.setCorrectDate(LocalDate.parse(correctDate.asText(), formatter));
			}
		}
		return dateSolution;
	}
}
