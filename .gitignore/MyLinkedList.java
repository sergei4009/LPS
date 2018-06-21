class MyLinkedList {
    private Shell first;
    private Shell last;
    private int size = 0;


    Object add(Object object) {
        size++;
        Shell tmp = last;
        Shell newObj = new Shell(tmp, object, null);
        last = newObj;
        if (tmp == null) {
            first = newObj;
        } else
            {
            tmp.setNext(newObj);

        }
        return object;
    }

    boolean remove(Object object) {
        int index = 0;
        Shell temp = first;

        while (index < size) {
            if (temp.getCurrent().equals(object)) {
                Shell previous = temp.getPrevious();
                Shell next = temp.getNext();

                if (previous == null) {
                    first = next;
                } else {
                    previous.setNext(next);
                    temp.setPrevious(null);
                }
                if (next == null) {
                    last = previous;
                } else {
                    next.setPrevious(previous);
                    temp.setNext(null);
                }
                temp.setCurrent(null);
                size--;
                return true;
            }
            index++;
            temp = temp.getNext();
        }
        return false;
    }

    int indexOf(Object object)
    {
        int index = 0;
        Shell temp = first;

        while (index < size) {
            if (temp.getCurrent().equals(object))
            {
                return index;
            }
            index++;
            temp = temp.getNext();
        }
        return -1;
    }

    Object get(int index)
    {
        if (index < size)
        {
            int[] dir = getDirection(index);
            Shell start = getShell(dir[0]);
            int startIndex = dir[1];

            switch (dir[2])
            {
                case 0:
                    break;
                case -1:
                    while (startIndex < index)
                    {
                        startIndex++;
                    }
                    break;
                case 1:
                    while (startIndex > index)
                    {
                        startIndex--;
                    }
                    break;
            }
            try
            {
                return start.getCurrent();

            } catch (NullPointerException e) {
                System.err.println("Ошибка: NullPointerException");
                System.exit(105);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        Shell temp = first;

        sb.append("[");
        if (size != 0)
        {
            for (int i = 0; i < size - 1; i++)
            {
                sb.append(temp.getCurrent());
                sb.append(", ");
                temp = temp.getNext();
            }
            sb.append(temp.getCurrent());
        }
        sb.append("]");
        return sb.toString();
    }

    boolean contains(Object obj)
    {
        return indexOf(obj) >= 0;
    }

    int size()
    {
        return size;
    }

    private Shell getShell(int i)
    {
        switch (i)
        {
            case 0:
                return first;
            case 1:
                return last;
            default:
                System.err.println();
                System.exit(103);
        }
        return null;
    }

    //Определение индекса ссылок (first, last)
    private int[] getDirection(int index)
    {
        int[] direction = new int[3];
        int min = size;
        int[] indexes = {0, size - 1};

        for (int i = 0; i < indexes.length; i++) {
            int tmp = Math.abs(indexes[i] - index);
            if (tmp < min) {
                min = tmp;
                direction[0] = i;
                direction[1] = indexes[i];
                try {
                    direction[2] = (indexes[i] - index) / tmp;
                } catch (ArithmeticException e) {
                    direction[2] = 0;
                    break;
                }
            }
        }
        return direction;
    }

    private int calc(double d) {
        return (int) Math.ceil(sizeDoubleDec() * d);
    }

    private double sizeDoubleDec() {
        return (double) size - 1;
    }
}

class Shell
{
    private Object current;
    private Shell previous;
    private Shell next;

    Shell(Shell previous, Object current, Shell next)
    {
        this.previous = previous;
        this.current = current;
        this.next = next;
    }

    Shell getNext() {
        return next;
    }

    void setNext(Shell next) {
        this.next = next;
    }

    Shell getPrevious() {
        return previous;
    }

    void setPrevious(Shell previous) {
        this.previous = previous;
    }

    Object getCurrent() {
        return current;
    }

    void setCurrent(Object current) {
        this.current = current;
    }

}
