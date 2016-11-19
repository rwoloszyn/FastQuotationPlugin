package com.debugduck.design.fastquotation.plugin;

import com.debugduck.design.fastquotation.plugin.model.Char;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FastTypedQuotationHandler implements TypedActionHandler {

    private static final Logger LOGGER = Logger.getInstance(FastTypedQuotationHandler.class);
    private static final int CHAR_COUNT_TRIGGER = 2;

    private final TypedActionHandler defaultHandler;

    private char SUPP_CHARS[] = {'\'', '"', '[', ']', '(', ')', '`', '{', '}', '/', '\\', '|'};
    private char COMPL_CHAR[] = {'[', ']', '(', ')', '{', '}'};


    public boolean moveCaretToEnd = true;
    public boolean addColon = true;  //This will require language check. It's ok for java but python ?!
    public boolean addNewLine = true;


    private CharacterManager charManager;


    public FastTypedQuotationHandler(TypedActionHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        charManager = CharacterManager.getInstance();
    }


    @Override
    public void execute(@NotNull Editor editor, final char typedChar, @NotNull DataContext dataContext) {

        if (!quota(editor, typedChar)) {
            defaultHandler.execute(editor, typedChar, dataContext);
        }
    }

    private boolean quota(@NotNull Editor editor, final char typedChar) {

        final CaretModel caretModel = editor.getCaretModel();
        final Caret currentCarret = caretModel.getCurrentCaret();
        final int offest = currentCarret.getLeadSelectionOffset();
        final Document document = editor.getDocument();
        Project project = editor.getProject();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Char mainChar = (Char) charManager.getChar(typedChar);
                Char complementaryChar = mainChar.getComplementary();

                char startingChar = mainChar.getValue();
                char endingChar = complementaryChar.getValue();

                if (!mainChar.isLeft()) {
                    startingChar = complementaryChar.getValue();
                    endingChar = mainChar.getValue();
                }

                //Support multi caret
                for (Caret caret : caretModel.getAllCarets()) {

                    //Don't do anything on empty strings
                    if (caret.getSelectedText() == null || caret.getSelectedText().isEmpty()) {
                        return;
                    }

                    document.insertString(caret.getSelectionStart(), String.valueOf(startingChar));
                    document.insertString(caret.getSelectionEnd(), String.valueOf(endingChar));

                    if (addColon) {
                        document.insertString(caret.getSelectionEnd() + 1, ";");
                    }

                    if (moveCaretToEnd) {
                        caretModel.moveCaretRelatively(caret.getSelectionEnd(), 0, false, false, false);
                    }

                    if (addNewLine) {
                        document.insertString(caret.getSelectionEnd(), String.valueOf('\n'));
                    }

                }
            }
        };

        /* Check if typed character is supported */
        boolean isSupported = charManager.isSupported(typedChar);

        if (document.isWritable() && isSupported) {

            WriteCommandAction.runWriteCommandAction(project, runnable);
            return true;
        } else {
            LOGGER.debug("Document not writable !");
            return false;
        }

    }
}
