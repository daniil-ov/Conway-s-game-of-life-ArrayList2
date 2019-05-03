package dovsyannikov;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Life {

    private static int widthField = 10000;
    private static int heightField = 10000;

    private static int centerX = 2500;
    private static int centerY = -5000;

    public static ArrayList<Row> currentLife = new ArrayList<>();
    public static ArrayList<Row> nextStepLife = new ArrayList<>();

    static String fileName = "caterpillar.rle";
    //static String fileName = "s0072.rle";
    //static String fileName = "test2.rle";

    public static void main(String[] args) {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(700, 700);
        StdDraw.setPenColor(Color.BLACK);

        createField();
        runLife();
    }


    private static void createField() {

        ReadFile.read(fileName, Charset.defaultCharset());
    }


    private static void runLife() {

        while (true) {

            drawCurrentLife();
            change();
            moveField();

            //задержка 0.5 сек
            //StdDraw.pause(500);
        }
    }


    private static void moveField() {

        char key = ' ';

        if (StdDraw.hasNextKeyTyped()) {

            key = StdDraw.nextKeyTyped();
        }

        if (widthField > 2 && widthField < 16000000) {

            switch (key) {

                case '-':
                    widthField *= 2;
                    heightField *= 2;
                    break;

                case '+':
                    widthField /= 2;
                    heightField /= 2;
                    break;

                case 's':
                    centerY -= heightField / 3;
                    break;

                case 'w':
                    centerY += heightField / 3;
                    break;

                case 'a':
                    centerX -= widthField / 3;
                    break;

                case 'd':
                    centerX += widthField / 3;
                    break;
            }
        }
    }


    private static void drawCurrentLife() {

        long start = System.currentTimeMillis();

        StdDraw.clear();

        StdDraw.setXscale(centerX - (widthField / 2), centerX + (widthField / 2));
        StdDraw.setYscale(centerY - (heightField / 2), centerY + (heightField / 2));

        for (Row strings : currentLife) {
            for (Integer tmpX : strings.listX) {
                StdDraw.filledSquare(tmpX, strings.y, 0.00001);
            }
        }

        StdDraw.show();

        long finish = System.currentTimeMillis();

        System.out.println("Время отрисовки: " + (finish - start));
    }


    private static void change() {

        long start = System.currentTimeMillis();

        ArrayList<Row> tmp;

        nextStepLife.add(new Row(currentLife.get(2).y + 2));
        nextStepLife.add(new Row(currentLife.get(2).y + 1));

        if (handlerStrings(currentLife.get(0), currentLife.get(1), currentLife.get(2)).listX.isEmpty()) {

            for (int i = 2; i < currentLife.size() - 1; i++) {

                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i, tmpRow);
            }

        } else {

            for (int i = 1; i < currentLife.size() - 1; i++) {

                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i + 1, tmpRow);
            }
        }

        if (nextStepLife.get(2).listX.isEmpty()) {
            nextStepLife.remove(2);
        }

        if (!(nextStepLife.get(nextStepLife.size() - 1).listX.isEmpty())) {
            nextStepLife.add(new Row(nextStepLife.get(nextStepLife.size() - 1).y - 1));
        }

        if (!(nextStepLife.get(nextStepLife.size() - 2).listX.isEmpty())) {
            nextStepLife.add(new Row(nextStepLife.get(nextStepLife.size() - 2).y - 2));
        }

        currentLife.clear();

        tmp = currentLife;
        currentLife = nextStepLife;
        nextStepLife = tmp;

        long finish = System.currentTimeMillis();

        System.out.println("Время рассчета след. поколения: " + (finish - start));
    }


    private static Row handlerStrings(Row row, Row row1, Row row2) {

        HashSet<Integer> revivalCells = new HashSet<>();
        HashSet<Integer> revivalCells2 = new HashSet<>();
        Row tmpRow = new Row();

        revivalCells.addAll(row.listX);
        revivalCells.addAll(row1.listX);
        revivalCells.addAll(row2.listX);

        for (Integer i : revivalCells) {
            revivalCells2.add(i - 1);
            revivalCells2.add(i);
            revivalCells2.add(i + 1);
        }

        for (Integer tmpInt : row1.listX) {

            int cntNb = countNeighbours(row, row1, row2, tmpInt);

            if ((cntNb == 2) || (cntNb == 3)) {
                tmpRow.listX.add(tmpInt);
            }
        }

        for (Integer tmpInt : revivalCells2) {

            if ((countNeighbours(row, row1, row2, tmpInt) == 3) && !tmpRow.listX.contains(tmpInt)) {
                tmpRow.listX.add(tmpInt);
            }
        }

        return tmpRow;
    }


    private static int countNeighbours(Row row, Row row1, Row row2, Integer currentInteger) {

        int cnt = 0;

        for (int i = -1; i < 2; i++) {

            if (row.listX.contains(currentInteger + i)) {

                cnt++;
            }
        }

        if (row1.listX.contains(currentInteger - 1)) {

            cnt++;
        }

        if (row1.listX.contains(currentInteger + 1)) {

            cnt++;
        }

        for (int i = -1; i < 2; i++) {

            if (row2.listX.contains(currentInteger + i)) {

                cnt++;
            }
        }

        return cnt;
    }
}
