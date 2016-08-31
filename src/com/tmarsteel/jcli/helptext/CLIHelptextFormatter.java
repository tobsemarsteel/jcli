/*
 * Copyright (C) 2016 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.tmarsteel.jcli.helptext;

import static java.util.stream.StreamSupport.*;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Identifiable;
import com.tmarsteel.jcli.Option;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link HelptextFormatter} suited for the needs of CLI interfaces.
 */
public class CLIHelptextFormatter implements HelptextFormatter<String> {

    /**
     * Maximum number of characters in the output lines.
     */
    private int maxWidth = 100;

    /**
     * The line separator to use
     */
    private char lineSeparator = '\n';

    /**
     * Returns the maximum number of characters {@link #format} will put into one output line.
     * @return The maximum number of characters {@link #format} will put into one output line.
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * Sets the maximum number of characters {@link #format} should put into one output line. That number should be
     * greater than 50.
     * @param maxWidth The maximum number of characters {@link #format} should put into one output line
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    /**
     * Returns the line separator to use. Is used both when parsing input as well as when outputting.
     * @return The line separator to use. Is used both when parsing input as well as when outputting.
     */
    public char getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Sets the line separator to use. Is used both when parsing input as well as when outputting.
     * @param lineSeparator the line separator to use. Is used both when parsing input as well as when outputting.
     */
    public void setLineSeparator(char lineSeparator)
    {
        this.lineSeparator = lineSeparator;
    }

    @Override
    public String format(Helptext t)
    {
        StringBuilder out = new StringBuilder(1000);

        // USAGE
        out.append("Usage: ");
        boolean isFirst = true;
        for (String example : t.usageExamples()) {
            if (t.getExecutableName().length() + 1 + example.length() < maxWidth) {
                out.append(isFirst? t.getExecutableName() : new String(new char[t.getExecutableName().length() + 7]).replace('\0', ' '));
                out.append(' ');
                out.append(example);
                out.append('\n');
                isFirst = false;
            }
        }

        // DESCRIPTION
        out.append(wrap(t.getProgramDescription()));
        out.append(lineSeparator);
        out.append(lineSeparator);

        // OPTIONS and FLAGS
        if (t.options().size() > 0) {
            out.append("-- Options --");
            out.append(lineSeparator);
            out.append(toTable(t.options()));
            out.append(lineSeparator);
        }
        if (t.flags().size() > 0) {
            out.append("-- Flags --");
            out.append(lineSeparator);
            out.append(toTable(t.flags()));
            out.append(lineSeparator);
        }
        if (t.arguments().size() > 0) {
            out.append("-- Arguments --");
            out.append(lineSeparator);
            out.append(toTable_Arguments(t.arguments()));
            out.append(lineSeparator);
        }

        if (t.getNotes() != null && !t.getNotes().isEmpty())
        {
            out.append(lineSeparator);
            out.append(wrap(t.getNotes()));
            out.append(lineSeparator);
        }

        return out.toString();
    }

    /**
     * Indents all lines but the first one by {@code nSpaces} ' '  characters.
     * @param toBeIndented
     * @param nSpaces The number of spaces to indent by
     */
    protected String indentFromSecondLine(String toBeIndented, int nSpaces) {
        String[] lines = toBeIndented.split("" + lineSeparator);

        if (lines.length == 0) {
            return toBeIndented;
        }

        String pad = new String(new char[nSpaces]).replace('\0', ' ');
        StringBuilder out = new StringBuilder(toBeIndented.length() + (lines.length - 1) * nSpaces);

        for (int i = 0;i < lines.length;i++) {
            if (i != 0) {
                out.append(pad);
            }
            out.append(lines[i]);
        }

        return out.toString();
    }

    protected String padLeft(String toBePadded, int nSpaces) {
        return (new String(new char[nSpaces]).replace('\0', ' ')) + toBePadded;
    }

    protected String toTable(Collection<? extends Identifiable> identifiables) {
        int leftColWidth = getLongestNameLength(identifiables);
        StringBuilder out = new StringBuilder(1000);

        identifiables.forEach(identifiable -> {
            String[] names = identifiable.names();
            String description = identifiable.getDescription();
            if (description == null || description.isEmpty()) {
                description = "<no description>";
            }
            String[] descriptionLines = wrap(description, maxWidth - leftColWidth - 1).split("" + lineSeparator);
            for (int i = 0;i < Math.max(names.length, descriptionLines.length);i++) {
                int nPad = leftColWidth + 1;
                if (i < names.length) {
                    out.append(names[i]);
                    out.append(' ');
                    nPad -= names[i].length();
                }
                if (i < descriptionLines.length) {
                    out.append(padLeft(descriptionLines[i], nPad));
                }
                out.append(lineSeparator);
            }
            out.append(lineSeparator);
        });

        return out.toString();
    }

    protected String toTable_Arguments(Collection<? extends Argument> args) {
        int leftColWidth = Integer.toString(args.size(), 10).length() + 2;
        StringBuilder out = new StringBuilder(1000);

        args.stream()
            .sorted((arg1, arg2) -> arg1.getIdentifier().compareTo(arg2.getIdentifier()))
            .forEach(arg -> {
            out.append('#');
            out.append(arg.getIndex());
            out.append("  ");
            out.append(
                indentFromSecondLine(
                    wrap(arg.getDescription(), maxWidth - leftColWidth - 1),
                    leftColWidth + 1
                )
            );
        });

        return out.toString();
    }

    /**
     * Reformats the given string so that it does not contain more than {@link #maxWidth} characters per line, splitting
     * at whitespaces if possible.
     * @param inputString
     * @return The changed string
     */
    protected String wrap(String inputString) {
        return wrap(inputString, maxWidth);
    }

    /**
     * Reformats the given string so that it does not contain more than {@code maxWidth} characters per line, splitting
     * at whitespaces if possible.
     * @param inputString
     * @param maxWidth
     * @return The changed string
     */
    protected String wrap(String inputString, int maxWidth) {
        char[] input = inputString.toCharArray();

        StringBuilder finalOut = new StringBuilder(input.length + 15);

        StringBuilder currentLineBuilder = new StringBuilder(maxWidth);
        int lastWSSeenAtLinePos = -1; // position of the most recent whitespace in the current line
        char current;

        for (int inputPos = 0;inputPos < input.length;inputPos++) {
            current = input[inputPos];
            if (current == lineSeparator) {
                finalOut.append(currentLineBuilder);
                finalOut.append(lineSeparator);
                if (currentLineBuilder.length() != 0) currentLineBuilder = new StringBuilder(maxWidth);
                continue;
            } else if (Character.isWhitespace(current)) {
                lastWSSeenAtLinePos = currentLineBuilder.length();
                if (currentLineBuilder.length() == maxWidth) {
                    // luckily there is a space right where the line limit is ... :O
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    lastWSSeenAtLinePos = -1;
                    continue;
                } else {
                    // do not output leading whitespace onto a new line
                    if (currentLineBuilder.length() != 0) {
                        currentLineBuilder.append(current);
                        continue;
                    }
                }
            }

            if (currentLineBuilder.length() == maxWidth) {
                // we just hit the line limit mid-word
                // try to break at the most recent whitespace

                if (lastWSSeenAtLinePos == -1) {
                    // there is no whitespace in the line - break mid-word
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(current);
                    continue;
                } else {
                    String afterBreak = currentLineBuilder.substring(lastWSSeenAtLinePos + 1);
                    finalOut.append(currentLineBuilder.substring(0, lastWSSeenAtLinePos));
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(afterBreak);
                    currentLineBuilder.append(current);
                    lastWSSeenAtLinePos = -1;
                    continue;
                }
            }

            currentLineBuilder.append(current);
        }

        finalOut.append(currentLineBuilder);
        return finalOut.toString().trim();
    }

    protected int getLongestNameLength(Collection<? extends Identifiable> identifiables) {
        int max = 0;
        for (Identifiable e : identifiables) {
            int localMax = Arrays.stream(e.names()).mapToInt(String::length).max().getAsInt();
            if (localMax > max) {
                max = localMax;
            }
        }

        return max;
    }
}
