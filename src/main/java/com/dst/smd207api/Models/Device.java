package com.dst.smd207api.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

import static com.dst.smd207api.Constants.Constants.DEVICES_COLLECTION_NAME;
import static com.dst.smd207api.Constants.Constants.IMEI_FIELD;

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
    @Field("PCB")
    private String pcbRevision;
    @Field("SWV")
    private String softwareVersion;
    @Field("LAC")
    private Boolean licenseActive;
    @Field("ISD")
    private Instant issueDate;
    @Field("EXD")
    private Instant expirationDate;
    @Field("DES")
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
