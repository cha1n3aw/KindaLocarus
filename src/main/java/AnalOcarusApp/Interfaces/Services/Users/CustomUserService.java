package AnalOcarusApp.Interfaces.Services.Users;

import AnalOcarusApp.Models.API.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomUserService
{
    ResponseEntity<Response<?>> getUsers(final List<String> usernames);
}
