package com.debugduck.design.fastquotation.plugin.model;

import java.util.List;

public class Char extends BaseChar {

    private Char complementary;
    private boolean isLeft = false;

    /**
     * @param character
     */
    public Char(char character) {
        super(character);
    }

    /**
     * @param character
     */
    public Char(char character, List<BaseChar> list) {
        this(character, list, true, null);
    }

    /**
     * Main constructor. Complementary
     *
     * @param character
     * @param complementary
     */
    public Char(char character, List<BaseChar> list, Char complementary) {
        this(character, list, true, complementary);
    }

    public Char(char character, List<BaseChar> list, boolean isLeft, Char complementary) {
        super(list, character);
        this.complementary = complementary;
        this.isLeft = isLeft;
    }

    /**
     * Checks if this character have theier complementary, By default
     * "(", ")" are complementary, same as: "{", "}", "[", "]"
     *
     * @return true if complementary character exists, false otherwise
     */
    @Override
    public boolean hasComplementary() {
        return this.complementary != null;
    }

    @Override
    public boolean isLeft() {
        return this.isLeft;
    }

    /**
     * Gets complentatry character if exists;
     *
     * @return Char as complementary character if exists or the same character
     * if it does not exist.
     */
    @Override
    public Char getComplementary() {
        if (complementary != null) {
            return complementary;
        }
        return this;
    }

    /**
     * Set complementary to this character
     *
     * @param complementary - Char object complementar to this
     */
    @Override
    public void setComplementary(Char complementary) {
        this.complementary = complementary;
    }

}


