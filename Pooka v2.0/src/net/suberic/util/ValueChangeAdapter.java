package net.suberic.util;

public abstract class  ValueChangeAdapter implements ValueChangeListener {
    public ValueChangeAdapter() {
	super();
    }

    public abstract void valueChanged(String changedValue);
}
