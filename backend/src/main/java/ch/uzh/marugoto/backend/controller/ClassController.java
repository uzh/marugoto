package ch.uzh.marugoto.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.naming.AuthenticationException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/class")
public class ClassController extends BaseController {

    /**
     * List all classes
     * @return
     */
    @ApiOperation(value = "Get all classes", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("/list")
    public List<Object> listClasses() throws AuthenticationException {
        isSupervisorAuthenticated();
        return null;
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Create new class", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Object createClass() throws AuthenticationException {
        isSupervisorAuthenticated();
        return null;
    }

    /**
     * Edit selected class
     * @return class that is updated
     */
    @ApiOperation(value = "Edit class", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("/edit/{classId}")
    public Object editClass(@PathVariable String classId) throws AuthenticationException {
        isSupervisorAuthenticated();
        return null;
    }
}
