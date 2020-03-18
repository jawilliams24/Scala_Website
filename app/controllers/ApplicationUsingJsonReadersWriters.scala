package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.play.json._
import collection._
import models.User
import models.JsonFormats._
import play.api.libs.json.{JsValue, Json}
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{
  MongoController, ReactiveMongoComponents, ReactiveMongoApi
}

class ApplicationUsingJsonReadersWriters @Inject()(
                                                    components: ControllerComponents,
                                                    val reactiveMongoApi: ReactiveMongoApi
                                                  ) extends AbstractController(components)
  with MongoController with ReactiveMongoComponents {

  implicit def ec: ExecutionContext = components.executionContext

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("persons"))

  def create: Action[AnyContent] = Action.async {
    val user = User(29, "James", "isTrash")
    val futureResult = collection.flatMap(_.insert.one(user))
    futureResult.map(_ => Ok("User inserted"))
  }

  def createFromJson: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].map { user =>
      collection.flatMap(_.insert.one(user)).map { _ => Ok("User inserted")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findByName(lastName: String): Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj("lastName" -> lastName)).
        sort(Json.obj("created" -> -1)).
        cursor[User]()
    }

    val futureUsersList: Future[List[User]] =
      cursor.flatMap(
        _.collect[List](
          -1,
          Cursor.FailOnError[List[User]]()
        )
      )

    futureUsersList.map { persons =>
      Ok(persons.toString)
    }
  }

  def findAll() = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj()).cursor[User]()

    }
    val futureUserList: Future[List[User]] =
      cursor.flatMap(
        _.collect[List](
          -1,
          Cursor.FailOnError[List[User]]()

        )
      )
    futureUserList.map { persons =>
      Ok(persons.toString)
    }
  }


  //  def updateUser(firstName: String): Action[AnyContent] = Action.async {
  //val user =
  //    val futureResult =
  //  }

  def deleteOne: Action[JsValue] = Action.async(parse.json){ request =>
    request.body.validate[User].map { user =>
      collection.flatMap(c => c.remove(user)).map{_ => Ok("removed")
      }
    }.getOrElse(Future.successful(BadRequest("invalid Json")))
  }

  def updateNoteFromJson(message:String): Action[JsValue] = Action.async(parse.json) {
    request => request.body.validate[User].map { user =>
      collection.flatMap(c => c.update.one(user, Json.obj("firstName"-> user.firstName, "message" -> message), upsert = false)).map{_ => Ok("updated")
      }
    }.getOrElse(Future.successful(BadRequest("invalid Json")))
  }

}
