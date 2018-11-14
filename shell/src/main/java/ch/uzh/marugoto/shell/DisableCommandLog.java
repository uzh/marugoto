package ch.uzh.marugoto.shell;

import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.stereotype.Component;

@Component
public class DisableCommandLog extends DefaultHistory {
    @Override
    public void save() {
    	// No-op
    }
}