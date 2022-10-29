import fansi.*
import com.monovore.decline.*
import cats.syntax.all.*

import java.nio.file.*

object Main
    extends CommandApp(
      name = "ast-formatter",
      header = "Formats the AST printed with Printer.TreeStructure",
      main = {
        val fileOption =
          Opts
            .option[String]("filepath", "the path to the structure file", "f", "path")
            .map(Path.of(_))

        fileOption.map(Files.readString).map { contents =>
          AST
            .parse(contents)
            .map(Printer.print)
            .map(println)
            .leftMap(err => println(s"Encountered error during parsing: $err"))
        }
      }
    )
