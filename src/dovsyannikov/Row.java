package dovsyannikov;

import java.util.ArrayList;

public class Row {

    public int y;
    public ArrayList<Integer> listX;

    public Row(int y, ArrayList<Integer> listX) {
        this.y = y;
        this.listX = listX;
    }

    public Row(int i) {
        ArrayList<Integer> listX = new ArrayList<>();
        Integer y = i;

        this.y = y;
        this.listX = listX;
    }

    public Row() {
        ArrayList<Integer> listX = new ArrayList<>();
        Integer y = 0;

        this.y = y;
        this.listX = listX;
    }

    @Override
    public String toString() {
        return "String " + y + ", x = " + listX.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (y != row.y) return false;
        return listX != null ? listX.equals(row.listX) : row.listX == null;
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + (listX != null ? listX.hashCode() : 0);
        return result;
    }
}
