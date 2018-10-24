package ch.uzh.marugoto.shell;

import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DisableCommandLog extends DefaultHistory {
    @Override
    public void save() {
    	// No-op
    }
}