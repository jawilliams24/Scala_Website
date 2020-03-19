package models

import play.api.data.Form
import play.api.data.Forms._

case class UserDetails(firstName: String, lastName: String, username: String, password: String, confirmedPassword: String)

object UserDetails {

  val personList: Form[UserDetails] = Form(
    mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "username" -> nonEmptyText,
    "password" -> nonEmptyText,
    "confirmedPassword" -> nonEmptyText
  )(UserDetails.apply)(UserDetails.unapply)
  )

  def checkIfPassIsValid(userDetails: UserDetails): Boolean = userDetails.password == userDetails.confirmedPassword

  def checkIfUserExists(userDetails: UserDetails): Boolean = {
    var userDoesNotExist = true
    for (i <- 0 until LoginDetails.userList.length) {
      if(LoginDetails.userList(i).username == userDetails.username) {
        userDoesNotExist = false
      }
    }
    userDoesNotExist
  }

//  def getUsername(username: String, password: String): Option[UserDetails] = personList.find(user => user.username == username)
}