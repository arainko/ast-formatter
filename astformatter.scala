import com.monovore.decline.*
import cats.syntax.all.*
import fs2.io.file.*
import cats.effect.*
import cats.effect.std.*
import cats.parse.*
import scala.util.control.NoStackTrace
import cats.effect.unsafe.implicits.*
import com.monovore.decline.effect.CommandIOApp
import cats.data.Validated

final class ParseException(error: Parser.Error) extends NoStackTrace {
  override def getMessage(): String = show"""Encountered error during parsing (offset: ${error.failedAtOffset}): ${error}"""
}

object astformatter
    extends CommandIOApp(
      name = "astformatter",
      header = "Formats the AST printed with Printer.TreeStructure"
    ) {
  override def main: Opts[IO[ExitCode]] = {

    val useColorFlag =
      Opts
        .flag(
          long = "no-color",
          help = "Turn off ANSI color codes in the output."
        )
        .orFalse
        .map(noColor => UseColor.fromBoolean(!noColor)) // no-no-color = yes-color

    val useColorEnv =
      Opts
        .env[String](name = "NO_COLOR", help = "Turn off ANSI color codes in the output.")
        .orNone
        .map(noColor => UseColor.fromBoolean(noColor.isEmpty))

    val useColor = (useColorFlag, useColorEnv).mapN(_ && _)

    val filepathArg = Opts.argument[String]("filepath").map(Path.apply)

    def processFile(path: Path, useColor: UseColor) =
      for {
        contents <- Files[IO].readUtf8Lines(path).compile.string
        ast <- IO.fromEither(AST.parse(contents).leftMap(ParseException(_)))
        _ <- Console[IO].println(Printer.print(ast, useColor))
      } yield ExitCode.Success

    filepathArg
      .product(useColor)
      .map(processFile)
      .map(_.onError(err => Console[IO].errorln(s"Encountered error: $err")))
  }
}
