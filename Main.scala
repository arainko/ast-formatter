import com.monovore.decline.*
import cats.syntax.all.*
import fs2.io.file.*
import cats.effect.*
import cats.effect.std.*
import cats.parse.*
import scala.util.control.NoStackTrace
import cats.effect.unsafe.implicits.*

final class ParseException(error: Parser.Error) extends NoStackTrace {
  override def getMessage(): String = s"Encountered error during parsing: ${error.toString()}"
}

object Main
    extends CommandApp(
      name = "ast-formatter",
      header = "Formats the AST printed with Printer.TreeStructure",
      main = {

        val useColorFlag =
          Opts
            .flag(
              long = "no-color",
              help = "turns off ANSI color codes in the output, you can also set the NO_COLOR env var to achieve the same effect"
            )
            .orFalse
            .map(noColor => UseColor.fromBoolean(!noColor)) // no-no-color = yes-color

        val filepathArg = Opts.argument[String]("filepath").map(Path.apply)

        def processFile(path: Path, useColor: UseColor) =
          for {
            contents <- Files[IO].readUtf8Lines(path).compile.string
            ast <- IO.fromEither(AST.parse(contents).leftMap(ParseException(_)))
            useColorEnv <- Env[IO].get("NO_COLOR").map(flag => UseColor.fromBoolean(flag.isEmpty))
            _ <- Console[IO].println(Printer.print(ast, useColor && useColorEnv))
          } yield ()

        filepathArg
          .product(useColorFlag)
          .map(processFile)
          .map(_.unsafeRunSync())
      }
    )
