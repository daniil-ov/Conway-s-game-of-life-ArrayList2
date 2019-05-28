package dovsyannikov;

import gnu.trove.list.array.TIntArrayList;

public class Row {

    public int y;
    public TIntArrayList listX;


    public Row(int i) {
        TIntArrayList listX = new TIntArrayList();
        int y = i;

        this.y = y;
        this.listX = listX;
    }

    public Row() {
        TIntArrayList listX = new TIntArrayList();
        int y = 0;

        this.y = y;
        this.listX = listX;
    }

    @Override
    public String toString() {
        return "Row " + y + ", x = " + listX.toString();
    }

}
