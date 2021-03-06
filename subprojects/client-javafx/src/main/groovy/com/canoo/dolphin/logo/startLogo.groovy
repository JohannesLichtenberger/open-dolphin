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

package com.canoo.dolphin.logo

import groovyx.javafx.GroovyFX
import javafx.scene.media.AudioClip

GroovyFX.start { app ->
    boolean first=true

    def effect = dropShadow(offsetY: 2, offsetX: 2, radius: 3, color:grey, input: lighting{distant(azimuth: -135.0)})
    logo = new DolphinLogo(width:401, height: 257, shuffle: true, effect: effect)

    AudioClip sonar = new AudioClip(this.class.getResource("pulse.mp3").toString());
    sonar.cycleCount = 1

    stage title: "Tickle the Dolphin!", {
        scene width:441, height: 297, {
            stackPane cache:true, {
                rectangle width:441, height: 297,
                          fill:radialGradient(radius: 0.95, center: [0.4, 0.2], stops: [[0, lightcyan], [0.8, groovyblue]])
                ellipse translateX: -40, translateY: 110, radiusX: 220, radiusY: 20, opacity: 0.2,
                        fill: radialGradient(radius:1, center: [0.5, 0.5], stops: [[0, lightcyan], [0.3, transparent]])
            }
            logo.addTo delegate
            circle id:"pulse", fill:transparent, stroke:rgb(207, 0, 58), strokeWidth: 3, opacity:0, translateX:-100, translateY: -20, effect:boxBlur(), {
                anim = timeline cycleCount: 3, {
                    onFinished { pulse.radius = 10 }
                    at (1.8.s) { change(pulse, "radius") to 200 tween ease_in }
                    at (1.8.s) { change(pulse, "opacity") to 0  tween linear  }
                }
            }
            onMouseClicked {
                if (first) { first = false; return }
                primaryStage.scene.lookup("#dolphinLogoStrokes").cache = true
                pulse.opacity = 1
                anim.play()
                sonar.play()
            }
        }
    }
    primaryStage.show()
}
