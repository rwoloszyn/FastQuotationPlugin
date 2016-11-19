package com.debugduck.design.fastquotation.plugin.model;

import java.util.List;

public abstract class BaseChar {

    private List<BaseChar> charList;
    private char value;

    public BaseChar(char character) {
        this.value = character;
    }

    public BaseChar(List<BaseChar> list, char character) {
        this(character);

        this.charList = list;

        if (charList == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        charList.add(this);
    }

    public abstract Char getComplementary();

    public abstract void setComplementary(Char complementary);

    public abstract boolean hasComplementary();

    public abstract boolean isLeft();

    public char getValue() {
        return this.value;
    }

    /**
     * Checks if objecs equals. They equals only by its value. Other fields
     * are not checked
     *
     * @param obj to compare
     * @return true if objects are the same type and their values match, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Char) {
            BaseChar object = (BaseChar) obj;
            if (object.getValue() == this.getValue()) {
                return true;
            }
        }
        return false;
    }
}
