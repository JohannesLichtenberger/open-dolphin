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
import com.canoo.dolphin.core.client.ClientDolphin

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.bindInfo
import static com.canoo.dolphin.core.Attribute.DIRTY_PROPERTY
import static com.canoo.dolphin.core.Tag.LABEL
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.ATT.*
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.paint.Color.RED
import static javafx.scene.paint.Color.WHITE

/**
 * An example where not only the values and dirty properties are bound but also the
 * text of labels is determined by tag attributes.
 * Hitting the "German" button sets the label tags to their German translation.
 * The dirty state of the presentation model must be independent of the labels now being "dirty".
 * Hitting "Undo" must revert all changes including the label changes and dirty states.
 */

class AttributeTagView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def model = dolphin.presentationModel 'person', (NAME):'', (LASTNAME):'Smith'

            dolphin.tag model, NAME,     LABEL, "First name: "
            dolphin.tag model, LASTNAME, LABEL, "Last name: "

            stage {
                scene {
                    gridPane {

                        label       id: 'header',        row: 0, column: 1, 'Person Form'

                        label       id: 'nameLabel',     row: 1, column: 0
                        textField   id: 'nameInput',     row: 1, column: 1

                        label       id: 'lastnameLabel', row: 2, column: 0
                        textField   id: 'lastnameInput', row: 2, column: 1

                        hbox row: 3, column: 1, spacing:5, {
                            button id: 'german', 'German'
                            button id: 'reset',  'Undo', onAction: { model.reset() }
                        }
                    }
                }
            }

            style delegate

            // binding the values
            bind NAME     of model         to FX.TEXT  of nameInput
            bind LASTNAME of model         to FX.TEXT  of lastnameInput
            bind FX.TEXT  of nameInput     to NAME     of model
            bind FX.TEXT  of lastnameInput to LASTNAME of model

            // binding tag attributes
            bind NAME    , LABEL of model  to FX.TEXT  of nameLabel
            bind LASTNAME, LABEL of model  to FX.TEXT  of lastnameLabel

            // binding meta properties
            bindInfo DIRTY_PROPERTY of model[NAME]     to FX.TEXT_FILL  of nameLabel,     { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model[LASTNAME] to FX.TEXT_FILL  of lastnameLabel, { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model           to TITLE         of primaryStage , { it ? '** DIRTY **': '' }
            bindInfo DIRTY_PROPERTY of model           to 'disabled'    of reset        , { !it }

            german.onAction {
                model.getAt(NAME,     LABEL).value = "Vorname: "
                model.getAt(LASTNAME, LABEL).value = "Nachname: "
            }

            primaryStage.show()
        }
    }

}
