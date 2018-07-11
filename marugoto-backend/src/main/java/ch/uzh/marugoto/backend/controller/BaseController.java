package ch.uzh.marugoto.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base controller. Every controller implementation should inherit from this one.
 * 
 * This base class provides a logger instance, see field {@code Log}.
 * 
 * @author Rino
 */
public class BaseController {
    protected final Logger Log = LogManager.getLogger(this.getClass());

}
