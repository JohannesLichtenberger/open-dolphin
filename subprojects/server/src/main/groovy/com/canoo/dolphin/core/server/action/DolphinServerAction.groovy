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

package com.canoo.dolphin.core.server.action
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.server.DTO
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerDolphin

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */
abstract class DolphinServerAction implements ServerAction {
    ServerDolphin serverDolphin
    List<Command> dolphinResponse


    void presentationModel( String id, String presentationModelType, DTO dto){
        ServerDolphin.presentationModel(dolphinResponse, id, presentationModelType, dto)
    }

    void changeValue(ServerAttribute attribute, value){
        ServerDolphin.changeValue(dolphinResponse, attribute, value)
    }
}
