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

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientDolphin

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.core.Tag.MESSAGE
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.ATT.*
import static com.canoo.dolphin.demo.MyProps.CMD.*
import static com.canoo.dolphin.demo.MyProps.PM_ID.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.CENTER

/**
 * The demo shows how to use a presentation model as a "switch", i.e. a stand-in, placeholder, or "mold"
 * that captures a selected presentation model and synchronizes back to the original source.
 * The demo shows to data sets with "title" and "purpose" and two buttons that allow to switch between them.
 * Using the second row of buttons triggers server actions to change the values of the current selection only.
 * There is also an indicator for the "message" tag attribute for the title property, which is set
 * by the server action.
 */


class MultipleAttributeSwitchView {

    static show(ClientDolphin dolphin) {

        start { app ->

            def pm1 = dolphin.presentationModel('FirstDemo',
                new ClientAttribute(TITLE,   'First title',  "pm1-title"),
                new ClientAttribute(TITLE,   '',             "pm1-title-msg", MESSAGE),
                new ClientAttribute(PURPOSE, 'First purpose',"pm1-purpose")
            )
            def pm2 = dolphin.presentationModel('SecondDemo',
                new ClientAttribute(TITLE,   'Second title',   "pm2-title"),
                new ClientAttribute(TITLE,   '',               "pm2-title-msg", MESSAGE),
                new ClientAttribute(PURPOSE, 'Second purpose', "pm2-purpose")
            )

            def mold = dolphin.presentationModel(MOLD,
                 new ClientAttribute(TITLE,   ''),
                 new ClientAttribute(TITLE,   '',  null, MESSAGE),
                 new ClientAttribute(PURPOSE, '')
             )

            dolphin.apply pm1 to mold

            stage {
                scene {
                    gridPane {

                        label id: 'header', row:0, column:0, halignment: CENTER, columnSpan: 2

                        label 'Title',          row: 1, column: 0
                        label id: 'titleLabel', row: 1, column: 1
                        label id: 'titleMsg',   row: 2, column: 1

                        label 'Purpose',          row: 3, column: 0
                        label id: 'purposeLabel', row: 3, column: 1

                        hbox styleClass:"submit", row:4, column:1, {
                            button "Actual is one",
                                   onAction: { dolphin.apply pm1 to mold }
                            button "Actual is two",
                                   onAction: { dolphin.apply pm2 to mold }
                        }
                        hbox styleClass:"submit", row:5, column:1, {
                            button "Set title",
                                   onAction: { dolphin.send SET_TITLE }
                            button "Set purpose",
                                   onAction: { dolphin.send SET_PURPOSE }
            }   }   }   }

            style delegate

            bind TITLE       of mold to FX.TITLE of primaryStage
            bind TITLE       of mold to FX.TEXT  of header
            bind TITLE       of mold to FX.TEXT  of titleLabel
            bind PURPOSE     of mold to FX.TEXT  of purposeLabel

            bind TITLE, MESSAGE of mold to FX.TEXT of titleMsg

            primaryStage.show()
        }
    }

}