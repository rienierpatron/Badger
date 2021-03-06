package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._

import play.api.libs.json._
import play.api.libs.json.Json

object User extends UserGen {

  def authenticate(handle: String, password: String): Option[User] = DB.withConnection { implicit c =>
    SQL("select * from users where user_handle ilike {handle} and user_password = crypt({password}, user_password)")
    .on('handle -> handle, 'password -> password)
    .singleOpt(simple)
  }

  lazy val ANON = User(Id(-1))

  override def insert(o: User): Option[User] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into users (
            user_id,
            user_handle,
            user_email,
            user_password
          ) VALUES (
            DEFAULT,
            {handle},
            {email},
            crypt({password}, gen_salt('bf'))
          )
        """).on(
          'id -> o.id,
          'handle -> o.handle,
          'email -> o.email,
          'password -> o.password
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into users (
            user_id,
            user_handle,
            user_email,
            user_password
          ) VALUES (
            {id},
            {handle},
            {email},
            crypt({password}, gen_salt('bf'))
          )
        """).on(
          'id -> o.id,
          'handle -> o.handle,
          'email -> o.email,
          'password -> o.password
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  override def update(o: User): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update users set
        user_handle={handle},
        user_email={email},
      where user_id={id}
    """).on(
      'id -> o.id,
      'handle -> o.handle,
      'email -> o.email
    ).executeUpdate() > 0
  }

}

// GENERATED case class start
case class User(
  id: Pk[Int] = NA,
  handle: String = "",
  email: String = "",
  password: String = ""
) extends UserCCGen with Entity[User]
// GENERATED case class end
{

  def ratingFor(leaf: Leaf) = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM ratings
      WHERE user_id = {id}
      AND leaf_id = {leafId}
    """).on(
      'id -> id.get,
      'leafId -> leaf.id.get
    ).singleOpt(Rating.simple).map(_.stars).getOrElse(0)
  }

  def clickFor(leaf: Leaf) = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM clicks
      WHERE user_id = {id}
      AND leaf_id = {leafId}
    """).on(
      'id -> id.get,
      'leafId -> leaf.id.get
    ).singleOpt(Click.simple).map(c => Json.obj("lat" -> c.lat, "lng" -> c.lng))
    .getOrElse(JsNull)
  }

  def isAnonymous = (this.id.get == -1)

  def rate(leaf: Leaf, stars: Int): Boolean = {
    (Rating.findByUserAndLeaf(this, leaf) match {
      case Some(rating) => {
        rating.copy(stars = stars).save().map { r => 
          r.leaf.changeRating(rating.stars, stars)
        }
      }
      case None => {
        Rating(NA, id, leaf.id, stars).create().map { r =>
          r.leaf.addRating(stars)
        }
      }
    }).isDefined
  }

  def click(leaf: Leaf, lat: Double, lng: Double): Boolean = {
    (Click.findByUserAndLeaf(this, leaf) match {
      case Some(click) => click.copy(lat = lat, lng = lng).save()
      case None => Click(NA, id, leaf.id, lat, lng).create()
    }).isDefined
  }

}

// GENERATED object start
trait UserGen extends EntityCompanion[User] {
  val simple = {
    get[Pk[Int]]("user_id") ~
    get[String]("user_handle") ~
    get[String]("user_email") ~
    get[String]("user_password") map {
      case id~handle~email~password =>
        User(id, handle, email, password)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from users where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[User] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[User] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[User] = findOne("user_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[User] = DB.withConnection { implicit c =>
    SQL("select * from users limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: User): Option[User] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into users (
            user_id,
            user_handle,
            user_email,
            user_password
          ) VALUES (
            DEFAULT,
            {handle},
            {email},
            {password}
          )
        """).on(
          'id -> o.id,
          'handle -> o.handle,
          'email -> o.email,
          'password -> o.password
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into users (
            user_id,
            user_handle,
            user_email,
            user_password
          ) VALUES (
            {id},
            {handle},
            {email},
            {password}
          )
        """).on(
          'id -> o.id,
          'handle -> o.handle,
          'email -> o.email,
          'password -> o.password
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: User): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update users set
        user_handle={handle},
        user_email={email},
        user_password={password}
      where user_id={id}
    """).on(
      'id -> o.id,
      'handle -> o.handle,
      'email -> o.email,
      'password -> o.password
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from users where user_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait UserCCGen {
  val companion = User
}
// GENERATED object end

