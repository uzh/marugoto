package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.Page;

@Service
public class ExerciseService extends ComponentService {

    /**
     * Returns all the components that belong to page
     *
     * @param page
     * @return components
     */
    public List<Exercise> getExercises(Page page) {
        return getPageComponents(page)
                .stream()
                .filter(component -> component instanceof Exercise)
                .map(component -> (Exercise) component)
                .collect(Collectors.toList());
    }
    
    /**
     * Check whether page has exercise component or not
     *
     * @param page Page that has to be checked
     * @return boolean
     */
    public boolean hasExercise(Page page) {
        List<Component> components = getPageComponents(page);
        return components.stream()
                .anyMatch(component -> component instanceof Exercise);
    }
}
