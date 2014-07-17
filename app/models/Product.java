package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * Created by ruraj on 7/15/14.
 */
@Entity
public class Product extends Model {
    @Id
    public int id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique=true)
    public String name;

    @Constraints.Required
    @Formats.NonEmpty
    public String description;

    public String productCode;

}
