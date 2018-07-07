package ure.ui;

import ure.math.UColor;
import ure.render.URenderer;

import java.util.ArrayList;

public class UScrollPanel extends View {

    UColor fgColor, bgColor, borderColor;
    int textRows, textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int spacing = 1;
    int charWidth, charHeight;
    boolean suppressDuplicates = true;
    String lastMessage;
    ArrayList<String> lines;
    ArrayList<UColor> lineFades;

    public UScrollPanel(int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg, UColor borderc) {
        lines = new ArrayList<String>();
        lineFades = new ArrayList<UColor>();
        textRows = rows;
        textColumns = columns;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textColumns * cw + padX;
        pixelh = textRows * (ch + spacing) + padY;
        fgColor = fg;
        bgColor = bg;
        borderColor = borderc;
    }

    public void addLineFade(UColor fade) {
        lineFades.add(fade);
    }

    public void print(String line) {
        if (line != "") {
            if (line != lastMessage || !suppressDuplicates) {
                lines.add(0, line);
                lastMessage = line;
            }
        }
    }

    @Override
    public void draw(URenderer renderer) {
        renderer.drawRectBorder(1, 1, width-2, height-2, 1, bgColor, borderColor);
        int i = 0;
        while (i < textRows) {
            if (i < lines.size()) {
                UColor col;
                //MM FINISH THIS
                if (i < lineFades.size())
                    col = lineFades.get(i);
                else
                    col = lineFades.get(lineFades.size() - 1);
                renderer.drawString(padX, (padY + pixelh + 4) - ((i + 1) * (charHeight + spacing)), col, lines.get(i));
                //g.drawString(lines.get(i), padX, (padY + pixelh + 4) - ((i + 1) * (charHeight + spacing)));

            }
            i++;
        }
    }
}
