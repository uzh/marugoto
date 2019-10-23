package ch.uzh.marugoto.backend.controller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.helpers.HandlebarHelper;
import ch.uzh.marugoto.core.service.NotebookService;

/**
 * Controller only used for development purposes, e.g. testing exception responses, JSON formatting etc.
 */
@RestController
public class DevController extends BaseController {

	@Value("${marugoto.resource.static.dir}")
	protected String resourceStaticDirectory;
	@Autowired
	private NotebookEntryStateRepository notebookEntryStateRepository;


	@GetMapping("dev/throwException")
	public void throwException() throws Exception {
		throw new Exception("Exception message.");
	}

	@GetMapping("dev/throwWithInnerException")
	public void throwWithInnerException() throws Exception {
		throw new Exception("Exception message.", new IllegalStateException("Inner exception message."));
	}

	@GetMapping("dev/date")
	public Map<String, Object> returnDate() throws ParseException {
		var result = new HashMap<String, Object>();
		result.put("date", new SimpleDateFormat("yyyy/MM/dd").parse("2000/01/01").toString());
	    return result;
	}

	@GetMapping("dev/notebook-pdf-template")
	public String notebookTemplate(HttpServletRequest request) throws IOException {
		Iterable<NotebookEntryState> notebookEntries = notebookEntryStateRepository.findAll();
		Handlebars handlebars = new Handlebars();
		handlebars.registerHelpers(new HandlebarHelper());

		// load the template (.hbs file) from classpath or an external file
		Template template = handlebars.compile("templates/notebook-pdf");

		// run Handlebars render with the input data
		HashMap<String, Object> data = new HashMap<>();
		data.put("gameState", notebookEntries.iterator().next().getGameState());
		data.put("notebookEntries", notebookEntries);
		data.put("resourceStaticDirectory", request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()));
		String mergedTemplate = template.apply(data);

		return mergedTemplate;
	}
}
