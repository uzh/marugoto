package ch.uzh.marugoto.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Base API controller. Every controller implementation should inherit from this
 * one.
 * 
 * This base class provides a logger instance, see field {@code Log}.
 * 
 * Default URL route prefix is api/
 */
@RequestMapping("api")
public abstract class BaseController {
	protected final Logger Log = LogManager.getLogger(this.getClass());

}
