package ch.uzh.marugoto.core.helpers;

import com.github.jknack.handlebars.Handlebars;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseOption;
import ch.uzh.marugoto.core.data.entity.topic.ImageComponent;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.LinkComponent;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.service.DownloadService;

public class HandlebarHelper {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - ");

    public String formatDate(final LocalDateTime dateTime) {
        return dateFormatter.format(dateTime).toString();
    }
    public String formatDateTime(final LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime).toString();
    }

    /**
     * ImageComponent
     * @param component
     * @param resourcesPath
     * @return
     */
    public CharSequence renderImageComponent(final Component component, String resourcesPath) {
        CharSequence imageComponentHtml = null;

        if (component instanceof ImageComponent) {
            ImageComponent imageComponent = (ImageComponent) component;
            String imagePath = imageComponent.getImages().get(0).getPath();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component image-component'>");
            htmlBuilder.append("<img src='" + resourcesPath.concat("/").concat(imagePath) + "' alt='Image' />");
            if (imageComponent.getCaption() != null) {
                htmlBuilder.append("<p class='caption'>" + imageComponent.getCaption() + "</p>");
            }
            htmlBuilder.append("</div>");
            imageComponentHtml = new Handlebars.SafeString(htmlBuilder);
        }
        return imageComponentHtml;
    }

    /**
     * TextExerciseComponent
     * @param component
     * @param notebookContent
     * @return
     */
    public CharSequence renderTextExerciseComponent(final Component component, NotebookContent notebookContent) {
        CharSequence textExerciseHtml = null;

        if (component instanceof TextExercise) {
            String exerciseInput = notebookContent.getExerciseState().getInputState();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component text-exercise'>");
            htmlBuilder.append("<h4 class='underline-title'>My input</h4>");
            htmlBuilder.append("<p>" + exerciseInput + "</p>");
            htmlBuilder.append("</div>");
            textExerciseHtml = new Handlebars.SafeString(htmlBuilder);
        }

        return textExerciseHtml;
    }

    /**
     * Text Component
     * @param component
     * @return
     */
    public CharSequence renderTextComponent(final Component component) {
        CharSequence textComponentHtml = null;

        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) component;
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component text-component'>");
            htmlBuilder.append(parseMarkdownToHtml(textComponent.getMarkdownContent().replace("<br>", "<br />")));
            htmlBuilder.append("</div>");
            textComponentHtml = new Handlebars.SafeString(htmlBuilder);
        }

        return textComponentHtml;
    }

    /**
     * Mail Content
     * @param notebookContent
     * @return
     */
    public CharSequence renderMailContent(final NotebookContent notebookContent) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div class='component mail-component'>");
        htmlBuilder.append("<h4 class='underline-title'>" + notebookContent.getMail().getSubject() + "</h4>");
        htmlBuilder.append("<p class='p-l-20'>" + notebookContent.getMailReply().getBody() + "</p>");
        htmlBuilder.append("</div>");
        return new Handlebars.SafeString(htmlBuilder);
    }

    /**
     * Radio Button Exercise
     * @param component
     * @param notebookContent
     * @return
     */
    public CharSequence renderRadioButtonExercise(final Component component, NotebookContent notebookContent) {
        CharSequence radioExerciseHtml = null;
        if (component instanceof RadioButtonExercise) {
            RadioButtonExercise radioButtonExercise = ((RadioButtonExercise) component);
            radioExerciseHtml = renderList(radioButtonExercise.getOptions(), notebookContent);
        }
        return radioExerciseHtml;
    }

    /**
     * Checkbox Exercise
     * @param component
     * @param notebookContent
     * @return
     */
    public CharSequence renderCheckboxExercise(final Component component, NotebookContent notebookContent) {
        CharSequence checkboxExerciseHtml = null;
        if (component instanceof CheckboxExercise) {
            CheckboxExercise checkboxExercise = ((CheckboxExercise) component);
            checkboxExerciseHtml = renderList(checkboxExercise.getOptions(), notebookContent);
        }
        return checkboxExerciseHtml;
    }

    /**
     * TextExerciseComponent
     * @param component
     * @param notebookContent
     * @return
     */
    public CharSequence renderDateExercise(final Component component, NotebookContent notebookContent) {
        CharSequence dateExerciseHtml = null;

        if (component instanceof DateExercise) {
            String exerciseInput = notebookContent.getExerciseState().getInputState();
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component date-exercise'>");
            htmlBuilder.append("<h4 class='underline-title'>My input</h4>");
            htmlBuilder.append("<p>" + exerciseInput + "</p>");
            htmlBuilder.append("</div>");
            dateExerciseHtml = new Handlebars.SafeString(htmlBuilder);
        }

        return dateExerciseHtml;
    }

    public CharSequence renderUploadExercise(final Component component, NotebookContent notebookContent, String uploadDirectory) {
        CharSequence uploadExerciseHtml = null;
        if (component instanceof UploadExercise) {
            Path filePath = Paths.get(uploadDirectory + notebookContent.getExerciseState().getInputState());
            
            String fileName = notebookContent.getExerciseState().getInputState();
            fileName = DownloadService.removeIdFromFileName(fileName);
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component upload-exercise'>");
            htmlBuilder.append("<h4 class='underline-title'>My Upload</h4>");
            htmlBuilder.append("<div class='upload-file'>");
            htmlBuilder.append("<div class='type'>");
            htmlBuilder.append(fileName.substring(fileName.lastIndexOf(".") + 1));
            htmlBuilder.append("</div>");
            htmlBuilder.append("<div class='name'>");
            htmlBuilder.append(fileName);
            htmlBuilder.append("</div>");
            htmlBuilder.append("</div>");
            htmlBuilder.append("</div>");
            uploadExerciseHtml = new Handlebars.SafeString(htmlBuilder);
        }
        return uploadExerciseHtml;
    }

    public CharSequence renderLinkComponent(Component component, String resourcesPath) {
        CharSequence linkComponentHtml = null;

        if (component instanceof LinkComponent) {
            LinkComponent linkComponent = (LinkComponent) component;
            Path iconPath = Paths.get(resourcesPath + File.separator + linkComponent.getIcon().getThumbnailPath());
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<div class='component link-component'>");
            htmlBuilder.append("<div class='icon' style='background: url(" + iconPath + ") center no-repeat' ></div>");
            htmlBuilder.append("<div class='text'>");
            htmlBuilder.append(linkComponent.getLinkText());
            htmlBuilder.append("</div>");
            htmlBuilder.append("</div>");
            linkComponentHtml = new Handlebars.SafeString(htmlBuilder);
        }

        return linkComponentHtml;
    }

    public CharSequence renderAppendixImages(NotebookEntryState notebookEntryState, String resourcesPath) {
        StringBuilder htmlBuilder = new StringBuilder();

        for (NotebookContent notebookContent: notebookEntryState.getNotebookContent()) {
            if (notebookContent.getComponent() instanceof ImageComponent) {
                ImageComponent imageComponent = (ImageComponent) notebookContent.getComponent();
                if (imageComponent.isZoomable()) {
                    htmlBuilder.append("<h3>");
                    htmlBuilder.append(notebookEntryState.getTitle());
                    htmlBuilder.append("</h3>");
                    htmlBuilder.append("<div class='appendix-images'>");
                    for (ImageResource imageResource : imageComponent.getImages()) {
                        htmlBuilder.append("<img src='" + resourcesPath.concat("/").concat(imageResource.getPath()) + "' alt='Image' />");
                        if (imageComponent.getCaption() != null && imageComponent.getCaption().isEmpty() == false) {
                            htmlBuilder.append("<p class='caption'>");
                            htmlBuilder.append(imageComponent.getCaption());
                            htmlBuilder.append("</p>");
                        }
                    }
                    htmlBuilder.append("</div>");

                }
            }
        }

        return new Handlebars.SafeString(htmlBuilder);
    }

    /**
     * Render ul list
     * @param exerciseOptionList
     * @param notebookContent
     * @return
     */
    private CharSequence renderList(List<ExerciseOption> exerciseOptionList, NotebookContent notebookContent) {
        StringBuilder htmlBuilder = new StringBuilder();

        int index = 0;
        htmlBuilder.append("<ul class='component list-exercise'>");
        for (ExerciseOption exerciseOption: exerciseOptionList) {
            if (notebookContent.getExerciseState().getInputState().contains(Integer.toString(index))) {
                htmlBuilder.append("<li class='item correct'>");
            } else {
                htmlBuilder.append("<li class='item incorrect'>");
            }
            htmlBuilder.append(exerciseOption.getText());
            htmlBuilder.append("</li>");
            index++;
        }
        htmlBuilder.append("</ul>");
        return new Handlebars.SafeString(htmlBuilder);
    }

    private String parseMarkdownToHtml(String markdownText) {
        String htmlOutput;
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        htmlOutput =  renderer.render(document);
        return htmlOutput;
    }
}
