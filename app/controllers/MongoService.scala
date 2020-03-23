package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.play.json._
import collection._
import models.JsonFormats._
import models.{User, UserDetails}
import play.api.libs.json.{JsObject, JsValue, Json}
import reactivemongo.api.Cursor
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONObjectID
import scala.concurrent.ExecutionContext.Implicits.global

class MongoService @Inject()(
                              val reactiveMongoApi: ReactiveMongoApi
                            ) extends ReactiveMongoComponents {

  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("persons"))

  def createUser(user: User): Future[WriteResult] = {
    collection.flatMap(_.insert.one(user))
  }

  def createUserDetails(user: UserDetails): Future[WriteResult] = {
    collection.flatMap(_.insert.one(user))
  }

//  def findAll(): Future[List[User]] = {
//    collection.map {
//      _.find(Json.obj())
//        .sort(Json.obj("created" -> -1))
//        .cursor[User]()
//    }.flatMap(
//      _.collect[List](
//        -1,
//        Cursor.FailOnError[List[User]]()
//      )
//    )
//  }

//  def findAll() = Action.async {
//    val cursor: Future[Cursor[User]] = collection.map {
//      _.find(Json.obj()).cursor[User]()
//
//    }
//    val futureUserList: Future[List[User]] =
//      cursor.flatMap(
//        _.collect[List](
//          -1,
//          Cursor.FailOnError[List[User]]()
//
//        )
//      )
//    futureUserList.map { persons =>
//      Ok(persons.toString)
//    }
//  }


  def create(user: UserDetails): Future[WriteResult] = {
    collection.flatMap(_.insert.one(user))
  }

  def deleteUser(firstName: String): Future[WriteResult] = {
    collection.flatMap(_.delete.one(
      Json.obj(
        {
          "firstName" -> firstName
        }
      )))
  }

  def getPerson(filter: JsObject): Future[List[Any]] = {
    val cursor: Future[Cursor[UserDetails]] = collection.map {
      _.find(filter)
        .cursor[UserDetails]()
    }
    cursor.flatMap(
      _.collect[List](
        -1,
        Cursor.FailOnError[List[UserDetails]]()
      ))
  }

  def updateUser(id: String, user: UserDetails): Future[UpdateWriteResult] = {
    collection.flatMap(_.update(false).one(
      Json.obj(
        {
          "_id" -> BSONObjectID.parse(id).get
        }
      ), user
    ))
  }


}