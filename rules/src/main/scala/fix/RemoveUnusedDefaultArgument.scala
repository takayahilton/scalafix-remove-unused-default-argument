package fix

import metaconfig.Configured
import scalafix.v1._

import scala.collection.mutable
import scala.meta.Term.Param
import scala.meta._

class RemoveUnusedDefaultArgument extends SemanticRule("RemoveUnusedDefaultArgument") {

  override def description: String =
    "Removes unused default argument that reported by the compiler under -Ywarn-unused"

  override def withConfiguration(config: Configuration): Configured[Rule] =
    if (!config.scalacOptions.exists(_.startsWith("-Ywarn-unused"))) {
      Configured.error(
        """|The Scala compiler option "-Ywarn-unused" is required to use RemoveUnusedDefaultArgument.
           |To fix this problem, update your build to use at least one Scala compiler
           |option that starts with "-Ywarn-unused"""".stripMargin
      )
    } else {
      super.withConfiguration(config)
    }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val isUnusedUnusedDefaultArgument = mutable.Set.empty[Position]

    doc.diagnostics.foreach { diagnostic =>
      if (diagnostic.message.startsWith("private default argument") && diagnostic.message.endsWith("is never used")) {
        isUnusedUnusedDefaultArgument += diagnostic.position
      } else if(diagnostic.message.startsWith("local default argument") && diagnostic.message.endsWith("is never used")) {
        isUnusedUnusedDefaultArgument += diagnostic.position
      }
    }

    if (isUnusedUnusedDefaultArgument.isEmpty) {
      // Optimization: don't traverse if there are no diagnostics to act on.
      Patch.empty
    } else {
      doc.tree.collect {
        case Defn.Def(_, _, _, args, _, _) =>
          args.map(_.collect {
            case p@Param(_, _, _, Some(_)) if isUnusedUnusedDefaultArgument.exists(_.start == p.pos.start) =>
              val removed = p.copy(default = None)
              Patch.replaceTree(p, removed.toString())
          }.asPatch).asPatch
      }.asPatch
    }
  }
}
