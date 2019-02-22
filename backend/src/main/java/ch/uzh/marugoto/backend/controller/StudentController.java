package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/student")
public class StudentController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * List all students
     * @return students
     */
    @ApiOperation(value = "List all students", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public List<User> listStudents() throws AuthenticationException {
        isSupervisorAuthenticated();
        return userService.getStudents();
    }
}
