package models.relations

import play.db.ebean.Model
import javax.persistence.Entity

import sorm.Persisted

/**
 * Created by ruraj on 7/17/14.
 */
case class UserDealer(dealerId: Int = 0, users: Set[models.User])