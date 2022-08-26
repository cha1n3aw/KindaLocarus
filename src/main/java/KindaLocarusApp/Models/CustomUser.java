package KindaLocarusApp.Models;

import KindaLocarusApp.Interfaces.Implementation.CustomRolesDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Set<GrantedAuthority> roles; //user roles: USER, ADMIN
    private Set<String> devices; //list of imeies of owned devices
    private String desc; //user description string, accessible only to admin
    private Boolean accEnabled;
    private Boolean accNonLocked;
    private Boolean crNonExpired;
    private Boolean accNonExpired;

    public Boolean getAccountEnabled()
    {
        return accEnabled;
    }

    public Boolean getAccountNonLocked()
    {
        return accNonLocked;
    }

    public Boolean getAccountNonExpired()
    {
        return accNonExpired;
    }

    public Boolean getCredentialsNonExpired()
    {
        return crNonExpired;
    }

    public ObjectId getId()
    {
        return this._id;
    }

    public void setId(ObjectId id)
    {
        this._id = id;
    }

    public String getDescription()
    {
        return desc;
    }

    public void setDescription(String description)
    {
        this.desc = description;
    }

    public Set<String> getDevices()
    {
        return devices;
    }

    public void setDevices(Object devices)
    {
        this.devices = (Set<String>) devices;
    }

    public void setAccountEnabled(Boolean isEnabled)
    {
        this.accEnabled = isEnabled;
    }
    public void setAccountNonLocked(Boolean isAccountNonLocked)
    {
        this.accNonLocked = isAccountNonLocked;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired)
    {
        this.crNonExpired = credentialsNonExpired;
    }

    public void setAccountNonExpired(Boolean isAccountNonExpired)
    {
        this.accNonExpired = isAccountNonExpired;
    }

    @JsonIgnore
    @Override
    public Set<GrantedAuthority> getAuthorities()
    {
        return this.roles;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities)
    {
        setRoles((Object)authorities);
    }

    @JsonDeserialize(using = CustomRolesDeserializer.class)
    public Object getRoles()
    {
        return this.roles;
    }

    public void setRoles(Object roles)
    {
        this.roles = (Set<GrantedAuthority>)roles;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired()
    {
        return this.accNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked()
    {
        return this.accNonLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired()
    {
        return this.crNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled()
    {
        return this.accEnabled;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_id);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUser user = (CustomUser) o;
        return Objects.equals(username, user.username);
    }
}