package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ruraj on 7/16/14.
 */
@Entity
public class ProductFeature extends Model {
    @Id
    public int id;

    @Constraints.Required
    @Formats.NonEmpty
    public String name;

    @Constraints.Required
    @Formats.NonEmpty
    public String description;

    @Constraints.Required
    @Formats.NonEmpty
    public String type = "text";

    @Column(length=1024)
    public String values;

    //Whether of not this feature value must be one of the list
    public Boolean selectFromList = false;

}
