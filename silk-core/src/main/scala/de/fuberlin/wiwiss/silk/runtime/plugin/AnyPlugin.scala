/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fuberlin.wiwiss.silk.runtime.plugin

/**
 * Plugin interface.
 */
trait AnyPlugin {

  // TODO should be cleaner
  private[plugin] var pluginParameters = Map[String, String]()

  /**
   * The description for this plugin.
   */
  lazy val plugin = PluginDescription(getClass)

  /**
   * The parameters for this plugin as Map.
   */
  def parameters = pluginParameters

  override def toString = {
    getClass.getSimpleName + "(" + parameters.map { case (key, value) => key + "=" + value }.mkString(" ") + ")"
  }
}
