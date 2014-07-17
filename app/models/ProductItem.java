package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ruraj on 7/17/14.
 */
@Entity
public class ProductItem extends Model {
    @Id
    public int id;

    public String itemCode;

    //Hold values of all features as JSON
    @Column(length=2048)
    public String values;
}
