package dovsyannikov;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

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
            strings.listX.forEach(new TIntProcedure() {
                @Override
                public boolean execute(int i) {
                    StdDraw.filledSquare(i, strings.y, 0.000001);
                    return true;
                }
            });
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

        int ti = -1;
        int ci = -1;
        int bi = -1;

        int row1Min;
        int row2Min;
        int row3Min;

        int xmin = row1.listX.isEmpty() ? Integer.MAX_VALUE : row1.listX.get(0);
        xmin = row2.listX.isEmpty() ? xmin : Math.min(xmin, row2.listX.get(0));
        xmin = row3.listX.isEmpty() ? xmin : Math.min(xmin, row3.listX.get(0));

        int xmax = row1.listX.isEmpty() ? Integer.MIN_VALUE : row1.listX.get(row1.listX.size() - 1);
        xmax = row2.listX.isEmpty() ? xmax : Math.max(xmax, row2.listX.get(row2.listX.size() - 1));
        xmax = row3.listX.isEmpty() ? xmax : Math.max(xmax, row3.listX.get(row3.listX.size() - 1));

        //строка для записи готовой центральной строки
        Row tmpRow = new Row();

        for (int currentInteger = xmin - 1; currentInteger <= xmax + 1; currentInteger++) {

            int count = 0;

            ti = updateIndex(ti, row1, currentInteger);
            ci = updateIndex(ci, row2, currentInteger);
            bi = updateIndex(bi, row3, currentInteger);

            //получение местоположения цетрального столбца
            /*int ti = row1.listX.binarySearch(currentInteger);
            int ci = row2.listX.binarySearch(currentInteger);
            int bi = row3.listX.binarySearch(currentInteger);*/

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

                    row1Min = (-ti - 1 < row1.listX.size()) ? row1.listX.get(-ti - 1) : Integer.MAX_VALUE;
                    row2Min = (-ci - 1 < row2.listX.size()) ? row2.listX.get(-ci - 1) : Integer.MAX_VALUE;
                    row3Min = (-bi - 1 < row3.listX.size()) ? row3.listX.get(-bi - 1) : Integer.MAX_VALUE;

                    currentInteger = Math.min(Math.min(row1Min, row2Min), row3Min) - 2;
                }
            }
        }

        return tmpRow;

    }

    private static int updateIndex(int tmpIndex, Row row, int currentInteger) {
        int newIndex = tmpIndex;

        if (tmpIndex < 0) {
            if ((Math.abs(tmpIndex) - 1 < row.listX.size()) && (row.listX.get(Math.abs(tmpIndex) - 1) == currentInteger)) {
                newIndex = Math.abs(tmpIndex) - 1;
            }
        } else {
            if ((tmpIndex + 1 < row.listX.size()) && (row.listX.get(tmpIndex + 1) == currentInteger)) {
                newIndex = tmpIndex + 1;
            } else {
                newIndex = -tmpIndex - 2;
            }
        }

        return newIndex;
    }


    //считаем левых и правых соседей в списке, исходя от индекса центральной точки
    private static int countLeftAndRight(TIntArrayList listX, int centerIndexRow, int currentInteger) {

        int cntLeftAndRight = 0;

        int posLeft = Math.abs((centerIndexRow >= 0) ? centerIndexRow - 1 : -centerIndexRow - 2); // позиция для проверки левого
        int posRight = Math.abs((centerIndexRow >= 0) ? centerIndexRow + 1 : -centerIndexRow - 1); // позиция для проверки правого

        int size = listX.size();

        if ((posLeft < size) && (listX.get(posLeft) == currentInteger - 1)) {

            cntLeftAndRight++;
        }

        if ((posRight < size) && (listX.get(posRight) == currentInteger + 1)) {

            cntLeftAndRight++;
        }

        return cntLeftAndRight;
    }
}