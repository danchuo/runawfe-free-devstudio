package ru.runa.gpd.ui.action;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import ru.runa.gpd.Localization;
import ru.runa.gpd.ProcessCache;
import ru.runa.gpd.editor.ProcessEditorBase;
import ru.runa.gpd.lang.Language;
import ru.runa.gpd.lang.model.Node;
import ru.runa.gpd.lang.model.StartState;
import ru.runa.gpd.lang.model.ProcessDefinition;
import ru.runa.gpd.lang.model.Transition;
import ru.runa.gpd.lang.model.bpmn.ExclusiveGateway;
import ru.runa.gpd.ui.custom.Dialogs;
import ru.runa.gpd.algorithms.CycleDetector;

public class CycleDetectorAction extends BaseActionDelegate {

    @Override
    public void run(IAction action) {
        IEditorPart editorPart = getActiveEditor();
        if (editorPart != null) {
            IEditorInput editorInput = editorPart.getEditorInput();
            if (editorInput instanceof FileEditorInput) {
                ProcessDefinition definition = ProcessCache.getProcessDefinition(
                    ((FileEditorInput) editorInput).getFile());
                StartState startNode = definition.getChildren(StartState.class).get(0);

                CycleDetector cycleDetector = new CycleDetector();
                boolean isCycle = cycleDetector.hasCycle(startNode);

                if (isCycle) {
                    Dialogs.warning(Localization.getString("CycleDetectorAction.CycleExist.Message"));
                } else {
                    Dialogs.information(Localization.getString("CycleDetectorAction.CycleNotExist.Message"));
                }
            }
        }
    }

        @Override
        public void selectionChanged (IAction action, ISelection selection){
            ProcessEditorBase editor = getActiveDesignerEditor();
            List<ExclusiveGateway> nodes = null;
            if (editor != null) {
                nodes = editor.getDefinition().getChildren(ExclusiveGateway.class);
            }
            action.setEnabled(nodes != null && !nodes.isEmpty());
        }

        private IEditorPart[] getDirtyEditors () {
            return window.getActivePage().getDirtyEditors();
        }
    }