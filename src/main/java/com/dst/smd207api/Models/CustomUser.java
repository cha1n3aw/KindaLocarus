package com.dst.smd207api.Models;

import com.dst.smd207api.Interfaces.Implementation.CustomRolesDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Objects;

import static com.dst.smd207api.Constants.Constants.USERNAME_FIELD;
import static com.dst.smd207api.Constants.Constants.USERS_COLLECTION_NAME;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = USERS_COLLECTION_NAME)
public class CustomUser implements UserDetails
{
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private @MongoId ObjectId _id;

    @Getter(AccessLevel.NONE)
    @Field(USERNAME_FIELD)
    private String username; //username, unique

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Field("PWD")
    private String password; //password is stored hashed

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Field("RLS")
    private HashSet<GrantedAuthority> roles; //user roles: USER, ADMIN

    @JsonDeserialize(as = HashSet.class)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Setter(AccessLevel.NONE)
    @Field("DVC")
    private HashSet<String> devices; //list of imeis of owned devices

    @Field("DSC")
    private String description; //user description string, accessible only to admin

    @Getter(AccessLevel.NONE)
    @Field("AEN")
    private Boolean accountEnabled;

    @Getter(AccessLevel.NONE)
    @Field("ANL")
    private Boolean accountNonLocked;

    @Getter(AccessLevel.NONE)
    @Field("CNE")
    private Boolean credentialsNonExpired;

    @Getter(AccessLevel.NONE)
    @Field("ANE")
    private Boolean accountNonExpired;

    @JsonIgnore
    public ObjectId getId()
    {
        return this._id;
    }

    public void setId(ObjectId id)
    {
        this._id = id;
    }

    public void setDevices(Object devices)
    {
        this.devices = (HashSet<String>) devices;
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