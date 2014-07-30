package models

import models.utils.Hash
import org.joda.time.DateTime
import sorm.Persisted


case class User(var email: String, fullname: String, confirmationToken: String, passwordHash: String, dateCreation: DateTime, validated: Boolean) {

  def email_(value:String) = {email = value}

  def getId = Db.query[User].whereEqual("email", this.email).fetchOne().get.id

  def changePassword(password: String) {
    val nUser : User = Db.query[User]
      .whereEqual("email", this.email)
      .fetchOne()
      .map(a => a.copy(passwordHash=Hash.createPassword(password))).get

    Db.save(nUser)
  }

  def save() = Db.save(this)

  def getTypeTag = reflect.runtime.universe.typeOf[User]
}

object User {

  /**
   * Retrieve a user from an email.
   *
   * @param email email to search
   * @return a user
   */
  def findByEmail(email: String): User with Persisted = {
    Db.query[User].whereEqual("email", email).fetchOne().orNull
  }

  /**
   * Retrieve a user from a fullname.
   *
   * @param fullname Full name
   * @return a user
   */
  def findByFullname(fullname: String): User with Persisted = {
    Db.query[User].whereEqual("fullname", fullname).fetchOne().orNull
  }

  /**
   * Retrieves a user from a confirmation token.
   *
   * @param token the confirmation token to use.
   * @return a user if the confirmation token is found, null otherwise.
   */
  def findByConfirmationToken(token: String): User with Persisted = {
    Db.query[User].whereEqual("confirmationToken", token).fetchOne().orNull
  }

  /**
   * Authenticate a User, from a email and clear password.
   *
   * @param email         email
   * @param clearPassword clear password
   * @return User if authenticated, null otherwise
   * @throws AppException App Exception
   */
  def authenticate(email: String, clearPassword: String): User = {
    val user: User = Db.query[User].whereEqual("email", email).fetchOne().orNull
    if (user != null) {
      if (Hash.checkPassword(clearPassword, user.passwordHash)) {
        user
      }
    }
    null
  }

  /**
   * Confirms an account.
   *
   * @return true if confirmed, false otherwise.
   * @throws AppException App Exception
   */
  def confirm(user: User): Boolean = {
    if (user == null) {
      false
    }
    val nUser : User = Db.query[User]
      .whereEqual("email", user.email)
      .fetchOne()
      .map(a => a.copy(validated=true)).get

    Db.save(nUser)

    true
  }
}

