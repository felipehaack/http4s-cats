package com.baked.auth.api

import cats.effect.{ ExitCode, IO, IOApp }

object Main extends IOApp with ApiApp {

  override def run(args: List[String]): IO[ExitCode] =
    application[IO].as(ExitCode.Success)
}
