package controllers.tabs

import play.api.mvc.Controller
import play.api.mvc.Action
import de.fuberlin.wiwiss.silk.workspace.{Constants, User}
import de.fuberlin.wiwiss.silk.workspace.util.PrefixRegistry
import models.WorkbenchConfig
import java.io.FileInputStream
import de.fuberlin.wiwiss.silk.util.convert.SparqlRestrictionParser
import de.fuberlin.wiwiss.silk.entity.{ForwardOperator, SparqlRestriction}
import de.fuberlin.wiwiss.silk.entity.Restriction.{Operator, Or, Condition}
import de.fuberlin.wiwiss.silk.util.Uri

object Workspace extends Controller {

  def index = Action {
    Ok(views.html.workspace.workspace())
  }

  def newProjectDialog() = Action {
    Ok(views.html.workspace.newProjectDialog())
  }

  def importProjectDialog() = Action {
    Ok(views.html.workspace.importProjectDialog())
  }

  def importLinkSpecDialog(project: String) = Action {
    Ok(views.html.workspace.importLinkSpecDialog(project))
  }

  def prefixDialog(project: String) = Action {
    val prefixes = User().workspace.project(project).config.prefixes

    Ok(views.html.workspace.prefixDialog(project, prefixes, PrefixRegistry.all))
  }

  def resourcesDialog(project: String) = Action {
    val resourceManager = User().workspace.project(project).resourceManager

    Ok(views.html.workspace.resourcesDialog(project, resourceManager))
  }

  def sourceDialog(project: String, source: String) = Action {
    Ok(views.html.workspace.sourceDialog(project, source))
  }

  def linkingTaskDialog(project: String, task: String) = Action {
    Ok(views.html.workspace.linkingTaskDialog(project, task))
  }

  def restrictionDialog(projectName: String, sourceName: String, sourceOrTarget: String, restriction: String) = Action {
    val project = User().workspace.project(projectName)
    val pathCache = project.sourceModule.task(sourceName).cache
    implicit val prefixes = project.config.prefixes

    val variable = sourceOrTarget match {
      case "source" => Constants.SourceVariable
      case "target" => Constants.TargetVariable
    }

    // Parse the SPARQL restriction
    val restrictionParser = new SparqlRestrictionParser()
    val restrictionTree = restrictionParser(SparqlRestriction.fromSparql(variable, restriction))

    // Collect all type statements
    def collectTypes(op: Operator): Set[String] = op match {
      case Condition(path, value) => path.operators match {
        case ForwardOperator(uri) :: Nil if uri.uri == "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" => Set(value)
        case _ => Set.empty
      }
      case Or(ops) => ops.toSet.flatMap(collectTypes)
      case _ => Set.empty
    }
    val types = restrictionTree.operator match {
      case Some(op) => collectTypes(op)
      case None => Nil
    }

    // TODO forward types to template and highlight them
    println("Types " + types)

    Ok(views.html.workspace.restrictionDialog(project, restriction, pathCache))
  }

  def outputDialog(project: String, output: String) = Action {
    Ok(views.html.workspace.outputDialog(project, output))
  }

  def importExample(project: String) = Action {
    val inputStream = WorkbenchConfig.getResourceLoader.get("example.zip").load
    User().workspace.importProject(project, inputStream)
    inputStream.close()

    Ok
  }
}