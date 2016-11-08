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


    private char SUPP_CHARS[] = { '\'', '"', '[', ']', '(', ')', '`', '{', '}', '/', '\\', '|'};
    private char COMPL_CHAR[] = {'[', ']', '(', ')', '{', '}' };


    public boolean moveCaretToEnd = true;
    public boolean addColon = true;  //This will require language check. It's ok for java but python ?!
    public boolean addNewLine = true;

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

                char startingChar = typedChar;
                char endingChar = complementaryChar;

                if(!isLeftChar(typedChar)){
                    startingChar = complementaryChar;
                    endingChar = typedChar;
                }

                //Support multi caret
                for(Caret caret : caretModel.getAllCarets()){

                    //Don't do anything on empty strings
                    if(caret.getSelectedText() == null || caret.getSelectedText().isEmpty()){
                        return;
                    }

                    document.insertString(caret.getSelectionStart(), String.valueOf(startingChar));
                    document.insertString(caret.getSelectionEnd(), String.valueOf(endingChar));

                    if(addColon){
                        document.insertString(caret.getSelectionEnd() + 1, ";");
                    }

                    if(moveCaretToEnd){
                        caretModel.moveCaretRelatively(caret.getSelectionEnd(), 0, false,false,false);
                    }

                    if(addNewLine){
                        document.insertString(caret.getSelectionEnd(), String.valueOf('\n'));
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


    private boolean isLeftChar(char typedChar) {
        int charIndex = Arrays.binarySearch(COMPL_CHAR, typedChar);
        boolean hasComplementary = charIndex > 0;
        if (hasComplementary) {
            return charIndex % 2 == 0;
        }
        return false;
    }

    /**
     * Checks if typed character have complementary equivalent
     *
     * @param typedChar - typed character
     * @return equivalent complementary character if found or typedChar otherwise
     */
    private char getComplementaryChar(char typedChar) {
        int complIndex = 0;
        char complementaryChar = typedChar;
        int charIndex = Arrays.binarySearch(COMPL_CHAR, typedChar);
        boolean hasComplementary = charIndex > 0;
        if (hasComplementary) {
            if (charIndex % 2 == 0) {
                complIndex = charIndex + 1;
            } else {
                complIndex = charIndex - 1;

            }
            return COMPL_CHAR[complIndex];
        }
        return complementaryChar;
    }
}
