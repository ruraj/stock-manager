package models.utils;

import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created by ruraj on 7/17/14.
 */
@Entity
public class UserDealer extends Model {

    public int userId;

    public int dealerId;
}
