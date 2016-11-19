package com.debugduck.design.fastquotation.plugin;

import com.debugduck.design.fastquotation.plugin.model.BaseChar;
import com.debugduck.design.fastquotation.plugin.model.Char;

import java.util.ArrayList;
import java.util.List;

public class CharacterManager {

    private List<BaseChar> charList;

    private static CharacterManager sInstance;

    public static CharacterManager getInstance() {
        if (sInstance == null) {
            sInstance = new CharacterManager();
        }
        return sInstance;
    }

    private CharacterManager() {
        charList = new ArrayList<>();
        loadSupportedChars();
    }

    private void loadSupportedChars() {
        Char roundBracketLeft = new Char('(', charList);
        Char roundBracketRight = new Char(')', charList, false, roundBracketLeft);
        roundBracketLeft.setComplementary(roundBracketRight);

        Char squareBracketLeft = new Char('[', charList);
        Char squareBracketRight = new Char(']', charList, false, squareBracketLeft);
        squareBracketLeft.setComplementary(squareBracketRight);

        Char curlyBracketLeft = new Char('{', charList);
        Char curlyBracketRight = new Char('}', charList, false, curlyBracketLeft);
        curlyBracketLeft.setComplementary(curlyBracketRight);

        new Char('"', charList);
        new Char('\'', charList);
        new Char('`', charList);
    }

    public boolean isSupported(char character) {
        return charList.contains(new Char(character));
    }

    public BaseChar getChar(char character) {
        for (BaseChar c : charList) {
            if (c.getValue() == character) {
                return c;
            }
        }
        return null;
    }


}