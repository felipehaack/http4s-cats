package com.baked.auth.api

import cats.effect.{ ContextShift, IO, Timer }
import com.baked.auth.api.config.JwtConfig
import com.baked.auth.api.db.{ PostgresDb, PostgresSession }
import com.baked.auth.api.util.JwtCodec
import org.specs2.execute.AsResult
import org.specs2.mutable.Specification
import scalikejdbc.{ DBSession, NoSession }

import scala.concurrent.ExecutionContext

trait UnitSpec extends Specification with UnitSuite with UnitMock with UnitRepo

trait UnitSuite {
  implicit val timer: Timer[IO]               = IO.timer(ExecutionContext.global)
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit def ioAsResult[A : AsResult]: AsResult[IO[A]] =
    new AsResult[IO[A]] {
      override def asResult(io: => IO[A]) =
        AsResult {
          io.unsafeRunSync()
        }
    }
}

trait UnitMock {
  val jwtCodec = JwtCodec.instance[IO](
    jwtConfig = JwtConfig(
      secret = "secret",
      expireInDays = 10
    )
  )

  val noSessionPostgresDb = new PostgresDb[IO] {
    override def read[A](f: DBSession => IO[A]): IO[A]                                     = f(NoSession)
    override def transaction[A](f: DBSession => IO[A])(implicit S: PostgresSession): IO[A] = f(NoSession)
  }
}
