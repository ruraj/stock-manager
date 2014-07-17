package models;

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
}
