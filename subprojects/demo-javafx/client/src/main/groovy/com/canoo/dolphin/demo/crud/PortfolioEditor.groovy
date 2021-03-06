package com.canoo.dolphin.demo.crud
import com.canoo.dolphin.core.ModelStoreEvent
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovyx.javafx.SceneGraphBuilder
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JavaFxUtil.cellEdit
import static com.canoo.dolphin.binding.JavaFxUtil.value
import static javafx.scene.layout.GridPane.REMAINING
import static com.canoo.dolphin.demo.crud.CrudConstants.*

@SuppressWarnings("GroovyAssignabilityCheck")
class PortfolioEditor {

    ClientPresentationModel portfolioPM

    private javafx.scene.Node view
    private ClientDolphin clientDolphin
    private ObservableList<ClientPresentationModel> observableListOfPositions  = FXCollections.observableArrayList()
    private plus, minus, nameField, tableBox, positions, totalField, fixedField, chart

    PortfolioEditor(ClientPresentationModel portfolioPM, ClientDolphin clientDolphin) {
        this.portfolioPM = portfolioPM
        this.clientDolphin = clientDolphin
    }

    javafx.scene.Node initView(SceneGraphBuilder sgb) {
        if (! view) {
            view = createView sgb
            bindings          sgb
            attachListeners   sgb
            pull              sgb
        }
        return view
    }

    private javafx.scene.Node createView(SceneGraphBuilder sgb) {
        sgb.with {
            javafx.scene.Node result = gridPane hgap:10, vgap:12, padding: 20, {
                columnConstraints     minWidth: 80, halignment: "right"
                label       "Portfolio",    row: 0, column: 0
                nameField = textField       row: 0, column: 1, minHeight:32
                label       "Positions",    row: 1, column: 0
                tableBox  = vbox            row: 1, column: 1, {
                    positions = tableView   selectionMode:"single", editable:true, styleClass:'noBorder', {
                        value 'instrument', tableColumn('Instrument', prefWidth: 100, editable:true,
                                                        onEditCommit: cellEdit('instrument', { it.toString() } ) )
                        value 'weight'    , tableColumn('Weight',     prefWidth:  60, editable:true,
                                                        onEditCommit: cellEdit('weight',     { it.toInteger() } ) )
                    }
                    hbox {
                        plus  = button '+', styleClass:"bottomButton"
                        minus = button '-', styleClass:"bottomButton"
                    }
                }
                label       'Total',        row: 2, column: 0
                totalField = text           row: 2, column: 1
                label       'Fixed',        row: 3, column: 0
                fixedField = checkBox       row: 3, column: 1
                chart      = pieChart       row: 0, column: 2, rowSpan:REMAINING, animated: true
            }
            positions.items = observableListOfPositions
            result.opacity  = 0.3d
            return result
        }
    }

    private void bindings(SceneGraphBuilder sgb) {
        sgb.with {
            bind ATT_FIXED  of portfolioPM to 'selected'  of fixedField
            bind 'selected' of fixedField  to ATT_FIXED   of portfolioPM

            bind ATT_NAME   of portfolioPM to 'text'      of nameField
            bind 'text'     of nameField   to ATT_NAME    of portfolioPM

            bind ATT_TOTAL  of portfolioPM to 'text'      of totalField
        }
    }

    private void attachListeners(SceneGraphBuilder sgb) {
        sgb.with {
            def chart = chart // do not delete! Needed for local reference lookup.
            def observableListOfPositions = observableListOfPositions
            def positions = positions

            clientDolphin.addModelStoreListener TYPE_POSITION, { ModelStoreEvent event ->
                PresentationModel position = event.presentationModel
                if (position[ATT_PORTFOLIO_ID].value != portfolioPM[ATT_DOMAIN_ID].value) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << position
                        position[ATT_WEIGHT].addPropertyChangeListener('value', {
                            setCurrentPortfolio()
                            clientDolphin.send CMD_UPDATE_TOTAL
                        } as PropertyChangeListener)
                        def pieDataPoint = new PieChart.Data("",0)
                        bind ATT_INSTRUMENT of position to 'name'     of pieDataPoint
                        bind ATT_WEIGHT     of position to 'pieValue' of pieDataPoint, { it.toDouble() }

                        position[ATT_INSTRUMENT].addPropertyChangeListener('value', { // Workaround for http://javafx-jira.kenai.com/browse/RT-26845
                            def index = chart.data.indexOf pieDataPoint
                            def newDataPoint = new PieChart.Data(it.newValue, pieDataPoint.pieValue)
                            bind 'instrument' of position to 'name'     of newDataPoint
                            bind 'weight'     of position to 'pieValue' of newDataPoint, { it.toDouble() }
                            chart.data[index] = newDataPoint       // consider unbinding pieDataPoint
                        } as PropertyChangeListener)

                        chart.data.add pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        def index = observableListOfPositions.indexOf position // assuming list and chartData have the same order
                        observableListOfPositions.remove position
                        chart.data.remove index
                        break
                }
            }

            plus.onAction {
                setCurrentPortfolio()
                clientDolphin.presentationModel(null, TYPE_POSITION, instrument:'changeme', weight:10, portfolioId:portfolioPM[ATT_DOMAIN_ID].value)
                clientDolphin.send CMD_UPDATE_TOTAL
            }

            minus.onAction {
                def position = positions.selectionModel.selectedItem
                if (! position) return
                clientDolphin.delete(position)
                positions.selectionModel.clearSelection() // this may become a server decision
                setCurrentPortfolio()
                clientDolphin.send CMD_UPDATE_TOTAL
            }
        }
    }

    private void pull(SceneGraphBuilder sgb) {
        sgb.with {
            setCurrentPortfolio()
            clientDolphin.send CMD_PULL_POSITIONS, {
                clientDolphin.send CMD_UPDATE_TOTAL, {
                    fadeTransition(1.s, node: view, to: 1).playFromStart()
                }
            }
        }
    }

    def void setCurrentPortfolio() {
        def visiblePortfolio = clientDolphin.findPresentationModelById(PM_SELECTED_PORTFOLIO)
        visiblePortfolio[ATT_PORTFOLIO_ID].value = portfolioPM.id
    }
}
