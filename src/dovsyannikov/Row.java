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

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (y != row.y) return false;
        return listX != null ? listX.equals(row.listX) : row.listX == null;
    }*/

    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + (listX != null ? listX.hashCode() : 0);
        return result;
    }
}
