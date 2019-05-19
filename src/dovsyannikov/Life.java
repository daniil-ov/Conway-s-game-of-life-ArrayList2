package dovsyannikov;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;
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

            drawCurrentLife(); //отрисовка поля
            change(); //рассчет следующего поколения
            moveField(); //метод для перемещения и масшатабирования поля

            //задержка 0.5 сек
            //StdDraw.pause(500);
        }
    }

    //метод для перемещения и масшатабирования поля
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

    //отрисовка поля
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

    //рассчет следующего поколения
    private static void change() {

        long start = System.currentTimeMillis();

        ArrayList<Row> tmp; //временная переменная для обмена ссылками текущего поколения и следующего


        if (handlerStrings(currentLife.get(0), currentLife.get(1), currentLife.get(2)).listX.isEmpty()) {
            //если не возродится жизнь в первой строке, то считать следующее поле со смещением не нужно

            //добавляем 2 технологические строки для подсчета след. поколения
            for (int i = 2; i > 0; i--) {

                nextStepLife.add(new Row(currentLife.get(2).y + i));
            }

            //проход по всем строкам в поле
            for (int i = 2; i < currentLife.size() - 1; i++) {

                //получаем новую центральную строку и записываем ее в новое поколение
                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i, tmpRow);
            }

        } else {
            //жизнь возродится в первой строке и нужно записывать след. поколение на строку ниже

            //добавляем 2 технологические строки для подсчета след. поколения, но с учетом того, что будем записывать со смещением
            // на одну строку ниже
            for (int i = 3; i > 1; i--) {

                nextStepLife.add(new Row(currentLife.get(2).y + i));
            }

            //проход по всем строкам в поле и запись со смещением
            for (int i = 1; i < currentLife.size() - 1; i++) {

                //получаем новую центральную строку и записываем ее в новое поколение
                Row tmpRow = handlerStrings(currentLife.get(i - 1), currentLife.get(i), currentLife.get(i + 1));
                tmpRow.y = currentLife.get(i).y;

                nextStepLife.add(i + 1, tmpRow);
            }
        }

        //добавляем 2 технологические строки снизу для подсчета след. поколения, если  какая-нибудь из них занята
        for (int i = 1; i < 3; i++) {

            if (!(nextStepLife.get(nextStepLife.size() - i).listX.isEmpty())) {

                nextStepLife.add(new Row(nextStepLife.get(nextStepLife.size() - 1).y - i));
            }
        }

        //очищаем исходное поле
        currentLife.clear();

        //обмениваем через ссылку исходное поле и следующее поколение
        tmp = currentLife;
        currentLife = nextStepLife;
        nextStepLife = tmp;

        long finish = System.currentTimeMillis();

        System.out.println("Время рассчета след. поколения: " + (finish - start));
    }

    //на вход получаем три строки и возвращаем готовую цетральную
    private static Row handlerStrings(Row row1, Row row2, Row row3) {

        int row1Min = Integer.MAX_VALUE;
        int row2Min = Integer.MAX_VALUE;
        int row3Min = Integer.MAX_VALUE;

        int row1Max = Integer.MIN_VALUE;
        int row2Max = Integer.MIN_VALUE;
        int row3Max = Integer.MIN_VALUE;

        //запись размера трех строк
        int rowSize1 = row1.listX.size();
        int rowSize2 = row2.listX.size();
        int rowSize3 = row3.listX.size();

        if (rowSize1 != 0) {
            row1Min = row1.listX.get(0);
            row1Max = row1.listX.get(rowSize1 - 1);
        }

        if (rowSize2 != 0) {
            row2Min = row2.listX.get(0);
            row2Max = row2.listX.get(rowSize2 - 1);
        }

        if (rowSize3 != 0) {
            row3Min = row3.listX.get(0);
            row3Max = row3.listX.get(rowSize3 - 1);
        }

        int xmin = min(min(row1Min, row2Min), row3Min);
        int xmax = max(max(row1Max, row2Max), row3Max);

        //строка для записи готовой центральной строки
        Row tmpRow = new Row();

        for (int currentInteger = xmin - 1; currentInteger <= xmax + 1; currentInteger++) {

            int count = 0;

            //получение местоположения цетрального столбца
            int ti = binarySearch(row1.listX, currentInteger);
            int ci = binarySearch(row2.listX, currentInteger);
            int bi = binarySearch(row3.listX, currentInteger);

            //если бинарный поиск нашел индекс чисел центрального столбца, то суммируем не считая центральную точку
            count = (ti >= 0) ? count + 1 : count;
            count = (bi >= 0) ? count + 1 : count;

            //подсчет количества левых и правых соседей в трех строках
            count += countLeftAndRight(row1.listX, ti, currentInteger);
            count += countLeftAndRight(row2.listX, ci, currentInteger);
            count += countLeftAndRight(row3.listX, bi, currentInteger);


            if (count == 3) {
                tmpRow.listX.add(currentInteger);
            } else if ((ci >= 0) && (count == 2)) {
                tmpRow.listX.add(currentInteger);
            } else {

                if (count == 0 && ci < 0) {

                    if (row1Min != Integer.MAX_VALUE && rowSize1 > -(ti + 1)) {
                        row1Min = row1.listX.get(-(ti + 1));
                    } else if (row1Min != Integer.MAX_VALUE && rowSize1 == -(ti + 1)) {
                        row1Min = Integer.MAX_VALUE;
                    }

                    if (row2Min != Integer.MAX_VALUE && rowSize2 > -(ci + 1)) {
                        row2Min = row2.listX.get(-(ci + 1));
                    } else if (row2Min != Integer.MAX_VALUE && rowSize2 == -(ci + 1)) {
                        row2Min = Integer.MAX_VALUE;
                    }

                    if (row3Min != Integer.MAX_VALUE && rowSize3 > -(bi + 1)) {
                        row3Min = row3.listX.get(-(bi + 1));
                    } else if (row3Min != Integer.MAX_VALUE && rowSize3 == -(bi + 1)) {
                        row3Min = Integer.MAX_VALUE;
                    }

                    currentInteger = min(min(row1Min, row2Min), row3Min) - 2;
                }
            }



        }

        return tmpRow;

    }


    //считаем левых и правых соседей в списке, исходя от индекса центральной точки
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
