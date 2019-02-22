package ch.uzh.marugoto.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.application.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/student")
public class StudentController extends BaseController {

    /**
     * List all students
     * @return
     */
    @ApiOperation(value = "List all students", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public List<User> listStudents() throws AuthenticationException {
        isSupervisorAuthenticated();
        // TODO implement functionality and change response object
        return null;
    }
}
