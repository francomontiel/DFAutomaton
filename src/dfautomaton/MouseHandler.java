package dfautomaton;

import dfautomaton.UI.DrawingState;
import dfautomaton.UI.ModState;
import dfautomaton.data.Constants;
import dfautomaton.model.State;
import dfautomaton.model.Transition;
import dfautomaton.model.basics.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private State selectedState;
    private State startState;
    private State endState;

    public MouseHandler() {
        selectedState = null;
        startState = null;
        endState = null;
    }
    
    public void reset() {
        selectedState = null;
        startState = null;
        endState = null;
    }

    private Point getClickedPoint(MouseEvent e) {
        int x = e.getX();
        int y = Constants.PANEL_HEIGHT - e.getY();
        return new Point(x, y);
    }

    private void handleStateCreation(Point clickedPoint) {
        UI.getAutomaton().addState(new State(String.format("q%d", UI.getAutomaton().getCreatedStatesQuantity()), false, clickedPoint));
        UI.drawingState = DrawingState.Drawing;
    }
    
    private void handleTransitionCreation(Point clickedPoint) {
        handleStateSelection(clickedPoint);
        if (selectedState != null) {
            if (startState == null) {
                startState = selectedState;
            } else if (endState == null) {
                endState = selectedState;
            }
        }
        if (endState != null && startState != null) {
            UI.getAutomaton().addTransition(new Transition(startState, 'a', endState));
            reset();
            UI.drawingState = DrawingState.Drawing;
        }
    }
    
    private void handleStateDeletion(Point clickedPoint) {
        for (State current : UI.getAutomaton().getStates()) {
            if (current.checkPointCollision(clickedPoint)) {
                UI.getAutomaton().getStates().remove(current);
                UI.drawingState = DrawingState.Drawing;
            }
        }
    }
    
    private void handleStateSelection(Point clickedPoint) {
        selectedState = null;
        for (State current : UI.getAutomaton().getStates()) {
            if (current.checkPointCollision(clickedPoint)) {
                selectedState = current;
            }
        }
    }

    private void handleStateMovement(Point draggedPoint) {
        if (selectedState != null) {
            selectedState.getPos().setX(draggedPoint.getX());
            selectedState.getPos().setY(draggedPoint.getY());
            UI.drawingState = DrawingState.Drawing;
        }
    }
    
    private void handleStateAccepted(Point clickedPoint) {
        for (State current : UI.getAutomaton().getStates()) {
            if (current.checkPointCollision(clickedPoint)) {
                current.updateAccepted();
                UI.drawingState = DrawingState.Drawing;
            }
        }
    }
    
    private void handleStateInitial(Point clickedPoint) {
        for (State current : UI.getAutomaton().getStates()) {
            if (current.checkPointCollision(clickedPoint)) {
                UI.getAutomaton().setInitialState(current);
                UI.drawingState = DrawingState.Drawing;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clickedPoint = getClickedPoint(e);
        if (UI.modState == ModState.Creating) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                handleStateCreation(clickedPoint);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                handleStateDeletion(clickedPoint);
            }
        } else if (UI.modState == ModState.Editing) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                handleStateInitial(clickedPoint);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                handleStateAccepted(clickedPoint);
            }
        } else if (UI.modState == ModState.Transition) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                handleTransitionCreation(clickedPoint);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            Point clickedPoint = getClickedPoint(e);
            handleStateSelection(clickedPoint);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (UI.modState == ModState.Creating) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Point draggedPoint = getClickedPoint(e);
                handleStateMovement(draggedPoint);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
