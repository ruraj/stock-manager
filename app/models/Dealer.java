package models;

import models.relations.DealerProduct;
import models.relations.UserDealer;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ruraj on 7/16/14.
 */
@Entity
public class Dealer extends Model {
    @Id
    public int id;

    public String name;

    public String owner;
    
    public String pan;

    public int productCount() {
        return new Finder(Integer.class, DealerProduct.class).where().eq("dealerId", this.id).findRowCount();
    }

    public int userCount() {
        return new Finder(Integer.class, UserDealer.class).where().eq("dealerId", this.id).findRowCount();
    }
}
