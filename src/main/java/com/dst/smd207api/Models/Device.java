package com.dst.smd207api.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

import static com.dst.smd207api.Constants.Constants.*;

@Data
@NoArgsConstructor
@Document(collection = DEVICES_COLLECTION_NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device
{
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private @MongoId ObjectId _id;
    @Field(IMEI_FIELD)
    private String deviceImei;
    @Field(PCB_VERSION_FIELD)
    private String pcbRevision;
    @Field(SOFTWARE_VERSION_FIELD)
    private String softwareVersion;
    @Field(LICENSE_ACTIVE_FIELD)
    private Boolean licenseActive;
    @Field(ISSUE_DATE_FIELD)
    private Instant issueDate;
    @Field(EXPIRATION_DATE_FIELD)
    private Instant expirationDate;
    @Field(DEVICE_DESCRIPTION_FIELD)
    private String deviceDescription;

    @JsonIgnore
    public ObjectId getId()
    {
        return this._id;
    }
    public void setId(ObjectId id)
    {
        this._id = id;
    }
}
