package com.tmarsteel.jcli.util.formatting;

public final class FormattingUtils
{
    private FormattingUtils() {}

    /**
     * Appends space characters ({@code ' '}) to the end of the given string to make the string of the given target
     * length. Returns that string. If the given string is as long or longer than the target length the given string
     * is returned without any modification.
     * @return The padded string, e.g. for inputs "abc" and 6 returns "abc   "
     */
    public static String padRight(String string, int targetLength) {
        int stringLength = string.length();
        if (stringLength >= targetLength) {
            return string;
        }

        char[] target = new char[targetLength];

        System.arraycopy(string.toCharArray(), 0, target, 0, stringLength);

        for (int paddingIndex = stringLength;paddingIndex < targetLength;paddingIndex++) {
            target[paddingIndex] = ' ';
        }

        return new String(target);
    }
}
