package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;

import java.util.List;

public interface CustomUserService
{
    Response<?> usersGet(final List<String> usernames, final List<String> fields);
    Response<?> usersAdd(final List<CustomUser> customUsers);
    Response<?> usersDelete(final List<String> usernames);
    Response<?> usersEdit(final List<CustomUser> customUsers);
}
