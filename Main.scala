import com.monovore.decline.*
import com.monovore.decline.effect.CommandIOApp
import cats.syntax.all.*
import fs2.io.file.*
import cats.effect.*
import cats.effect.std.Console
import cats.parse.*
import scala.util.control.NoStackTrace

object Main
    extends CommandIOApp(
      name = "ast-formatter",
      header = "Formats the AST printed with Printer.TreeStructure"
    ) {

  override def main: Opts[IO[ExitCode]] =
    Opts
      .option[String]("filepath", "the path to the structure file", "f", "path")
      .map(Path.apply)
      .map(processFile)

  private def processFile(path: Path) =
    for {
      contents <- Files[IO].readUtf8Lines(path).compile.string
      ast <- IO.fromEither(AST.parse(contents).left.map(ParseException(_)))
      _ <- Console[IO].println(Printer.print(ast))
    } yield ExitCode.Success

  private final class ParseException(error: Parser.Error) extends NoStackTrace {
    override def getMessage(): String = s"Encountered error during parsing: ${error.toString()}"
  }
}
