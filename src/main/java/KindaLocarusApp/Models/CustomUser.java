package KindaLocarusApp.Models;

import KindaLocarusApp.Interfaces.Implementation.CustomAuthorityDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = USERS_COLLECTION_NAME)
public class CustomUser implements UserDetails
{
    private @MongoId ObjectId _id;
    @Indexed(unique = true)
    private String username; //username, unique
    private String password; //password is stored hashed
    private Set<GrantedAuthority> grantedAuthorities; //user roles: USER, ADMIN
    private Set<String> ownedDevices; //list of imeies of owned devices
    private String userDescription; //user description string, accessible only to admin
    private boolean accountEnabled;

    public ObjectId getId()
    {
        return this._id;
    }

    public void setId(ObjectId id)
    {
        this._id = id;
    }

    public String getUserDescription()
    {
        return userDescription;
    }

    public void setUserDescription(String userDescription)
    {
        this.userDescription = userDescription;
    }

    public Set<String> getOwnedDevices()
    {
        return ownedDevices;
    }

    public void setOwnedDevices(Set<String> ownedDevices)
    {
        this.ownedDevices = ownedDevices;
    }

    public void setAuthorities(Set<GrantedAuthority> grantedAuthorities)
    {
        this.grantedAuthorities = grantedAuthorities;
    }

    public void setAccountEnabled(boolean isEnabled)
    {
        this.accountEnabled = isEnabled;
    }

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Set<GrantedAuthority> getAuthorities()
    {
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
    public boolean isEnabled()
    {
        return accountEnabled;
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