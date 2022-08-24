package AnalOcarusApp.Models.Users;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Document(collection = "Users")
public class CustomUser implements UserDetails
{
    public CustomUser()
    {

    }
    private @MongoId String _id;
    @Indexed(unique = true)
    private String username; //username, unique
    private String password; //password is stored hashed
    private List<GrantedAuthority> grantedAuthorities; //user roles: USER, ADMIN

    public String getId()
    {
        return this._id;
    }

    public void  setId(String id)
    {
        this._id = id;
    }
//
//    private Set<String> ownedDevices; //list of imeies of owned devices
//
//    private String userDescription; //user description string, accessible only to admin
//
//    public String getUserDescription()
//    {
//        return userDescription;
//    }
//
//    public void setUserDescription(String userDescription)
//    {
//        this.userDescription = userDescription;
//    }
//
//    public Set<String> getOwnedDevices()
//    {
//        return ownedDevices;
//    }
//
//    public void setOwnedDevices(Set<String> ownedDevices)
//    {
//        this.ownedDevices = ownedDevices;
//    }

    public void setAuthorities(List<GrantedAuthority> grantedAuthorities)
    {
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomUser user = (CustomUser) o;
        return Objects.equals(username, user.username);
    }
}