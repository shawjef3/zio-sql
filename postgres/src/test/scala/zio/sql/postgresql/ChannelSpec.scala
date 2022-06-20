package zio.sql.postgresql
import zio.test.Spec

class ChannelSpec extends PostgresRunnableSpec with DbSchema {
  override def specLayered: Spec[JdbcEnvironment, Object] = suite("Postgres module notify listen")(
    test("can listen") {
      val query = listen("channel")
      execute()
    }
  )
}
