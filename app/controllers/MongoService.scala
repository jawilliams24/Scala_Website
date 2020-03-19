package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.play.json._
import collection._
import models.{User, UserDetails}
import models.JsonFormats._
import play.api.libs.json.{JsValue, Json}
import reactivemongo.api.Cursor
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult

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



}