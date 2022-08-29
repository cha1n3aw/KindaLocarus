package KindaLocarusApp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

import static KindaLocarusApp.Constants.Constants.IMEI_FIELD;

@Data
@NoArgsConstructor
@Document(collection = "Devices")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device
{
    private @MongoId ObjectId _id;
    @Field(IMEI_FIELD)
    private String deviceImei;
    @Field("PCB")
    private String pcbRevision;
    @Field("SWW")
    private String softwareVersion;
    @Field("LAC")
    private Boolean licenseActive;
    @Field("ISD")
    private Instant issueDate;
    @Field("EXD")
    private Instant expirationDate;
    @Field("DES")
    private String deviceDescription;

    public ObjectId getId()
    {
        return this._id;
    }

    public void setId(ObjectId id)
    {
        this._id = id;
    }
}
