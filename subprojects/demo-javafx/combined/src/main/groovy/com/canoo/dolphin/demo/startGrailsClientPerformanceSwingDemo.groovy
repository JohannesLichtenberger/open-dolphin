/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.UiThreadHandler

import javax.swing.SwingUtilities

/*
start standalone via
gradlew demo-javafx-combined:run --stacktrace -PappProp=GrailsClientPerformanceSwing
*/

def dolphin = StarterUtil.setupForRemote()
dolphin.clientConnector.uiThreadHandler = { Closure todo -> SwingUtilities.invokeLater { todo() } } as UiThreadHandler

PerformanceSwingView.show(dolphin)
