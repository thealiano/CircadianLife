package com.example.aliano.clientcircadian_va.Data;

/**
 * Created by John on 06.05.2015.
 */
public interface Data<E> {
    public String getType();
    public E getData();
    public void setData(E data);
    public String toJsonString();
}
