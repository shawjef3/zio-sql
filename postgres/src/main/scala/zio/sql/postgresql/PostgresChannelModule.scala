package zio.sql.postgresql

import zio.ZIO
import zio.sql.Jdbc
import zio.sql.driver.Renderer

trait PostgresChannelModule {
  self: PostgresRenderModule with Jdbc =>

  def listen(channel: String): Listen =
    Listen(channel)

  def notify(channel: String, maybePayload: Option[String]): Notify =
    Notify(channel, maybePayload)

  def execute(listen: Listen): ZIO[SqlDriver, Exception, Int] =
    ZIO.serviceWithZIO(_.)

  sealed case class Listen(channel: String)

  sealed case class Notify(channel: String, maybePayload: Option[String])

  def renderListenImpl(listen: Listen)(implicit render: Renderer) = {
    render("LISTEN ")
    render(PostgresRenderer.quoted(listen.channel))
    render.toString
  }

  def renderNotifyImpl(notify: Notify)(implicit render: Renderer) = {
    render("NOTIFY ")
    render(PostgresRenderer.quoted(notify.channel))
    notify.maybePayload match {
      case Some(payload) =>
        render(",")
        PostgresRenderer.renderLit(Expr.Literal(payload))
    }
    render.toString
  }

  def renderListen(listen: Listen): String = {
    implicit val render: Renderer = Renderer()
    renderListenImpl(listen)
    render.toString
  }

  def rendererNotify(notify: Notify): String = {
    implicit val render: Renderer = Renderer()
    renderNotifyImpl(notify)
    render.toString
  }

}
