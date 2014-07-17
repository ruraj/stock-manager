package models.relations;

import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created by ruraj on 7/17/14.
 */
@Entity
public class DealerProduct extends Model {

    public int dealerId;

    public int productId;

}
