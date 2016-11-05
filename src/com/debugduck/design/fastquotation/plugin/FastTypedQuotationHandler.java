package com.debugduck.design.fastquotation.plugin;

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


    private static final char ROUND_BRACKET1 = '(';
    private static final char ROUND_BRACKET2 = ')';

    private static final char SQUARE_BRACKET1 = '[';
    private static final char SQUARE_BRACKET2 = ']';

    private static final char QUOTE_BRACKET = '\"';
    private static final char QUTOE_BRACKET2 = '\'';
    private final TypedActionHandler defaultHandler;

    private char lastCharacter;
    private int  charCount = 0;


    private char SUPP_CHARS[] = { '\'', '\"', '[', ']', '(', ')', '`', '{', '}', '/', '\\', '|'};
    private char COMPL_CHAR[] = {'[', ']', '(', ')', '{', '}' };


    public boolean moveCaretToEnd = true;

    public FastTypedQuotationHandler(TypedActionHandler defaultHandler){
        this.defaultHandler = defaultHandler;
    }


    @Override
    public void execute(@NotNull Editor editor, final char typedChar, @NotNull DataContext dataContext) {

        if(!quota(editor, typedChar)){
            defaultHandler.execute(editor,typedChar,dataContext);
        }

        lastCharacter = typedChar;
    }

    private boolean quota(@NotNull Editor editor, final char typedChar){

        final CaretModel caretModel = editor.getCaretModel();
        final Caret currentCarret = caretModel.getCurrentCaret();
        final int offest = currentCarret.getLeadSelectionOffset();
        final Document document = editor.getDocument();
        Project project = editor.getProject();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                char complementaryChar = getComplementaryChar(typedChar);

                //Support multi caret
                for(Caret carret : caretModel.getAllCarets()){
                    document.insertString(carret.getSelectionStart(), String.valueOf(typedChar));
                    document.insertString(carret.getSelectionEnd(), String.valueOf(complementaryChar));

                    //Move carret to end of quotated string
                    if(moveCaretToEnd){
                        caretModel.moveCaretRelatively(carret.getSelectionEnd(), 0, false,false,false);
                    }
                }
            }
        };

        /* Check if typed character is supported */
        boolean isSuppportedChar = Arrays.binarySearch(SUPP_CHARS, typedChar) > 0;

        if(document.isWritable() && isSuppportedChar){

            WriteCommandAction.runWriteCommandAction(project, runnable);
            lastCharacter = 0;
            return true;
        } else {
            LOGGER.debug("Document not writable !");
            return  false;
        }

    }

    /**
     * Checks if typed character have complementary equivalent
     * @param typedChar - typed character
     * @return equivalent complementary character if found or typedChar otherwise
     */
    private char getComplementaryChar(char typedChar){
        boolean isComplementaryChar = Arrays.binarySearch(COMPL_CHAR, typedChar) > 0;
        int complIndex = Arrays.binarySearch(COMPL_CHAR, typedChar) + 1;
        char complementaryChar =  isComplementaryChar ? COMPL_CHAR[complIndex] : typedChar;

        return complementaryChar;
    }
}
