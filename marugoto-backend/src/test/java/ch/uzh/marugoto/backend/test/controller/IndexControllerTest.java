package ch.uzh.marugoto.backend.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.marugoto.backend.controller.IndexController;
import ch.uzh.marugoto.backend.test.BaseTest;

//@WebMvcTest(IndexController.class)
//public class IndexControllerTest extends BaseTest {
//    private static final Logger logger = LogManager.getLogger(IndexControllerTest.class);

//    @Autowired
//    private MockMvc mvc;
//    
//    
//	@Test
//	public void indexTest() throws Exception {
//		var res = mvc.perform(get("/"))
//        	.andExpect(status().isOk())
//			.andReturn();
//		
//		logger.info(res.getResponse().getContentAsString());
//	}
//}
