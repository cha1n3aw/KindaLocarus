package KindaLocarusApp.Models;

import KindaLocarusApp.Interfaces.Implementation.CustomRolesDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERNAME_FIELD;
import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = USERS_COLLECTION_NAME)
public class CustomUser implements UserDetails
{
    private @MongoId ObjectId _id;
    @Field(USERNAME_FIELD)
    private String username; //username, unique
    @Field("PWD")
    private String password; //password is stored hashed
    @Field("RLS")
    private HashSet<GrantedAuthority> roles; //user roles: USER, ADMIN
    @JsonDeserialize(as = HashSet.class)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Field("DVC")
    private HashSet<String> devices; //list of imeies of owned devices
    @Field("DSC")
    private String description; //user description string, accessible only to admin
    @Field("AEN")
    private Boolean accountEnabled;
    @Field("ANL")
    private Boolean accountNonLocked;
    @Field("CNE")
    private Boolean credentialsNonExpired;
    @Field("ANE")
    private Boolean accountNonExpired;

    public Boolean getAccountEnabled()
    {
        return accountEnabled;
    }

    public Boolean getAccountNonLocked()
    {
        return accountNonLocked;
    }

    public Boolean getAccountNonExpired()
    {
        return accountNonExpired;
    }

    public Boolean getCredentialsNonExpired()
    {
        return credentialsNonExpired;
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
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public HashSet<String> getDevices()
    {
        return devices;
    }

    public void setDevices(Object devices)
    {
        this.devices = (HashSet<String>) devices;
    }

    public void setAccountEnabled(Boolean isEnabled)
    {
        this.accountEnabled = isEnabled;
    }
    public void setAccountNonLocked(Boolean isAccountNonLocked)
    {
        this.accountNonLocked = isAccountNonLocked;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired)
    {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setAccountNonExpired(Boolean isAccountNonExpired)
    {
        this.accountNonExpired = isAccountNonExpired;
    }

    @JsonIgnore
    @Override
    public HashSet<GrantedAuthority> getAuthorities()
    {
        return this.roles;
    }

    public void setAuthorities(HashSet<GrantedAuthority> authorities)
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
        this.roles = (HashSet<GrantedAuthority>)roles;
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
        return this.accountNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked()
    {
        return this.accountNonLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired()
    {
        return this.credentialsNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled()
    {
        return this.accountEnabled;
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