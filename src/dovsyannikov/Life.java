package dovsyannikov;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static java.util.Collections.binarySearch;

public class Life {

    private static int widthField = 5000;
    private static int heightField = 5000;

    private static int centerX = 2300;
    private static int centerY = -2300;

    public static ArrayList<Row> currentLife = new ArrayList<>();
    public static ArrayList<Row> nextStepLife = new ArrayList<>();

    static String fileName = "caterpillar.rle";
    //static String fileName = "s0072.rle";
    //static String fileName = "glader.rle";
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

        if ((widthField > 2) && (widthField < 16000000)) {

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
                StdDraw.filledSquare(tmpX, strings.y, 0.000001);
            }
        }

        StdDraw.show();

        long finish = System.currentTimeMillis();

        System.out.println("Время отрисовки: " + (finish - start));
    }


    private static void change() {

        long start = System.currentTimeMillis();

        ArrayList<Row> tmp;

        if (handlerStrings(currentLife.get(0), currentLife.get(1), currentLife.get(2)).listX.isEmpty()) {

            for (int i = 2; i > 0; i--) {

                nextStepLife.add(new Row(currentLife.get(2).y + i));
            }

            for (int i = 2; i < currentLife.size() - 1; i++) {

                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i, tmpRow);
            }

        } else {

            for (int i = 3; i > 1; i--) {

                nextStepLife.add(new Row(currentLife.get(2).y + i));
            }

            for (int i = 1; i < currentLife.size() - 1; i++) {

                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i + 1, tmpRow);
            }
        }

        /*if (nextStepLife.get(2).listX.isEmpty()) {
            nextStepLife.remove(2);
        }*/

        for (int i = 1; i < 3; i++) {

            if (!(nextStepLife.get(nextStepLife.size() - i).listX.isEmpty())) {

                nextStepLife.add(new Row(nextStepLife.get(nextStepLife.size() - 1).y - i));
            }
        }

        currentLife.clear();

        tmp = currentLife;
        currentLife = nextStepLife;
        nextStepLife = tmp;

        long finish = System.currentTimeMillis();

        System.out.println("Время рассчета след. поколения: " + (finish - start));
    }


    private static Row handlerStrings(Row row1, Row row2, Row row3) {

        Row tmpRow = new Row();

        int tmpIndexRow1 = 0;
        int tmpIndexRow2 = 0;
        int tmpIndexRow3 = 0;

        int rowSize1 = row1.listX.size();
        int rowSize2 = row2.listX.size();
        int rowSize3 = row3.listX.size();

        int tmpNumberRow1 = Integer.MAX_VALUE;
        int tmpNumberRow2 = Integer.MAX_VALUE - 1;
        int tmpNumberRow3 = Integer.MAX_VALUE - 2;

        while ((tmpIndexRow1 < rowSize1) || (tmpIndexRow2 < rowSize2) || (tmpIndexRow3 < rowSize3)) {


            tmpNumberRow1 = updateNumber(row1, rowSize1, tmpIndexRow1, tmpNumberRow1);
            tmpNumberRow2 = updateNumber(row2, rowSize2, tmpIndexRow2, tmpNumberRow2);
            tmpNumberRow3 = updateNumber(row3, rowSize3, tmpIndexRow3, tmpNumberRow3);

            if (tmpNumberRow1 == tmpNumberRow2) {
                tmpIndexRow1++;
            }

            if (tmpNumberRow3 == tmpNumberRow2) {
                tmpIndexRow3++;
            }

            if (tmpNumberRow1 == tmpNumberRow3) {
                tmpIndexRow3++;
            }

            tmpNumberRow1 = updateNumber(row1, rowSize1, tmpIndexRow1, tmpNumberRow1);
            tmpNumberRow2 = updateNumber(row2, rowSize2, tmpIndexRow2, tmpNumberRow2);
            tmpNumberRow3 = updateNumber(row3, rowSize3, tmpIndexRow3, tmpNumberRow3);

            if (tmpIndexRow1 >= rowSize1) {
                tmpNumberRow1 = Integer.MAX_VALUE;
            }

            if (tmpIndexRow2 >= rowSize2) {
                tmpNumberRow2 = Integer.MAX_VALUE - 1;
            }

            if (tmpIndexRow3 >= rowSize3) {
                tmpNumberRow3 = Integer.MAX_VALUE - 2;
            }

            if (minNumber(tmpNumberRow1, tmpNumberRow2, tmpNumberRow3) == tmpNumberRow2) {

                if (checkAdd(tmpRow, tmpNumberRow2 - 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow2 - 1);
                }

                if (checkAdd(tmpRow, tmpNumberRow2, true, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow2);
                }

                if (checkAdd(tmpRow, tmpNumberRow2 + 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow2 + 1);
                }

                tmpIndexRow2++;

            } else if (tmpNumberRow1 < tmpNumberRow3) {

                if (checkAdd(tmpRow, tmpNumberRow1 - 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow1 - 1);
                }

                if (checkAdd(tmpRow, tmpNumberRow1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow1);
                }

                if (checkAdd(tmpRow, tmpNumberRow1 + 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow1 + 1);
                }

                tmpIndexRow1++;

            } else {

                if (checkAdd(tmpRow, tmpNumberRow3 - 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow3 - 1);
                }

                if (checkAdd(tmpRow, tmpNumberRow3, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow3);
                }

                if (checkAdd(tmpRow, tmpNumberRow3 + 1, false, row1, row2, row3)) {
                    tmpRow.listX.add(tmpNumberRow3 + 1);
                }

                tmpIndexRow3++;
            }
        }

        return tmpRow;
    }


    private static int updateNumber(Row row, int rowSize, int tmpIndexRow, int currentNumber) {
        if (rowSize != 0 && (tmpIndexRow < rowSize)) {

            return row.listX.get(tmpIndexRow);
        } else {

            return currentNumber;
        }
    }


    private static boolean checkAdd(Row tmpRow, int addPoint, boolean flagCntNb, Row row1, Row row2, Row row3) {

        boolean done = false;

        int sizeRow = tmpRow.listX.size();

        if (flagCntNb) {

            if ((((sizeRow > 0) && (tmpRow.listX.get(sizeRow - 1) < addPoint)) || (sizeRow == 0))) {

                int tmpCntNb = countNeighbours(row1, row2, row3, addPoint);

                if ((tmpCntNb == 2) || (tmpCntNb == 3)) {

                    done = true;
                }

            } else {
                done = false;
            }

        } else {

            if ((((sizeRow > 0) && (tmpRow.listX.get(sizeRow - 1) < addPoint)) || (sizeRow == 0))
                    && (countNeighbours(row1, row2, row3, addPoint) == 3)) {
                done = true;
            } else {
                done = false;
            }
        }
        return done;
    }


    private static int minNumber(int a, int b, int c) {

        int smallest;

        if (a <= b && a <= c) {

            smallest = a;
        } else if (b <= c && b <= a) {

            smallest = b;
        } else {

            smallest = c;
        }

        return smallest;
    }


    private static int countNeighbours(Row row1, Row row2, Row row3, Integer currentInteger) {

        int count = 0;

        int ti = binarySearch(row1.listX, currentInteger);
        int ci = binarySearch(row2.listX, currentInteger);
        int bi = binarySearch(row3.listX, currentInteger);

        count = (ti >= 0) ? count + 1 : count;
        count = (bi >= 0) ? count + 1 : count;

        count += countLeftAndRight(row1.listX, ti, currentInteger);
        count += countLeftAndRight(row2.listX, ci, currentInteger);
        count += countLeftAndRight(row3.listX, bi, currentInteger);

        return count;
    }

    private static int countLeftAndRight(ArrayList<Integer> listX, int centerIndexRow, Integer currentInteger) {

        int cntLeftAndRight = 0;

        int posLeft = (centerIndexRow >= 0) ? centerIndexRow - 1 : -centerIndexRow - 2; // позиция для проверки левого
        int posRight = (centerIndexRow >= 0) ? centerIndexRow + 1 : -centerIndexRow - 1; // позиция для проверки правого

        int size = listX.size();

        if ((posLeft >= 0) && (posLeft < size) && (listX.get(posLeft) == currentInteger - 1)) {

            cntLeftAndRight++;
        }

        if ((posRight >= 0) && (posRight < size) && (listX.get(posRight) == currentInteger + 1)) {

            cntLeftAndRight++;
        }

        return cntLeftAndRight;
    }
}