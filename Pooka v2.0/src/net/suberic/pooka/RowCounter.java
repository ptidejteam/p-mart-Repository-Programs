package net.suberic.pooka;

public class RowCounter {
    static RowCounter instance = new RowCounter();

    private RowCounter() {
    }

    public static RowCounter getInstance() {
	return instance;
    }

}
